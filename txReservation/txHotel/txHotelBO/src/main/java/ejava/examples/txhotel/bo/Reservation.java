package ejava.examples.txhotel.bo;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Reservation implements Serializable {
    private long id;
    private long version;
    private String confirmation;
    private Date startDate;
    private Date endDate;
    private Person person;
    
    public Reservation() {}
    public Reservation(
            long id, long version, String confirmation, Person person,
            Date startDate, Date endDate) {
        setId(id);
        setVersion(version);
        setConfirmation(confirmation);
        setPerson(person);        
        setStartDate(startDate);
        setEndDate(endDate);
    }
    
    public long getId() {
        return id;
    }
    private void setId(long id) {
        this.id = id;
    }
    public String getConfirmation() {
        return confirmation;
    }
    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }
    public Person getPerson() {
        return person;
    }
    public void setPerson(Person person) {
        this.person = person;
    }
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    @Override
	public boolean equals(Object obj) {
    	try {
    		if (this==obj) return true;
    		Reservation rhs = (Reservation)obj;
    		return person.equals(rhs.person) && 
    				startDate.getTime()==rhs.startDate.getTime() &&
    				endDate.getTime()==rhs.endDate.getTime();
    	} catch (Exception ex) {
    		return false;
    	}
	}
	@Override
	public int hashCode() {
		return person.hashCode()+startDate.hashCode()+endDate.hashCode();
	}
    
	public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", version=" + version);
        text.append(", conf#" + confirmation);
        text.append(", person={" + person);
        text.append("}, start=" + startDate);
        text.append(", end=" + endDate);
        return text.toString();
    }
}
