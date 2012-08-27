package ejava.examples.ejbsessionbank.ejbclient;

import java.util.List;



import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LazyInitializationException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import ejava.examples.ejbsessionbank.bl.BankException;
import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Owner;
import ejava.examples.ejbsessionbank.dto.OwnerDTO;
import ejava.examples.ejbsessionbank.ejb.TellerRemote;
import ejava.util.ejb.EJBClient;

public class TellerOwnerRemoteIT {
    private static Log log = LogFactory.getLog(TellerOwnerRemoteIT.class);
    protected InitialContext jndi;
    String jndiName = System.getProperty("jndi.name",
    	EJBClient.getEJBLookupName("ejbsessionBankEAR", "ejbsessionBankEJB", "", 
    			"TellerEJB", TellerRemote.class.getName()));
    protected TellerRemote teller;

    @Before
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        
        teller = (TellerRemote)jndi.lookup(jndiName);
        log.debug("got teller:" + teller);
        cleanup();
    }
    
    private void cleanup() throws Exception {
        if (jndi!=null) {
            for (int index=0; ; index+=100) {
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
                List<Account> accounts = teller.getAccounts(0, 100);
                if (accounts.size() == 0) { break; }
                for (Account a: accounts) {
                    log.debug("cleaning up account:" + a);
                    zeroAccount(a);
                    teller.closeAccount(a.getAccountNumber());                        
                }
            }
        }
    }
    
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
    
    @Test
    public void testLazy() throws Exception {
        log.info("*** testLazy ***");
        
        Owner owner = teller.createOwner("joe", "jones", "123");
        owner = teller.openAccount(owner.getId(), "111");
        Owner owner2 = teller.createOwner("jane", "jones", "333");
        owner2 = teller.addOwner(owner2.getId(), "111");
        
        //get a set of owners that have been lazily loaded
        List<Owner> owners = teller.getOwners(0, 100);
        assertEquals("unexpected number of owners", 2, owners.size());
        
        try {
            for(Owner o : owners) {
                for (Account a: o.getAccounts()) {
                    log.info("account=" + a);
                }
            }
            fail("did not encounter expected exception");
        }
        catch (LazyInitializationException expected) {
            log.info("encountered expected exception:" + expected);    
        }
        
        //now get them fully loaded
        owners = teller.getOwnersLoaded(0, 100);
        for(Owner o : owners) {
            for (Account a: o.getAccounts()) {
                log.info("account=" + a);
            }
        }
    }
    
    @Test
    public void testPOJO() throws Exception {
        log.info("*** testPOJO ***");
        
        Owner owner = teller.createOwner("joe", "jones", "123");
        owner = teller.openAccount(owner.getId(), "111");
        Owner owner2 = teller.createOwner("jane", "jones", "333");
        owner2 = teller.addOwner(owner2.getId(), "111");
        
        
        //now get the objects fully loaded
        List<Owner> owners = teller.getOwnersLoaded(0, 100);
        for(Owner o : owners) {
            for (Account a: o.getAccounts()) {
                log.info("account=" + a);
            }
            log.debug("addresses=" + o.getAccounts().getClass().getName());
            assertTrue("unexpected collection class", 
                    o.getAccounts().getClass().getName().contains("hibernate"));
        }
        
        //now get the objects cleaned of persistence classes
        owners = teller.getOwnersPOJO(0, 100);
        for(Owner o : owners) {
            for (Account a: o.getAccounts()) {
                log.info("account=" + a);
            }
            log.debug("addresses=" + o.getAccounts().getClass().getName());
            assertFalse("unexpected collection class", 
                    o.getAccounts().getClass().getName().contains("hibernate"));
        }
    }
    
    @Test
    public void testDTO() throws Exception {
        log.info("*** testDTO ***");
        
        int startCount = teller.getOwnersDTO(0, 100).size();
        
        Owner owner = teller.createOwner("joe", "jones", "123");
        owner = teller.openAccount(owner.getId(), "111");
        Owner owner2 = teller.createOwner("jane", "jones", "333");
        owner2 = teller.addOwner(owner2.getId(), "111");
        
        
        //get DTOs instead
        List<OwnerDTO> owners = teller.getOwnersDTO(0, 100);
        for(OwnerDTO o : owners) {
            log.debug("ownerDTO=" + o);
        }
        assertEquals("unexpected number of owners", startCount + 2,
                owners.size());
    }
}
