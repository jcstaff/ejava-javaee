package ejava.examples.ejbsessionbank.ejb;

import javax.ejb.Remote;

import ejava.examples.ejbsessionbank.bl.BankException;
import ejava.examples.ejbsessionbank.bl.Teller;
import ejava.examples.ejbsessionbank.dto.LedgerDTO;

@Remote
public interface TellerRemote extends Teller {
    LedgerDTO getLedger2() throws BankException;
}
