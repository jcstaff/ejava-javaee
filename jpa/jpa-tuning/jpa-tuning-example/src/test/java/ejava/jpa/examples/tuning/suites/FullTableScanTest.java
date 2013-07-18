package ejava.jpa.examples.tuning.suites;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;

import ejava.jpa.examples.tuning.SuiteBase;
import ejava.jpa.examples.tuning.benchmarks.FullTableScanForRow;
import ejava.jpa.examples.tuning.benchmarks.FullTableScanForRows;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	FullTableScanForRow.class,
	FullTableScanForRows.class
})
public class FullTableScanTest extends SuiteBase {
	public static final double ROW_AXIS_MIN=0;
	public static final double ROW_AXIS_MAX=0.5;
	public static final double ROWS_AXIS_MIN=0;
	public static final double ROWS_AXIS_MAX=3;
	public static final int MAX_ROWS=1000;
}
