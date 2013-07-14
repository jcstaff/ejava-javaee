package ejava.jpa.examples.tuning.dao;

import java.util.List;


import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ejava.jpa.examples.tuning.bo.Person;

public class MovieDAOImpl {
	private EntityManager em;
	
	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	protected <T> TypedQuery<T> withPaging(TypedQuery<T> query, Integer offset, Integer limit) {
    	if (offset != null && offset > 0) {
    		query.setFirstResult(offset);
    	}
    	if (limit != null && limit > 0) {
    		query.setMaxResults(limit);
    	}
    	return query;
	}

	/**
	 * Returns a Person instance for the Kevin Bacon actor who
	 * played in Tremors.
	 * @return
	 */
	public Person getKevinBacon() {
    	return em.createQuery(
    			"select r.actor.person " +
    			"from MovieRole r " +
    			"where r.movie.title = 'Tremors' and " +
    			"r.actor.person.lastName='Bacon' and " +
    			"r.actor.person.firstName='Kevin'", Person.class)
    			.getSingleResult();
	}

	//find people who are 1 step from Kevin Bacon
    public List<Person> oneStepFromPerson(Person p, Integer offset, Integer limit) {
    	return withPaging(em.createQuery(
			"select a.person from Actor a " +
			"join a.roles ar " +
			"join a.person ap " +
			"where ar.movie in (select m from Movie m " +
			    "inner join m.cast mr " +
		        "inner join mr.actor ma " +
		        "inner join ma.person mp " +
			    "where mp.id = :id))" +
			 "and ap.id not = :id", Person.class)
			 .setParameter("id", p.getId()), offset, limit)
			.getResultList();
    }
}
