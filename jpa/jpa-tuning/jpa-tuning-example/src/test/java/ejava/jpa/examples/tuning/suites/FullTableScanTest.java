package ejava.jpa.examples.tuning.suites;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;

import ejava.jpa.examples.tuning.SuiteBase;
import ejava.jpa.examples.tuning.benchmarks.FullTableScan;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	FullTableScan.class,
})
public class FullTableScanTest extends SuiteBase {
	public static final int AXIS_MIN=0;
	public static final int AXIS_MAX=10;
	public static final int MAX_ROWS=7000;
}
