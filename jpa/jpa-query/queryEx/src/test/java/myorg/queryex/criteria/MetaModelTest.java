package myorg.queryex.criteria;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TemporalType;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import myorg.queryex.Actor;
import myorg.queryex.Director;
import myorg.queryex.Movie;
import myorg.queryex.MovieRating;
import myorg.queryex.MovieRole;
import myorg.queryex.Movie_;
import myorg.queryex.Person;
import myorg.queryex.Person_;
import myorg.queryex.QueryBase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MetaModelTest extends QueryBase {
	private static final Log log = LogFactory.getLog(MetaModelTest.class);
	private EntityManager em2; //second persistence context for parallel queries
	
	@Before
	public void init() {
		if (emf!=null) {
			em2=emf.createEntityManager();
		}
	}
	
	@After
	public void destroy() {
		destroy(em2);
	}
	
	private static void destroy(EntityManager emgr) {
		if (emgr!=null && emgr.isOpen()) {
			if (emgr.getTransaction().isActive()) {
				if (emgr.getTransaction().getRollbackOnly()) {
					emgr.getTransaction().rollback();
				} else {
					emgr.getTransaction().commit();
				}
			}
			emgr.close();
		}
	}
	

	@Test
	public void testMetaModel() {
		log.info("*** testMetaModel ***");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Movie> cqdef = cb.createQuery(Movie.class);
		Root<Movie> m = cqdef.from(Movie.class);
		EntityType<Movie> m_ = m.getModel();
		
		//metamodel can be inspected
		for (Attribute<? super Movie, ?> prop: m_.getAttributes()) {
			log.info(String.format("%20s\t%s:%s", prop.getPersistentAttributeType(), prop.getName(), prop.getJavaType()));
		}
		
		//metamodel can be used for type-safe property access
		Join<Movie,String> genre = m.join(m_.getSet("genres", String.class)); 
		cqdef.select(m).distinct(true)
			.where(cb.equal(
					m.get(m_.getSingularAttribute("rating",MovieRating.class)), 
					MovieRating.R),
		       cb.like(genre, "Comedy"),
			   cb.greaterThanOrEqualTo(
					   m.get(m_.getSingularAttribute("releaseDate", Date.class)), 
					   new GregorianCalendar(1995,0,0).getTime())
				);
		TypedQuery<Movie> cquery = em2.createQuery(cqdef);
		
		log.debug("execute Criteria API query");
		List<Movie> cresults = cquery.getResultList();		
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);
	}

	@Test
	public void testCanonicalMetaModel() {
		log.info("*** testCanonicalMetaModel ***");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Movie> cqdef = cb.createQuery(Movie.class);
		Root<Movie> m = cqdef.from(Movie.class);
		
		//canonical metamodel can be used for easy, type-safe property access
		Join<Movie,String> genre = m.join(Movie_.genres);
		cqdef.select(m).distinct(true)
			.where(cb.equal(
					m.get(Movie_.rating), MovieRating.R),
		       cb.like(genre, "Comedy"),
			   cb.greaterThanOrEqualTo(
					   m.get(Movie_.releaseDate), 
					   new GregorianCalendar(1995,0,0).getTime())
				);
		TypedQuery<Movie> cquery = em2.createQuery(cqdef);
		
		log.debug("execute Criteria API query");
		List<Movie> cresults = cquery.getResultList();		
		log.debug("accessing criteria results");
		log.debug("critera results=" + cresults);
	}
}
