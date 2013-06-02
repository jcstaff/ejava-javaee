package ejava.jpa.examples.query.jpaql;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.jpa.examples.query.Clerk;
import ejava.jpa.examples.query.Customer;
import ejava.jpa.examples.query.QueryBase;
import ejava.jpa.examples.query.Receipt;
import ejava.jpa.examples.query.Sale;
import ejava.jpa.examples.query.Store;

public class JPAQLTest extends QueryBase {
	private static final Log log = LogFactory.getLog(BulkQueryTest.class);

    private <T> List<T> executeQuery(String ejbqlString, Class<T> resultType) {
        return executeQuery(ejbqlString, null, resultType);
    }

    private <T> List<T> executeQuery(String ejbqlString, 
            Map<String, Object> params, Class<T> resultType) {
        TypedQuery<T> query = em.createQuery(ejbqlString, resultType);
        log.info("executing query:" + ejbqlString);
        if (params != null && !params.isEmpty()) {
            StringBuilder text=new StringBuilder();
            for(String key: params.keySet()) {
                Object param = params.get(key);
                text.append(key +"=" + param + ",");
                query.setParameter(key, param);
            }
            log.info("   with params:{" + text + "}");
        }
        List<T> objects = query.getResultList();
        for(Object o: objects) {
           log.info("found result:" + o);
        }
        return objects;
    }
    
    @Test
    public void testSimpleSelect() {
        log.info("*** testSimpleSelect() ***");
        
        int rows = executeQuery(
        		"select object(c) from Customer as c", 
        		Customer.class).size();
        assertTrue("unexpected number of customers:" + rows, rows > 0);
    }
    
