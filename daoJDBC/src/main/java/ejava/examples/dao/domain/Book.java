package ejava.examples.dao.domain;

import java.io.Serializable;

public class Book implements Serializable {
	private long id;
    private long version = 0;
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
        text.append(", title=" + title);
        text.append(", author=" + author);
        text.append(", pages=" + pages);
        text.append(", version=" + version);
        return text.toString();
    }
}
