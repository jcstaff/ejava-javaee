package ejava.examples.orm.core.mapped;

import java.io.Serializable;
/**
 * This class demonstrates the use of IDENTITY generator strategy using
 * orm.xml.
 * 
 * $Id:$
 */
public class Gadget implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id=0;
    private String make;    
    
    public Gadget() {}
    public Gadget(long id) { this.id = id; }
    
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
