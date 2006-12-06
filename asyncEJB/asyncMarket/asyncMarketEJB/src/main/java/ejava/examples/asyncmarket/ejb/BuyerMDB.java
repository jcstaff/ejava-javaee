package ejava.examples.asyncmarket.ejb;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.asyncmarket.MarketException;
import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Order;
import ejava.examples.asyncmarket.dao.AuctionItemDAO;
import ejava.examples.asyncmarket.dao.OrderDAO;
import ejava.examples.asyncmarket.jpa.JPAAuctionItemDAO;
import ejava.examples.asyncmarket.jpa.JPAOrderDAO;

//lets put defaults here and let DD provide overrides
@MessageDriven(name="BuyerMDB", activationConfig={
        @ActivationConfigProperty(
                propertyName="destinationType",
                propertyValue="javax.jms.Topic"),            
        @ActivationConfigProperty(
                propertyName="destination",
                propertyValue="topic/ejava/examples/asyncMarket/topic1"),            
        @ActivationConfigProperty(
                propertyName="messageSelector",
                propertyValue="JMSType in ('forSale', 'saleUpdate')"),
        @ActivationConfigProperty(
                propertyName="acknowledgeMode",
                propertyValue="Auto-acknowledge")            
})
public class BuyerMDB implements MessageListener {
    private static final Log log = LogFactory.getLog(BuyerMDB.class);
    
    //ideally this would be mapped in DD, but would not work for 4.0.4GA MDBs
    //so I'm using a @Resource mapped directly to a JNDI name. Alternately
    //we could also perform the manual JNDI lookup within the init().
    @Resource(mappedName="ejava/examples/asyncMarket/BuyerEJB/local")
    private BuyerLocal buyer;
    //this is needed if injection didn't set buyer
    private String buyerJNDI="ejava/examples/asyncMarket/BuyerEJB/local";
    
    @Resource(mappedName="ejava/examples/asyncMarket/AuctionMgmtEJB/local")
    private AuctionMgmtLocal auctionMgmt; 
    
    @PersistenceContext(unitName="asyncMarket")
    private EntityManager em;
    
    private AuctionItemDAO auctionItemDAO;
    private OrderDAO orderDAO;
    
    @Resource
    private MessageDrivenContext ctx;
    
    @PostConstruct
    public void init() {
        log.info("*** BuyerMDB init() ***");
        log.debug("ctx=" + ctx);
        log.debug("ctx.lookup(ejb/BuyerEJB)=" + ctx.lookup("ejb/BuyerEJB"));
        log.debug("buyer=" + buyer);
        log.debug("auctionMgmt=" + auctionMgmt);
        log.debug("em=" + em);
        
        orderDAO = new JPAOrderDAO();
        ((JPAOrderDAO)orderDAO).setEntityManager(em);
        
        auctionItemDAO = new JPAAuctionItemDAO();
        ((JPAAuctionItemDAO)auctionItemDAO).setEntityManager(em);
        
        if (buyer==null) {
            log.info("injection didn't work for MDB, using manual kludge");
            try {
                buyer=(BuyerLocal)new InitialContext().lookup(buyerJNDI);
            } catch (Exception ex) {
                log.error("error looking up BuyerEJB:" + ex);
            }
            log.debug("kludged buyer=" + buyer);
        }

        //lots of debug that should have been unecessary!!!
        
        String name="java:/comp.ejb3/env";
        StringBuilder text = new StringBuilder("jndi("+name+")=");
        try {
            text.append(new InitialContext().lookup(name));
        } catch (Exception ex) {
            text.append(ex.toString());
        } finally {
            log.debug(text.toString());
        }

        name="java:/comp.ejb3/env/ejb";
        text = new StringBuilder("jndi("+name+")=");
        try {
            text.append(new InitialContext().lookup(name));
        } catch (Exception ex) {
            text.append(ex.toString());
        } finally {
            log.debug(text.toString());
        }

        name="java:/comp.ejb3/env/ejb/BuyerEJB";
        text = new StringBuilder("jndi("+name+")=");
        try {
            text.append(new InitialContext().lookup(name));
        } catch (Exception ex) {
            text.append(ex.toString());
        } finally {
            log.debug(text.toString());
        }
        
    }

    public void onMessage(Message message) {
        try {
            log.debug("onMessage:" + message.getJMSMessageID());
            MapMessage auctionMsg = (MapMessage)message;
            long itemId = auctionMsg.getLong("id");
            processAuctionItem(itemId);
        }
        catch (Exception ex) {
            log.error("error processing message", ex);
        }
    }
    
    protected void processAuctionItem(long itemId) {
        int index=0;
        List<Order> orders = null;
        do {
            orders = orderDAO.getOrdersforItem(itemId, index, 10);
            for (Order order: orders) {
                processOrder(order);
            }
            index += orders.size();            
        } while (orders.size() > 0);        
    }

    protected void processOrder(Order order) {
        log.debug("processing order:" + order);
        try {
            AuctionItem item = order.getItem();
            if (item.getHighestBid() == null) {
                if (item.getMinBid() < order.getMaxBid()) {
                    buyer.bidProduct(item.getId(), 
                                     order.getBuyer().getUserId(), 
                                     item.getMinBid());
                    log.debug("placed initial bid for order:" + order);
                }
            }
            else if (item.getHighestBid().getAmount() < order.getMaxBid()){
                buyer.bidProduct(item.getId(), 
                                 order.getBuyer().getUserId(), 
                                 item.getHighestBid().getAmount() + 1.00);
                log.debug("placed new bid for order:" + order);
            }
        }
        catch (MarketException ex) {
            log.error("error processing order:" + order, ex);
        }
    }
}
