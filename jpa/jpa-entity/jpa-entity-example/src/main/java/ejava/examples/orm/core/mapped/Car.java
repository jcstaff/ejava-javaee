package ejava.examples.orm.core.mapped;

import java.io.Serializable;

/**
 * This class provides an example of providing more explicite mappings between
 * the entity class and the database using orm.xml.
 * 
 * @author jcstaff
 * $Id:$
 */

public class Car implements Serializable {    
    private static final long serialVersionUID = 1L;
    private long id;
    private String make;
    private String model;
    private int year;
    private double cost;
    
    public Car() {}
    public Car(long id) { this.id = id; }
    
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
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }    
    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }    
    public String toString() {
        return super.toString()
            + ", id=" + id
            + ", make=" + make
            + ", model=" + model
            + ", year=" + year
            + "$" + cost;        
    }
}
