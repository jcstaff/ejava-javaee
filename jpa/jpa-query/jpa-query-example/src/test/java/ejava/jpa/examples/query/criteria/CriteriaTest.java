package ejava.jpa.examples.query.criteria;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LazyInitializationException;
import org.junit.Test;

import ejava.jpa.examples.query.Clerk;
import ejava.jpa.examples.query.Customer;
import ejava.jpa.examples.query.QueryBase;
import ejava.jpa.examples.query.Receipt;
import ejava.jpa.examples.query.Sale;
import ejava.jpa.examples.query.Store;

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

    /**
     * This test provides an example of navigating a path formed by a 
     * relationship. In this case the path used is a single element.
     */
    @Test
    public void testPathExpressions() {
        log.info("*** testPathExpressions() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> qdef = cb.createQuery(Object[].class);
        
        //select s.id, s.store.name from Sale s
        Root<Sale> s = qdef.from(Sale.class);
        qdef.select(cb.array(s.get("id"),
        		             s.get("store").get("name")));
        
        TypedQuery<Object[]> query = em.createQuery(qdef);
        List<Object[]> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Object[] result : results) {
            assertEquals("unexpected result length", 2, result.length);
            Long id = (Long) result[0];
            String name = (String) result[1];
            log.info("sale.id=" + id + ", sale.store.name=" + name);
        }
    }

    /**
     * This test provides an example collection path using an INNER JOIN
     */
    @Test
    public void testCollectionPathExpressionsInnerJoin() {
        log.info("*** testCollectionPathExpressionsInnerJoin ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Date> qdef = cb.createQuery(Date.class);
        
        //select sale.date from Clerk c JOIN c.sales sale
        Root<Clerk> c = qdef.from(Clerk.class);
        Join<Clerk, Sale> sale = c.join("sales", JoinType.INNER);
        qdef.select(sale.<Date>get("date"));

        int rows=executeQuery(qdef).size();
        assertTrue("unexpected number of sales:" + rows, rows > 0);
    }

    /**
     * This test provides an example collection path using an LEFT OUTER JOIN
     */
    @Test
    public void testOuterJoin() {
        log.info("*** testOuterJoin() ***");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> qdef = cb.createQuery(Object[].class);
        
        //select c.id, c.firstName, sale.amount 
        //from Clerk c 
        //LEFT JOIN c.sales sale
        Root<Clerk> c = qdef.from(Clerk.class);
        Join<Clerk, Sale> sale = c.join("sales", JoinType.LEFT);
        qdef.select(cb.array(c.get("id"),
        		             c.get("firstName"),
        		             sale.get("amount")));
        
        TypedQuery<Object[]> query = em.createQuery(qdef);
        List<Object[]> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Object[] result : results) {
            assertEquals("unexpected result length", 3, result.length);
            Long id = (Long) result[0];
            String name = (String) result[1];
            BigDecimal amount = (BigDecimal) result[2];
            log.info("clerk.id=" + id + ", clerk.firstName=" + name +
                    ", amount=" + amount);
        }
    }
    
    /**
     * This test demonstrates creating an explicit JOIN based on adhoc criteria
     */
    @Test
    public void testExplicitJoin() {
    	log.info("*** testExplicitJoin ***");
    	
    	CriteriaBuilder cb = em.getCriteriaBuilder();
    	CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
    	
    	//select c from Sale s, Customer c 
    	//where c.id = s.buyerId
    	Root<Sale> s = qdef.from(Sale.class);
    	Root<Customer> c = qdef.from(Customer.class);
    	qdef.select(c)
    	    .where(cb.equal(c.get("id"), s.get("buyerId")));
    	
    	int rows = executeQuery(qdef).size();
        assertTrue("unexpected number of customers:" + rows, rows > 0);
    }
    
    /**
     * This test demonstrates the function of a JOIN FETCH to perform the 
     * EAGER retrieval of entities as a side-effect of the query
     */
    @Test
    public void testFetchJoin1() {        
        log.info("** testFetchJoin1() ***");
        
        EntityManager em2 = createEm();
        CriteriaBuilder cb = em2.getCriteriaBuilder();
        CriteriaQuery<Store> qdef = cb.createQuery(Store.class);
        
        //select s from Store s JOIN s.sales
        //where s.name='Big Al''s'
        Root<Store> s = qdef.from(Store.class);
        s.join("sales");
        qdef.select(s)
            .where(cb.equal(s.get("name"), "Big Al's"));
        
        Store store = em2.createQuery(qdef).getSingleResult();
        log.info("em.contains(" + em2.contains(store) + ")");
        em2.close();
        try {
        	store.getSales().get(0).getAmount();
        	fail("did not trigger lazy initialization exception");
        } catch (LazyInitializationException expected) {
        	log.info("caught expected exception:" + expected);
        }
    }
    @Test
    public void testFetchJoin2() {        
        log.info("** testFetchJoin2() ***");

        EntityManager em2 = createEm();
        CriteriaBuilder cb = em2.getCriteriaBuilder();
        CriteriaQuery<Store> qdef = cb.createQuery(Store.class);

        //select s from Store s JOIN FETCH s.sales
        //where s.name='Big Al''s'
        Root<Store> s = qdef.from(Store.class);
        s.fetch("sales");
        qdef.select(s)
            .where(cb.equal(s.get("name"), "Big Al's"));
        
        Store store = em2.createQuery(qdef).getSingleResult();
        log.info("em.contains(" + em2.contains(store) + ")");
        em2.close();
       	store.getSales().get(0).getAmount();
    }
    
    /**
     * This test demonstrates the use of DISTINCT to limit the results
     * to only unique values
     */
    @Test
    public void testDISTINCT() {
        log.info("*** testDISTINCT() ***");

        CriteriaBuilder cb = em.getCriteriaBuilder();
        {
	        CriteriaQuery<String> qdef = cb.createQuery(String.class);
	        
	        //select DISTINCT c.lastName from Customer c
	        Root<Customer> c = qdef.from(Customer.class);
	        qdef.select(c.<String>get("lastName"))
	            .distinct(true);
	        
	        int rows = executeQuery(qdef).size();
	        assertEquals("unexpected number of rows", 3, rows);
        }
        
        {        
        	CriteriaQuery<String> qdef = cb.createQuery(String.class);
        	
        	//select DISTINCT c.firstName from Customer c
        	Root<Customer> c = qdef.from(Customer.class);
        	qdef.select(c.<String>get("firstName"))
        		.distinct(true);
        
	        int rows = executeQuery(qdef).size();
	        assertEquals("unexpected number of rows for DISTINCT", 2, rows);
        }
    }
}
