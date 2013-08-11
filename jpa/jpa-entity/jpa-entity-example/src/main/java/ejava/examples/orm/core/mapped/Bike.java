package ejava.examples.orm.core.mapped;

import java.io.Serializable;

/**
 * This class provides a pure POJO class that is mapped by BIKE-orm.xml
 * into the database. See the annotated Bike example for how this can be done 
 * through class annotations.
 *  
 * @author jcstaff
 * $Id:$
 */
public class Bike implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String make;
    private String model;
    private int size;
    
    public Bike() {}
    public Bike(long id) { this.id = id; }

    //orm.xml file will map this field to Identity
    public long getId() {
        return id;
    }
    @SuppressWarnings("unused") //ORM will use this to set pk value
    private void setId(long id) {
        this.id = id;
    }
    public String getMake() {
        return make;
    }
    public void setMake(String make) {
        this.make = make;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public String toString() {
        return super.toString() + " id=" + id + 
            ", make=" + make + 
            ", model=" + model + 
            ", " + size + "in";
    }
}
