package foobank;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.BadRequestException;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import foobank.service.BankAccountService;
import foobank.service.TransferInfo;

public class BankServiceTest {

    private HttpServer server;
    private BankAccountService client;
    private static final double EPS = 0.001;

    @Before
    public void setUp() throws Exception {
        // start the server
        server = Main.startServer();
        client = JAXRSClientFactory.create(Main.BASE_URI, BankAccountService.class, Arrays.asList(new JacksonJsonProvider()));
     }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test(expected=BadRequestException.class)
    public void testCreateWithInvalidCustomer() {
        client.createBankAccount(-1, 0);
    }
    
    @Test(expected=BadRequestException.class)
    public void testBalanceWithInvalidAccount() {
        client.getBalance(9999);
    }

    @Test(expected=BadRequestException.class)
    public void testHistoyWithInvalidAccount() {
        client.getTransferHistory(9999);
    }
    
    @Test()
    public void testCreateWithoutBalance() {
        long accountId = client.createBankAccount(1, 0);
        Assert.assertEquals(0, client.getBalance(accountId), 0);
        List<TransferInfo> transfers = client.getTransferHistory(accountId);
        Assert.assertEquals(0, transfers.size());
    }
    
    @Test()
    public void testCreateWithBalance() {
        long accountId = client.createBankAccount(1, 10);
        Assert.assertEquals(10, client.getBalance(accountId), EPS);
        List<TransferInfo> transfers = client.getTransferHistory(accountId);
        Assert.assertEquals(1, transfers.size());
        Assert.assertEquals(10.0, transfers.get(0).getAmount(), EPS);
        Assert.assertEquals(null, transfers.get(0).getSourceAccountId());
        Assert.assertEquals((Long)accountId, transfers.get(0).getDestinationAccountId());
    }
    
    @Test()
    public void testTransfer() {
        long srcAccountId   = client.createBankAccount(1, 100);
        long destAccountId  = client.createBankAccount(2, 200);
        long otherAccountId = client.createBankAccount(3, 300);
        double amount = 10;
        
        client.transfer(srcAccountId, destAccountId, amount);
        Assert.assertEquals( 90, client.getBalance(srcAccountId), EPS);
        Assert.assertEquals(210, client.getBalance(destAccountId), EPS);
        Assert.assertEquals(300, client.getBalance(otherAccountId), EPS);
        Assert.assertEquals(2, client.getTransferHistory(srcAccountId).size());
        Assert.assertEquals(2, client.getTransferHistory(destAccountId).size());
        Assert.assertEquals(1, client.getTransferHistory(otherAccountId).size());
    }
    
    @Test()
    public void testTransferNegative() {
        long srcAccountId = client.createBankAccount(1, 100);
        long destAccountId = client.createBankAccount(2, 200);
        long otherAccountId = client.createBankAccount(3, 300);
        double amount = -10;
        try {
            client.transfer(srcAccountId, destAccountId, amount);
            Assert.fail();
        } catch (BadRequestException e) {
            //Assert the money was not transfered
            Assert.assertEquals(100, client.getBalance(srcAccountId), EPS);
            Assert.assertEquals(200, client.getBalance(destAccountId), EPS);         
            Assert.assertEquals(300, client.getBalance(otherAccountId), EPS);
            Assert.assertEquals(1, client.getTransferHistory(srcAccountId).size());
            Assert.assertEquals(1, client.getTransferHistory(destAccountId).size());
            Assert.assertEquals(1, client.getTransferHistory(otherAccountId).size());
        }
    }

    @Test()
    public void testTransferInvalidSource() {
        long srcAccountId = client.createBankAccount(1, 100);
        long destAccountId = 9999;
        long otherAccountId = client.createBankAccount(3, 300);
        double amount = 10;
        try {
            client.transfer(srcAccountId, destAccountId, amount);
            Assert.fail();
        } catch (BadRequestException e) {
            //Assert the money was not transfered
            Assert.assertEquals(100, client.getBalance(srcAccountId), EPS);
            Assert.assertEquals(300, client.getBalance(otherAccountId), EPS);
            Assert.assertEquals(1, client.getTransferHistory(srcAccountId).size());
            Assert.assertEquals(1, client.getTransferHistory(otherAccountId).size());
        }
    }

    @Test()
    public void testTransferInvalidDestination() {
        long srcAccountId = 9999;
        long destAccountId = client.createBankAccount(1, 100);
        long otherAccountId = client.createBankAccount(3, 300);
        double amount = 10;
        try {
            client.transfer(srcAccountId, destAccountId, amount);
            Assert.fail();
        } catch (BadRequestException e) {
            //Assert the money was not transfered
            Assert.assertEquals(100, client.getBalance(destAccountId), EPS);
            Assert.assertEquals(300, client.getBalance(otherAccountId), EPS);
            Assert.assertEquals(1, client.getTransferHistory(destAccountId).size());
            Assert.assertEquals(1, client.getTransferHistory(otherAccountId).size());
        }
    }
    
    @Test()
    public void testTransferHistory() {
        long accountA = client.createBankAccount(1, 100);
        long accountB = client.createBankAccount(2, 200);
        long accountC = client.createBankAccount(3, 300);
        long accountD = client.createBankAccount(4, 400);
        
        client.transfer(accountA, accountB, 11);
        client.transfer(accountB, accountC, 12);
        client.transfer(accountA, accountC, 13);
        client.transfer(accountB, accountA, 14);
        
        assertEquals(90, client.getBalance(accountA), EPS);
        assertTransferEquals(
                client.getTransferHistory(accountA),
                new TransferInfo(null, accountA, 100),
                new TransferInfo(accountA, accountB, 11),
                new TransferInfo(accountA, accountC, 13),
                new TransferInfo(accountB, accountA, 14));
        
        assertEquals(185, client.getBalance(accountB), EPS);
        assertTransferEquals(
                client.getTransferHistory(accountB),
                new TransferInfo(null, accountB, 200),
                new TransferInfo(accountA, accountB, 11),
                new TransferInfo(accountB, accountC, 12),
                new TransferInfo(accountB, accountA, 14));

        assertEquals(325, client.getBalance(accountC), EPS);
        assertTransferEquals(
                client.getTransferHistory(accountC),
                new TransferInfo(null, accountC, 300),
                new TransferInfo(accountB, accountC, 12),
                new TransferInfo(accountA, accountC, 13));

        assertEquals(400, client.getBalance(accountD), EPS);
        assertTransferEquals(
                client.getTransferHistory(accountD),
                new TransferInfo(null, accountD, 400));
    }
    
    private void assertTransferEquals(List<TransferInfo> actual, TransferInfo... expected) {
        Assert.assertEquals(expected.length, actual.size());
        for (int i=0; i<expected.length; i++) {
            Assert.assertEquals(expected[i].getSourceAccountId(), actual.get(i).getSourceAccountId());
            Assert.assertEquals(expected[i].getDestinationAccountId(), actual.get(i).getDestinationAccountId());
            Assert.assertEquals(expected[i].getAmount(), actual.get(i).getAmount(), EPS);
        }
    }
}
