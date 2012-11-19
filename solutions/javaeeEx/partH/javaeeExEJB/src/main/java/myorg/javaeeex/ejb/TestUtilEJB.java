package myorg.javaeeex.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import myorg.javaeeex.bl.TestUtil;

@Stateless
public class TestUtilEJB implements TestUtilRemote {
    private static Log log = LogFactory.getLog(TestUtilEJB.class);

    //@Inject @JavaeeEx
    //private EntityManager em;

    @Inject
    private TestUtil testUtil;
    
    @Resource
    private SessionContext ctx;

    @PostConstruct
    public void init() {
        log.info(" *** TestUtilEJB:init() ***");
        //testUtil = new TestUtilImpl();
        //((TestUtilImpl)testUtil).setEntityManager(em);
    }

    @PermitAll
    @Override
    public String ping() throws Exception {
    	log.debug("caller=" + ctx.getCallerPrincipal().getName());
    	return ctx.getCallerPrincipal().getName();
    }

    @RolesAllowed({"admin"})
    @Override
    public void resetAll() throws Exception {
        try {
        	log.debug("caller=" + ctx.getCallerPrincipal().getName());
        	testUtil.resetAll();
        }
        catch (Exception ex) {
            log.warn("error in resetAll", ex);
            throw ex;
        }
    }
}
