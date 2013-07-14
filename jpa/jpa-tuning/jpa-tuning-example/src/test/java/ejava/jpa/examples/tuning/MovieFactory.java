package ejava.jpa.examples.tuning;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MovieFactory {
	private static final Log log = LogFactory.getLog(MovieFactory.class);
	private EntityManager em;
	
	private class SQLStatement {
		final String sql;
		final boolean required;
		public SQLStatement(String sql, boolean required) {
			this.sql = sql;
			this.required = required;
		}
	}
	
	public MovieFactory setEntityManager(EntityManager em) {
		this.em = em;
		return this;
	}
	
	public void populate() {
	}

	public void cleanup() {
		dropFKIndexes();
	}

	public void dropFKIndexes() {
		SQLStatement sql[] = new SQLStatement[]{
			new SQLStatement("drop index actor_person_fkx", false),
			new SQLStatement("drop index director_person_fkx", false),
			new SQLStatement("drop index movie_director_fkx", false),
			new SQLStatement("drop index genre_movie_fkx", false),
			new SQLStatement("drop index movierole_actor_fkx", false),
			new SQLStatement("drop index movierole_movie_fkx", false)
		};
		executeSQL(sql);
	}
	
	protected void executeSQL(SQLStatement sql[]) {
		for (SQLStatement s: sql) {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			try {
			    em.createNativeQuery(s.sql).executeUpdate();
			} catch (Exception ex) {
				if (s.required) {
					log.error("failed:" + s.sql, ex);
					throw new RuntimeException("failed:" + s.sql, ex);
				}
			}
			if (!tx.getRollbackOnly()) { tx.commit(); }
			else { tx.rollback(); }
		}
	}
}
