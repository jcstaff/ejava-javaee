package myorg.entitymgrex;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity @Table(name="EM_AUTO")
public class Auto implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String make;
    private String model;
    private String color;
    private int mileage;
        
    @Id @GeneratedValue
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
    public int getMileage() {
        return mileage;
    }
    public void setMileage(int mileage) {
        this.mileage = mileage;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }    
    
    public String toString() {
        return super.toString() +
            ", id=" + id +
            ", make=" + make +
            ", model=" + model +
            ", color=" + color +
            ", mileage=" + mileage;            
    }
}
