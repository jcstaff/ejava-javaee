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
import ejava.jpa.examples.tuning.bo.MovieRating;
import ejava.jpa.examples.tuning.suites.FullTableScanTest;

@AxisRange(min=FullTableScanTest.ROWS_AXIS_MIN, max=FullTableScanTest.ROWS_AXIS_MAX)
@TestLabel(label="Table Access")
public class FullTableScanForRows extends TestBase {
	private static int MAX_ROWS=FullTableScanTest.MAX_ROWS;
	

	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		MovieFactory mf = new MovieFactory().setEntityManager(em);
		SQLConstruct[] constructs = new SQLConstruct[]{
				mf.MOVIE_RATING_IDX,
				mf.MOVIE_RATING_LOWER_IDX,
		};
		mf.executeSQL(constructs).assertConstructs(constructs).flush();
		em.close();
	}

	/**
	 * This test shows the speed at which a full table scan can work when there
	 * are no constraints on the rows returned.
	 */
	@TestLabel(label="Unrestricted Scan")
	@Test
	public void unrestrictedScan() {
		assertEquals(MAX_ROWS,getDAO().getMovies(0,MAX_ROWS).size());
	}
	
	/**
	 * This test demonstrates the speed at which a page of rows are returned when the
	 * rows are selected by a criteria on an indexed column. 
	 */
	@TestLabel(label="Indexed Value Access")
	@Test
	public void valueAccess() {
		assertEquals(MAX_ROWS,getDAO().getMoviesByRatingValue(MovieRating.R, 0,MAX_ROWS).size());
	}

	/**
	 * This test demonstrates the impact of calling a function on row data within 
	 * a query -- invalidating any data index created on that column.
	 */
	@TestLabel(label="Unindexed Function Access")
	@Test
	public void unindexedFunctionAccess() {
		assertEquals(MAX_ROWS,getDAO().getMoviesByRatingUpperFunction(MovieRating.R, 0,MAX_ROWS).size());
	}
	
	/**
	 * This test demonstrates the impact of adding a function index on a column
	 * that matches the function used on row data within the query
	 */
	@TestLabel(label="Indexed Function Access")
	@Test
	public void indexedFunctionAccess() {
		assertEquals(MAX_ROWS,getDAO().getMoviesByRatingLowerFunction(MovieRating.R, 0,MAX_ROWS).size());
	}

}
