package myorg.javaeeex.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class PersonDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String firstName;
    private String lastName;
    private Collection<AddressDTO> addresses = new ArrayList<AddressDTO>();

    public PersonDTO() {}
    public PersonDTO(long id) { setId(id); }

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
    public Collection<AddressDTO> getAddresses() {
        return addresses;
    }
    public void setAddresses(Collection<AddressDTO> addresses) {
        this.addresses = addresses;
    }
    public String toString() {
        StringBuffer text = new StringBuffer();
        text.append("id=" + id);
        text.append(":" + firstName);
        text.append(" " + lastName);
        text.append(", addresses={");
        for (AddressDTO address : addresses) {
            text.append("{" + address.toString() + "},");
        }
        text.append("}");
        return text.toString();
    }
}
