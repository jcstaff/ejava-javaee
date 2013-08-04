package ejava.jpa.examples.tuning.benchmarks;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.bo.Person;
import ejava.jpa.examples.tuning.dao.MovieDAOImpl;

/**
 * This test will perform nested queries that can either be expressed as subqueries
 * or repeated loops in the DAO.
 */
public class LoopQuery extends TestBase {
	protected static Person kevinBacon;
	protected MovieDAOImpl dao;
	
	@BeforeClass 
	public static void setUpClass() {
		kevinBacon = getDAO().getKevinBacon();
	}
	
	@Before
	public void setUp() {
		dao=getDAO();
	}
	
	@TestLabel(label="Get Movies")
	//@Test
	public void getMovies() {
//		log.info("" + dao.getMoviesForPerson(kevinBacon, null, null).size() + " movies found");
	}

	@TestLabel(label="DB Loop")
	@Test
	public void dbLoop() {
		log.info("" + dao.stepsFromPerson(kevinBacon, 2, null, null).size() + " people found");
	}

}