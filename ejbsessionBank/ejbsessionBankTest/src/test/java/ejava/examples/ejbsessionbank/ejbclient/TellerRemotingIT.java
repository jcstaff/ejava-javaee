package ejava.examples.ejbsessionbank.ejbclient;

import static org.junit.Assert.*;

import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.examples.ejbsessionbank.bl.BankException;
import ejava.examples.ejbsessionbank.bl.Teller;
import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Ledger;
import ejava.examples.ejbsessionbank.bo.Owner;
import ejava.examples.ejbsessionbank.ejb.TellerRemote;
import ejava.util.ejb.EJBClient;

/**
 * This RMI test using the legacy jboss-remoting mechanism to communicate 
 * with the EJB. Simply put -- the jboss-remoting mechanism is a generic
 * remote interface that does not directly understand anything it speaks to.
 * Every serialized object is an opaque object that is only understood by 
 * the client that is looking it up. This test assumes there is a 
 * jndi.properties file in the classpath with the following information
 * to speak with the server.<p/>
 * <pre>
java.naming.factory.initial=org.jboss.naming.remote.client.InitialContextFactory
java.naming.provider.url=remote://127.0.0.1:4447
java.naming.factory.url.pkgs=
java.naming.security.principal=known
java.naming.security.credentials=password
jboss.naming.client.ejb.context=true
 * </pre></p>
 * The security properties are needed when the server is configured to 
 * require an authenticated principle to connect. Since we are not dealing
 * with security roles, etc. in this project -- we will assign a single,
 * group login credential in the jndi.properties for all clients to share.
 */
public class TellerRemotingIT {
    private static final Log log = LogFactory.getLog(TellerRemotingIT.class);
    private static InitialContext jndi;
    /*
     * The remote lookup name is a general purpose name that the EJB remote
     * interface is registered under for this type of client to connect to.
     * It is exported using the following name on the server
     * 
     java:jboss/exported/(ear)/(module)/(ejbClass)!(remoteInterface)

	 * but remote clients look it up with just the name part starting after 
	 * exported
	 * 
	 (ear)/(module)/(ejbClass)!(remoteInterface)
     */
    public static final String jndiName = System.getProperty("jndi.name",
    	EJBClient.getRemoteLookupName("ejbsessionBankEAR", "ejbsessionBankEJB", 
    			"TellerEJB", TellerRemote.class.getName()));
    
    //remote interface to teller, setup in setUp() method
    TellerRemote teller;
    
    /**
     * Setup will establish a connection to the server using the properties
     * in the jndi.properties file and keep that connection for all the 
     * tests executed.
     * @throws Exception
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
    	log.info("== setUpClass() ==");
        log.debug("jndiName=" + jndiName);
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
    }

    /**
     * Setup will get the system under test into a common known state prior
     * to running each test. 
     */
    @Before
    public void setUp() throws Exception {
    	log.info("== setUp() ==");
    	
        teller = (TellerRemote)jndi.lookup(jndiName);
        log.debug("teller=" + teller);
        cleanup();
    }
    
    /**
     * The class teardown will close out the JNDI context so that it
     * does not bleed into other test cases.
     */
    @AfterClass
    public static void tearDownClass() throws NamingException {
    	if (jndi != null) {
    		jndi.close();
    		jndi = null;
    	}
    }

    /**
     * Remotes all accounts and owners from the application under test.
     */
    private void cleanup() throws Exception {
        for (int index=0; ; index+=100) {
        		//do this in pages
            List<Owner> owners = teller.getOwnersLoaded(index, 100);
            if (owners.size() == 0) { break; }
            for (Owner owner : owners) {
                log.debug("removing owner:" + owner);
                for (Account a: owner.getAccounts()) {
                    zeroAccount(a);
                }
                teller.removeOwner(owner.getId());
            }
        }
        
        for (@SuppressWarnings("unused")
		int index=0; ; index+= 100) {
        		//do this in pages
            List<Account> accounts = teller.getAccounts(0, 100);
            if (accounts.size() == 0) { break; }
            for (Account a: accounts) {
                log.debug("cleaning up account:" + a);
                zeroAccount(a);
                teller.closeAccount(a.getAccountNumber());                        
            }
        }
    }
    
