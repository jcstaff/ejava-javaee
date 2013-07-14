package ejava.jpa.examples.tuning;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.annotation.AxisRange;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkHistoryChart;

import ejava.jpa.examples.tuning.dao.MovieDAOImpl;

@BenchmarkOptions(warmupRounds=1, benchmarkRounds=3)
@BenchmarkHistoryChart
@AxisRange(min=0, max=10)
public class TestBase {
    protected static Log log = LogFactory.getLog(TestBase.class);
    private static final String PERSISTENCE_UNIT = "movietune-test";
    protected static EntityManagerFactory emf;
    private static EntityManager em_;
	private static MovieDAOImpl dao;
	
	public @Rule BenchmarkRule benchmarkRun = new BenchmarkRule();
	
	protected static EntityManagerFactory getEMF() {
		if (emf==null) {
	        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		}
		return emf;
	}
	protected static MovieDAOImpl getDAO() {
		if (dao == null) {
			dao = new MovieDAOImpl();
			em_ = getEMF().createEntityManager();
			dao.setEntityManager(em_);
		}
		return dao;
	}

	@BeforeClass
    public static void setUpBase() {
        EntityManager em = getEMF().createEntityManager();
        cleanup(em);
        em.close();
    }
	
	@AfterClass
	public static void tearDownBase() {
		if (em_ != null) {
			em_.close(); 
			em_ = null;
			dao = null;
		}
	}

	/**
	 * Get back to a known starting point
	 * @param em
	 */
	public static void cleanup(EntityManager em) {
    	new MovieFactory().setEntityManager(em).cleanup();
    }
}
