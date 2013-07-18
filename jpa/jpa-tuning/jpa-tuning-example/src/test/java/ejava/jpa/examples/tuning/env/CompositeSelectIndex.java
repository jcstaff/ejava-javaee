package ejava.jpa.examples.tuning.env;

import javax.persistence.EntityManager;

import org.junit.BeforeClass;

import ejava.jpa.examples.tuning.MovieFactory;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.MovieFactory.SQLConstruct;
import ejava.jpa.examples.tuning.benchmarks.ValueQuery;

/**
 * This test environment sets up the queries with a composite index on the 
 * where and select columns.
 */
@TestLabel(label="Composite Select Index")
public class CompositeSelectIndex extends ValueQuery {
	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		MovieFactory mf = new MovieFactory().setEntityManager(em);
		SQLConstruct[] constructs = new SQLConstruct[]{
				mf.MOVIE_RATING_TITLE_IDX
		};
		mf.executeSQL(constructs).assertConstructs(constructs).flush();
		em.close();
	}

}
