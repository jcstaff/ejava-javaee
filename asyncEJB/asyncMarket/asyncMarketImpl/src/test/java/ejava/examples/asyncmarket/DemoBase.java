package ejava.examples.asyncmarket;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;
import ejava.examples.asyncmarket.bo.Person;
import ejava.examples.asyncmarket.dao.AuctionItemDAO;
import ejava.examples.asyncmarket.dao.OrderDAO;
import ejava.examples.asyncmarket.dao.PersonDAO;
import ejava.examples.asyncmarket.jpa.JPAAuctionItemDAO;
import ejava.examples.asyncmarket.jpa.JPAOrderDAO;
import ejava.examples.asyncmarket.jpa.JPAPersonDAO;

import junit.framework.TestCase;

public abstract class DemoBase extends TestCase {
    protected Log log = LogFactory.getLog(getClass());
    private static final String PERSISTENCE_UNIT = "asyncMarket";
    protected PersonDAO personDAO = null;
    protected AuctionItemDAO auctionItemDAO = null;
    protected OrderDAO orderDAO = null;
    protected EntityManager em;

    protected void setUp() throws Exception {
        em = JPAUtil.getEntityManager(PERSISTENCE_UNIT);
        personDAO = new JPAPersonDAO();
        ((JPAPersonDAO)personDAO).setEntityManager(em);
        auctionItemDAO = new JPAAuctionItemDAO();
        ((JPAAuctionItemDAO)auctionItemDAO).setEntityManager(em);
        orderDAO = new JPAOrderDAO();
        ((JPAOrderDAO)orderDAO).setEntityManager(em);
        cleanup();
        em.getTransaction().begin();
    }

    protected void tearDown() throws Exception {
        EntityTransaction tx = em.getTransaction();
        if (tx.isActive()) {
            if (tx.getRollbackOnly() == true) { tx.rollback(); }
            else                              { tx.commit(); }
        }
        JPAUtil.closeEntityManager();
    }
    
    @SuppressWarnings("unchecked")
    protected void cleanup() {
        log.info("cleaning up database");
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        List<Bid> bids = 
            em.createQuery("select b from Bid b").getResultList();
        for(Bid b: bids) {
            em.remove(b);
        }
        
        List<AuctionItem> items = 
            em.createQuery("select ai from AuctionItem ai").getResultList();
        for(AuctionItem item: items) {
            em.remove(item);
        }
        
        List<Person> people = 
            em.createQuery("select p from Person p").getResultList();
        for(Person p: people) {
            em.remove(p);
        }        
        
        em.getTransaction().commit();
    }
    
    protected void populate() {
        log.info("populating database");
        //EntityManager em = JPAUtil.getEntityManager();
        //em.getTransaction().begin();
        
        //em.getTransaction().commit();
    }
}
