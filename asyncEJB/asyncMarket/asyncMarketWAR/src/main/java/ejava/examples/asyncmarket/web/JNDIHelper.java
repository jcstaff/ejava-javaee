package ejava.examples.asyncmarket.web;

import java.util.Enumeration;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.asyncmarket.AuctionMgmt;
import ejava.examples.asyncmarket.Buyer;
import ejava.examples.asyncmarket.Seller;
import ejava.examples.asyncmarket.UserMgmt;
import ejava.examples.asyncmarket.ejb.AuctionMgmtLocal;
import ejava.examples.asyncmarket.ejb.AuctionMgmtRemote;
import ejava.examples.asyncmarket.ejb.BuyerLocal;
import ejava.examples.asyncmarket.ejb.BuyerRemote;
import ejava.examples.asyncmarket.ejb.SellerLocal;
import ejava.examples.asyncmarket.ejb.SellerRemote;
import ejava.examples.asyncmarket.ejb.UserMgmtLocal;
import ejava.examples.asyncmarket.ejb.UserMgmtRemote;

public class JNDIHelper {
    private static final Log log = LogFactory.getLog(JNDIHelper.class);
    
    public Context getInitialContext(ServletContext context) 
        throws NamingException {
        
        //build an InitialContext from Servlet.init properties in web.xml
        Properties jndiProperties = new Properties();
        for(Enumeration e=context.getInitParameterNames();
            e.hasMoreElements(); ) {
            String key = (String)e.nextElement();
            String value=(String)context.getInitParameter(key);
            if (key.startsWith("java.naming")) {
                jndiProperties.put(key, value);
            }                    
        }
        log.debug("jndiProperties=" + jndiProperties);
        InitialContext jndi = new InitialContext(jndiProperties);
        log.debug("jndi=" + jndiProperties);
        return jndi;
    }

    public AuctionMgmt getAuctionMgmt(ServletContext context) 
        throws NamingException {
        AuctionMgmt auctionMgmt = null;
        
        Context jndi = getInitialContext(context);
        String jndiName = context.getInitParameter("auctionmgmt.local");
        try { auctionMgmt = (AuctionMgmtLocal)jndi.lookup(jndiName); }
        catch (Throwable ex) {
            log.debug(jndiName + " not found, trying remote");
            jndiName = context.getInitParameter("auctionmgmt.remote");
            auctionMgmt = (AuctionMgmtRemote)jndi.lookup(jndiName);
        }
        log.debug("auctionMgmt=" + auctionMgmt);
        return auctionMgmt;
    }
    
    public UserMgmt getUserMgmt(ServletContext context) 
        throws NamingException {
        UserMgmt userMgmt = null;
        
        Context jndi = getInitialContext(context);
        String jndiName = context.getInitParameter("usermgmt.local");
        try { userMgmt = (UserMgmtLocal)jndi.lookup(jndiName); }
        catch (Throwable ex) {
            log.debug(jndiName + " not found, trying remote");
            jndiName = context.getInitParameter("usermgmt.remote");
            userMgmt = (UserMgmtRemote)jndi.lookup(jndiName);
        }
        log.debug("auctionMgmt=" + userMgmt);
        return userMgmt;
    }

    public Seller getSeller(ServletContext context) 
        throws NamingException {
        Seller seller = null;
        
        Context jndi = getInitialContext(context);
        String jndiName = context.getInitParameter("seller.local");
        try { seller = (SellerLocal)jndi.lookup(jndiName); }
        catch (Throwable ex) {
            log.debug(jndiName + " not found, trying remote");
            jndiName = context.getInitParameter("seller.remote");
            seller = (SellerRemote)jndi.lookup(jndiName);
        }
        log.debug("seller=" + seller);
        return seller;
   }
    
    public Buyer getBuyer(ServletContext context) 
        throws NamingException {
        Buyer buyer = null;
        
        Context jndi = getInitialContext(context);
        String jndiName = context.getInitParameter("buyer.local");
        try { buyer = (BuyerLocal)jndi.lookup(jndiName); }
        catch (Throwable ex) {
            log.debug(jndiName + " not found, trying remote");
            jndiName = context.getInitParameter("buyer.remote");
            buyer = (BuyerRemote)jndi.lookup(jndiName);
        }
        log.debug("buyer=" + buyer);
        return buyer;
    }
    
}