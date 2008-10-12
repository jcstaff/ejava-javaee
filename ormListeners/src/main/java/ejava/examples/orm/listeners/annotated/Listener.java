package ejava.examples.orm.listeners.annotated;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Listener extends Person {
    private static Log log = LogFactory.getLog(Listener.class);
    @PrePersist public void prePersist(Object entity) {
        if (entity instanceof Person) {
            Person person = (Person)entity;
            log.debug("Person Listener.prePersist:" + person);
        }
        else if (entity instanceof Residence) {
            Residence residence = (Residence)entity;
            log.debug("Residence Listener.prePersist:" + residence);
        }
        else if (entity == null) {
            log.debug("null entity type");
        }
        else {
            log.debug("unknown entity type:" + entity.getClass());
        }
    }
    @PostPersist public void postPersist(Object entity) {
        if (entity instanceof Person) {
            Person person = (Person)entity;
            log.debug("Person Listener.postPersist:" + person);
        }
        else if (entity instanceof Residence) {
            Residence residence = (Residence)entity;
            log.debug("Residence Listener.postPersist:" + residence);
        }
        else if (entity == null) {
            log.debug("null entity type");
        }
        else {
            log.debug("unknown entity type:" + entity.getClass());
        }
    }
}
