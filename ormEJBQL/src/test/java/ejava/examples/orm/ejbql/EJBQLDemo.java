package ejava.examples.orm.ejbql;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

public class EJBQLDemo extends DemoBase {
    
    @SuppressWarnings("unchecked")
    private List<Object> executeQuery(String ejbqlString) {
        Query query = em.createQuery(ejbqlString);
        log.info("executing query:" + ejbqlString);
        List<Object> objects = query.getResultList();
        for(Object o: objects) {
           log.info("found result:" + o);
        }
        return objects;
    }
    
    public void testSimpleSelect() {
        log.info("testSimpleSelect");
        
        int rows = executeQuery("select object(c) from Customer as c").size();
        assertTrue("unexpected number of customers:" + rows, rows > 0);
    }
    
    @SuppressWarnings("unchecked")
    public void testEntityProperties() {
        log.info("testEntityProperties");
        
        Query query = em.createQuery(
                "select c.firstName, c.hireDate from Clerk c");
        List<Object> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Iterator<Object> itr=results.iterator(); itr.hasNext(); ) {
            Object[] result = (Object[])itr.next();
            assertNotNull("no result array", result);
            assertEquals("unexpected result length:" + result.length, 
                    2, result.length);
            String firstName = (String) result[0];
            Date hireDate = (Date) result[1];
            log.info("firstName=" + firstName + " hireDate=" + hireDate);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void testEntityRelationships() {
        log.info("testEntityRelationships");
        
        Query query = em.createQuery(
                "select s.id, s.store.name from Sale s");
        List<Object> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Iterator<Object> itr=results.iterator(); itr.hasNext(); ) {
            Object[] result = (Object[])itr.next();
            assertNotNull("no result array", result);
            assertEquals("unexpected result length:" + result.length, 
                    2, result.length);
            Long id = (Long) result[0];
            String name = (String) result[1];
            log.info("sale.id=" + id + ", sale.store.name=" + name);
        }
    }

    @SuppressWarnings("unchecked")
    public void testConstructorExpressions() {
        log.info("testConstructorExpressions");
        
        Query query = em.createQuery(
                "select new ejava.examples.orm.ejbql.Receipt(" +
                "s.id,s.buyerId,s.date, s.amount) " +
                "from Sale s");
        List<Object> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Iterator<Object> itr=results.iterator(); itr.hasNext(); ) {
            Receipt receipt = (Receipt)itr.next();
            assertNotNull("no receipt", receipt);
            log.info("receipt=" + receipt);
        }        
    }
    
    public void testIN() {
        log.info("testIN");
        
        int rows = executeQuery(
                "select sale from Store s, IN(s.sales) sale").size();
        assertTrue("unexpected number of sales:" + rows, rows > 0);

        rows = executeQuery(
                "select sale.date from Store s, IN(s.sales) sale").size();
        assertTrue("unexpected number of sales:" + rows, rows > 0);
    }
    
    public void testInnerJoin() {
        log.info("testInnerJoin");
        
        int rows = executeQuery(
                "select sale from Store s INNER JOIN s.sales sale").size();
        assertTrue("unexpected number of sales:" + rows, rows > 0);

        rows = executeQuery(
                "select sale.date from Store s INNER JOIN s.sales sale").size();
        assertTrue("unexpected number of sales:" + rows, rows > 0);
    }
    
    @SuppressWarnings("unchecked")
    public void testOuterJoin() {
        log.info("testOuterJoin");
        
        Query query = em.createQuery(
            "select c.id, c.firstName, sale.amount " +
            "from Clerk c " +
            "LEFT JOIN c.sales sale");
        List<Object> results = query.getResultList();
        assertTrue("no results", results.size() > 0);
        for(Iterator<Object> itr=results.iterator(); itr.hasNext(); ) {
            Object[] result = (Object[])itr.next();
            assertNotNull("no result array", result);
            assertEquals("unexpected result length:" + result.length, 
                    3, result.length);
            Long id = (Long) result[0];
            String name = (String) result[1];
            BigDecimal amount = (BigDecimal) result[2];
            log.info("clerk.id=" + id + ", clerk.firstName=" + name +
                    ", amount=" + amount);
        }
    }
    
    public void testFetchJoin() {        
        log.info("testFetchJoin");
        executeQuery(
                "select s from Store s LEFT JOIN s.sales sale");
    }
    
    public void testDISTINCT() {
        log.info("testDISTINCT");
        
        int rows = executeQuery(
                "select DISTINCT c.lastName from Customer c").size();
        assertEquals("unexpected number of rows:" + rows, 3, rows);
        rows = executeQuery(
                "select DISTINCT c.firstName from Customer c").size();
        assertEquals("unexpected number of rows for DISTINCT:" + rows, 2, rows);
    }
    
    public void testLiteral() {
        log.info("testLiteral");
        int rows = executeQuery(
                "select c from Customer c " +
                "where c.firstName='cat'"
                ).size();
        assertEquals("unexpected number of rows:" + rows, 1, rows);
    }
    
    public void testLogical() {
        log.info("testLogical");
        int rows = executeQuery(
                "select c from Customer c " +
                "where (c.firstName='cat' AND" +
                "      c.lastName='inhat') OR" +
                "      c.firstName='thing' "
                ).size();
        assertEquals("unexpected number of rows:" + rows, 3, rows);        

        rows = executeQuery(
                "select c from Customer c " +
                "where NOT (c.firstName='cat' AND" +
                "      c.lastName='inhat') OR" +
                "      c.firstName='thing' "
                ).size();
        assertEquals("unexpected number of rows:" + rows, 2, rows);        
    }
}
