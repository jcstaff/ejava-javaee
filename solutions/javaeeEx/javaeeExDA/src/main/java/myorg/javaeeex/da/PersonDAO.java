package myorg.javaeeex.da;

import java.util.List;
import java.util.Map;

import myorg.javaeeex.bo.Person;

public interface PersonDAO {
    static final String GET_ALL_PEOPLE_QUERY = "getAllPeople";
    Person createPerson(Person person) throws PersonDAOException;
    List<Person> findPeople(String queryName, Map<String, Object> params,
            int index, int count) throws PersonDAOException;
}