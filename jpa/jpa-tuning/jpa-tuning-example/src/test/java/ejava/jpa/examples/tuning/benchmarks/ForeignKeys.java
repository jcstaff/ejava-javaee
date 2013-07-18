package ejava.jpa.examples.tuning.benchmarks;

import org.junit.Test;

import com.carrotsearch.junitbenchmarks.annotation.AxisRange;

import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.bo.Person;
import ejava.jpa.examples.tuning.suites.ForeignKeyIndexTest;

@AxisRange(min=ForeignKeyIndexTest.AXIS_MIN, max=ForeignKeyIndexTest.AXIS_MAX)
public class ForeignKeys extends TestBase {
	protected static Person kevinBacon;

	@TestLabel(label = "Single JOIN")
	@Test
	public void getKevin() {
		log.info("*** getKevin() ***");
		getDAO().getKevinBacon();
	}

	@TestLabel(label = "Multiple JOINs")
	@Test
	public void kevin1Step() {
		log.info("*** kevin1Step ***");
		getDAO().oneStepFromPerson(kevinBacon, null, null);
	}

}