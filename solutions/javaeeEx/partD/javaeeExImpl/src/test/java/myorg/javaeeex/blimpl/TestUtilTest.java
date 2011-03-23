package myorg.javaeeex.blimpl;

import static org.junit.Assert.*;

import org.junit.Test;

import myorg.javaeeex.bl.TestUtil;
import myorg.javaeeex.bo.Address;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.jpa.DemoBase;

public class TestUtilTest extends DemoBase {
    private TestUtil testUtil;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        testUtil = new TestUtilImpl();
        ((TestUtilImpl)testUtil).setEntityManager(em);
    }

    @Test
    public void testResetAll() throws Exception {
        log.info("*** testResetAll");

        if (em.createQuery("select p from Person p").getResultList().size() 
            == 0) {
            Person person = new Person();
            person.setFirstName("joe");
            Address address = new Address();
            address.setZip("00000");
            person.getAddresses().add(address);
            em.persist(person);
            em.getTransaction().commit();
            em.getTransaction().begin();
        }
        
        assertFalse("unexpected person count", 0 ==
            em.createQuery("select p from Person p").getResultList().size()); 
        assertFalse("unexpected address count", 0 ==
            em.createQuery("select a from Address a").getResultList().size()); 

        testUtil.resetAll();
        
        assertEquals("unexpected person count", 0,
            em.createQuery("select p from Person p").getResultList().size()); 
        assertEquals("unexpected address count", 0,
                em.createQuery("select a from Address a").getResultList().size()); 
    }
    
}
