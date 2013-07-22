package ejava.jpa.examples.tuning.benchmarks;

import static org.junit.Assert.*;

import org.junit.Test;
import com.carrotsearch.junitbenchmarks.annotation.AxisRange;
import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.bo.MovieRating;

/**
 * This set of tests demonstrate how having no index, single column indexes,
 * and composite indexes can impact the query for data for a row.
 */
@AxisRange(min=1, max=1.5)
@TestLabel(label="Table Row Access")
public class ValueQuery extends TestBase {
	private static int MAX_ROWS=2000;
	
	/**
	 * This test demonstrates getting a value based on a criteria term
	 */
	@TestLabel(label="Query for Value")
	@Test
	public void queryForValue() {
		assertEquals(2,getDAO().getRatingsByTitle("Tremors", 0, MAX_ROWS, null).size());
	}

	/**
	 * This test demonstrates getting many rows for a column back from a criteria.
	 */
	@TestLabel(label="Query for Values")
	@Test
	public void queryForValues() {
		assertEquals(MAX_ROWS,getDAO().getRatingsLikeTitle("A%", 0, MAX_ROWS, null).size());
	}
}
