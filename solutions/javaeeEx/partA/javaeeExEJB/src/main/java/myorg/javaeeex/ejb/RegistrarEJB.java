package myorg.javaeeex.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@Stateless
public class RegistrarEJB implements RegistrarLocal, RegistrarRemote {
    private static Log log = LogFactory.getLog(RegistrarEJB.class);
    
    @PostConstruct
    public void init() {
        log.debug("**** init ****");
    }
    
    @PreDestroy
    public void close() {
        log.debug("*** close() ***");
    }

    public void ping() {
        log.debug("ping called");
    }
}
