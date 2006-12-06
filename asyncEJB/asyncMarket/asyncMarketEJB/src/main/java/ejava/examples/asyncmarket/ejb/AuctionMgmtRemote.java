package ejava.examples.asyncmarket.ejb;

import javax.ejb.Remote;

import ejava.examples.asyncmarket.AuctionMgmt;

@Remote
public interface AuctionMgmtRemote extends AuctionMgmt {
}
