package ejava.jpa.examples.tuning;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MovieFactory {
	private static final Log log = LogFactory.getLog(MovieFactory.class);
	private EntityManager em;

	public MovieFactory setEntityManager(EntityManager em) {
		this.em = em;
		return this;
	}

	private class SQLStatement {
		final SQLConstruct sql;
		final boolean required;
		public SQLStatement(SQLConstruct sql, boolean required) {
			this.sql = sql;
			this.required = required;
		}
	}
	
	protected abstract class SQLConstruct {
		protected String name;
		protected String create;
		protected String drop;
		protected boolean required;
		public SQLConstruct(String name)             { this.name = name; }
		public SQLConstruct setCreate(String create) { this.create = create; return this; }
		public SQLConstruct setDrop(String drop)     { this.drop = drop; return this; }
		public SQLConstruct setRequired(boolean required) { this.required = required; return this; }
		public String getName()     { return name; }
		public String getCreate()   { return create; }
		public String getDrop()     { return drop; }
		public boolean isRequired() { return required; }
		public abstract boolean exists();
	}
	protected class SQLIndex extends SQLConstruct {
		public SQLIndex(String name, String create) { 
			super(name);
			setCreate(create);
			setDrop("drop index " + name);
		}
		public boolean exists() {
			return ((Number)em.createNativeQuery(
					"select count(*) from user_indexes where index_name=?1")
					.setParameter(1, name.toUpperCase())
			        .getSingleResult()).intValue()==1;
		}
	}
	
	private SQLIndex MOVIE_DIRECTOR_FKX = new SQLIndex("movie_director_fkx", "create index movie_director_fkx on jpatune_movie(director_id)");
	private SQLIndex GENRE_MOVIE_FKX = new SQLIndex("genre_movie_fkx", "create index genre_movie_fkx on jpatune_moviegenre(movie_id)");
	private SQLIndex MOVIEROLE_ACTOR_FKX = new SQLIndex("movierole_actor_fkx", "create index movierole_actor_fkx on jpatune_movierole(actor_id)");
	private SQLIndex MOVIEROLE_MOVIE_FKX = new SQLIndex("movierole_movie_fkx", "create index movierole_movie_fkx on jpatune_movierole(movie_id)");
	
	
	public void populate() {
	}

	public void cleanup() {
		dropFKIndexes();
	}

	protected void executeSQL(SQLStatement sql[], boolean drop) {
		for (SQLStatement s: sql) {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			try {
				boolean exists = s.sql.exists();
				log.debug(drop ? s.sql.getDrop() : s.sql.getCreate());
				if (!drop && !exists) {
					em.createNativeQuery(s.sql.getCreate()).executeUpdate();
				} else if (drop && exists) {
					em.createNativeQuery(s.sql.getDrop()).executeUpdate();
				}
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

	public void dropFKIndexes() {
		SQLStatement sql[] = new SQLStatement[]{
			new SQLStatement(MOVIE_DIRECTOR_FKX, false),
			new SQLStatement(GENRE_MOVIE_FKX, false),
			new SQLStatement(MOVIEROLE_ACTOR_FKX, false),
			new SQLStatement(MOVIEROLE_MOVIE_FKX, false)
		};
		executeSQL(sql, true);
	}
	
	public void createFKIndexes() {
		SQLStatement sql[] = new SQLStatement[]{
				new SQLStatement(MOVIE_DIRECTOR_FKX, true), 
				new SQLStatement(GENRE_MOVIE_FKX, true),
				new SQLStatement(MOVIEROLE_ACTOR_FKX, true),
				new SQLStatement(MOVIEROLE_MOVIE_FKX, true)
			};
			executeSQL(sql, false);
	}
}
