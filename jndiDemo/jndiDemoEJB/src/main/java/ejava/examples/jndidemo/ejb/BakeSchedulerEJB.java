package ejava.examples.jndidemo.ejb;

import javax.annotation.PostConstruct;import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBs;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.sql.DataSource;

import ejava.examples.jndidemo.JNDIHelper;

/**
 * This class is primarily an example of configuring an EJB through 
 * @Annotations. There is no external ejb-jar.xml deployment descriptor 
 * entries for this EJB.
 * 
 * @author jcstaff
 *
 */
@Stateful(name="BakeScheduler")
// This populates the local JNDI ENC with EJBs that can be de-referenced
// within the EJB.
@EJBs({
    @EJB(name="ejb/cook", beanInterface=CookLocal.class, beanName="CookEJB")
})
public class BakeSchedulerEJB 
    extends SchedulerBase implements BakeSchedulerRemote {

    public String getName() { return "BakeSchedulerEJB"; }
    
    /*
     * This declaration creates an EntityManager, with an extended
     * persistence context (only legal in Stateful session beans), 
     * for the named persistence unit, and places it in the JNDI context 
     * java:comp/env/persistence/jndidemo
     */
    @PersistenceContext(unitName="jndidemo",
                        name="persistence/jndidemo",
                        type=PersistenceContextType.EXTENDED)
    private EntityManager em;

    /*
     * This declaration obtains a reference to the SQL DataSource in the '
     * global JNDI tree and initializes ds to that value
     */
    @Resource(mappedName="java:/ejavaDS")
    private DataSource ds;
    
    /*
     * This declaration will cause the container to inject a SessionContext
     * into the EJB at startup.
     */
    @Resource
    protected void setSessionContext(SessionContext ctx) {
        super.ctx = ctx;
    }
    
    /* 
     * We will manually assign this value using a JNDI lookup inside the 
     * @PostConstruct method
     */
    protected CookLocal cook; 

    /*
     * This reference will be injected by the container based on the name
     * provided here and the information either located within the @EJBs spec
     * at the top of the class or the ejb-jar.xml deployment descriptor file. 
     */
    @Resource(name="ejb/cook")
    protected CookLocal cook2; 

    /*
     * This won't resolve to anything since this example does not use an 
     * external deployment descriptor to give us a value.
     */
    @Resource(name="vals/message")
    String message;

    @PostConstruct
    public void init() {        
        log.info("******************* BakeScheduler Created ******************");
        log.debug("ctx=" + ctx);
        log.debug("ejb/cook=" + ctx.lookup("ejb/cook"));
        log.debug("em=" + em);
        log.debug("ds=" + ds);
        log.debug("persistence/jndidemo=" + ctx.lookup("persistence/jndidemo"));
        log.debug("message=" + message);
        log.debug("cook=" + cook);
        log.debug("cook2=" + cook2);
        cook = (CookLocal)ctx.lookup("ejb/cook");
        //note that JBoss stashes things in a proprietary context in v4.x -
        //java:comp/env doesn't end up really holding anything although
        //lookups act like they really resolve to expected objects (see output
        //from running the RMI test program
        try { new JNDIHelper().dump(new InitialContext(), "java:comp.ejb3");
        } catch (NamingException e) { log.fatal("" + e); }
        try { new JNDIHelper().dump(new InitialContext(), "java:comp/env");
        } catch (NamingException e) { log.fatal("" + e); }
    }
}
