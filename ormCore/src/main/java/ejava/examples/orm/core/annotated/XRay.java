package ejava.examples.orm.core.annotated;

import java.io.Serializable;

import javax.persistence.*;

/**
 * This class provides an example of embedding another object within a 
 * containing object being mapped to the database. In this case, XRay is
 * assigned a primary key and mapped to the database. It has two local 
 * properties, but maker name, address, and phone are part of a Manufacturer
 * class. This works much like the @EmbeddedId case, except the embedded class
 * is just used for normal properties and not primary key values.
 * 
 * @author jcstaff
 * $Id:$
 */
@Entity
@Table(name="ORMCORE_XRAY")
public class XRay implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private Manufacturer maker;
    private String model;
    
    public XRay() {}
    public XRay(long id) { this.id = id; }
    
    @Id
    public long getId() {
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name="name", column=@Column(name="XRAY_MAKER"))
        //note that we are letting address and phone default 
    })
    public Manufacturer getMaker() {
        return maker;
    }
    public void setMaker(Manufacturer maker) {
        this.maker = maker;
    }
    
    
    public String toString() {
        return new StringBuilder()
           .append(super.getClass().getName())
           .append(", id=").append(id)
           .append(", model=").append(model)
           .append(", maker=").append(maker)
           .toString();
    }
}
