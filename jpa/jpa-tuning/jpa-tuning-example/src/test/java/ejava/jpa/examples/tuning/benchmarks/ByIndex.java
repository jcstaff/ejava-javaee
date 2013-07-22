package ejava.jpa.examples.tuning.benchmarks;

import static org.junit.Assert.*;


import org.junit.Test;

import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.suites.IndexTest;

@TestLabel(label="Table Access")
public class ByIndex extends TestBase {
	private static int MAX_ROWS=IndexTest.MAX_ROWS;
	static { //add a new mapping to the persistence unit
	    TestBase.PERSISTENCE_UNIT = "movietune-test-utitle";
	}
	
	/**
	 * This will demonstrate how the unique-ness and non-null aspects of an index impacts
	 * a query plan.
	 */
	@TestLabel(label="Values By Index")
	@Test
	public void valuesByIndex() {
		assertEquals(MAX_ROWS,getDAO().getMoviesLikeTitle("A%", 0, MAX_ROWS, null).size());
	}
	
	/**
	 * This will demonstrate how the unique-ness and non-null aspects of an index impacts
	 * a query plan when order-by is added.
	 */
	@TestLabel(label="Values By Index Ordered")
	@Test
	public void valuesByIndexOrdered() {
		assertEquals(MAX_ROWS,getDAO().getMoviesLikeTitle("A%", 0, MAX_ROWS, "title ASC").size());
	}
}