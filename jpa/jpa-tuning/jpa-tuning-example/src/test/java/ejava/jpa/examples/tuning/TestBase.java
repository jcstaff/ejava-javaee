package ejava.jpa.examples.tuning;

import org.junit.Rule;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;

public class TestBase {
	@Rule
	public BenchmarkRule benchmarkRun = new BenchmarkRule();

}
