package myorg.javaeeex.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import myorg.javaeeex.bl.TestUtil;
import myorg.javaeeex.blimpl.TestUtilImpl;

@Stateless
public class TestUtilEJB implements TestUtilRemote {
    private static Log log = LogFactory.getLog(TestUtilEJB.class);

    @PersistenceContext(unitName="javaeeEx")
    private EntityManager em;

    private TestUtil testUtil;

    @PostConstruct
    public void init() {
        log.info(" *** TestUtilEJB:init() ***");
        testUtil = new TestUtilImpl();
        ((TestUtilImpl)testUtil).setEntityManager(em);
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
