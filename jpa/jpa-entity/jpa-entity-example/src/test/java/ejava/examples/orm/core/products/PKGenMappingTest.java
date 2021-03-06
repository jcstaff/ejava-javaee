package ejava.examples.orm.core.products;

import static org.junit.Assert.*;

import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assume;
import org.junit.Test;

import ejava.examples.orm.core.mapped.Drill;
import ejava.examples.orm.core.mapped.EggBeater;
import ejava.examples.orm.core.mapped.Fan;
import ejava.examples.orm.core.mapped.Gadget;

/**
 * This test case provides a demo of using automatically generated primary
 * keys setup using class annotations.
 */
public class PKGenMappingTest extends TestBase {
    private static Log log = LogFactory.getLog(BasicAnnotationTest.class);
    
    /**
     * This test provides a demo of using the AUTO GeneratedType. This value
     * is provided by the Java Persistance provider.
     */
    @Test
    public void testAUTOGood() {
        log.info("testAUTOGood");
        //note that since PKs are generated, we must pass in an object that
        //has not yet been assigned a PK value.
        ejava.examples.orm.core.mapped.Drill drill = new Drill(0);
        drill.setMake("acme");
        
        //insert a row in the database
        em.persist(drill);
        log.info("created drill (before flush):" + drill);
        em.flush(); 
        log.info("created drill (after flush):" + drill);
        
        assertFalse(drill.getId() == 0L);        
    }
    
    /**
     * This test provides a demo of the error that can occure when passing an
     * object with the PK already assigned when using GeneratedValues.
     */
    @Test
    public void testAUTOBad() {
        log.info("testAUTOBad");
        //he's not going to like they non-zero PK value here
        ejava.examples.orm.core.mapped.Drill drill = new Drill(25L);
        drill.setMake("BD");
        
        //insert a row in the database
        boolean exceptionThrown = false;
        try { 
            assertFalse(drill.getId() == 0L);        
            log.info("trying to create drill with pre-exist pk:" + drill);
            em.persist(drill);
        }
        catch (PersistenceException ex) {
            log.info("got expected exception: " + ex);
            exceptionThrown = true;
        }        
        assertTrue(exceptionThrown);
    }        

    @Test
    public void testTABLE() {
        log.info("testTABLE");
        //note that since PKs are generated, we must pass in an object that
        //has not yet been assigned a PK value.
        ejava.examples.orm.core.mapped.EggBeater eggbeater = new EggBeater(0);
        eggbeater.setMake("done right");
        
        //insert a row in the database
        em.persist(eggbeater);
        log.info("created eggbeater (before flush):" + eggbeater);
        em.flush(); 
        log.info("created eggbeater (after flush):" + eggbeater);
        
        assertFalse(eggbeater.getId() == 0L);        
    }

    public void testSEQUENCE() {
        Assume.assumeTrue(Boolean.parseBoolean(System.getProperty("sql.sequences", "true")));
        log.info("testSEQUENCE");
        //note that since PKs are generated, we must pass in an object that
        //has not yet been assigned a PK value.
        ejava.examples.orm.core.mapped.Fan fan = new Fan(0);
        fan.setMake("cool runner 2");
        
        //insert a row in the database
        em.persist(fan);
        log.info("created fan (before flush):" + fan);
        em.flush(); 
        log.info("created fan (after flush):" + fan);
        
        assertFalse(fan.getId() == 0L);                
    }

    @Test
    public void testIDENTITY() {
        log.info("testIDENTITY");
        ejava.examples.orm.core.mapped.Gadget gadget = new Gadget(0);
        gadget.setMake("gizmo 2");
        
        //insert a row in the database
        em.persist(gadget);
        log.info("created gadget (before flush):" + gadget);
        em.flush(); 
        log.info("created gadget (after flush):" + gadget);
        
        assertFalse(gadget.getId() == 0L);                
    }
}
