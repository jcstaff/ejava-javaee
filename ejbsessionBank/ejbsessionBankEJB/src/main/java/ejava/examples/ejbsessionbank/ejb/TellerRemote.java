package ejava.examples.ejbsessionbank.ejb;

import javax.ejb.Remote;

import ejava.examples.ejbsessionbank.bl.Teller;

@Remote
public interface TellerRemote extends Teller {

}
