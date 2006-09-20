package ejava.examples.dao.jpa;

import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.dao.AuthorDAO;
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
public class JPAAuthorDAO extends JPADAOBase implements AuthorDAO {
    @SuppressWarnings("unused")
    private static Log log_ = LogFactory.getLog(JPAAuthorDAO.class);
    
    /* (non-Javadoc)
     * @see ejava.examples.dao.jpa.AuthorDAO#create(ejava.examples.dao.domain.Author)
     */
    public void create(Author author) {
        getEntityManager().persist(author);
    }
    
    /* (non-Javadoc)
     * @see ejava.examples.dao.jpa.AuthorDAO#get(long)
     */
    public Author get(long id) {
        return getEntityManager().find(Author.class, id);
    }
    
    /* (non-Javadoc)
     * @see ejava.examples.dao.jpa.AuthorDAO#getByQuery(long)
     */
    public Author getByQuery(long id) {
        Query query = getEntityManager().
            createQuery("from jpaAuthor where id=" + id);
        return (Author)query.getSingleResult();
    }
    
    /* (non-Javadoc)
     * @see ejava.examples.dao.jpa.AuthorDAO#update(ejava.examples.dao.domain.Author)
     */
    public Author update(Author author) {
        Author dbAuthor = getEntityManager().find(Author.class, author.getId());
        dbAuthor.setFirstName(author.getFirstName());
        dbAuthor.setLastName(author.getLastName());
        dbAuthor.setSubject(author.getSubject());
        dbAuthor.setPublishDate(author.getPublishDate());
        return dbAuthor;
    }

    /* (non-Javadoc)
     * @see ejava.examples.dao.jpa.AuthorDAO#updateByMerge(ejava.examples.dao.domain.Author)
     */
    public Author updateByMerge(Author author) {
        return getEntityManager().merge(author);
    }
    
    /* (non-Javadoc)
     * @see ejava.examples.dao.jpa.AuthorDAO#remove(ejava.examples.dao.domain.Author)
     */
    public void remove(Author author) {
        getEntityManager().remove(author);
    }
}
