package myorg.javaeeex.dao;

import java.util.List;
import java.util.Map;

import myorg.javaeeex.bo.Address;
import myorg.javaeeex.bo.Person;

public interface PersonDAO {
    public static final String GET_ALL_PEOPLE_QUERY = "getAllPeople";
    public static final String GET_PEOPLE_LIKE_NAME_QUERY = "getPeopleLikeName";
    public static final int MAX_RESULTS = 100;
    Person createPerson(Person person);
    Person getPerson(long id);
    void removePerson(Person person);
    void removeAddress(Address address);
    List<Person> findPeopleByName(String firstName, String lastName);
    List<Person> findPeople(String queryName, Map<String, Object> params,
            int index, int count);
}