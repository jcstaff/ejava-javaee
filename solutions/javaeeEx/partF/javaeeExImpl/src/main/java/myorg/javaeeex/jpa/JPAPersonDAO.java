package myorg.javaeeex.jpa;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import myorg.javaeeex.bo.Address;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.cdi.JavaeeEx;
import myorg.javaeeex.dao.PersonDAO;
import myorg.javaeeex.dao.PersonDAOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JPAPersonDAO implements PersonDAO {
    private static final Log log = LogFactory.getLog(JPAPersonDAO.class);
    
    private EntityManager em;
    
    @Inject 
    public void setEntityManager(@JavaeeEx EntityManager em) {
        this.em = em;
    }

    public Person createPerson(Person person) throws PersonDAOException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("creating person:" + person);
                log.debug("em=" + em);
            }
            em.persist(person);
            log.debug("person created:" + person);
            return person;
        }
        catch (Throwable ex) {
            log.debug("error creating person:" + person, ex);
            throw new PersonDAOException(
                    "error creating person:" + person,ex);
        }
    }
    
    public Person getPerson(long id) {
        return em.find(Person.class, id);
    }
    
    public void removePerson(Person person) {
        em.remove(person);
    }
    
    public void removeAddress(Address address) {
        em.remove(address);
    }
    
    @SuppressWarnings("unchecked")
    public List<Person> findPeopleByName(String firstName, String lastName) {
        return
            em.createNamedQuery(GET_PEOPLE_LIKE_NAME_QUERY)
              .setFirstResult(0)
              .setMaxResults(MAX_RESULTS)
              .setParameter("firstName", firstName)
              .setParameter("lastName", lastName)
              .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Person> findPeople(String queryName,
            Map<String, Object> params, int index, int count)
            throws PersonDAOException {
        try {
            Query query = 
                em.createNamedQuery(queryName)
                  .setFirstResult(index)
                  .setMaxResults(count<=MAX_RESULTS ? count : MAX_RESULTS);
            if (params != null && params.size() != 0) {
                for(String key: params.keySet()) {
                    query.setParameter(key, params.get(key));
                }
            }
            log.debug("named query:" + queryName + ", params=" + params);
            return query.getResultList();
        }
        catch (Throwable ex) {
            throw new PersonDAOException(
                    "error executing named query:" + queryName,ex);
        }
    }
}
