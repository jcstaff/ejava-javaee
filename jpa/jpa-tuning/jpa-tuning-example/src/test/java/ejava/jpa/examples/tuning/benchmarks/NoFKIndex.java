package ejava.jpa.examples.tuning.benchmarks;

import javax.persistence.EntityManager;


import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.junitbenchmarks.annotation.AxisRange;

import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.bo.Person;
import ejava.jpa.examples.tuning.suites.ForeignKeyIndexTest;

@AxisRange(min=ForeignKeyIndexTest.AXIS_MIN, max=ForeignKeyIndexTest.AXIS_MAX)
@TestLabel(label="No FKs")
public class NoFKIndex extends TestBase {
	private static Person kevinBacon;
	
	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		kevinBacon = getDAO().getKevinBacon();
		new MovieFactory().setEntityManager(em).dropIndexes().flush();
		em.close();
	}
	
	@TestLabel(label="Single JOIN")
	@Test public void getKevin() {
		log.info("*** getKevin() ***");
		getDAO().getKevinBacon();
	}
	
	@TestLabel(label="Multiple JOINs")
	@Test
	public void kevin1Step() {
		log.info("*** kevin1Step ***");
		getDAO().oneStepFromPerson(kevinBacon, null, null);
	}

}
