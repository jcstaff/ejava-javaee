package ejava.jpa.examples.tuning.benchmarks;

import javax.persistence.EntityManager;

import org.junit.BeforeClass;
import org.junit.Test;

import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.bo.Person;

public class NoFKIndex extends TestBase {
	private static Person kevinBacon;
	
	@BeforeClass
	public static void setUpClass() {
		EntityManager em=getEMF().createEntityManager();
		kevinBacon = getDAO().getKevinBacon();
		em.close();
	}
	
	@Test
	public void kevin1Step() {
		log.info("*** kevin1Step ***");
		getDAO().oneStepFromPerson(kevinBacon, null, null);
	}

}
