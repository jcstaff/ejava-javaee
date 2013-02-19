package ejava.examples.orm.core.annotated;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;

/**
 * This class provides an example of providing more explicite mappings between
 * the entity class and the database using annotations.
 * 
 * @author jcstaff
 * $Id:$
 */

@Entity
@Table(name="ORMCORE_CAR")//, schema="PUBLIC")
//we use the @Table name property to specifically name the table in DB
//we can also specify vendor-specific constraints with uniqueConstraints prop
public class Car implements Serializable {    
    private static final long serialVersionUID = 1L;
    private long id;
    private String make;
    private String model;
    private int year;
    private BigDecimal cost;
    
    public Car() {}
    public Car(long id) { this.id = id; }
    
    @Id
    @Column(name="CAR_ID", nullable=false)
    public long getId() {
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }
    
    @Column(name="CAR_MAKE", 
            unique=false, 
            nullable=false, 
            insertable=true,
            updatable=true,
            table="",  //note: we can point to another table to get prop
            length=20)
    public String getMake() {
        return make;
    }
    public void setMake(String make) {
        this.make = make;
    }
    
    @Column(name="CAR_MODEL", nullable=false, length=20)
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    @Column(name="CAR_YEAR", nullable=false)
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    
    @Column(name="CAR_COST", precision=7, scale=2)
    public BigDecimal getCost() {
        return cost;
    }
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString())
	           .append(", id=").append(id)
		       .append(", make=").append(make)
			   .append(", model=").append(model)
			   .append(", year=").append(year)
			   .append(", cost=$").append(cost);
		return builder.toString();
	}
    
}
