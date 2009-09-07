package ejava.projects.edmv.test;


import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.projects.edmv.ejb.ParserTestRemote;

public class ParserServerTest {
	private static final Log log = LogFactory.getLog(ParserServerTest.class);
	private static final String jndiName = System.getProperty("jndi.name");
	
	private static ParserTestRemote parser;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		InitialContext jndi = new InitialContext();
		
		log.trace("looking up name:" + jndiName);
		parser = (ParserTestRemote)jndi.lookup(jndiName);
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIngest() throws Exception {
		log.info("*** testIngest ***");
		parser.ingest();
	}
}
