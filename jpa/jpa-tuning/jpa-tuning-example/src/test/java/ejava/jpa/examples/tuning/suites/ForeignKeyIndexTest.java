package ejava.jpa.examples.tuning.suites;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;

import ejava.jpa.examples.tuning.benchmarks.AllFKIndex;
import ejava.jpa.examples.tuning.benchmarks.NoFKIndex;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	NoFKIndex.class,
	AllFKIndex.class
})
@BenchmarkMethodChart(filePrefix="target/ForeignKeyIndexTest")
public class ForeignKeyIndexTest {
	
	@BeforeClass
	public static void setUpClass() {
		System.setProperty("jub.consumers", "CONSOLE,H2");
		System.setProperty("jub.db.file", "target/benchmarks");
	}

	@AfterClass
	public static void tearDownClass() {
	}
}
