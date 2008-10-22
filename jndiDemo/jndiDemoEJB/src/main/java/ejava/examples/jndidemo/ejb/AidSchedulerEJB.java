package ejava.examples.jndidemo.ejb;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import ejava.examples.jndidemo.JNDIHelper;

/**
 * This class is primarily an example of an EJB mostly configured through 
 * an external ejb-jar.xml deployment descriptor. There are very few Java
 * @Annotations defined here. All properties are injected through definitions
 * in the ejb-jar.xml file.
 *  
 * @author jcstaff
 *
 */
//@Stateless declared by ejb-jar.xml entry
public class AidSchedulerEJB extends SchedulerBase
    implements AidSchedulerLocal, AidSchedulerRemote {
        
    //this is injected by ejb-jar.xml entry
    private EntityManager em;
    //this is injected by the ejb-jar.xml entry
    private DataSource ds;
    //this is injected by ejb-jar.xml entry
    private String message;
    //this is injected by ejb-jar.xml entry
    private HospitalLocal hospital;
    
    @Resource 
    public void setSessionContext(SessionContext ctx) { this.ctx = ctx; }
    
    public void init() {
        log.info("******************* AidScheduler Created ******************");
        log.debug("ctx=" + ctx);
        log.debug("ejb/hospital=" + ctx.lookup("ejb/hospital"));
        log.debug("message=" + message);
        log.debug("em=" + em);
        log.debug("ds=" + ds);
        log.debug("hospital=" + hospital);
        //see common in BaseSchedulerEJB about java:comp/env and java:comp.ejb3
        try { new JNDIHelper().dump(new InitialContext(), "java:comp.ejb3");
        } catch (NamingException e) { log.fatal("" + e); }        
        try { new JNDIHelper().dump(new InitialContext(), "java:comp/env");
        } catch (NamingException e) { log.fatal("" + e); }        
    }
    
    public String getName() { return "AidScheduler"; }    
}
