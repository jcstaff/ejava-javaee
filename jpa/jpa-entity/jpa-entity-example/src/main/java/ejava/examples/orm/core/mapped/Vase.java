package ejava.examples.orm.core.mapped;

import java.io.Serializable;
import java.util.Date;

import ejava.examples.orm.core.ColorType;

/**
 * This class provides an example of mapping various types to the database,
 * like dates, enums, etc.
 * 
 * @author jcstaff
 * 
 * $Id:$
 */
public class Vase implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private Date aDate;
    private Date aTime;
    private Date aTimestamp;
    private ColorType colorId;
    private ColorType colorName;

    public Vase() {}
    public Vase(long id) { this.id = id; }
    
    public Date getADate() {
        return aDate;
    }
    public void setADate(Date date) {
        aDate = date;
    }
    
    public Date getATime() {
        return aTime;
    }
    public void setATime(Date time) {
        aTime = time;
    }
    
    public Date getATimestamp() {
        return aTimestamp;
    }
    public void setATimestamp(Date timestamp) {
        aTimestamp = timestamp;
    }
    
    public ColorType getColorId() {
        return colorId;
    }
    public void setColorId(ColorType colorId) {
        this.colorId = colorId;
    }

    public ColorType getColorName() {
        return colorName;
    }
    public void setColorName(ColorType colorName) {
        this.colorName = colorName;
    }
    
    public long getId() {
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }
    
    public String toString() {
        return super.toString() +
            ", id=" + id +
            ", aDate=" + aDate +
            ", aTime=" + aTime +
            ", aTimestamp=" + aTimestamp +            
            ", colorId=" + colorId +
            ", colorName=" + colorName;        
    }
}

