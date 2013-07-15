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
import ejava.jpa.examples.tuning.MovieFactory.SQLStatement;
import ejava.jpa.examples.tuning.bo.MovieRating;
import ejava.jpa.examples.tuning.suites.FullTableScanTest;

@AxisRange(min=FullTableScanTest.AXIS_MIN, max=FullTableScanTest.AXIS_MAX)
@TestLabel(label="Table Access")
public class FullTableScan extends TestBase {
	private static int MAX_ROWS=FullTableScanTest.MAX_ROWS;
	

	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		MovieFactory mf = new MovieFactory().setEntityManager(em);
		SQLConstruct[] constructs = new SQLConstruct[]{
				mf.MOVIE_RATING_IDX,
				mf.MOVIE_RATING_LOWER_IDX
		};
		mf.executeSQL(constructs).assertConstructs(constructs).flush();
		em.close();
	}
	
	@TestLabel(label="Unrestricted Scan")
	@Test
	public void unrestrictedScan() {
		assertEquals(MAX_ROWS,getDAO().getMovies(0,MAX_ROWS).size());
	}
	
	@TestLabel(label="Unindexed Function Access")
	@Test
	public void unindexedFunctionAccess() {
		assertEquals(MAX_ROWS,getDAO().getMoviesByRatingUpperFunction(MovieRating.R, 0,MAX_ROWS).size());
	}
	
	@TestLabel(label="Indexed Value Access")
	@Test
	public void valueAccess() {
		assertEquals(MAX_ROWS,getDAO().getMoviesByRatingValue(MovieRating.R, 0,MAX_ROWS).size());
	}

	@TestLabel(label="Indexed Function Access")
	@Test
	public void indexedFunctionAccess() {
		assertEquals(MAX_ROWS,getDAO().getMoviesByRatingLowerFunction(MovieRating.R, 0,MAX_ROWS).size());
	}
}