    @Test
    public void testEntityProperties() {
        log.info("*** testEntityProperties() ***");
        
        TypedQuery<Object[]> query = em.createQuery(
                "select c.firstName, c.hireDate from Clerk c", Object[].class);
        List<Object[]> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Object[] result : results) {
            assertEquals("unexpected result length", 2, result.length);
            String firstName = (String) result[0];
            Date hireDate = (Date) result[1];
            log.info("firstName=" + firstName + " hireDate=" + hireDate);
        }
    }
    
    @Test
    public void testEntityRelationships() {
        log.info("*** testEntityRelationships() ***");
        
        TypedQuery<Object[]> query = em.createQuery(
                "select s.id, s.store.name from Sale s", Object[].class);
        List<Object[]> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Object[] result : results) {
            assertEquals("unexpected result length", 2, result.length);
            Long id = (Long) result[0];
            String name = (String) result[1];
            log.info("sale.id=" + id + ", sale.store.name=" + name);
        }
    }

    @Test
    public void testConstructorExpressions() {
        log.info("*** testConstructorExpressions() ***");
        
        TypedQuery<Receipt> query = em.createQuery(
            String.format("select new %s(", Receipt.class.getName()) +
            "s.id,s.buyerId,s.date, s.amount) " +
            "from Sale s", Receipt.class);
        List<Receipt> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Receipt receipt : results) {
            assertNotNull("no receipt", receipt);
            log.info("receipt=" + receipt);
        }        
    }
    
    @Test
    public void testIN() {
        log.info("*** testIN() ***");
        
        int rows = executeQuery(
                "select sale from Store s, IN(s.sales) sale", 
                Sale.class).size();
        assertTrue("unexpected number of sales:" + rows, rows > 0);

        rows = executeQuery(
                "select sale.date from Store s, IN(s.sales) sale",
                Date.class).size();
        assertTrue("unexpected number of sales:" + rows, rows > 0);
    }
    
    @Test
    public void testInnerJoin() {
        log.info("*** testInnerJoin() ***");
        
        int rows = executeQuery(
                "select sale from Store s INNER JOIN s.sales sale",
                Sale.class).size();
        assertTrue("unexpected number of sales:" + rows, rows > 0);

        rows = executeQuery(
                "select sale.date from Store s INNER JOIN s.sales sale",
        		Date.class).size();
        assertTrue("unexpected number of sales:" + rows, rows > 0);
    }
    
    @Test
    public void testOuterJoin() {
        log.info("*** testOuterJoin() ***");
        
        TypedQuery<Object[]> query = em.createQuery(
            "select c.id, c.firstName, sale.amount " +
            "from Clerk c " +
            "LEFT JOIN c.sales sale", Object[].class);
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
    
    @Test
    public void testFetchJoin() {        
        log.info("** testFetchJoin() ***");
        executeQuery(
                "select s from Store s JOIN FETCH s.sales", Store.class);
    }
    
    @Test
    public void testDISTINCT() {
        log.info("*** testDISTINCT() ***");
        
        int rows = executeQuery(
                "select DISTINCT c.lastName from Customer c",
                Object[].class).size();
        assertEquals("unexpected number of rows", 3, rows);
        rows = executeQuery(
                "select DISTINCT c.firstName from Customer c",
                Object[].class).size();
        assertEquals("unexpected number of rows for DISTINCT", 2, rows);
    }
    
    @Test
    public void testLiteral() {
        log.info("*** testLiteral() ***");
        int rows = executeQuery(
                "select c from Customer c " +
                "where c.firstName='cat'",
                Customer.class).size();
        assertEquals("unexpected number of rows:", 1, rows);
    }
    
    @Test
    public void testSpecialCharacter() {
        log.info("*** testSpecialCharacter() ***");
        int rows = executeQuery(
                "select s from Store s " +
                "where s.name='Big Al''s'",
                Store.class).size();
        assertEquals("unexpected number of rows", 1, rows);
    }
    
    @Test
    public void testLike() {
        log.info("*** testLike() ***");
        
        int rows = executeQuery(
                  "select c from Clerk c " +
                  "where c.firstName like 'M%'",
                  Clerk.class).size();
        assertEquals("unexpected number of rows", 2, rows);
        
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("firstName", "M%");
        rows = executeQuery(
                "select c from Clerk c " +
                "where c.firstName like :firstName)",
                params,Clerk.class).size();
        assertEquals("unexpected number of rows", 2, rows);        

        params = new HashMap<String, Object>();
        params.put("firstName", "M");
        rows = executeQuery(
                "select c from Clerk c " +
                "where c.firstName like concat(:firstName,'%')",
                params, Clerk.class).size();
        assertEquals("unexpected number of rows", 2, rows);    
        
        rows = executeQuery(
                "select c from Clerk c " +
                "where c.firstName like '_anny'",
                Clerk.class).size();
        assertEquals("unexpected number of rows", 1, rows);
        
    }
    
    @Test
    public void testArithmetic() {
        log.info("*** testArithmetic() ***");
        
        String query = "select s from Sale s " +
            "where (s.amount * :tax) > :amount";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("amount", new BigDecimal(10.00));
                
        double tax = 0.04;
        int rows=0;
        //keep raising taxes until somebody pays $10.00 in tax
        while (rows==0) {
            params.put("tax", new BigDecimal(tax));
            rows = executeQuery(query, params, Sale.class).size();    
            if (rows == 0) { tax += 0.01; }
        }
        log.info("raise taxes to:" + tax);
        
        assertEquals("unexpected level for tax:" + tax, 
        		(int)(0.07 * 100), (int)(tax * 100));
    }
    
    @Test
    public void testLogical() {
        log.info("*** testLogical() ***");
        int rows = executeQuery(
                "select c from Customer c " +
                "where (c.firstName='cat' AND" +
                "      c.lastName='inhat') OR" +
                "      c.firstName='thing' ",
                Customer.class).size();
        assertEquals("unexpected number of rows:" + rows, 3, rows);        

        rows = executeQuery(
                "select c from Customer c " +
                "where NOT (c.firstName='cat' AND" +
                "      c.lastName='inhat') OR" +
                "      c.firstName='thing' ",
                Customer.class).size();
        assertEquals("unexpected number of rows:" + rows, 2, rows);        
    }
    
    @Test
    public void testEquality() {
        log.info("*** testEquality() ***");
        
        //get a clerk entity
        Clerk clerk = (Clerk)
            em.createQuery(
                "select c from Clerk c where c.firstName = 'Manny'")
              .getSingleResult();
        
        //find all sales that involve this clerk
        String ejbqlQueryString = 
            "select s " +
            "from Sale s, IN (s.clerks) c " +
            "where c = :clerk";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clerk", clerk);
        int rows=executeQuery(ejbqlQueryString,params, Sale.class).size();
        assertEquals("unexpected number of rows", 2, rows);
    }
    
    @Test
    public void testBetween() {
        log.info("*** testBetween() ***");
        
        String query = "select s from Sale s " +
            "where s.amount BETWEEN :low AND :high";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("low", new BigDecimal(90.00));
        params.put("high", new BigDecimal(110.00));
                
        int rows = executeQuery(query, params, Sale.class).size();    
        assertEquals("unexpected number of rows", 1, rows);
    }
    
    @Test
    public void testIsNull() {
        log.info("*** testIsNull() ***");
        
        String query = "select s from Sale s " +
            "where s.store IS NULL";
                
        int rows = executeQuery(query, Sale.class).size();    
        assertEquals("unexpected number of rows", 0, rows);

        query = "select s from Sale s " +
            "where s.store IS NOT NULL";
            
        rows = executeQuery(query, Sale.class).size();    
        assertEquals("unexpected number of rows", 2, rows);
    }

    @Test
    public void testIsEmpty() {
        log.info("*** testIsEmpty() ***");
        
        String query = "select c from Clerk c " +
            "where c.sales IS EMPTY";
                
        int rows = executeQuery(query, Clerk.class).size();    
        assertEquals("unexpected number of rows", 1, rows);

        query = "select c from Clerk c " +
            "where c.sales IS NOT EMPTY";
            
        rows = executeQuery(query, Clerk.class).size();    
        assertEquals("unexpected number of rows", 2, rows);
    }

    @Test
    public void testMemberOf() {
        log.info("*** testMemberOf() ***");
        
        //get a clerk entity
        Clerk clerk =
            em.createQuery(
                "select c from Clerk c where c.firstName = 'Manny'",
                Clerk.class)
              .getSingleResult();
        
        //find all sales that involve this clerk
        String ejbqlQueryString = 
            "select s " +
            "from Sale s " +
            "where :clerk MEMBER OF s.clerks";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("clerk", clerk);
        int rows=executeQuery(ejbqlQueryString,params, Sale.class).size();
        assertEquals("unexpected number of rows", 2, rows);
    }
    
    @Test
    public void testStringFunctions() {
        log.info("*** testStringFunctions() ***");

        int rows = executeQuery(
                "select c from Customer c " +
                "where c.firstName='CAT'",
                Customer.class).size();
        assertEquals("unexpected number of rows:" + rows, 0, rows);

        rows = executeQuery(
                "select c from Customer c " +
                "where c.firstName=LOWER('CAT')",
                Customer.class).size();
        assertEquals("unexpected number of rows:" + rows, 1, rows);
    
        rows = executeQuery(
                "select UPPER(c.firstName) from Customer c " +
                "where c.firstName=LOWER('CAT')",
                String.class).size();
        assertEquals("unexpected number of rows:" + rows, 1, rows);
    
        rows = executeQuery(
                "select TRIM(LEADING 'c' FROM c.firstName) from Customer c " +
                "where c.firstName='cat')",
                String.class).size();
        assertEquals("unexpected number of rows:" + rows, 1, rows);
        
        rows = executeQuery(
                "select c from Customer c " +
                "where CONCAT(CONCAT(c.firstName,' '),c.lastName) ='cat inhat')",
                Customer.class).size();
        assertEquals("unexpected number of rows:" + rows, 1, rows);
        
        rows = executeQuery(
                "select c from Customer c " +
                "where LENGTH(c.firstName) = 3",
                Customer.class).size();
        assertEquals("unexpected number of rows:" + rows, 1, rows);
        
        rows = executeQuery(
                "select c from Customer c " +
                "where LOCATE('cat',c.firstName,2) > 0",
                Customer.class).size();
        assertEquals("unexpected number of rows:" + rows, 0, rows);        

        rows = executeQuery(
                "select c from Customer c " +
                "where LOCATE('at',c.firstName,2) > 1",
                Customer.class).size();
        assertEquals("unexpected number of rows:" + rows, 1, rows);        

        rows = executeQuery(
                "select SUBSTRING(c.firstName,2,2) from Customer c " +
                "where c.firstName = 'cat'",
                String.class).size();
        assertEquals("unexpected number of rows:" + rows, 1, rows);
        
        rows = executeQuery(
                "select c from Customer c " +
                "where SUBSTRING(c.firstName,2,2) = 'at'",
                Customer.class).size();
        assertEquals("unexpected number of rows:" + rows, 1, rows);        
    }

    @Test
    public void testDates() {        
        log.info("*** testDates() ***");

        int rows = executeQuery(
                "select s from Sale s " +
                "where s.date < CURRENT_DATE",
                Sale.class).size();
        assertEquals("unexpected number of rows", 2, rows);
        
        rows = executeQuery(
                "select s from Sale s " +
                "where s.date = CURRENT_DATE",
                Sale.class).size();
        assertEquals("unexpected number of rows", 0, rows);

        rows = em.createQuery(
                "update Sale s " +
                "set s.date = CURRENT_DATE").executeUpdate();
        assertEquals("unexpected number of rows", 2, rows);
        
        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();
        
        rows = executeQuery(
                "select s from Sale s " +
                "where s.date = CURRENT_DATE",
                Sale.class).size();
        assertEquals("unexpected number of rows", 2, rows);
    }
    
    @Test
    public void testCount() {        
        log.info("*** testCount() ***");

        List<Number> results= executeQuery(
                "select COUNT(s) from Sale s", Number.class);
        assertEquals("unexpected number of rows", 1, results.size());
        assertEquals("unexpected result", 2, results.get(0).intValue());
    }
    
    @Test
    public void testMaxMin() {        
        log.info("*** testMaxMin() ***");

        List<Number> results= executeQuery(
                "select max(s.amount) from Sale s", Number.class);
        assertEquals("unexpected number of rows", 1, results.size());
        assertEquals("unexpected result", 150, results.get(0).intValue());
        
        results= executeQuery(
                "select min(s.amount) from Sale s", Number.class);
        assertEquals("unexpected number of rows", 1, results.size());
        assertEquals("unexpected result", 100, results.get(0).intValue());
    }

    @Test
    public void testSumAve() {        
        log.info("*** testSumAve() ***");

        List<Number> results= executeQuery(
            "select sum(s.amount) from Sale s", Number.class);
        assertEquals("unexpected number of rows", 1, results.size());
        assertEquals("unexpected result", 250, results.get(0).intValue());
        
        results= executeQuery(
                "select avg(s.amount) from Sale s", Number.class);
        assertEquals("unexpected number of rows", 1, results.size());
        assertEquals("unexpected result", 125, results.get(0).intValue());
    }
    
    @Test
    public void testOrderBy() {
        log.info("*** testOrderBy() ***");

        List<Sale> results = executeQuery(
            "select s from Sale s ORDER BY s.amount ASC", Sale.class); 
        assertEquals("unexpected number of rows", 2, results.size());
        assertEquals("unexpected first element", 
                100, 
                results.get(0).getAmount().intValue());
        assertEquals("unexpected first element", 
                150, 
                results.get(1).getAmount().intValue());

        
        results = executeQuery(
                "select s from Sale s ORDER BY s.amount DESC", Sale.class); 
        assertEquals("unexpected number of rows", 2, results.size());
        assertEquals("unexpected first element", 
                150, 
                results.get(0).getAmount().intValue());
        assertEquals("unexpected first element", 
                100, 
                results.get(1).getAmount().intValue());
    }
    
    @Test
    public void testSubqueries() {
       log.info("*** testSubqueries() ***");   
       
       List<Customer> results = executeQuery(
               "select c from Customer c " +
               "where c.id IN " +
               "   (select s.buyerId from Sale s " +
               "    where s.amount > 100)",
              Customer.class);       
       assertEquals("unexpected number of rows", 1, results.size());
    }
    
    @Test
    public void testAll() {
        log.info("*** testAll() ***");  
        
        executeQuery("select s from Sale s", Sale.class);
        
        @SuppressWarnings("unused")
		List<Clerk> results = executeQuery(
                "select c from Clerk c " +
                "where 125 < ALL " +
                "   (select s.amount from c.sales s)",
               Clerk.class);       
        results = executeQuery(
                "select c from Clerk c " +
                "where 125 > ALL " +
                "   (select s.amount from c.sales s)",
               Clerk.class);       
        //assertEquals("unexpected number of rows", 1, results.size());
        
        results = executeQuery(
                "select c from Clerk c " +
                "where 125 < ANY " +
                "   (select s.amount from c.sales s)",
               Clerk.class);       
        results = executeQuery(
                "select c from Clerk c " +
                "where 125 > ANY " +
                "   (select s.amount from c.sales s)",
               Clerk.class);       
        //assertEquals("unexpected number of rows", 1, results.size());
     }

}
