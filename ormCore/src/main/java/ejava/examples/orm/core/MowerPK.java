package ejava.examples.orm.core;

import java.io.Serializable;

/**
 * This is an example of a class that can be used for a Java Persistence
 * IdClass. Its requirements are that it have a default ctor() and it 
 * correctly override hashCode() and equals(). Not annotations or orm.xml
 * mappings are required for this specific class. All annotations and orm.xml 
 * entries are supplied by the classes this PK class identifies.
 * 
 * @author jcstaff
 * $Id:$
 */
public class MowerPK implements Serializable {
    private static final long serialVersionUID = 1L;
    private String make;
    private String model;
    
    public MowerPK() {}
    public MowerPK(String make, String model) {
        this.make = make;
        this.model = model;
    }
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
    public String getMake() {
        return make;
    }
    @SuppressWarnings("unused")
    private void setMake(String make) {
        this.make = make;
    }
    public String getModel() {
        return model;
    }
    @SuppressWarnings("unused")
    private void setModel(String model) {
        this.model = model;
    }
    public int hashCode() {
        return make.hashCode() + model.hashCode();
    }
    public boolean equals(Object obj) {
        try {
            if (this == obj) return true;
            return make.equals(((MowerPK)obj).getMake()) &&
                   model.equals(((MowerPK)obj).getModel());
            
        } catch (Throwable ignored) { //catch NP & Cast Exceptions 
            return false;
        }
    }
    
    public String toString() {
        return super.toString() +
            ", make=" + make +
            ", model=" + model;
    }
}
