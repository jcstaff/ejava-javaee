package ejava.examples.orm.core.annotated;

import java.io.Serializable;
import javax.persistence.*; //brings in JPA Annotations

/**
 * This class provides the basic annotations required to make a class usable 
 * by Java Persistence without any further mapping. They are 
 * @javax.persistence.Entity to denote the class and @javax.persistence.Id
 * to denote the primary key property. See the mapped Bike example of how this 
 * can be done through a deployment descriptor instead of annotations. 
 *  
 * @author jcstaff
 * $Id:$
 */
@Entity  //tells ORM that this class can be mapped 
public class Bike implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String make;
    private String model;
    private int size;
    
    public Bike() {}
    public Bike(long id) { this.id = id; }

    @Id   //tells ORM that this property provides pk simple value
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