    /**
     * Empties a bank account of all funds so that it will be a state
     * that is legal to close.
     * @param teller
     * @param account
     * @throws BankException
     */
    private void zeroAccount(Account account) throws BankException {
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

    /**
     * Tests ability to create an account from the remote client.
     * @throws Exception
     */
    @Test
    public void testCreateAccount() throws Exception {
        log.info("*** testCreateAccount ***");
        Account account=null;
      
        	//try with what should be a unique number
        try {
            account = teller.createAccount("1234");
            log.debug("account created:" + account);
        }
        catch (Exception ex) {
            log.fatal("error creating account:" + ex, ex);
            fail("error creating account:" + ex);
        }        
        
        	//try with what we know is a duplicate number
        try {
            teller.createAccount(account.getAccountNumber());
            fail("created account with duplicate number");
        }
        catch (Exception expected) {
            log.info("got expected exception trying to create " +
                    "duplicate account:" + expected);
        }
    }
    
    /**
     * Tests ability to get an account by number.
     * @throws Exception
     */
    @Test
    public void testGetAccount() throws Exception {
        log.info("*** testGetAccount ***");
        try {
            Account account = teller.createAccount("1234");
            log.debug("account created:" + account);
            Account account2 = teller.getAccount(account.getAccountNumber());
            log.debug("got account:" + account);
            
            assertEquals("unexpected account num:"+account2.getAccountNumber(),
                    account.getAccountNumber(), account2.getAccountNumber());
            assertEquals("unexpected account bal:"+account2.getBalance(),
                    (int)(100*account.getBalance()), 
                    (int)(100*account2.getBalance()));
        }
        catch (Exception ex) {
            log.fatal("error getting account:" + ex, ex);
            fail("error getting account:" + ex);
        }        
    }    

    /**
     * Tests ability to update account.
     * @throws Exception
     */
    @Test
    public void testUpdateAccount() throws Exception {
        log.info("*** testUpdateAccount ***");
        Account account = null;
        
        log.debug("creating account, teller=" + teller);
        try {
            account = teller.createAccount("1234");
            log.debug("account created:" + account);
            
            account.deposit(5.00);
            assertEquals("unexpected balance:" + account.getBalance(),
                    5.00, 
                    account.getBalance(), .1);
            teller.updateAccount(account);
            log.debug("updated account:" + account);
            
            Account account2 = teller.getAccount(account.getAccountNumber());
            log.debug("retrieved updated account:" + account2);
                        
            assertEquals("unexpected account bal:"+account2.getBalance(),
                    account.getBalance(), 
                    account2.getBalance(), .1);
            
            account.withdraw(10.00);
            assertEquals("unexpected balance:" + account.getBalance(),
		            -5.00,
		            account.getBalance(), .1);
            teller.updateAccount(account);
            log.debug("updated account:" + account);
            
            account2 = teller.getAccount(account.getAccountNumber());
            log.debug("retrieved updated account:" + account2);
                        
            assertEquals("unexpected account bal:"+account2.getBalance(),
                    account.getBalance(), 
                    account2.getBalance(), .1);
        }
        catch (Exception ex) {
            log.fatal("error updating account:" + ex, ex);
            fail("error updating account:" + ex);
        }        
    }    
    
    /**
     * Tests ability to close an account.
     * @throws Exception
     */
    @Test
    public void testCloseAccount() throws Exception {
        log.info("*** testCloseAccount ***");

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

    /**
     * Tests query method of teller.
     * @throws Exception
     */
    @Test
    public void testFindOverdrawnAccounts() throws Exception {
        log.info("*** testFindOverdrawnAccounts ***");
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

    /**
     * Tests ability to find all accounts using paging controls.
     * @throws Exception
     */
    @Test
    public void testFindAllAccounts() throws Exception {
        log.info("*** testFindAllAccounts ***");

        int TOTAL = 20;
        try {
            for(int i=0; i<TOTAL; i++) {
                Account account = teller.createAccount("" + i);
                account.deposit(i);
                teller.updateAccount(account);                
            }
            
            int index=0;
            for(List<Account> accounts=teller.getAccounts(index, TOTAL/5);
                accounts.size() > 0;
                accounts = teller.getAccounts(index, TOTAL/5)) {
                log.debug("got " + accounts.size() + " accounts");
                for(Account a: accounts) {
                    assertEquals("unexpected balance",
	                    index++, 
	                    a.getBalance(),.1);
                }
            }            
            assertEquals("unexpected number of accounts:"+index, TOTAL, index);
        }
        catch (BankException ex) {
            log.fatal("error getting accounts:" + ex, ex);
            fail("error getting accounts:" + ex);
        }
    }
    
    /**
     * Tests ability to get a ledger summary for all accounts using the 
     * brute force technique on the server-side.
     * @throws Exception
     */
    @Test
    public void testGetLedger() throws Exception {
        log.info("*** testGetLedger ***");
        int TOTAL = 20;
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

    /**
     * Tests ability to get a ledger summary for all accounts using a 
     * database query to populate the DTO.
     * @throws Exception
     */
    @Test
    public void testGetLedger2() throws Exception {
        log.info("*** testGetLedger2 ***");
        int TOTAL = 20;

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
