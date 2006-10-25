package ejava.examples.jndischeduler.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import ejava.examples.jndischeduler.JNDIHelper;

@Stateful(name="BakeScheduler")
//@EJBs({
//    @EJB(name="ejb/cook", beanInterface=CookLocal.class, beanName="CookEJB",
//            mappedName="jndiSchedulerEAR-1.0-SNAPSHOT/CookEJB/local")
//})
@Resource(name="ejb/cook", type=CookLocal.class)
public class BakeSchedulerEJB 
    extends SchedulerBase implements BakeSchedulerRemote {

    public String getName() { return "BakeSchedulerEJB"; }
    
    @PersistenceContext(unitName="jndischeduler",
                        name="persistence/jndischeduler",
                        type=PersistenceContextType.EXTENDED)
    private EntityManager em;

    @Resource
    protected void setSessionContext(SessionContext ctx) {
        super.ctx = ctx;
    }
    
    @EJB
    protected CookLocal cook; 

    @Resource(name="ejb/cook", mappedName="jndiSchedulerEAR-1.0-SNAPSHOT/CookEJB/local")
    protected CookLocal cook2; 
    
    @Resource(name="vals/message")
    String message;

    @PostConstruct
    public void init() {        
        log.info("******************* BakeScheduler Created ******************");
        log.debug("ctx=" + ctx);
        log.debug("ejb/cook=" + ctx.lookup("ejb/cook"));
        log.debug("em=" + em);
        log.debug("persistence/jndischeduler=" + ctx.lookup("persistence/jndischeduler"));
        log.debug("message=" + message);
        log.debug("cook=" + cook);
        log.debug("cook2=" + cook2);
        try { new JNDIHelper().dump(new InitialContext(), "java:comp.ejb3");
        } catch (NamingException e) { log.fatal("" + e); }
    }
}
