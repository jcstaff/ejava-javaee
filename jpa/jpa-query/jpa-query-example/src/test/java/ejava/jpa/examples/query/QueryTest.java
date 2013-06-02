package ejava.jpa.examples.query;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

public class QueryTest extends QueryBase {
	private static final Log log = LogFactory.getLog(QueryTest.class);

	@Test
	public void exampleJPAQL() {
		log.info("*** exampleJPAQL");
		
		String jpaqlString =
			//c is part of the root query and 
			//represents rows from the Customer entity table(s)  
			"select c from Customer c " +
			//c.lastName is a path off the root term that expressed
			//property is evaluated against a parameter
			"where c.firstName = :firstName " +
			//we order by property located through a path based from root
			"order by c.lastName ASC"; 
		
		//use query string to build query
		TypedQuery<Customer> query = em
				.createQuery(jpaqlString,Customer.class);

		//at this point we are query-type agnostic
		List<Customer> customers = query
				.setParameter("firstName", "thing")
				.getResultList();
		log.info("result=" + customers);
		assertEquals("unexpected number of results", 2, customers.size());
	}
	
	@Test
	public void exampleSQL() {
		log.info("*** exampleSQL ***");
		
		Table table = Customer.class.getAnnotation(Table.class);

		String sqlString =
			//return several columns from the table represented by c
			"select c.CUSTOMER_ID, c.firstName, c.lastName " +						
			//base the query on the entity class table
			String.format("from %s c ", table.name()) +
			"where c.firstName = ? " +
			//we order by column in entity class table
			"order by c.lastName ASC";
			
		//use query string to build query
		Query query = em.createNativeQuery(sqlString,Customer.class);

		//at this point we are query-type agnostic (mostly)
		//native queries do not have an option for TypedQuery
		@SuppressWarnings("unchecked")
		List<Customer> customers = query
				//name substitution is specified as non-portable in JPA spec
				.setParameter(1, "thing")
				.getResultList();
		log.info("result=" + customers);
		assertEquals("unexpected number of results", 2, customers.size());
	}
	
	@Test 
	public void exampleCriteriaStringAPI() {
		log.info("*** exampleCriteriaStringAPI ***");

		CriteriaBuilder cb = em.getCriteriaBuilder();
		//definition that will hold query expression
		CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
		//c is part of the root query and 
		//represents rows from the Customer entity table(s)
		Root<Customer> c = qdef.from(Customer.class);
		//we hold onto the returned reference to c here so we can base
		//path references from it later
		qdef.select(c) //we are returning a single root object
		        //c.lastName obtained thru a path expressing using builder
		    .where(cb.equal(c.get("firstName"), 
		    	//property is evaluated against a parameter
		    		        cb.parameter(String.class,"firstName")))
		    	//we order by property located through a path based from root
		    .orderBy(cb.asc(c.get("lastName")));
		//use query string to build query
		TypedQuery<Customer> query = em.createQuery(qdef);

		//at this point we are query-type agnostic
		List<Customer> customers = query
				.setParameter("firstName", "thing")
				.getResultList();
		log.info("result=" + customers);
		assertEquals("unexpected number of results", 2, customers.size());
	}
}
