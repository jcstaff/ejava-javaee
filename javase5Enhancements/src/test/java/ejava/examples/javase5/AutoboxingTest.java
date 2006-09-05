package ejava.examples.javase5;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AutoboxingTest extends TestCase {
    private static final Log log = LogFactory.getLog(AutoboxingTest.class);
    
    
    private Integer passInteger(Integer i) {
        log.info("received Integer=" + i);
        return i;
    }
    private Long passLong(Long i) {
        log.info("received Long=" + i);
        return i;
    }
    
    public void testAutobox() {
        log.info("testAutoBox");
        
        //parameter values being manually wrapped
        //return values being manually unwrapped
        int intWrap = passInteger(new Integer(1)).intValue();
        long longWrap = passLong(new Long(1)).longValue();
        
        //parameter values being automatically wrapped ("auto-boxing")
        //return values being automatically unwrapped ("auto-unboxing")
        int intBox = passInteger(1);
        long longBox = passLong(1L);
        
        assertEquals(intWrap, intBox);
        assertEquals(longWrap, longBox);
    }
    
    public static Test suite() {
        return new TestSuite(AutoboxingTest.class);
    }

}
