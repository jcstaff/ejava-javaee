package ejava.examples.asyncmarket.ejbclient;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.naming.InitialContext;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.asyncmarket.AuctionMgmt;
import ejava.examples.asyncmarket.Buyer;
import ejava.examples.asyncmarket.Seller;
import ejava.examples.asyncmarket.UserMgmt;
import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;
import ejava.examples.asyncmarket.bo.Person;
import ejava.examples.asyncmarket.ejb.AuctionMgmtRemote;
import ejava.examples.asyncmarket.ejb.BuyerRemote;
import ejava.examples.asyncmarket.ejb.SellerRemote;
import ejava.examples.asyncmarket.ejb.UserMgmtRemote;

public class AuctionMgmtTest extends TestCase {
    Log log = LogFactory.getLog(AuctionMgmtTest.class);
    InitialContext jndi;
    static String auctionmgmtJNDI = System.getProperty("jndi.name.auctionmgmt");
    static String usermgmtJNDI = System.getProperty("jndi.name.usermgmt");
    static String sellerJNDI = System.getProperty("jndi.name.seller");
    static String buyerJNDI = System.getProperty("jndi.name.buyer");

    AuctionMgmt auctionmgmt;
    UserMgmt usermgmt;
    Seller seller;
    Buyer buyer;
    
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        
        log.debug("looking up:" + auctionmgmtJNDI);
        auctionmgmt = (AuctionMgmtRemote)jndi.lookup(auctionmgmtJNDI);

        log.debug("looking up:" + usermgmtJNDI);
        usermgmt = (UserMgmtRemote)jndi.lookup(usermgmtJNDI);

        log.debug("looking up:" + sellerJNDI);
        seller = (SellerRemote)jndi.lookup(sellerJNDI);
        
        log.debug("looking up:" + buyerJNDI);
        buyer = (BuyerRemote)jndi.lookup(buyerJNDI);
        
        cleanup();
    }
    
    private void cleanup() throws Exception {
        int index = 0;
        
        List<AuctionItem> items = null;
        index=0;
        do {
            items = auctionmgmt.getItems(index, 10);
            for (AuctionItem item : items) {
                log.debug("removing item:" + item);
                auctionmgmt.removeItem(item.getId());
            }
            
        } while (items.size() > 0);
        
        List<Person> users = null;
        index=0;
        do {
            users = usermgmt.getUsers(index, 10);
            for (Person user : users) {
                log.debug("removing user:" + user.getUserId());
                usermgmt.removeUser(user.getUserId());
            }
            index += users.size();
        } while (users.size() > 0);       
    }
    
    public void testAuction() throws Exception {
        log.info("*** testAuction ***");
        
        String userId = "mjones";
        String name = "Mary Jones";
        @SuppressWarnings("unused")
        long sellerId = usermgmt.createUser(userId, name);
        log.info("created user:" + name);
        
        AuctionItem item = new AuctionItem();
        item.setName("my stuff");
        item.setMinBid(4.00);
        Calendar cal = new GregorianCalendar();
        item.setStartDate(cal.getTime());
        cal.add(Calendar.SECOND, 10);
        item.setEndDate(cal.getTime());
        long itemId = seller.sellProduct(userId, item);
        log.info("created product:" + item);
        
        for(int i=0; i<3; i++) {
           usermgmt.createUser("asmith" + i, "Anna Smith" + i);            
        }
        buyer.bidProduct(itemId, "asmith0", 5.00);
        buyer.bidProduct(itemId, "asmith1", 6.00);
        buyer.bidProduct(itemId, "asmith2", 7.00);
        
        try {
            item = seller.getItem(itemId);            
            log.info("item:" + item);
            assertNotNull("item not found:" + itemId, item);
            assertEquals("unexpected number of bids:", 
                3, item.getBids().size());
            
            int items = auctionmgmt.checkAuction();
            log.debug("" + items + " still open for auction");
            assertEquals("unexpected number of open items", 1, items);

            assertFalse(item.isClosed());
            auctionmgmt.closeBidding(item.getId());
            item = seller.getItem(itemId);            
            assertTrue(item.isClosed());
            
            Bid winner = auctionmgmt.getWinningBid(item.getId());
            log.debug("winning bid=" + winner);
            assertEquals("asmith2", winner.getBidder().getUserId());

            items = auctionmgmt.checkAuction();
            log.debug("" + items + " still open for auction");
            assertEquals("unexpected number of open items", 0, items);            
        } catch (UndeclaredThrowableException ue) {
            fail("" + ue.getUndeclaredThrowable());
        }
        
    }
}
