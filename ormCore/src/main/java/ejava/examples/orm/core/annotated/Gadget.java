package ejava.examples.orm.core.annotated;

import java.io.Serializable;

import javax.persistence.*;

/**
 * This class demonstrates the use of IDENTITY generator strategy using
 * annotations.
 * 
 * $Id:$
 */
@Entity
@Table(name="ORMCORE_GADGET")
public class Gadget implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id=0;
    private String make;    
    
    public Gadget() {}
    public Gadget(long id) { this.id = id; }
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }
    public String getMake() {
        return make;
    }
    public void setMake(String make) {
        this.make = make;
    }

    public String toString() {
        return super.toString()
            + ", id=" + id
            + ", make=" + make;        
    }
}
