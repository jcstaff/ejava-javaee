package ejava.projects.eleague.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.projects.eleague.dto.Contest;
import ejava.projects.eleague.dto.Division;
import ejava.projects.eleague.dto.ELeague;
import ejava.projects.eleague.dto.Season;

import junit.framework.TestCase;

public class ELeagueParserTest extends TestCase {
	private static final Log log = LogFactory.getLog(ELeagueParserTest.class);
	private String inputDir = System.getProperty("inputDir");
	
	public void setUp() {
		assertNotNull("inputDir not supplied", inputDir);
	}
	
	public void testParser() throws Exception {
		File inDir = new File(inputDir);
		File[] files = inDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.startsWith("eLeague-") &&
						name.endsWith(".xml"));
			}
		});
		for (File file : files) {
			testParser(file.getCanonicalPath());
		}
	}
	public void testParser(String inputFile) throws Exception {
		log.info("*** testParser:" + inputFile + " ***");
		
		InputStream is = new FileInputStream(inputFile);
		BufferedInputStream bis = new BufferedInputStream(is);
		ELeagueParser parser = new ELeagueParser(ELeague.class, bis);
		Object object=null;
		do {
	        object = parser.getObject(
	                    "contact", "league-metadata", "club", "season");
			if (object != null) { 
				log.debug( dump(object) );
				if (object instanceof Season) {
					checkSeason((Season) object);
				}
			};			
		} while (object != null);
		bis.close();
	}

	int level = 0;
	static final int MAX_LEVELS = 25;
	
	private String dump(Object object) throws Exception {	    
	    level += 1;
		StringBuilder text = new StringBuilder();
		if (object == null) {
		    text.append("null");
		}
		else if (object instanceof Collection && level <= MAX_LEVELS) {
		    text.append("{");
		    for(@SuppressWarnings({ "rawtypes", "unchecked" })
			Iterator<Object> itr=((Collection)object).iterator();
		        itr.hasNext();) {
		        dump(itr.next());
		        if (itr.hasNext()) {
		            text.append(",");
		        }
		    }
            text.append("}");
		}
        else if (object.getClass().getName().startsWith("java.lang")) {
            if (object instanceof Contest) { log.debug("3");}
            text.append(object);    
        }
        else if (object.getClass().getName().startsWith("java.util.Date")) {
            if (object instanceof Contest) { log.debug("4");}

            text.append(object);    
        }
		else if (level <= MAX_LEVELS){		
    		for (Method m : object.getClass().getDeclaredMethods()) {
    			if (m.getName().startsWith("get")) {
    				String propertyName = m.getName().substring("get".length());
    				try {
        				Object value = m.invoke(object, new Object[] {});
        				text.append(object.getClass().getName() + "." +
        						propertyName + "=" +
        						dump(value) + "\n");
    				} catch (IllegalArgumentException notAGetter) {
                        text.append(object.getClass().getName() + "." +
                                propertyName + "=???\n");
    				} catch (IllegalAccessException notAGetter) {
                        text.append(object.getClass().getName() + "." +
                                propertyName + "=???\n");
                    }
    			}
    		}
		}
		level -= 1;
		return text.toString();
	}
	
	public void checkSeason(Season season) {
		for (Division division : season.getDivision()) {
			checkDivision(season, division);
		}
	}
	
	public void checkDivision(Season season, Division division) {
		if ("Spring NeverEnds".equals(season.getName())) {
	        assertNotNull("division contact was null", division.getContact());
		}
	}
}
