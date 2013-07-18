package ejava.jpa.examples.tuning;

import javax.persistence.EntityManager;

import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;

public class MovieFactory {
	private static final Log log = LogFactory.getLog(MovieFactory.class);
	private EntityManager em;

	public MovieFactory setEntityManager(EntityManager em) {
		this.em = em;
		return this;
	}

	public static class SQLStatement {
		final SQLConstruct sql;
		final boolean required;
		public SQLStatement(SQLConstruct sql, boolean required) {
			this.sql = sql;
			this.required = required;
		}
	}
	
	public abstract class SQLConstruct {
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
	public class SQLIndex extends SQLConstruct {
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
	
	public SQLIndex MOVIE_DIRECTOR_FKX = new SQLIndex("movie_director_fkx", "create index movie_director_fkx on jpatune_movie(director_id)");
	public SQLIndex MOVIE_RATING_IDX = new SQLIndex("movie_rating_idx", "create index movie_rating_idx on jpatune_movie(rating)");
	public SQLIndex MOVIE_RATING_LOWER_IDX = new SQLIndex("movie_rating_lower_idx", "create index movie_rating_lower_idx on jpatune_movie(lower(rating))");
	public SQLIndex MOVIE_TITLE_IDX = new SQLIndex("movie_title_idx", "create index movie_title_idx on jpatune_movie(title)");
	public SQLIndex MOVIE_RATING_TITLE_IDX = new SQLIndex("movie_rating_title_idx", "create index movie_rating_title_idx on jpatune_movie(rating, title)");
	public SQLIndex GENRE_MOVIE_FKX = new SQLIndex("genre_movie_fkx", "create index genre_movie_fkx on jpatune_moviegenre(movie_id)");
	public SQLIndex MOVIEROLE_ACTOR_FKX = new SQLIndex("movierole_actor_fkx", "create index movierole_actor_fkx on jpatune_movierole(actor_id)");
	public SQLIndex MOVIEROLE_MOVIE_FKX = new SQLIndex("movierole_movie_fkx", "create index movierole_movie_fkx on jpatune_movierole(movie_id)");
	
	
	public void populate() {
	}

	public void cleanup() {
		dropIndexes();
	}

	public MovieFactory executeSQL(SQLConstruct[] constructs) {
		SQLStatement[] statements = new SQLStatement[constructs.length];
		for (int i=0; i< constructs.length; i++) {
			statements[i] = new SQLStatement(constructs[i], true);
		}
		executeSQL(statements, false);
		return this;
	}

	public MovieFactory executeSQL(SQLStatement sql[], boolean drop) {
		for (SQLStatement s: sql) {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			try {
				boolean exists = s.sql.exists();
				StringBuilder text = new StringBuilder(drop ? s.sql.getDrop() : s.sql.getCreate());
				if (!drop && !exists) {
					em.createNativeQuery(s.sql.getCreate()).executeUpdate();
					text.append(" (created)");
				} else if (drop && exists) {
					em.createNativeQuery(s.sql.getDrop()).executeUpdate();
					text.append(" (dropped)");
				} else {
					text.append(" (noop)");
				}
				log.debug(text);
			} catch (Exception ex) {
				if (s.required) {
					log.error("failed:" + s.sql, ex);
					throw new RuntimeException("failed:" + s.sql, ex);
				}
			}
			if (!tx.getRollbackOnly()) { tx.commit(); }
			else { tx.rollback(); }
		}
		return this;
	}

	public MovieFactory executeSQL(String[] sql, boolean failOnError) {
		for (String s: sql) {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			try {
				em.createNativeQuery(s).executeUpdate();
			} catch (Exception ex) {
				if (failOnError) {
					log.error("failed:" + s, ex);
					throw new RuntimeException("failed:" + s, ex);
				}
			}
			if (!tx.getRollbackOnly()) { tx.commit(); }
			else { tx.rollback(); }
		}
		return this;
	}
	
	public MovieFactory assertConstructs(SQLConstruct[] sql) {
		for (SQLConstruct s: sql) {
			Assert.assertTrue(s.getName() + " does not exist", s.exists());
		}
		return this;
	}
	
	
	public MovieFactory dropIndexes() {
		SQLStatement sql[] = new SQLStatement[]{
			new SQLStatement(MOVIE_DIRECTOR_FKX, false),
			new SQLStatement(GENRE_MOVIE_FKX, false),
			new SQLStatement(MOVIEROLE_ACTOR_FKX, false),
			new SQLStatement(MOVIEROLE_MOVIE_FKX, false),
			new SQLStatement(MOVIE_RATING_IDX, false),
			new SQLStatement(MOVIE_RATING_LOWER_IDX, false),
			new SQLStatement(MOVIE_TITLE_IDX, false),
			new SQLStatement(MOVIE_RATING_TITLE_IDX, false)
		};
		executeSQL(sql, true);
		return this;
	}
	
	public MovieFactory createFKIndexes() {
		SQLStatement sql[] = new SQLStatement[]{
				new SQLStatement(MOVIE_DIRECTOR_FKX, true), 
				new SQLStatement(GENRE_MOVIE_FKX, true),
				new SQLStatement(MOVIEROLE_ACTOR_FKX, true),
				new SQLStatement(MOVIEROLE_MOVIE_FKX, true)
			};
			executeSQL(sql, false);
			return this;
	}
	
	public MovieFactory flush() {
		executeSQL(new String[]{
				//"alter system flush shared_pool",
				//"alter system flush buffer_cache"
		}, true);
		return this;
	}

}
