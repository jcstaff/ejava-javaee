package ejava.examples.ejbsessionbank.da;

import java.util.List;
import java.util.Map;

import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.dto.LedgerDTO;

public interface AccountDAO {
    Account getAccountById(long id) throws AccountDAOException;
    Account getAccountByNum(String acctNum) throws AccountDAOException;
    Account createAccount(Account account) throws AccountDAOException;
    Account updateAccount(Account account) throws AccountDAOException;
    Account removeAccount(Account account) throws AccountDAOException;
    List<Account> findAccounts(String queryName, Map<String, Object> params,
            int index, int count) throws AccountDAOException;
    long getLedgerCount() throws AccountDAOException;
    double getLedgerAveBalance() throws AccountDAOException;
    double getLedgerSum() throws AccountDAOException;
    LedgerDTO getLedger() throws AccountDAOException;
}
