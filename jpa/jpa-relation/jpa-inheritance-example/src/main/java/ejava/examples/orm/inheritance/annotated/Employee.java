package ejava.examples.orm.inheritance.annotated;

import java.util.Date;

import javax.persistence.*;

/**
 * This class provides an entity sub-class example for a join inheritance
 * strategy. The parent class will define a table and primary key value.
 * This class and all derived classes will form separate tables that are joined
 * by primary key.
 *
 * @author jcstaff
 */
@Entity 
@Table(name="ORMINH_EMPLOYEE") //joined with Person table to form Employee
public class Employee extends Person {
    private double payrate;
    private Date hireDate;
    
    @Temporal(TemporalType.DATE)
    public Date getHireDate() {
        return hireDate;
    }
    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }
    public double getPayrate() {
        return payrate;
    }
    public void setPayrate(double payrate) {
        this.payrate = payrate;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", payrate=" + payrate);
        return text.toString();
    }
}
