package ejava.jpa.examples.query.criteria;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.jpa.examples.query.Clerk;
import ejava.jpa.examples.query.Customer;
import ejava.jpa.examples.query.QueryBase;
import ejava.jpa.examples.query.Receipt;
import ejava.jpa.examples.query.Sale;

public class CriteriaTest extends QueryBase {
	private static final Log log = LogFactory.getLog(CriteriaTest.class);

    @SuppressWarnings("unused")
	private <T> List<T> executeQuery(CriteriaQuery<T> qdef) {
        return executeQuery(qdef, null);
    }

    private <T> List<T> executeQuery(CriteriaQuery<T> qdef, 
            Map<String, Object> params) {
        TypedQuery<T> query = em.createQuery(qdef);
        if (params != null && !params.isEmpty()) {
            for(String key: params.keySet()) {
                query.setParameter(key, params.get(key));
            }
        }
        List<T> objects = query.getResultList();
        for(T o: objects) {
           log.info("found result:" + o);
        }
        return objects;
    }
	
    /**
     * This test demonstrates a single entity query.
     */
	@Test
    public void testSimpleSelect() {
        log.info("*** testSimpleSelect() ***");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
        
        //"select object(c) from Customer as c"
        Root<Customer> c = qdef.from(Customer.class);        
        qdef.select(c);
        
        TypedQuery<Customer> query = em.createQuery(qdef);
        List<Customer> results = query.getResultList();
        for (Customer result : results) {
        	log.info("found=" + result);
        }
        int rows = results.size();
        assertTrue("unexpected number of customers:" + rows, rows > 0);
    }
	
    /**
     * This test demonstrates querying for a non-entity. The property queried
     * for is located off a path from the root query term.
     */
    @Test
    public void testNonEntityQuery() {
        log.info("*** testNonEntityQuery() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> qdef = cb.createQuery(String.class);
        
        //select c.lastName from Customer c
        Root<Customer> c = qdef.from(Customer.class);
        qdef.select(c.<String>get("lastName"));
        
        TypedQuery<String> query = em.createQuery(qdef);
        List<String> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(String result : results) {
            log.info("lastName=" + result);
        }
    }
	
    /**
     * This test demonstrates a query for multiple properties. In this
     * version we will use a generic Object[] for the return type.
     */
    @Test
    public void testMultiSelectObjectArray() {
        log.info("*** testMultiSelectObjectArray() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> qdef = cb.createQuery(Object[].class);
        
        //select c.firstName, c.hireDate from Clerk c
        Root<Clerk> c = qdef.from(Clerk.class);
        qdef.select(cb.array(c.get("firstName"), c.get("hireDate")));
        
        TypedQuery<Object[]> query = em.createQuery(qdef);
        List<Object[]> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Object[] result : results) {
            assertEquals("unexpected result length", 2, result.length);
            String firstName = (String) result[0];
            Date hireDate = (Date) result[1];
            log.info("firstName=" + firstName + " hireDate=" + hireDate);
        }
    }

    /**
     * This query demonstrates a query for multiple properties -- same as above
     * -- except this example used a Tuple return type and select aliases 
     */
    @Test
    public void testMultiSelectTuple() {
        log.info("*** testMultiSelectTuple() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> qdef = cb.createTupleQuery();
        
        //select c.firstName as firstName, c.hireDate as hireDate from Clerk c
        Root<Clerk> c = qdef.from(Clerk.class);
        qdef.select(cb.tuple(
        		c.get("firstName").alias("firstName"), 
        		c.get("hireDate").alias("hireDate")));

        TypedQuery<Tuple> query = em.createQuery(qdef);
        List<Tuple> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Tuple result : results) {
            assertEquals("unexpected result length", 2, result.getElements().size());
            String firstName = result.get("firstName", String.class);
            Date hireDate = result.get("hireDate", Date.class);
            log.info("firstName=" + firstName + " hireDate=" + hireDate);
        }
    }
    
    /**
     * This test provides another demonstration of selecting multiple properties --
     * with this example using a constructor expression to return a typed 
     * object for each result in the query.
     */
    @Test
    public void testMultiSelectConstructor() {
        log.info("*** testMultiSelectConstructor() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Receipt> qdef = cb.createQuery(Receipt.class);
        
        //select new ejava.jpa.examples.query.Receipt(s.id,s.buyerId,s.date, s.amount)
        //from Sale s
        Root<Sale> s = qdef.from(Sale.class);
        qdef.select(cb.construct(
		        		Receipt.class, 
		        		s.get("id"), 
		        		s.get("buyerId"),
		        		s.get("date"),
		        		s.get("amount")));
        
        TypedQuery<Receipt> query = em.createQuery(qdef);
        List<Receipt> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Receipt receipt : results) {
            assertNotNull("no receipt", receipt);
            log.info("receipt=" + receipt);
        }        
    }
}
