package ejava.examples.orm.core.annotated;

import java.io.Serializable;

import javax.persistence.*;

/**
 * This class provides an example of using a TABLE GeneratedValue schema. The
 * definition of the primary key generation is within the annotations supplied
 * in this class.
 * 
 * @author jcstaff
 * $Id:$
 */
@Entity
@Table(name="ORMCORE_EGGBEATER")
@TableGenerator(  //note that all but name are optional if generating schema
        name="eggbeaterGenerator",     //logical name of generator
        table="ORMCORE_EB_UID",        //name of table storing seq
        pkColumnName="UID_ID",         //pk column for seq table
        pkColumnValue="ORMCORE_EGGBEATER",  //pk value in pk column
        valueColumnName="UID_VAL",     //column for seq value
        allocationSize=17              //amount to increment when take
    )        
public class EggBeater implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id=0;
    private String make;    
    
    public EggBeater() {}
    public EggBeater(long id) { this.id = id; }
    
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE, //use DB table 
            generator="eggbeaterGenerator")        //point to logical def
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

	@Override
	public String toString() {
		return new StringBuilder()
		          .append(super.toString())	       
		          .append(", id=").append(id)
		          .append(", make=").append(make)
		          .toString();
	}    
}
