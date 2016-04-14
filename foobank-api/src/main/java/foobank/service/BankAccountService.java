package foobank.service;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * API with services related with bank accounts
 */
@Path("account")
public interface BankAccountService {

    /**
     * Create a new Bank Account for the specified customer
     * @param customerId Identifier of the customer which will own this account
     * @param initialBalance Initial deposit amount (defaults to 0)
     * @return Identifier of the new bank account
     */
    @POST
    public long createBankAccount(
            @FormParam("customerId") long customerId, 
            @DefaultValue("0") @FormParam("balance") double initialBalance);

    /**
     * Returns the balance of the specified account
     * @param accountId Identifier of the bank account
     * @return The balance
     */
    @GET
    @Path("{accountId}/balance")
    public double getBalance(
            @PathParam("accountId") long accountId);

    /**
     * Returns the transfer history of the specified account
     * @param accountId Identifier of the bank account
     * @return List of transfers from / to the specifiec bank account
     */
    @GET
    @Path("{accountId}/history")
    public List<TransferInfo> getTransferHistory (
            @PathParam("accountId") long accountId);

    /**
     * Transfer money between bank accounts 
     * @param srcAccountId Identifier of the account from which the money will be transfered
     * @param destAccountId Identifier of the account where the money will be transfered to
     * @param amount Amount of money being transfered 
     */
    @POST
    @Path("{srcAccountId}/transfer")
    public void transfer(
            @PathParam("srcAccountId") long srcAccountId,
            @FormParam("destAccountId") long destAccountId,
            @FormParam("amount") double amount);

}