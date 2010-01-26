package ejava.projects.esales.ejb;

import info.ejava.esales._1_0_2007.Account;
import info.ejava.esales._1_0_2007.Address;
import info.ejava.esales._1_0_2007.Auction;
import info.ejava.esales._1_0_2007.Bid;
import info.ejava.esales._1_0_2007.ESales;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.projects.esales.xml.ESalesParser;

@Stateless
public class ParserTestEJB implements ParserTestRemote {
	@Resource(name="vals/xmlFile")
	private static String xmlFile;
	
	private static final Log log = LogFactory.getLog(ParserTestEJB.class);
	
	@PostConstruct
	public void init() {
		log.debug("*** ParserTestEJB ***");
		log.debug("xmlFile=" + xmlFile);		
	}

	public void ingest() throws Exception {
		log.info("ingest");
		
		InputStream is = null;
		
		try {
			log.trace("getting input file:" + xmlFile);
			is = this.getClass().getResourceAsStream(xmlFile);
			if (is == null) {
				throw new Exception(xmlFile + " was not found");
			}
			
			log.trace("creating parser");
			ESalesParser parser = new ESalesParser(ESales.class, is);
			
			log.trace("starting parse loop");
			Object object=null;
			do {
		        object = parser.getObject(
		                    "address", "account", "auction", "bid");
		        if (object instanceof Address) {
		            log.debug("found address");
		        }
		        else if (object instanceof Account) {
		            log.debug("found Account");
		        }
		        else if (object instanceof Auction) {
		        	log.debug("found Auction");
		        }
		        else if (object instanceof Bid) {
		        	log.debug("found Bid");
		        }
		        else if (object != null) {
		            fail("object of unknown type:" + object);
		        }
			} while (object != null);
		}
		catch (Throwable ex) {
			log.error("error parsing doc",ex);
			throw new EJBException("error parsing doc:" + ex);
		}
		finally {
			if (is != null) is.close();
		}
	}

}
