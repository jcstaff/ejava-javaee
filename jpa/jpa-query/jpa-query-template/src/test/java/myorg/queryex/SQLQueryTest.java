package myorg.queryex;

import static org.junit.Assert.*;


import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

public class SQLQueryTest extends QueryBase {
    private static final Log log = LogFactory.getLog(SQLQueryTest.class);
    
    /**
     * This test method demonstrates building and executing a SQL query using 
     * the entity manager.
     */
    @Test
    public void testSQLQuery() {
    	log.info("*** testSQLQuery ***");    	
    	@SuppressWarnings("unchecked")
		List<String> titles = em.createNativeQuery(
    			"select title from queryex_movie " +
    			"order by title ASC").getResultList();
    	for (String title : titles) {
    		log.debug(title);
    	}
    	assertEquals("unexpected number of titles", 7, titles.size());
    }
    
    @Test
    public void testSQLResultMapping() {
    	log.info("*** testSQLResultMapping ***");    	
    	@SuppressWarnings("unchecked")
		List<Movie> movies = em.createNativeQuery(
    			"select m.* from queryex_movie m " +
    			"join queryex_director dir on dir.person_id = m.director_id " +
    			"join queryex_person p on p.id = dir.person_id " +
    			"where p.first_name = 'Ron'" +
    			"order by title ASC", Movie.class).getResultList();
    	log.debug("result=" + movies);
    	for (Movie movie: movies) {
    		log.debug("em.contains(" + movie + ")=" + em.contains(movie));
    		assertTrue(movie + " not managed", em.contains(movie));
    	}
    	assertEquals("unexpected number of movies", 2, movies.size());
    }
}