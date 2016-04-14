package foobank.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.BadRequestException;

import foobank.entity.BankAccount;
import foobank.entity.Customer;
import foobank.entity.Transfer;

/**
 * API with services related with bank accounts
 */
public class BankAccountServiceImpl implements BankAccountService {
    private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("foobank");
   
    /**
     * Create a new Bank Account for the specified customer
     * @param customerId Identifier of the customer which will own this account
     * @param initialBalance Initial deposit amount (defaults to 0)
     * @return Identifier of the new bank account
     */
    @Override
    public long createBankAccount(long customerId, double initialBalance) {
        
        if (initialBalance < 0) {
            throw new BadRequestException("Initial balance must be non-negative");
        }
        
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            Customer customer = em.find(Customer.class, customerId);
            if (customer == null) {
                throw new BadRequestException("Customer not found: " + customerId);
            }
            BankAccount newAccount = new BankAccount(customer, initialBalance);
            em.persist(newAccount);
            
            //Assuming there a "Transfer" object for the initial deposit
            if (initialBalance > 0) {
                em.persist(new Transfer(null, newAccount, initialBalance));
            }
            
            em.getTransaction().commit();
            return newAccount.getId();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    

    /**
     * Returns the balance of the specified account
     * @param accountId Identifier of the bank account
     * @return The balance
     */
    @Override
    public double getBalance(long accountId) {
        
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            BankAccount account = em.find(BankAccount.class, accountId);
            if (account == null) {
                throw new BadRequestException("Account not found: " + accountId);
            }
            return account.getBalance();
        } finally {
            em.close();
        }
    }

    
    /**
     * Returns the transfer history of the specified account
     * @param accountId Identifier of the bank account
     * @return List of transfers from / to the specifiec bank account
     */
    @Override
    public List<TransferInfo> getTransferHistory (long accountId) {
        
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            BankAccount account = em.find(BankAccount.class, accountId);
            if (account == null) {
                throw new BadRequestException("Account not found: " + accountId);
            }
            List<TransferInfo> ret = new ArrayList<>();
            List<Transfer> transfers = em.createQuery("FROM Transfer WHERE source=:account OR destination=:account", Transfer.class)
                    .setParameter("account", account)
                    .getResultList();
            for (Transfer transfer : transfers) {
                ret.add(new TransferInfo(
                        transfer.getSource() != null ? transfer.getSource().getId() : null,  
                        transfer.getDestination() != null ? transfer.getDestination().getId() : null,
                        transfer.getAmount()));
            }
            return ret;
        } finally {
            em.close();
        }
    }
    
    
    /**
     * Transfer money between bank accounts 
     * @param srcAccountId Identifier of the account from which the money will be transfered
     * @param destAccountId Identifier of the account where the money will be transfered to
     * @param amount Amount of money being transfered 
     */
    @Override
    public void transfer(long srcAccountId, long destAccountId, double amount) {        
        if (amount <= 0) {
            throw new BadRequestException("Transfer amount must be positive");
        }
        
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            BankAccount srcAccount = em.find(BankAccount.class, srcAccountId);
            if (srcAccount == null) {
                throw new BadRequestException("Source account not found: " + srcAccountId);
            }
            BankAccount destAccount = em.find(BankAccount.class, destAccountId);
            if (destAccount == null) {
                throw new BadRequestException("Destination account not found: " + destAccountId);
            }
            
            srcAccount.setBalance(srcAccount.getBalance() - amount);
            destAccount.setBalance(destAccount.getBalance() + amount);
            
            em.merge(srcAccount);
            em.merge(destAccount);
            em.persist(new Transfer(srcAccount, destAccount, amount));
            
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
