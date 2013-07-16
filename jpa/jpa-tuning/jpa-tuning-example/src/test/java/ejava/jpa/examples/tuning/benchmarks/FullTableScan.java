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
				mf.MOVIE_RATING_LOWER_IDX,
				mf.MOVIE_TITLE_IDX
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

	@TestLabel(label="Ending Wildcard")
	@Test
	public void endingWildcard() {
		assertEquals(1,getDAO().getMoviesLikeTitle("Natural Disasters: Forces of Nature%", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("Mystic River: From Page to Scree%", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("Seventeen: The Faces for Fal%", 0, MAX_ROWS).size());
	}

	@TestLabel(label="Leading Wildcard")
	@Test
	public void leadingWildcard() {
		assertEquals(1,getDAO().getMoviesLikeTitle("%atural Disasters: Forces of Nature", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("%ystic River: From Page to Screen", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("%eventeen: The Faces for Fall", 0, MAX_ROWS).size());
	}

	@TestLabel(label="Exact Like")
	@Test
	public void exactLike() {
		assertEquals(1,getDAO().getMoviesLikeTitle("Natural Disasters: Forces of Nature", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("Mystic River: From Page to Screen", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesLikeTitle("Seventeen: The Faces for Fall", 0, MAX_ROWS).size());
	}

	@TestLabel(label="Exact Equals")
	@Test
	public void exactEquals() {
		assertEquals(1,getDAO().getMoviesEqualsTitle("Natural Disasters: Forces of Nature", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesEqualsTitle("Mystic River: From Page to Screen", 0, MAX_ROWS).size());
		assertEquals(1,getDAO().getMoviesEqualsTitle("Seventeen: The Faces for Fall", 0, MAX_ROWS).size());
	}
}
