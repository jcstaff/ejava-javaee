package myorg.queryex.criteria;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TemporalType;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import myorg.queryex.Director;
import myorg.queryex.Movie;
import myorg.queryex.MovieRating;
import myorg.queryex.Person;
import myorg.queryex.QueryBase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

public class CriteriaTest extends QueryBase {
	private static final Log log = LogFactory.getLog(CriteriaTest.class);

	/**
	 * This test demonstrates a basic criteria query as a starting point.
	 * Notice how "p" is designed as the query root in both styles of query
	 * and all expressions from that point are derived from the root.
	 */
	@Test
	public void testBasicQuery() {
		log.info("*** testBasicQuery ***");
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select p from Person p ")
			.append("where p.firstName=:firstName");
		TypedQuery<Person> lquery = em.createQuery(qlString.toString(), Person.class)
			.setParameter("firstName", "Ron");
		
		//build criteria API query
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cqdef = cb.createQuery(Person.class);
		Root<Person> p = cqdef.from(Person.class); 
		TypedQuery<Person> cquery = em.createQuery(cqdef
				.select(p)
				.where(cb.equal(p.get("firstName"), "Ron")));
		
		log.debug("execute JPAQL query");
		List<Person> lresults = lquery.getResultList();
		log.debug("accessing jpaql results");
		log.debug("jpaql results  =" + lresults);
		em.clear();
		
		log.debug("execute Criteria API query");
		List<Person> cresults = cquery.getResultList();		
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);
		
		assertEquals("unexpected results",  lresults, cresults);
	}
	
	/**
	 * This test demonstrates the use of "from" in the Criteria to define 
	 * multiple query roots. In this example we are querying from both 
	 * Director and Person entities and forming our query based off these 
	 * query roots.
	 */
	@Test
	public void testFrom() {
		log.info("*** testFrom ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select d from Director d, " +  //from
					"Person p ")                    //from
			.append("where " +
					"p=d.person and " +
					"p.firstName=:firstName");
		TypedQuery<Director> lquery = em.createQuery(qlString.toString(), Director.class)
			.setParameter("firstName", "Ron");
		
		//build criteria API query
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Director> cqdef = cb.createQuery(Director.class);
		Root<Director> d = cqdef.from(Director.class); //from
		Root<Person> p = cqdef.from(Person.class);     //from
		TypedQuery<Director> cquery = em.createQuery(cqdef
				.select(d)
				.where(cb.and(
						cb.equal(d.get("person"), p)), 
						cb.equal(p.get("firstName"), "Ron"))
						);
		
		log.debug("execute JPAQL query");
		List<Director> lresults = lquery.getResultList();
		log.debug("accessing jpaql results");
		log.debug("jpaql results  =" + lresults);
		em.clear();
		
		log.debug("execute Criteria API query");
		List<Director> cresults = cquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);
		
		assertEquals("unexpected results",  lresults, cresults);
	}

	/**
	 * This test demonstrates the use of a path expression. JPAQL uses a 
	 * name(dot)name path notation. The criteria API uses a changed set of 
	 * get(name).get(name).
	 */
	@Test
	public void testPath() {
		log.info("*** testPath ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select d from Director d ")
			.append("where " +
					"d.person.firstName" +  //Path expression
					"=:firstName");
		TypedQuery<Director> lquery = em.createQuery(qlString.toString(), Director.class)
			.setParameter("firstName", "Ron");
		
		//build criteria API query
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Director> cqdef = cb.createQuery(Director.class);
		Root<Director> d = cqdef.from(Director.class);
		TypedQuery<Director> cquery = em.createQuery(cqdef
				.select(d)//non-portable to eliminate redundant select() matching from()
				.where(cb.equal( 
						d.get("person").get("firstName") //Path expression
						,"Ron"))
					);
		
		log.debug("execute JPAQL query");
		List<Director> lresults = lquery.getResultList();
		log.debug("jpaql results  =" + lresults);
		log.debug("accessing criteria results");
		em.clear();
		
		log.debug("execute Criteria API query");
		List<Director> cresults = cquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);

		assertEquals("unexpected results",  lresults, cresults);
	}
	
	/**
	 * This test demonstrates the use of select() to be passed a path 
	 * expression instead of the query root. This also demonstrates the
	 * use of setting distinct against the CriteriaQuery. The path expressions
	 * require a template parameter that expresses the return type of the 
	 * query -- which is not the query root type in this case.
	 */
	@Test
	public void testSingleSelect() {
		log.info("*** testSingleSelect ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
				//passing an path expression to select rather than root
			.append("select distinct p.firstName " +     //select
					"from Person p ")
			.append("where p.firstName like :pattern");
		TypedQuery<String> lquery = em.createQuery(qlString.toString(), String.class)
			.setParameter("pattern", "R%");
		
		//build criteria API query
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cqdef = cb.createQuery(String.class);
		Root<Person> p = cqdef.from(Person.class);
		TypedQuery<String> cquery = em.createQuery(cqdef
				//passing an path expression to select() rather than root
			.select(p.<String>get("firstName"))   //select
			.distinct(true)
				//path expressions must agree with type used in createQuery
			.where(cb.like(p.<String>get("firstName"),"R%"))
			);
		
		log.debug("execute JPAQL query");
		List<String> lresults = lquery.getResultList();
		log.debug("jpaql results  =" + lresults);
		log.debug("accessing criteria results");
		em.clear();
		
		log.debug("execute Criteria API query");
		List<String> cresults = cquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);

		assertEquals("unexpected results",  lresults, cresults);
	}

	@Test @Ignore
	public void testMultiSelect() {
		log.info("*** testSMultiSelect ***");
		
		//build JPAQL query
		StringBuilder qlString = new StringBuilder()
			.append("select distinct p.firstName, p.lastName " +
					"from Person p ")
			.append("where p.firstName like :pattern");
		TypedQuery<Object[]> lquery = em.createQuery(qlString.toString(), Object[].class)
			.setParameter("pattern", "R%");
		
		//build criteria API query
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cqdef = cb.createTupleQuery();
		Root<Person> p = cqdef.from(Person.class);
		TypedQuery<Tuple> cquery = em.createQuery(cqdef
				.select(cb.tuple(p.get("firstName"), p.get("lastName")))  
				.distinct(true)
				.where(cb.like(p.<String>get("firstName"),"R%"))
			);
		
		log.debug("execute JPAQL query");
		List<Object[]> lresults = lquery.getResultList();
		log.debug("jpaql results  =" + lresults);
		log.debug("accessing criteria results");
		em.clear();
		
		log.debug("execute Criteria API query");
		List<Tuple> cresults = cquery.getResultList();
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);

		assertEquals("unexpected results",  lresults, cresults);
	}
}
