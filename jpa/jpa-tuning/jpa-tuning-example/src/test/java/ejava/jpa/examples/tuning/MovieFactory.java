package ejava.jpa.examples.tuning;

import javax.persistence.EntityManager;

public class MovieFactory {
	private EntityManager em;
	
	public MovieFactory setEntityManager(EntityManager em) {
		this.em = em;
		return this;
	}
	
	public void populate() {
	}
}
