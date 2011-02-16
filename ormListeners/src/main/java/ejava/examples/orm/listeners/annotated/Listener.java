package ejava.examples.orm.listeners.annotated;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Listener {
    private static Log log = LogFactory.getLog("listener");
    
    @PrePersist public void prePersist(Object entity) {
        if (entity instanceof Person) {
            Person person = (Person)entity;
            log.debug("Person Listener.prePersist:" + person);
        }
        else if (entity instanceof Residence) {
            Residence residence = (Residence)entity;
            log.debug("Residence Listener.prePersist:" + residence);
            Person person = residence.getPerson();
            if (residence.peekId() != 0) {
                log.debug("Residence Listener.prePersist too late");
            }
            else if (person != null && person.peekId() != 0) {
            	residence.putId(person.peekId(), "Listener");
            }
            else {
                log.debug("Residence Listener.prePersist too early");
            }
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
            Residence residence = person.getResidence();
            if (residence.peekId() != 0) {
                log.debug("Person Listener.postPersist too late");
            }
            else if (residence != null && residence.peekId() == 0) { 
                residence.putId(person.peekId(), "Listener"); 
            } 
            else {
                log.debug("Person Listener.postPersist too late");
            }
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
