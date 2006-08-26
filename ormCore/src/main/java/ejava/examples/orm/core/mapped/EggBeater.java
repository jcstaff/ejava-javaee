package ejava.examples.orm.core.mapped;

import java.io.Serializable;

/**
 * This class provides an example of using a TABLE GeneratedValue schema. The
 * definition of the primary key generation is supplied in the orm.xml file.
 * 
 * @author jcstaff
 * $Id:$
 */
public class EggBeater implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id=0;
    private String make;    
    
    public EggBeater() {}
    public EggBeater(long id) { this.id = id; }
    
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
