package ejava.examples.dao;

import ejava.examples.dao.domain.Author;

public interface AuthorDAO {

    public abstract void create(Author author);

    public abstract Author get(long id);

    public abstract Author getByQuery(long id);

    public abstract Author update(Author author);

    public abstract Author updateByMerge(Author author);

    public abstract void remove(Author author);

}