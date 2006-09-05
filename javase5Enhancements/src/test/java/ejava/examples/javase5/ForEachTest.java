package ejava.examples.javase5;

import java.util.ArrayList;
import java.util.Collection;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class ForEachTest extends TestCase {
    private static final Log log = LogFactory.getLog(ForEachTest.class); 
    
    /**
     * legacy way
     */
    public void testIteratorCollection() {
        log.info("testForEachCollection");
        
        Collection<String> collection = new ArrayList<String>();
        collection.add(new String("1"));
        collection.add(new String("2"));
        collection.add(new String("3"));
        
        int i=0;
        for(Iterator<String> itr = collection.iterator(); itr.hasNext(); ) {
            log.info(itr.next());
            i++;
        }
        assertTrue("unexpected count:" + i, i==collection.size());        
    }

    /**
     * java SE 5 way
     *
     */
    public void testForLoopCollection() {
        log.info("testForEachCollection");
        
        Collection<String> collection = new ArrayList<String>();
        collection.add(new String("1"));
        collection.add(new String("2"));
        collection.add(new String("3"));
        
        int i=0;
        for(String s: collection) {
            log.info(s);
            i++;
        }
        assertTrue("unexpected count:" + i, i==collection.size());        
    }

    /**
     * legacy way
     *
     */
    public void testIteratorArray() {
        log.info("testIteratorArray");
        
        String[] array = new String[3];
        array[0] = new String("1");
        array[1] = new String("2");
        array[2] = new String("3");
        
        int i=0;
        for(i=0; i<array.length; i++) {
            log.info(array[i]);
        }
        assertTrue("unexpected count:" + i, i==array.length);        
    }
    
    /**
     * java SE 5 way
     *
     */
    public void testForLoopArray() {
        log.info("testForEachArray");
        
        String[] array = new String[3];
        array[0] = new String("1");
        array[1] = new String("2");
        array[2] = new String("3");
        
        int i=0;
        for(String s: array) {
            log.info(s);
            i++;
        }
        assertTrue("unexpected count:" + i, i==array.length);        
    }

    
    public static Test suite() {
        return new TestSuite(ForEachTest.class);
    }

}
