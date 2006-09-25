package ejava.examples.orm.core.annotated;

import java.io.Serializable;

import javax.persistence.*;

/**
 * This class demonstrates the use of SEQUENCE generator strategy using
 * annotations.
 * 
 * $Id:$
 */
@Entity
@Table(name="ORMCORE_FAN")
@SequenceGenerator(
        name="fanSequence", //required logical name
        sequenceName="FAN_SEQ", //name in database
        initialValue=44,        //start with something odd to be noticeable
        allocationSize=13) //I noticed with JBoss that this was used for initVal
public class Fan implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id=0;
    private String make;    
    
    public Fan() {}
    public Fan(long id) { this.id = id; }
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, //use DB sequence 
            generator="fanSequence")                  //point to logical def
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
