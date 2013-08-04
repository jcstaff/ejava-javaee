package ejava.jpa.examples.tuning.benchmarks;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.bo.Person;
import ejava.jpa.examples.tuning.dao.MovieDAOImpl;

/**
 * This test will perform nested queries that can either be expressed as subqueries
 * or repeated loops in the DAO.
 */
public class LoopQuery extends TestBase {
	protected static Person kevinBacon;
	protected MovieDAOImpl dao;
	
	@BeforeClass 
	public static void setUpClass() {
		kevinBacon = getDAO().getKevinBacon();
	}
	
	@Before
	public void setUp() {
		dao=getDAO();
	}
	
	@TestLabel(label="DAO Loop")
	@Test
	public void daoLoop() {
		log.info("" + dao.oneStepFromPersonByDAO(kevinBacon).size() + " people found");
	}
	
	@TestLabel(label="DB Loop Distinct")
	@Test
	public void dbLoopDistinct() {
		log.info("" + dao.oneStepFromPersonByDB(kevinBacon).size() + " people found");
	}

	/*
	private static int PAGE_SIZE=50;
	
	@TestLabel(label="DAO Page 1")
	@Test
	public void daoPage1() {
		final int offset=0*PAGE_SIZE;
		assertEquals(PAGE_SIZE,dao.oneStepFromPersonByDAO(kevinBacon, offset, PAGE_SIZE, "role.actor.person.lastName ASC").size());
	}
	@TestLabel(label="DB Page 1")
	@Test
	public void dbPage1() {
		final int offset=0*PAGE_SIZE;
		assertEquals(PAGE_SIZE,dao.oneStepFromPersonByDB(kevinBacon, offset, PAGE_SIZE, "role.actor.person.lastName ASC").size());
	}
	
	@TestLabel(label="DAO Page 3")
	@Test
	public void daoPage3() {
		final int offset=3*PAGE_SIZE;
		assertEquals(PAGE_SIZE,dao.oneStepFromPersonByDAO(kevinBacon, offset, PAGE_SIZE, "role.actor.person.lastName ASC").size());
	}
	@TestLabel(label="DB Page 3")
	@Test
	public void dbPage3() {
		final int offset=3*PAGE_SIZE;
		assertEquals(PAGE_SIZE,dao.oneStepFromPersonByDB(kevinBacon, offset, PAGE_SIZE, "role.actor.person.lastName ASC").size());
	}
	
	@TestLabel(label="DAO Page 5")
	@Test
	public void daoPage5() {
		final int offset=5*PAGE_SIZE;
		assertEquals(PAGE_SIZE,dao.oneStepFromPersonByDAO(kevinBacon, offset, PAGE_SIZE, "role.actor.person.lastName ASC").size());
	}
	@TestLabel(label="DB Page 5")
	@Test
	public void dbPage5() {
		final int offset=5*PAGE_SIZE;
		assertEquals(PAGE_SIZE,dao.oneStepFromPersonByDB(kevinBacon, offset, PAGE_SIZE, "role.actor.person.lastName ASC").size());
	}
	*/
	
}