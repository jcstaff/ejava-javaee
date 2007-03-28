package ejava.examples.ejbsessionbank.ejb;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.ejbsessionbank.bl.BankException;
import ejava.examples.ejbsessionbank.bl.Teller;
import ejava.examples.ejbsessionbank.bl.TellerImpl;
import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.da.AccountDAO;
import ejava.examples.ejbsessionbank.da.AccountDAOException;
import ejava.examples.ejbsessionbank.dto.LedgerDTO;
import ejava.examples.ejbsessionbank.jpa.JPAUtil;

/**
 * This class implements a Stateless Session Bean wrapper around the 
 * Teller business logic. With the lite-weight EJB3 design for session 
 * beans, there is no reason why the TellerImpl couldn't be the session bean
 * and we could possibly do so through the deployment descriptor. However,
 * adding this specific class permits a clear separation between implementing
 * the business logic and handling RMI, transactions, and security (coming)
 * issues that are part of being a session bean. 
 *
 * @author jcstaff
 */
@Stateless
public class TellerEJB implements TellerLocal, TellerRemote {
    Log log = LogFactory.getLog(TellerEJB.class);

    protected EntityManager em;
    protected Teller teller;
    
    @Resource
    protected SessionContext ctx;
    
    @Resource(name="daoClass")
    protected String daoClassName;

    /** The peristence context will be defined as a property to allow a 
     * derived class to override this value and assist in unit testing.
     * @return
     */    
    @PersistenceContext(unitName="ejbsessionbank")
    public void setEm(EntityManager em) {
        log.debug("container setting entity manager:" + em);
        this.em = em;
    }
    
    /**
     * This method performs one-time initialization for the session bean.
     * The name of the DAO class is provided through a JNDI ENC property 
     * value.
     */
    @PostConstruct
    public void init() {
        log.debug("init(), daoClass=" + daoClassName);
        teller = new TellerImpl();
        
        try {
            AccountDAO dao = (AccountDAO)Thread.currentThread()
                                               .getContextClassLoader()
                                               .loadClass(daoClassName)
                                               .newInstance();            
            ((TellerImpl)teller).setAcctDAO(dao);
        }
        catch (Exception ex) {
            throw new EJBException("error loading dao class:" + daoClassName);
        }
        
        log.debug("setting JPAUtil with entity manager=" + em);
        JPAUtil.setEntityManager(em);        
    }

    /**
     * This method will perform one-time cleanup for the object. There is 
     * really nothing that needs to be done at this time. The operations 
     * supplied demo some types of cleanup that could be done.
     *
     */
    @PreDestroy
    public void close() {
        log.debug("close");
        JPAUtil.setEntityManager(null);        
        teller = null;
    }

    public Account createAccount(String accountNumber) throws BankException {
        debug();
        try {
            return teller.createAccount(accountNumber);
        }
        catch (AccountDAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error creating account", ex);
            throw new BankException("internal error creating account:" + ex);
        }
    }
    
    public Account closeAccount(String acctNum) throws BankException {
        debug();
        try {
            return teller.closeAccount(acctNum);
        }
        catch (AccountDAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error closing account", ex);
            throw new BankException("internal error closing account:" + ex);
        }
    }

    public Account getAccount(String acctNum) throws BankException {
        debug();
        try {
            return teller.getAccount(acctNum);
        }
        catch (AccountDAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error getting account", ex);
            throw new BankException("internal error getting account:" + ex);
        }
    }

    public List<Account> getOverdrawnAccounts(int index, int count) throws BankException {
        debug();
        try {
            return teller.getOverdrawnAccounts(index, count);
        }
        catch (AccountDAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal getting accounts", ex);
            throw new BankException("internal getting accounts:" + ex);
        }
    }

    public List<Account> getAccounts(int index, int count) throws BankException {
        debug();
        try {
            return teller.getAccounts(index, count);
        }
        catch (AccountDAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal getting accounts", ex);
            throw new BankException("internal getting accounts:" + ex);
        }
    }

    public void updateAccount(Account account) throws BankException {
        debug();
        try {
            log.debug("in EJB, about to update account:" + account);
            teller.updateAccount(account);
        }
        catch (AccountDAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error updating account", ex);
            throw new BankException("internal error updating account:" + ex);
        }
    }

    public LedgerDTO getLedger() throws BankException {
        debug();
        try {
            LedgerDTO ledger = new LedgerDTO(
                    teller.getLedgerCount(),
                    teller.getLedgerSum(),
                    teller.getLedgerAveBalance());            
            return ledger;
        }
        catch (AccountDAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error getting ledger", ex);
            throw new BankException("internal error getting ledger:" + ex);
        }
    }

    /**
     * This method takes advantage of the DAO/Business logic knowledge
     * of how to create a DTO. Note that the query is not actually known
     * to the business logic or DAO. It is expressed in an ORM.xml file.
     */
    public LedgerDTO getLedger2() throws BankException {
        debug();
        try {
            return teller.getLedger();
        }
        catch (AccountDAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error getting ledger", ex);
            throw new BankException("internal error getting ledger:" + ex);
        }
    }
    
    /**
     * This method is and example of a method that may be too fine for a 
     * remote method and should be encapsulated in a remote facade call.
     */
    public double getLedgerAveBalance() throws BankException {
        debug();
        try {
            return teller.getLedgerAveBalance();
        }
        catch (AccountDAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error getting ledger ave balance", ex);
            throw new BankException(
                    "internal error getting ledger ave balance:" + ex);
        }
    }

    /**
     * This method is and example of a method that may be too fine for a 
     * remote method and should be encapsulated in a remote facade call.
     */
    public long getLedgerCount() throws BankException {
        debug();
        try {
            return teller.getLedgerCount();
        }
        catch (AccountDAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error getting ledger count", ex);
            throw new BankException(
                    "internal error getting ledger count:" + ex);
        }
    }

    /**
     * This method is and example of a method that may be too fine for a 
     * remote method and should be encapsulated in a remote facade call.
     */
    public double getLedgerSum() throws BankException {
        debug();
        try {
            return teller.getLedgerSum();
        }
        catch (AccountDAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error getting ledger sum", ex);
            throw new BankException(
                    "internal error getting ledger sum:" + ex);
        }
    }
    
    private void debug() {
        if (log.isDebugEnabled()) {
            //nothing yet...
        }
    }
}
