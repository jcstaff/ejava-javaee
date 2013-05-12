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
    
    private static class MovieRelease {
    	public final String title;
    	public final Date releaseDate;
    	public MovieRelease(String title, Date releaseDate) {
    		this.title = title;
    		this.releaseDate = releaseDate;
    	}
    }
    
    @Test @Ignore
    public void testResultClass() {
    	log.info("*** testSQLQuery ***");    	
    	@SuppressWarnings("unchecked")
		List<MovieRelease> results = em.createNativeQuery(
    			"select title, release_date " +
    			"from queryex_movie " +
    			"order by title", MovieRelease.class).getResultList();
    	for (MovieRelease movie : results) {
    		log.debug(String.format("%s (%s)", movie.title, movie.releaseDate));
    	}
    	assertEquals("unexpected number of results", 7, results.size());
    }
}