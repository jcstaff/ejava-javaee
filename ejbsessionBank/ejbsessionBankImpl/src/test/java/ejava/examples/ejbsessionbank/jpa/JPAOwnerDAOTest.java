package ejava.examples.ejbsessionbank.jpa;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.ejbsessionbank.DemoBase;
import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Owner;
import ejava.examples.ejbsessionbank.dao.AccountDAO;
import ejava.examples.ejbsessionbank.dao.OwnerDAO;

public class JPAOwnerDAOTest extends DemoBase {
    private Log log = LogFactory.getLog(JPAOwnerDAO.class);
    protected OwnerDAO ownerDAO;
    protected AccountDAO accountDAO;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        ownerDAO = new JPAOwnerDAO();
        ((JPAOwnerDAO)ownerDAO).setEntityManager(em);
        
        accountDAO = new JPAAccountDAO();
        ((JPAAccountDAO)accountDAO).setEntityManager(em);
    }

    public void testCreateOwner() {
        log.info("*** testCreateOwner ***");
        
        Owner owner = new Owner();
        owner.setFirstName("john");
        owner.setLastName("jones");
        owner.setSsn("111");
        
        ownerDAO.createOwner(owner);
        em.getTransaction().commit();
        em.clear();
        
        Owner owner2 = ownerDAO.getOwnerById(owner.getId());
        assertEquals("unexpected firstName", 
                owner.getFirstName(), owner2.getFirstName());
                
        Account account = new Account();
        account.setAccountNumber("000111");
        account.deposit(5.00);
        owner2.getAccounts().add(account);
        
        account = new Account();
        account.setAccountNumber("000222");
        account.deposit(10.00);
        owner2.getAccounts().add(account);        
        
        em.getTransaction().begin();
        em.getTransaction().commit();
        em.clear();
        
        Owner owner3 = ownerDAO.getOwnerById(owner.getId());
        assertEquals("unexpected number of accounts", 
                2, owner3.getAccounts().size());
        account = accountDAO.getAccountById(account.getId());
        
        
        for (int i=0; i<100; i++) {
            Owner owner4 = new Owner();
            owner4.setFirstName("fn." + i);
            owner4.setLastName("sn." + i);
            owner4.setSsn("ssn." + i);
            owner4.getAccounts().add(account);
            ownerDAO.createOwner(owner4);
        }
        em.getTransaction().begin();
        em.getTransaction().commit();
        em.clear();
        
        Collection<Owner> owners = 
            ownerDAO.findOwners(OwnerDAO.GET_OWNERS_QUERY, null, 0, 1000);
        assertEquals("unexpected number of owners found", 101, owners.size());
        for(Owner o : owners) {
            if (o.getId() == owner.getId()) {
                assertEquals("unexpected number of accounts for owner",
                        2, o.getAccounts().size());
            }
            else {
                assertEquals("unexpected number of accounts for owner",
                        1, o.getAccounts().size());
            }
        }
        
        
        int count = accountDAO.findAccounts(
                    AccountDAO.GET_ACCOUNTS_QUERY, null, 0, 1000).size();
        assertEquals("unexpected number of accounts", 2, count);
        
        em.getTransaction().begin();
        ownerDAO.removeOwner(owner);
        em.getTransaction().commit();
        em.clear();
                
        for(Owner o : owners) {
            count = accountDAO.findAccounts(
                    AccountDAO.GET_ACCOUNTS_QUERY, null, 0, 1000).size();
            assertEquals("unexpected number of accounts", 1, count);

            em.getTransaction().begin();
            ownerDAO.removeOwner(o);
            em.getTransaction().commit();
        }
        
        count = accountDAO.findAccounts(
                AccountDAO.GET_ACCOUNTS_QUERY, null, 0, 1000).size();
        assertEquals("unexpected number of accounts", 0, count);
    }

}
