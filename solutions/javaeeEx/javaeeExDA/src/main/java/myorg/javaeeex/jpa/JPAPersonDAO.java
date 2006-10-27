package myorg.javaeeex.jpa;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import myorg.javaeeex.bo.Person;
import myorg.javaeeex.da.PersonDAO;
import myorg.javaeeex.da.PersonDAOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JPAPersonDAO implements PersonDAO {
    private static Log log = LogFactory.getLog(JPAPersonDAO.class); 

    public Person createPerson(Person person) throws PersonDAOException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("creating person:" + person);
                log.debug("em=" + JPAUtil.getEntityManager());
            }
            JPAUtil.getEntityManager().persist(person);
            log.debug("person created:" + person);
            return person;
        }
        catch (Throwable ex) {
            log.debug("error creating person:" + person, ex);
            throw new PersonDAOException(
                    "error creating person:" + person,ex);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Person> findPeople(String queryName,
            Map<String, Object> params, int index, int count)
            throws PersonDAOException {
        try {
            Query query = 
                JPAUtil.getEntityManager().createNamedQuery(queryName)
                                          .setFirstResult(index)
                                          .setMaxResults(count);
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