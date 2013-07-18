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
	 * This test demonstrates how a trailing wildcard does not invalidate an index
	 */
	@TestLabel(label="Query for Value")
	@Test
	public void queryForValue() {
		assertEquals(MAX_ROWS,getDAO().getTitlesByRating(MovieRating.R, 0, MAX_ROWS).size());
	}
}
