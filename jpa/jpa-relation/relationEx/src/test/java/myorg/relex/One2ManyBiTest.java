package myorg.relex;

import static org.junit.Assert.*;


import javax.persistence.*;

import myorg.relex.one2manybi.Borrower;
import myorg.relex.one2manybi.Loan;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

public class One2ManyBiTest extends JPATestBase {
    private static Log log = LogFactory.getLog(One2ManyBiTest.class);
    @Test
    public void testSample() {
        log.info("testSample");
    }
    
    /**
     * This test will verify the ability to create a one-to-many/many-to-one, bi-directional
     * relationship.
     */
    @Test
    public void testOneToManyBiFK() {
    	log.info("*** testOneToManyBiFK ***");
    	
    	log.debug("persisting borrower");
    	Borrower borrower = new Borrower();
    	borrower.setName("fred");
    	em.persist(borrower);
    	em.flush();
    	
    	log.debug("persisting loan");
    	Loan loan = new Loan(borrower);
    	borrower.getLoans().add(loan);
    	em.persist(borrower); //cascade.PERSIST
    	em.flush();
    	
    	log.debug("getting new instances from parent side");
    	em.detach(borrower);
    	Borrower borrower2 = em.find(Borrower.class, borrower.getId());
    	log.debug("checking parent");
    	assertNotNull("borrower not found", borrower2);
    	log.debug("checking parent collection");
    	assertEquals("no loans found", 1, borrower2.getLoans().size());
    	log.debug("checking child");
    	assertEquals("unexpected child id", loan.getId(), borrower2.getLoans().get(0).getId());
    	
    	log.debug("adding new child");
    	Loan loanB = new Loan(borrower2);
    	borrower2.getLoans().add(loanB);
    	em.persist(borrower2);
    	em.flush();

    	log.debug("getting new instances from child side");
    	em.detach(borrower2);
    	Loan loan2 = em.find(Loan.class, loan.getId());
    	log.debug("checking child");
    	assertNotNull("child not found", loan2);
    	assertNotNull("parent not found", loan2.getBorrower());
    	log.debug("checking parent");
    	assertEquals("unexpected number of children", 2, loan2.getBorrower().getLoans().size());
    	
    	log.debug("orphaning one of the children");
    	int startCount = em.createQuery("select count(l) from Loan l", Number.class).getSingleResult().intValue();
    	Borrower borrower3 = loan2.getBorrower();
    	borrower3.getLoans().remove(loan2);
    	em.flush();
    	assertEquals("orphaned child not deleted", startCount-1,
    			em.createQuery("select count(l) from Loan l", Number.class).getSingleResult().intValue());
    	
    	log.debug("deleting parent");
    	em.remove(borrower3);
    	em.flush();
    	assertEquals("orphaned child not deleted", startCount-2,
    			em.createQuery("select count(l) from Loan l", Number.class).getSingleResult().intValue());
    }
}