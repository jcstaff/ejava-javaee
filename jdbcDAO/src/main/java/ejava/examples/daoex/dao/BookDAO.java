package ejava.examples.daoex.dao;

import java.util.Collection;

import ejava.examples.daoex.bo.Book;

/**
 * This interface provides an example DAO interface for a Book. Note that
 * the techology associate with the DAO should not be exposed in this 
 * interface. 
 *
 * @author jcstaff
 */
public interface BookDAO {
    /**
     * Add the book to the database.
     */
    Book create(Book book) throws DAOException;

    /**
     * Updates the book in the database with the values in this object.
     */
    Book update(Book book) throws DAOException;

    /**
     * Gets a book from the database by its ID.
     */
    Book get(long id) throws DAOException;

    /**
     * Removes a book from the database.
     */
    boolean remove(Book book) throws DAOException;
    
    /**
     * Returns a collection of books, starting at the index provided and
     * limiting the collection to the count value.
     */
    Collection<Book> findAll(int start, int count) throws DAOException;
}