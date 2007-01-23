package myorg.webtier.data;

import java.io.Serializable;

public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String firstName;
    private String lastName;
    
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
    public String toString() {
        return "" + id + ":" + firstName + " " + lastName;
    }
}
