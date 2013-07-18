package ejava.jpa.examples.tuning.benchmarks;

import static org.junit.Assert.*;


import javax.persistence.EntityManager;

import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.annotation.AxisRange;

import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.MovieFactory.SQLConstruct;
import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.suites.FullTableScanTest;

/**
 * This set of tests demonstrate how the lack/presence of an index or the invalidation
 * of an index can impact the query time for a specific row.
 */
@AxisRange(min=FullTableScanTest.ROW_AXIS_MIN, max=FullTableScanTest.ROW_AXIS_MAX)
@TestLabel(label="Table Row Access")
public class FullTableScanForRow extends TestBase {
	private static int MAX_ROWS=3;
	

	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		MovieFactory mf = new MovieFactory().setEntityManager(em);
		SQLConstruct[] constructs = new SQLConstruct[]{
				mf.MOVIE_TITLE_IDX
		};
		mf.executeSQL(constructs).assertConstructs(constructs).flush();
		em.close();
	}
	
	/**
	 * This test demonstrates how a trailing wildcard does not invalidate an index
	 */
	@TestLabel(label="Ending Wildcard")
	@Test
	public void endingWildcard() {
		assertEquals(1,getDAO().getMoviesLikeTitle("Natural Disasters: Forces of Nature%", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("Mystic River: From Page to Scree%", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("Seventeen: The Faces for Fal%", 0, MAX_ROWS).size());
	}

	/**
	 * This test demonstrates how a leading wildcard invalidates an index
	 */
	@TestLabel(label="Leading Wildcard")
	@Test
	public void leadingWildcard() {
		assertEquals(1,getDAO().getMoviesLikeTitle("%atural Disasters: Forces of Nature", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("%ystic River: From Page to Screen", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("%eventeen: The Faces for Fall", 0, MAX_ROWS).size());
	}

	/**
	 * The results of this and the next test demonstrate the lack of impact in using like
	 * for exact match strings.
	 */
	@TestLabel(label="Exact Like")
	@Test
	public void exactLike() {
		assertEquals(1,getDAO().getMoviesLikeTitle("Natural Disasters: Forces of Nature", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("Mystic River: From Page to Screen", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("Seventeen: The Faces for Fall", 0, MAX_ROWS).size());
	}

	/*
	 * The results of this and the previous test demonstrate the lack of impact in using like
	 * for exact match strings.
	 */
	@TestLabel(label="Exact Equals")
	@Test
	public void exactEquals() {
		assertEquals(1,getDAO().getMoviesEqualsTitle("Natural Disasters: Forces of Nature", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesEqualsTitle("Mystic River: From Page to Screen", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesEqualsTitle("Seventeen: The Faces for Fall", 0, MAX_ROWS).size());
	}
}
