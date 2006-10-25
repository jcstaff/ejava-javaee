package ejava.examples.ejbsessionbank.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.LedgerDTO;
import ejava.examples.ejbsessionbank.da.AccountDAO;
import ejava.examples.ejbsessionbank.da.AccountDAOException;

public class JPAAccountDAO implements AccountDAO {
    private static Log log = LogFactory.getLog(JPAAccountDAO.class); 
    public static final String ACCOUNT_BY_NUM = "getAccountsByAccountNumber"; 
    public static final String ACCOUNT_NUM_PARAM = "accountNumber"; 
    public static final String GET_LEDGER = "getLedger"; 

    public Account createAccount(Account account) throws AccountDAOException {
        try {
            JPAUtil.getEntityManager().persist(account);
            return account;
        }
        catch (Throwable ex) {
            throw new AccountDAOException(
                    "error creating account:" + account,ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Account> findAccounts(String queryName,
            Map<String, Object> params, int index, int count)
            throws AccountDAOException {
        try {
            Query query = 
                JPAUtil.getEntityManager().createNamedQuery(queryName)
                                          .setFirstResult(index)
                                          .setMaxResults(count);
            if (params != null && params.size() != 0) {
                for(String key: params.keySet()) {
                    query.setParameter(key, params.get(key));
                }
            }
            log.debug("named query:" + queryName + ", params=" + params);
            return query.getResultList();
        }
        catch (Throwable ex) {
            throw new AccountDAOException(
                    "error executing named query:" + queryName,ex);
        }
    }

    public Account getAccountById(long id) throws AccountDAOException {
        try {
            return JPAUtil.getEntityManager().find(Account.class, id);
        }
        catch (Throwable ex) {
            throw new AccountDAOException(
                    "error finding account:" + id,ex);
        }
    }

    public Account getAccountByNum(String acctNum) throws AccountDAOException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(ACCOUNT_NUM_PARAM, acctNum);
        List<Account> accounts = findAccounts(ACCOUNT_BY_NUM, params, 0, 1);
        return (accounts.size() == 1) ? accounts.get(0) : null;
    }

    public Account removeAccount(Account account) throws AccountDAOException {
        try {
            EntityManager em = JPAUtil.getEntityManager();
            if (!em.contains(account)) {
                account = em.find(Account.class, account.getId());
            }
            if (account != null) {
                JPAUtil.getEntityManager().remove(account);
            }
            return account;
        }
        catch (Throwable ex) {
            throw new AccountDAOException(
                    "error removing account:" + account,ex);
        }
    }

    public Account updateAccount(Account account) throws AccountDAOException {
        try {
            JPAUtil.getEntityManager().merge(account);
            return account;
        }
        catch (Throwable ex) {
            throw new AccountDAOException(
                    "error removing account:" + account,ex);
        }
    }

    public LedgerDTO getLedger() throws AccountDAOException {
        try {
            return (LedgerDTO) JPAUtil.getEntityManager()
                                      .createNamedQuery(GET_LEDGER)
                                      .getSingleResult();
        }
        catch (Throwable ex) {
            throw new AccountDAOException(
                    "error getting edger",ex);
        }
    }
}
