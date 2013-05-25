package myorg.queryex.criteria;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import myorg.queryex.Movie;
import myorg.queryex.MovieRating;
import myorg.queryex.Person;
import myorg.queryex.QueryBase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class CriteriaTest extends QueryBase {
	private static final Log log = LogFactory.getLog(CriteriaTest.class);

	@Test
	public void testBasicQuery() {
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
		List<Person> lpeople = lquery.getResultList();
		
		log.debug("execute Criteria API query");
		List<Person> cpeople = cquery.getResultList();
		
		log.debug("jpaql results  =" + lpeople);
		log.debug("critera results=" + cpeople);
		assertEquals("unexpected results",  lpeople, cpeople);
	}
}
