package foobank.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import foobank.entity.BankAccount;
import foobank.entity.Customer;
import foobank.entity.Transfer;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("account")
public class BankAccountService {
    private static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("foobank");
   
    /**
     * Create a new Bank Account for the specified customer
     * @param customerId Identifier of the customer which will own this account
     * @param initialBalance Initial deposit amount (defaults to 0)
     * @return Identifier of the new bank account
     */
    @POST
    public long createBankAccount(
            @FormParam("customerId") long customerId, 
            @DefaultValue("0") @FormParam("balance") double initialBalance) {
        
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance must be non-negative");
        }
        
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            Customer customer = em.find(Customer.class, customerId);
            if (customer == null) {
                throw new IllegalArgumentException("Customer not found: " + customerId);
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
    @GET
    @Path("{accountId}/balance")
    public double getBalance(
            @PathParam("accountId") long accountId) {
        
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            BankAccount account = em.find(BankAccount.class, accountId);
            if (account == null) {
                throw new IllegalArgumentException("Account not found: " + accountId);
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
    @GET
    @Path("{accountId}/history")
    public List<TransferInfo> getTransferHistory (
            @PathParam("accountId") long accountId) {
        
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            BankAccount account = em.find(BankAccount.class, accountId);
            if (account == null) {
                throw new IllegalArgumentException("Account not found: " + accountId);
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
    @POST
    @Path("{srcAccountId}/transfer")
    @Produces(MediaType.TEXT_PLAIN)
    public void transfer(
            @PathParam("srcAccountId") long srcAccountId,
            @FormParam("destAccountId") long destAccountId,
            @FormParam("amount") double amount) {
        
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            BankAccount srcAccount = em.find(BankAccount.class, srcAccountId);
            if (srcAccount == null) {
                throw new IllegalArgumentException("Source account not found: " + srcAccountId);
            }
            BankAccount destAccount = em.find(BankAccount.class, destAccountId);
            if (destAccount == null) {
                throw new IllegalArgumentException("Destination account not found: " + destAccountId);
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
