package myorg.javaeeex.bl;

import java.util.List;

import myorg.javaeeex.bo.Person;

public interface Registrar {
    Person createPerson(String firstName, String lastName) 
        throws RegistrarException;
    List<Person> getAllPeople(int index, int count)
        throws RegistrarException;
}
