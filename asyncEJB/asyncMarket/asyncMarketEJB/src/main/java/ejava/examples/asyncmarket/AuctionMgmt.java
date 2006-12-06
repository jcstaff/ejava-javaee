package ejava.examples.asyncmarket;

import java.util.List;

import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;

public interface AuctionMgmt {
    void closeBidding(long itemId) throws MarketException;
    Bid getWinningBid(long itemId) throws MarketException;
    int checkAuction() throws MarketException;
    
    //cleanup functions
    void removeBid(long bidId) throws MarketException;
    void removeItem(long id) throws MarketException;
    List<AuctionItem> getItems(int index, int count) throws MarketException;

    
    //infrastructure functions
    void initTimers();
    void initTimers(long delay);
    void cancelTimers();
}
