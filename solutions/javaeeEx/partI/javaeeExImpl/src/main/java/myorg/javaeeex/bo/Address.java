package myorg.javaeeex.bo;

import java.io.Serializable;

public class Address implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String street;
    private String city;
    private String state;
    private String zip;
    
    public Address() {}
    public Address(
            long id, String street, String city, String state, String zip) {
        this.id = id;
        this.city = city;
        this.state = state;
        this.street = street;
        this.zip = zip;
    }
    
    public long getId() {
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }
    
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getZip() {
        return zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        
        text.append(street + " ");
        text.append(city + ", ");
        text.append(state + " ");
        text.append(zip);
        return text.toString();
    }
}
