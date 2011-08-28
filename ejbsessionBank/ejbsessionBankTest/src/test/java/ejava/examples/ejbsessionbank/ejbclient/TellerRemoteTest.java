package ejava.examples.ejbsessionbank.ejbclient;

import java.util.List;

import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.ejbsessionbank.bl.BankException;
import ejava.examples.ejbsessionbank.bl.Teller;
import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Ledger;
import ejava.examples.ejbsessionbank.bo.Owner;
import ejava.examples.ejbsessionbank.ejb.TellerRemote;

public class TellerRemoteTest extends TestCase {
    Log log = LogFactory.getLog(TellerRemoteTest.class);
    InitialContext jndi;
    String jndiName = System.getProperty("jndi.name", "TellerEJB/remote");
    
    public void setUp() throws Exception {
        Thread.sleep(1000); //hack -- give JBoss extra time to finish deploy
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        
        cleanup();
    }
    
    private void cleanup() throws Exception {
        if (jndi!=null) {
            TellerRemote teller = (TellerRemote)jndi.lookup(jndiName);
            if (jndi!=null) {
                for (int index=0; ; index+=100) {
                    List<Owner> owners = teller.getOwnersLoaded(index, 100);
                    if (owners.size() == 0) { break; }
                    for (Owner owner : owners) {
                        log.debug("removing owner:" + owner);
                        for (Account a: owner.getAccounts()) {
                            zeroAccount(teller, a);
                        }
                        teller.removeOwner(owner.getId());
                    }
                }
                
                for (@SuppressWarnings("unused")
				int index=0; ; index+= 100) {
                    List<Account> accounts = teller.getAccounts(0, 100);
                    if (accounts.size() == 0) { break; }
                    for (Account a: accounts) {
                        log.debug("cleaning up account:" + a);
                        zeroAccount(teller, a);
                        teller.closeAccount(a.getAccountNumber());                        
                    }
                }
            }
        }
    }
    
    private void zeroAccount(
            Teller teller, Account account) throws BankException {
        log.debug("cleaning up account:" + account);
        if (account.getBalance() > 0) {
            account.withdraw(account.getBalance());
           teller.updateAccount(account);
        }
        else if (account.getBalance() < 0) {
            account.deposit(account.getBalance() * -1);
            teller.updateAccount(account);
        }
    }

    
    public void testLookupTellerRemote() throws Exception {
        log.info("*** testLookupTellerRemote ***");
        @SuppressWarnings("unused")
        TellerRemote teller = null;
        
        log.debug("looking up remote:" + jndiName);
        try {
            Object object = jndi.lookup(jndiName);
            log.debug("found object:" + object);
            teller = (TellerRemote)object;
        }
        catch (Exception ex) {
            log.fatal("error getting teller remote:" + ex);
            fail("error getting teller remote:" + ex);
        }        
    }    

    public void testCreateAccount() throws Exception {
        log.info("*** testCreateAccount ***");
        Account account=null;
        TellerRemote teller = (TellerRemote)jndi.lookup(jndiName);
        
        log.debug("creating account, teller=" + teller);
        try {
            account = teller.createAccount("1234");
            log.debug("account created:" + account);
        }
        catch (Exception ex) {
            log.fatal("error creating account:" + ex, ex);
            fail("error creating account:" + ex);
        }        
        
        try {
            teller.createAccount(account.getAccountNumber());
            fail("created account with duplicate number");
        }
        catch (Exception expected) {
            log.info("got expected exception trying to create " +
                    "duplicate account:" + expected);
        }
    }
    
    public void testGetAccount() throws Exception {
        log.info("*** testGetAccount ***");
        Account account = null;
        TellerRemote teller = (TellerRemote)jndi.lookup(jndiName);
        
        log.debug("creating account, teller=" + teller);
        try {
            account = teller.createAccount("1234");
            log.debug("account created:" + account);
            Account account2 = teller.getAccount(account.getAccountNumber());
            log.debug("got account:" + account);
            
            assertEquals("unexpected account num:"+account2.getAccountNumber(),
                    account.getAccountNumber(), account2.getAccountNumber());
            assertEquals("unexpected account bal:"+account2.getBalance(),
                    account.getBalance(), account2.getBalance());
        }
        catch (Exception ex) {
            log.fatal("error getting account:" + ex, ex);
            fail("error getting account:" + ex);
        }        
    }    

    public void testUpdateAccount() throws Exception {
        log.info("*** testUpdateAccount ***");
        Account account = null;
        TellerRemote teller = (TellerRemote)jndi.lookup(jndiName);
        
        log.debug("creating account, teller=" + teller);
        try {
            account = teller.createAccount("1234");
            log.debug("account created:" + account);
            
            account.deposit(5.00);
            assertEquals("unexpected balance:" + account.getBalance(),
                    5.00, account.getBalance());
            teller.updateAccount(account);
            log.debug("updated account:" + account);
            
            Account account2 = teller.getAccount(account.getAccountNumber());
            log.debug("retrieved updated account:" + account2);
                        
            assertEquals("unexpected account bal:"+account2.getBalance(),
                    account.getBalance(), account2.getBalance());
            
            account.withdraw(10.00);
            assertEquals("unexpected balance:" + account.getBalance(),
                    -5.00, account.getBalance());
            teller.updateAccount(account);
            log.debug("updated account:" + account);
            
            account2 = teller.getAccount(account.getAccountNumber());
            log.debug("retrieved updated account:" + account2);
                        
            assertEquals("unexpected account bal:"+account2.getBalance(),
                    account.getBalance(), account2.getBalance());
        }
        catch (Exception ex) {
            log.fatal("error updating account:" + ex, ex);
            fail("error updating account:" + ex);
        }        
    }    
    
