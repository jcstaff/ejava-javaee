package ejava.examples.orm.core.annotated;

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
    name="fanSequence",     //required logical name
    sequenceName="FAN_SEQ", //name in database
    initialValue=44,        //start with something odd to be noticeable
    allocationSize=13)      //I noticed with JBoss that this was used for initVal
public class Fan {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, //use DB sequence 
            generator="fanSequence")                  //point to logical def
    private long id;
    private String make;    
    
    public Fan() {}
    public Fan(long id) { this.id = id; }
    
    public long getId() { return id; }

    public String getMake() { return make; }
    public void setMake(String make) {
        this.make = make;
    }

    @Override
    public String toString() {
        return new StringBuilder()
              .append(super.toString())
              .append(", id=").append(id)
              .append(", make=").append(make)
              .toString();
    }    
}
