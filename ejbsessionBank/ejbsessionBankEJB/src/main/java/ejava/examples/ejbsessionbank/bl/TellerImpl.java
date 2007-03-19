package ejava.examples.ejbsessionbank.bl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.da.AccountDAO;
import ejava.examples.ejbsessionbank.dto.LedgerDTO;

public class TellerImpl implements Teller {
    Log log = LogFactory.getLog(TellerImpl.class);
    public static final String OVERDRAWN_ACCOUNTS = "getOverdrawnAccounts";
    public static final String ALL_ACCOUNTS = "getAccounts";
    private AccountDAO acctDAO;
    public AccountDAO getAcctDAO() {
        return acctDAO;
    }
    public void setAcctDAO(AccountDAO acctDAO) {
        this.acctDAO = acctDAO;
    }
    
    public Account createAccount(String accountNum) throws BankException {
        log.debug("createAccount(num=" + accountNum + ")");
        
        Account account = new Account();
        account.setAccountNumber(accountNum);
        account = acctDAO.createAccount(account);
        
        log.debug("account created:" + account);
        return account;        
    }

    public Account getAccount(String accountNum) throws BankException { 
        log.debug("getAccount(num=" + accountNum + ")");
        
        Account account = acctDAO.getAccountByNum(accountNum);
        if (account==null) {
            throw new BankException("unable to locate account:" + accountNum);
        }
        log.debug("found account:" + account);
        return account;
    }

    public List<Account> getOverdrawnAccounts(int index, int count) throws BankException {
        log.debug("getOverdrawnAccounts(" +
                "index=" + index + ", count=" + count + ")");
        
        List<Account> accounts = acctDAO.findAccounts(
                OVERDRAWN_ACCOUNTS, null, index, count);
        
        log.debug("found " + accounts.size() + " accounts");
        return accounts;
    }

    public List<Account> getAccounts(int index, int count) throws BankException {
        log.debug("getAccounts(" +
                "index=" + index + ", count=" + count + ")");
        
        List<Account> accounts = acctDAO.findAccounts(
                ALL_ACCOUNTS, null, index, count);
        
        log.debug("found " + accounts.size() + " accounts");
        return accounts;
    }

    public Account closeAccount(String accountNum) throws BankException {
        log.debug("removeAccount(num" + accountNum + ")");

        Account account = acctDAO.getAccountByNum(accountNum);
        if (account == null) {
            log.debug("account num found:" + accountNum);
            throw new BankException("unable to locate account:" + accountNum);
        }
        else if (account.getBalance() != 0) {
            log.debug("account balance not 0; bal=" + account.getBalance());
            throw new BankException("unable to close account, " +
                    "invalid balance:" + account.getBalance());
        }
        else {
            acctDAO.removeAccount(account);
        }
        
        log.debug("removed account:" + account);
        return account;
    }

    public void updateAccount(Account account) throws BankException {
        log.debug("updateAccount(update=" + account + ")");

        Account updated = acctDAO.updateAccount(account);
        
        log.debug("updated account:" + updated);
    }
    public LedgerDTO getLedger() throws BankException {
        return acctDAO.getLedger();
    }
    
    public double getLedgerAveBalance() throws BankException {
        return acctDAO.getLedgerAveBalance();
    }
    public long getLedgerCount() throws BankException {
        return acctDAO.getLedgerCount();
    }
    public double getLedgerSum() throws BankException {
        return acctDAO.getLedgerSum();
    }    
}
