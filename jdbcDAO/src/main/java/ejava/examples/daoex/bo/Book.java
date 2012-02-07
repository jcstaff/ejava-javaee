package ejava.examples.daoex.bo;

import java.io.Serializable;

/**
 * This is an example entity class that will get mapped into the database 
 * using a DAO. Note that the id carries the primary key and should not be 
 * modified after the object is created. This is why setId() is set to private.
 * 
 * @author jcstaff
 */
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private long version;
    private String title;
    private String author;
    private int pages;
    private String description;
    
    public Book(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getPages() {
        return pages;
    }
    public void setPages(int pages) {
        this.pages = pages;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", title=").append(title);
        text.append(", author=").append(author);
        text.append(", pages=").append(pages);
        text.append(", version=").append(version);
        return text.toString();
    }
}
