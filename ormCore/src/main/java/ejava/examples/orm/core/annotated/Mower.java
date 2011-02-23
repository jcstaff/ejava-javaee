package ejava.examples.orm.core.annotated;

import java.io.Serializable;

import javax.persistence.*;

import ejava.examples.orm.core.MowerPK;

/**
 * This class provides an example of expressing an IdClass for a compound 
 * primary key using annotations. The primary key class does not use 
 * annotations. All annotations are within the using class.
 * 
 * @author jcstaff
 * $Id:$
 */
@Entity
@Table(name="ORMCORE_MOWER")
@IdClass(MowerPK.class)
public class Mower implements Serializable {
    private static final long serialVersionUID = 1L;
    private String make;
    private String model;    
    private int size;
    
    
    public Mower() {}
    public Mower(String make, String model) {
        this.make = make;
        this.model = model;
    }
    
    @Id
    @Column(nullable=false, updatable=false)
    public String getMake() {
        return make;
    }
    @SuppressWarnings("unused")
    private void setMake(String make) {
        this.make = make;
    }
    
    @Id
    @Column(nullable=false, updatable=false)
    public String getModel() {
        return model;
    }
    @SuppressWarnings("unused")
    private void setModel(String model) {
        this.model = model;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    
    public String toString() {
        return new StringBuilder()
           .append(super.toString())
           .append(", make=").append(make)
           .append(", model=").append(model)
           .append(", size=").append(size)
           .toString();
    }
}
