package ejava.examples.orm.core.annotated;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import ejava.examples.orm.core.ColorType;

/**
 * This class provides an example of mapping various types to the database,
 * like dates, enums, etc.
 * @author jcstaff
 * 
 * $Id:$
 */
@Entity
@Table(name="ORMCORE_VASE")
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
    
    @Temporal(TemporalType.DATE)
    public Date getADate() {
        return aDate;
    }
    public void setADate(Date date) {
        aDate = date;
    }
    
    @Temporal(TemporalType.TIME)
    public Date getATime() {
        return aTime;
    }
    public void setATime(Date time) {
        aTime = time;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    public Date getATimestamp() {
        return aTimestamp;
    }
    public void setATimestamp(Date timestamp) {
        aTimestamp = timestamp;
    }
    
    @Enumerated(EnumType.ORDINAL)
    public ColorType getColorId() {
        return colorId;
    }
    public void setColorId(ColorType colorId) {
        this.colorId = colorId;
    }

    @Enumerated(EnumType.STRING)
    public ColorType getColorName() {
        return colorName;
    }
    public void setColorName(ColorType colorName) {
        this.colorName = colorName;
    }
    
    @Id
    public long getId() {
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString())
		       .append(", id=").append(id)
		       .append(", aDate=").append(aDate)
			   .append(", aTime=").append(aTime)
			   .append(", aTimestamp=").append(aTimestamp)
			   .append(", colorId=").append(colorId)
			   .append(", colorName=").append(colorName);
		return builder.toString();
	}
}

