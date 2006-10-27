package myorg.javaeeex.bo;

import java.io.Serializable;
import javax.persistence.*;

@Entity @Table(name="JAVAEEEX_PERSON")
@NamedQueries({
    @NamedQuery(name="getAllPeople", query="select p from Person p")
})
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String firstName;
    private String lastName;
    
    public Person() {}
    public Person(long id) { setId(id); }
    
    @Id @GeneratedValue
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