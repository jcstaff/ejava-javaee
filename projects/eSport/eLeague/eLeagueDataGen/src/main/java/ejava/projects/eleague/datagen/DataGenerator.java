package ejava.projects.eleague.datagen;

import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.projects.eleague.dao.ELeagueDAO;
import ejava.projects.eleague.dao.JPALeagueDAO;
import ejava.projects.eleague.dto.ELeague;
import ejava.projects.eleague.xml.SampleGen;

public class DataGenerator {
	Log log = LogFactory.getLog(DataGenerator.class);
	private ELeagueDAO dao;
	public static final String OUTPUT_FILE = 
		"ejava.projects.eleague.datagen.outputFile";
	private Marshaller m;
	
	public DataGenerator() throws JAXBException {
		JAXBContext jaxbc = JAXBContext.newInstance(ELeague.class);
		m = jaxbc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	}
	
	public void setELeagueDAO(ELeagueDAO dao) {
		this.dao = dao;
	}
	
	public int generate(Writer writer, int auctionCount) 
	    throws Exception {
		ELeague league = new SampleGen().createLeague();
		
		m.marshal(league, writer);
		
		return 1;
	}
	
	public static DataGenerator createDataGenerator(
			Map<String, String> props) throws JAXBException {		
		EntityManagerFactory emf = props != null ?
			Persistence.createEntityManagerFactory(
					JPALeagueDAO.PERSISTENCE_UNIT, props) :
			Persistence.createEntityManagerFactory(
					JPALeagueDAO.PERSISTENCE_UNIT);
		EntityManager em = emf.createEntityManager();
		
		ELeagueDAO dao = new JPALeagueDAO();
		((JPALeagueDAO)dao).setEntityManager(em);
		
		DataGenerator gen = new DataGenerator();
		gen.setELeagueDAO(dao);
		
		return gen;
	}
	
	public static Map<String, String> getProps(String prefix) {
		Map<String, String> props = new HashMap<String, String>();
		Properties sysProps = System.getProperties();
		for(Iterator itr=sysProps.keySet().iterator(); itr.hasNext();) {
			String key = (String)itr.next();
			if (key.startsWith(prefix + ".")) {
				String name = key.substring(prefix.length()+1);
				String value = sysProps.getProperty(key);
				props.put(name, value);
			}
		}
		return props;
	}	
}
