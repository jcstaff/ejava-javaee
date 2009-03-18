package myorg.javaeeex.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String firstName;
    private String lastName;
    private String ssn;
    private Collection<Address> addresses = new ArrayList<Address>();
    
    public String getSsn() {
        return ssn;
    }
    public void setSsn(String ssn) {
        this.ssn = ssn;
    }
    public Person() {}
    public Person(long id) { setId(id); }
    
    public long getId() {
        return id;
    }
    private void setId(long id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }    
    public Collection<Address> getAddresses() {
        return addresses;
    }
    public void setAddresses(Collection<Address> addresses) {
        this.addresses = addresses;
    }
    public String toString() {
        StringBuffer text = new StringBuffer();
        text.append("id=" + id); 
        text.append(":" + firstName);
        text.append(" " + lastName);
        text.append(" " + ssn);
        text.append(", addresses={");
        for (Address address : addresses) {
            text.append("{" + address.toString() + "},");
        }
        text.append("}");
        return text.toString();
    }
}