    public void testCloseAccount() throws Exception {
        log.info("*** testCloseAccount ***");
        TellerRemote teller = (TellerRemote)jndi.lookup(jndiName);

        try {
            Account account = teller.createAccount("1234");
            account.deposit(10.0);
            teller.updateAccount(account);
            try {
                log.debug("trying to close account with + balance:" + account);
                teller.closeAccount(account.getAccountNumber());
                fail("account was reported closed");
            }
            catch (BankException expected) {}            
            
            account.withdraw(20.0);
            teller.updateAccount(account);
            try {
                log.debug("trying to close account with - balance:" + account);
                teller.closeAccount(account.getAccountNumber());
                fail("account was reported closed");
            }
            catch (BankException expected) {}
            
            account.deposit(10.0);
            teller.updateAccount(account);
            log.debug("trying to close account with no balance:" + account);
            teller.closeAccount(account.getAccountNumber());
        }
        catch (BankException ex) {
            log.fatal("error getting overdrawn accounts:" + ex, ex);
            fail("error getting overdrawn accounts:" + ex);
        }
    }

    public void testFindOverdrawnAccounts() throws Exception {
        log.info("*** testFindOverdrawnAccounts ***");
        TellerRemote teller = (TellerRemote)jndi.lookup(jndiName);

        try {
            int num = 0;
            Account account1 = teller.createAccount("" + ++num);
            @SuppressWarnings("unused")
            Account account2 = teller.createAccount("" + ++num);
            Account account3 = teller.createAccount("" + ++num);
            log.debug("created 3 accounts");

            account1.deposit(10.0);
            teller.updateAccount(account1);                
            account3.withdraw(10.0);
            teller.updateAccount(account3);                
            log.debug("updated 2 accounts");
            
            List<Account> accounts=teller.getOverdrawnAccounts(0, 100);
            log.debug("overdrawn accounts:" + accounts);
            assertEquals("unexpected number of accounts:"+accounts, 
                    1, accounts.size());
        }
        catch (BankException ex) {
            log.fatal("error getting overdrawn accounts:" + ex, ex);
            fail("error getting overdrawn accounts:" + ex);
        }
    }

    public void testFindAllAccounts() throws Exception {
        log.info("*** testFindAllAccounts ***");
        TellerRemote teller = (TellerRemote)jndi.lookup(jndiName);
        int TOTAL = 100;

        try {
            for(int i=0; i<TOTAL; i++) {
                Account account = teller.createAccount("" + i);
                account.deposit(i);
                teller.updateAccount(account);                
            }
            
            int index=0;
            for(List<Account> accounts=teller.getAccounts(index, TOTAL/10);
                accounts.size() > 0;
                accounts = teller.getAccounts(index, TOTAL/10)) {
                log.debug("got " + accounts.size() + " accounts");
                for(Account a: accounts) {
                    assertEquals("unexpected balance",
                            (double)index++, a.getBalance());
                }
            }            
            assertEquals("unexpected number of accounts:"+index, TOTAL, index);
        }
        catch (BankException ex) {
            log.fatal("error getting accounts:" + ex, ex);
            fail("error getting accounts:" + ex);
        }
    }
    
    public void testGetLedger() throws Exception {
        log.info("*** testGetLedger ***");
        TellerRemote teller = (TellerRemote)jndi.lookup(jndiName);
        int TOTAL = 100;

        try {
            for(int i=0; i<TOTAL; i++) {
                Account account = teller.createAccount("" + i);
                account.deposit(i);
                teller.updateAccount(account);                
            }

            Ledger ledger = teller.getLedger();
            assertNotNull("ledger is null", ledger);
            log.debug("got ledger:" + ledger);
            
            assertEquals("unexpected number of accounts:"+
                    ledger.getNumberOfAccounts(), 
                    TOTAL, ledger.getNumberOfAccounts());
        }
        catch (BankException ex) {
            log.fatal("error getting ledger:" + ex, ex);
            fail("error getting ledger:" + ex);
        }
    }

    public void testGetLedger2() throws Exception {
        log.info("*** testGetLedger2 ***");
        TellerRemote teller = (TellerRemote)jndi.lookup(jndiName);
        int TOTAL = 100;

        try {
            for(int i=0; i<TOTAL; i++) {
                Account account = teller.createAccount("" + i);
                account.deposit(i);
                teller.updateAccount(account);                
            }

            Ledger ledger = teller.getLedger2();
            assertNotNull("ledger is null", ledger);
            log.debug("got ledger:" + ledger);
            
            assertEquals("unexpected number of accounts:"+
                    ledger.getNumberOfAccounts(), 
                    TOTAL, ledger.getNumberOfAccounts());
        }
        catch (BankException ex) {
            log.fatal("error getting ledger2:" + ex, ex);
            fail("error getting ledger2:" + ex);
        }
    }

}
