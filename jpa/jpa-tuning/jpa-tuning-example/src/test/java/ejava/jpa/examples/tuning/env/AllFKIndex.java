package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;
import org.junit.BeforeClass;
import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.benchmarks.ForeignKeys;

/**
 * This environment sets up the queries with indexes on foreign keys between tables.
 */
@TestLabel(label="FKs Enabled")
public class AllFKIndex extends ForeignKeys {

	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		kevinBacon = getDAO().getKevinBacon();
		new MovieFactory().setEntityManager(em).createFKIndexes().flush();
		em.close();
	}
}
