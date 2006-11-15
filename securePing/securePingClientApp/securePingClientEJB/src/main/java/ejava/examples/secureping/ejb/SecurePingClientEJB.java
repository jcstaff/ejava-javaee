package ejava.examples.secureping.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This session bean allows all methods to be invoked and then performs 
 * the matching operation on SecurePingEJB using a run-as with an admin role.
 *
 * @author jcstaff
 */
@Stateless
@PermitAll
public class SecurePingClientEJB 
    implements SecurePingClientRemote, SecurePingClientLocal {
    private static final Log log = LogFactory.getLog(SecurePingClientEJB.class);
    
    @Resource
    SessionContext ctx;
   
    //set by dependency injection in ejb-jar.xml
    SecurePingRemote securePingServer;
    
    @PostConstruct
    public void init() {
        log.debug("*** SecurePingClientEJB initializing ***");
        log.debug("securePingServer=" + securePingServer);
    }
    
    /**
     * Return what this EJB's container thinks about the caller.
     */
    private String getInfo(String prefix) {
        StringBuilder text = new StringBuilder();
        text.append("called " + prefix);
        try {
            text.append(", principal=" + ctx.getCallerPrincipal().getName());
            text.append(", isUser=" + ctx.isCallerInRole("user"));
            text.append(", isAdmin=" + ctx.isCallerInRole("admin"));
            text.append(", isInternalRole=" + 
                    ctx.isCallerInRole("internalRole"));
        }
        catch (Throwable ex) {
            log.debug("error calling session context:", ex);
            text.append(", error calling Session Context:" + ex);
        }
        String result = text.toString();
        log.debug(result);
        return result;        
    }

    /**
     * Return info from this bean and the securePingServer after performing
     * a run-as.
     */
    public String pingAll() {
        return getInfo("pingAll") + ":" + securePingServer.pingAll();
    }

    /**
     * Return info from this bean and the securePingServer after performing
     * a run-as.
     */
    public String pingUser() {
        return getInfo("pingUser") + ":" + securePingServer.pingUser();
    }

    /**
     * Return info from this bean and the securePingServer after performing
     * a run-as.
     */
    public String pingAdmin() {        
        return getInfo("pingAdmin") + ":" + securePingServer.pingAdmin();
    }

    /**
     * Return info from this bean and the securePingServer after performing
     * a run-as.
     */
    public String pingExcluded() {
        return getInfo("pingExcluded") + ":" + securePingServer.pingExcluded();
    }
    
    /**
     * Return info from this bean and the securePingServer after performing
     * a run-as. Most of the details are written to the log since the return
     * type here is a simple boolean.
     */
    public boolean isCallerInRole(String role) {
        boolean result = ctx.isCallerInRole(role);
        log.debug("user=" + ctx.getCallerPrincipal().getName() + 
                ", isCallerInRole(" + role + ")=" + result + 
                ":" + securePingServer.isCallerInRole(role));  
        return result;
    }
}
