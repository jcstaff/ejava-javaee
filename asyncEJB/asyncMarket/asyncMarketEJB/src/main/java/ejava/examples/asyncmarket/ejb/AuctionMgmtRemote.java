package ejava.examples.asyncmarket.ejb;

import java.util.concurrent.Future;

import javax.ejb.Remote;

import ejava.examples.asyncmarket.AuctionMgmt;

@Remote
public interface AuctionMgmtRemote extends AuctionMgmt {
	/**
	 * An example of performing an async task while client doesn't
	 * wait for result.
	 * @param msecs
	 * @return
	 */
	Future<Long> delay(long msecs);
}
