package ejava.examples.ejbsessionbank.bo;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.ejbsessionbank.DemoBase;

public class AccountDemo extends DemoBase {
    Log log = LogFactory.getLog(AccountDemo.class);
    
    public void testCreateAccount() {
        log.info("*** testCreateAccount ***");
        
        Account account = new Account();
        account.setAccountNumber("12345");
        account.deposit(5.0);
        
        em.persist(account);
        assertTrue("account not managed", em.contains(account));
        assertTrue("unexpected id:" + account.getId(), account.getId() != 0);        
    }
    
    public void testLedger() throws Exception {
        log.info("*** testLedger ***"); 
        
        for(int i=0; i<5; i++) {
            Account account = new Account();
            account.setAccountNumber(""+i);
            account.deposit(100*i);
            em.persist(account);        
        }
        em.flush();
        
        Query query = em.createQuery(
                "select new ejava.examples.ejbsessionbank.bo.LedgerDTO(" +
                "count(a), sum(a.balance), avg(a.balance)) " +
                "from Account a");
        Object result = query.getSingleResult();
        if (result instanceof Object[]) {
            for(Object o: ((Object[])result)) {
                log.info("result=" + o);
            }
        }
        else {
            log.info("result=" + result);
        }
        
        LedgerDTO ledger = 
            (LedgerDTO)em.createNamedQuery("getLedger").getSingleResult();
        log.info("got ledger from named query:" + ledger);
        assertEquals(5, ledger.getNumberOfAccounts());
    }

}
