package ejava.jpa.examples.tuning.benchmarks;

import static org.junit.Assert.*;

import javax.persistence.Persistence;


import org.junit.Test;

import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.suites.IndexTest;

@TestLabel(label="Table Access")
public class ByIndex extends TestBase {
	private static int MAX_ROWS=IndexTest.MAX_ROWS;
	static { //add a new mapping to the persistence unit
		emf.close(); emf=null;
	    PERSISTENCE_UNIT = "movietune-test-utitle";
        getDAO();
	}
	
	/**
	 * This will demonstrate how the unique-ness and non-null aspects of an index impacts
	 * a query plan.
	 */
	@TestLabel(label="Values By Index")
	@Test
	public void valuesByIndex() {
		assertEquals(1,getDAO().getMoviesEqualsTitle("Tremors(m836199)", null, null).size());
	}

	@TestLabel(label="Values By Index (limit=1)")
	@Test
	public void valuesByIndexMaxOne() {
		assertEquals(1,getDAO().getMoviesEqualsTitle("Tremors(m836199)", null, 1).size());
	}
}