package myorg.queryex;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class QueryTest extends QueryBase {
	private static final Log log = LogFactory.getLog(QueryTest.class);
	
	@Test
	public void testMulti() {
		log.info("*** testMulti ***");
		
		List<Movie> movies = em.createQuery(
				"select m from Movie m " +
				"order by title ASC", Movie.class)
				.getResultList();
		log.debug("result=" + movies);
		assertEquals("unexpected number of movies", 7, movies.size());
	}

	@Test
	public void testSingle() {
		log.info("*** testSingle ***");
		
		Movie movie = em.createQuery(
				"select m from Movie m " +
				"where m.title='Animal House'", Movie.class)
				.getSingleResult();
		log.debug("result=" + movie);
		assertNotNull("no movie", movie);
	}

	@Test(expected=NoResultException.class)
	public void testSingleNoResult() {
		log.info("*** testSingleNoResult ***");
		
		em.createQuery(
				"select m from Movie m " +
				"where m.title='Animal Hut'", Movie.class)
				.getSingleResult();
		log.debug("query did not produce expected exception");
	}

	@Test(expected=NonUniqueResultException.class)
	public void testSingleNonUniqueResult() {
		log.info("*** testSingleNonUniqueResult ***");
		
		em.createQuery(
				"select m from Movie m " +
				"where m.rating='R'", Movie.class)
				.getSingleResult();
		log.debug("query did not produce expected exception");
	}

	@Test
	public void testParameters() {
		log.info("*** testParameters ***");
		
		List<Movie> movies = em.createQuery(
				"select m from Movie m " +
				"where m.rating=:rating " +
				"and m.releaseDate > :date " +
				"and lower(m.title) like concat(concat('%',lower(:title)),'%')", Movie.class)
				.setParameter("rating", MovieRating.R)
				.setParameter("date", new GregorianCalendar(1980, 0, 0).getTime(), TemporalType.DATE)
				.setParameter("title", "wag")
				.getResultList();
		log.debug("result=" + movies);
		assertEquals("unexpected number of movies", 1, movies.size());
	}


	@Test
	public void testPaging() {
		log.info("*** testPaging ***");

		List<Movie> movies = new LinkedList<Movie>();
		TypedQuery<Movie> query = em.createQuery(
				"select m from Movie m " +
				"order by title", Movie.class)
				.setMaxResults(3);
		List<Movie> page=null;
		int offset=0;
		do {
			page = query.setFirstResult(offset).getResultList();
			log.debug("page=" + page);
			movies.addAll(page);
			offset += page.size();
			log.debug("page.size=" + page.size() + ", offset=" + offset);
		} while (page.size() > 0);
		
		log.debug("result=" + movies);
		assertEquals("unexpected number of movies", 7, movies.size());
	}
	
	/**
	 * This method demonstrates passing a query hint to the provider for  
	 * the query execution.
	 */
	@Test
	public void testNamedQuery() {
		log.info("*** testNamedQuery ***");
		
		Movie movie = em.createNamedQuery("Movie.findByTitle", Movie.class)
				.setParameter("title", "wag")
				.getSingleResult();
		log.debug("result=" + movie);
		assertNotNull("no movie", movie);
	}
	
	
}
