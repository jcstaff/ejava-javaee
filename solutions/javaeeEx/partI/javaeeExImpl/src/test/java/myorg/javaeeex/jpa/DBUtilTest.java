package myorg.javaeeex.jpa;

import static org.junit.Assert.*;

import org.junit.Test;

public class DBUtilTest extends DemoBase {
    private String dropPath = 
    	System.getProperty("dropPath", "ddl/javaeeExImpl-dropJPA.ddl");
    private String createPath = 
    	System.getProperty("createPath", "ddl/javaeeExImpl-createJPA.ddl");
    private DBUtil dbUtil;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        assertNotNull("dropPath not supplied", dropPath);
        assertNotNull("createPath not supplied", createPath);
        
        dbUtil = new DBUtil(em, dropPath, createPath);        
    }

    /* this is a basic sanity check of the ability to drop and create schema */
    @Test
    public void testDropCreate() throws Exception {
        log.info("*** testDropCreate ***");
        
        int count = dbUtil.dropAll();
        assertFalse("unexpected drop count:" + count, count == 0);
        
        em.getTransaction().commit();
        em.getTransaction().begin();

        count = dbUtil.createAll();
        assertFalse("unexpected create count:" + count, count == 0);

        em.getTransaction().commit();
        em.getTransaction().begin();
    }
}
