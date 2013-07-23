package ejava.jpa.examples.tuning.suites;

import org.junit.Ignore;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;

import ejava.jpa.examples.tuning.SuiteBase;
import ejava.jpa.examples.tuning.env.AllFKIndex;
import ejava.jpa.examples.tuning.env.NoFKIndex;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	NoFKIndex.class,
	AllFKIndex.class
})
@Ignore
public class ForeignKeyIndexTest extends SuiteBase {
	public static final int AXIS_MIN=0;
	public static final int AXIS_MAX=6;
}
