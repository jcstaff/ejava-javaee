package ejava.examples.orm.listeners.annotated;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides an example of an entity who is the non-owner in 
 * a OneToOne uni-directional relationship. The solution uses a primary key
 * join, but the primary key of the owning entity is automatically generated.
 * That means we cannot ask the provider to automatically assign the PK for 
 * this entity and we have to wait until the provider assigns the PK for the
 * owning entity. The PK will be set by the owning side using an Entity 
 * Callback.
 * 
 * @author jcstaff
 *
 */
@Entity @Table(name="ORMLISTEN_RESIDENCE")
//@EntityListeners(Listener.class) -specified in deployment descriptor
public class Residence {
    private static final Log log = LogFactory.getLog(Residence.class);
    private static final Log cbLog = LogFactory.getLog("callback.Residence");

    private long id;
    private String street;
    private String city;
    private String state;
    private String zip;
    private Person person;
    
    public Residence() {}
    
    public long peekId() { return id; } //peek at ID without logging
	public void putId(long id, String source) { 
		this.id = id; 
        log.debug("Residence.setId() called from " + source + " with:" + id);
	}

    @Id
    public long getId() {
        log.debug("Residence.getId() returning:" + id);
        return id;
    }
    void setId(long id) {
        log.debug("Residence.setId() called with:" + id);
        this.id = id;
    }
    
    @OneToOne(optional=false)
    @PrimaryKeyJoinColumn
    public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
    

    public String getCity() {
        return city;
    }

	public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getZip() {
        return zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }
    
    @PrePersist public void prePersist() {
        cbLog.debug("Callback.prePersist event:" + this.toString());
        //id = person.getId();
        if (id != 0) {
            cbLog.debug("Residence Callback.prePersist too late");
        }
        else if (person != null && person.peekId() != 0) {
        	putId(person.peekId(), "Resident.Callback");
        }
        else {
        	cbLog.debug("Residence Callback.prePersist too early");
        }
    }
    @PostPersist public void postPersist() {
    	cbLog.debug("Callback.postPersist event:" + this.toString());
    }
    @PostLoad public void postLoad() {
    	cbLog.debug("Callback.postLoad event:" + this.toString());
    }
    @PreUpdate public void preUpdate() {
    	cbLog.debug("Callback.preUpdate event:" + this.toString());
    }
    @PostUpdate public void postUpdate() {
    	cbLog.debug("Callback.postUpdate event:" + this.toString());
    }
    @PreRemove public void preRemove() {
    	cbLog.debug("Callback.preRemove event:" + this.toString());
    }
    @PostRemove public void postRemove() {
    	cbLog.debug("Callback.postRemove event:" + this.toString());
    }    
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", " + street);
        text.append(", " + city);
        text.append(", " + state);
        text.append(" " + zip);        
        return text.toString();
    }
}
