package ejava.examples.orm.inheritance.annotated;

import javax.persistence.*;

/**
 * This class provides an example base class in a single table inheritance
 * strategy. In this case, a single table is defined to hold the properties of
 * all sub-classes. A discriminator field is necessary to denote the type
 * of each row in the table. Note that the primary key is being generated 
 * here since this class definition is shared among the sub-classes.
 * 
 * @author jcstaff
 */
@Entity @Table(name="ORMINH_PRODUCT")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TYPE", //column in root table indicating type
    discriminatorType=DiscriminatorType.STRING,//data type of column
    length=32) //length of disriminator string
public abstract class Product {
    private long id;
    private double cost;

    @Id @GeneratedValue
    public long getId() {
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }

    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }
    
    @Transient
    public abstract String getName();
    
    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", id=" + id);
        text.append(", cost=" + cost);
        return text.toString();
    }
}
