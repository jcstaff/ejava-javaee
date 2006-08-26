package ejava.examples.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.dao.domain.Author;

/**
 * This class implements a DAO using javax.persistence.EntityManager. Most
 * of the work of mapping the objects to the database is being performed in
 * either the @Entity class or in a orm.xml descriptor file. The caller of 
 * this object must manage the transaction scope. The EntityManagers can be
 * injected when used in a container. This example used a simple setter in the
 * base class so that it can run outside of a container.
 * 
 * @author jcstaff
 * $Id:$
 */
public class JPAAuthorDAO extends JPADAOBase {
    private static Log log_ = LogFactory.getLog(JPAAuthorDAO.class);
    private EntityManager em = getEntityManager();

    
    public void create(Author author) {
        em.persist(author);
    }
    
    public Author get(long id) {
        return em.find(Author.class, id);
    }
    
    public Author getByQuery(long id) throws Exception {
        Query query = em.createQuery("from jpaAuthor where id=" + id);
        return (Author)query.getSingleResult();
    }
    
    public Author update(Author author) {
        Author dbAuthor = em.find(Author.class, author.getId());
        dbAuthor.setFirstName(author.getFirstName());
        dbAuthor.setLastName(author.getLastName());
        dbAuthor.setSubject(author.getSubject());
        dbAuthor.setPublishDate(author.getPublishDate());
        return dbAuthor;
    }

    public Author updateByMerge(Author author) {
        return em.merge(author);
    }
    
    public void remove(Author author) {
        em.remove(author);
    }
}
