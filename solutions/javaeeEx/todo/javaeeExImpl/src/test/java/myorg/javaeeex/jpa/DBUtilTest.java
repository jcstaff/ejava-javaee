package myorg.javaeeex.jpa;

public class DBUtilTest extends DemoBase {
    private String dropPath = System.getProperty("dropPath");
    private String createPath = System.getProperty("createPath");
    private DBUtil dbUtil;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        assertNotNull("dropPath not supplied", dropPath);
        assertNotNull("createPath not supplied", createPath);
        
        dbUtil = new DBUtil(em, dropPath, createPath);        
    }

    /* this is a basic sanity check of the ability to drop and create schema */
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
