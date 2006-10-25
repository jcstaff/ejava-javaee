package ejava.examples.jndischeduler.ejb;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import ejava.examples.jndischeduler.JNDIHelper;

@Stateless(name="AidScheduler")
public class AidSchedulerEJB extends SchedulerBase
    implements AidSchedulerLocal, AidSchedulerRemote {
        
    private EntityManager em;
    private String message;
    private HospitalLocal hospital;
    
    @Resource 
    public void setSessionContext(SessionContext ctx) { this.ctx = ctx; }
    
    public void init() {
        log.info("******************* AidScheduler Created ******************");
        log.debug("ctx=" + ctx);
        log.debug("ejb/hospital=" + ctx.lookup("ejb/hospital"));
        log.debug("message=" + message);
        log.debug("em=" + em);
        log.debug("hospital=" + hospital);
        try { new JNDIHelper().dump(new InitialContext(), "java:comp.ejb3");
        } catch (NamingException e) { log.fatal("" + e); }        
    }
    
    public String getName() { return "AidScheduler"; }    
}
