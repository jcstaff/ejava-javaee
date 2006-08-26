package ejava.examples.orm.core.annotated;

import java.io.Serializable;
import javax.persistence.*;

/**
 * This class provides an example of embedding a primary key class within
 * the conatining class and doing all the mapping if the PK here instead
 * of the PK class. Note that we are able to better generalize the PK class
 * because of where the mapping is placed.
 * 
 * @author jcstaff
 * $Id:$
 */
@Entity
@Table(name="ORMCORE_PEN")
public class Pen implements Serializable {
    private static final long serialVersionUID = 1L;
    private MakeModelPK pk;
    private int size;
    
    
    public Pen() {}
    public Pen(String make, String model) {
        this.pk = new MakeModelPK(make, model);
    }
    
    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name="make", column=@Column(name="PEN_MAKE")),
        @AttributeOverride(name="model", column=@Column(name="PEN_MODEL"))             
        })
    public MakeModelPK getPk() {
        return pk;
    }
    public void setPk(MakeModelPK pk) {
        this.pk = pk;
    }
    
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    
    public String toString() {
        return super.toString() +
           "pk=" + pk +
           ", size=" + size;
    }
}
