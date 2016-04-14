package foobank.service;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("account")
public class BankAccountService {

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
        
        return 0;
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
        
        return 0;
    }

    /**
     * Returns the transfer history of the specified account
     * @param accountId Identifier of the bank account
     * @return The transfer history
     */
    @GET
    @Path("{accountId}/history")
    public double getTransferHistory(
            @PathParam("accountId") long accountId) {
        
        return 0;
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
            @QueryParam("destAccountId") long destAccountId,
            @QueryParam("amount") double amount) {
    }
}
