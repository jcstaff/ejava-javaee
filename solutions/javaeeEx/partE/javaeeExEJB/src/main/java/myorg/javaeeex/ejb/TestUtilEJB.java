package myorg.javaeeex.ejb;

import javax.annotation.PostConstruct;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import myorg.javaeeex.bl.TestUtil;

@Stateless
public class TestUtilEJB implements TestUtilRemote {
    private static Log log = LogFactory.getLog(TestUtilEJB.class);

    //@Inject @JavaeeEx2
    //private EntityManager em;

    @Inject
    private TestUtil testUtil;

    @PostConstruct
    public void init() {
        log.info(" *** TestUtilEJB:init() ***");
        //testUtil = new TestUtilImpl();
        //((TestUtilImpl)testUtil).setEntityManager(em);
    }

    public void resetAll() throws Exception {
        try {
            testUtil.resetAll();
        }
        catch (Exception ex) {
            log.warn("error in resetAll", ex);
            throw ex;
        }
    }
}
