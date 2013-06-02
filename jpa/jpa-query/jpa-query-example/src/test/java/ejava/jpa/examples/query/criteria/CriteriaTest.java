package ejava.jpa.examples.query.criteria;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.jpa.examples.query.Customer;
import ejava.jpa.examples.query.QueryBase;

public class CriteriaTest extends QueryBase {
	private static final Log log = LogFactory.getLog(CriteriaTest.class);

    private <T> List<T> executeQuery(CriteriaQuery<T> qdef) {
        return executeQuery(qdef, null);
    }

    private <T> List<T> executeQuery(CriteriaQuery<T> qdef, 
            Map<String, Object> params) {
        TypedQuery<T> query = em.createQuery(qdef);
        if (params != null && !params.isEmpty()) {
            for(String key: params.keySet()) {
                query.setParameter(key, params.get(key));
            }
        }
        List<T> objects = query.getResultList();
        for(T o: objects) {
           log.info("found result:" + o);
        }
        return objects;
    }
	
	@Test
    public void testSimpleSelect() {
        log.info("*** testSimpleSelect() ***");
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> qdef = cb.createQuery(Customer.class);
        
        //"select object(c) from Customer as c"
        Root<Customer> c = qdef.from(Customer.class);        
        qdef.select(c);
        
        int rows = executeQuery(qdef).size();
        assertTrue("unexpected number of customers:" + rows, rows > 0);
    }
}
