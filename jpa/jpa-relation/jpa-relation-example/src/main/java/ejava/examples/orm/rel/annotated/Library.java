package ejava.examples.orm.rel.annotated;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

/** 
 * This class provides an example of using the java.util.Map with 
 * relationships.
 *
 * @author jcstaff
 */
@Entity @Table(name="ORMREL_LIBRARY")
public class Library {
    private long id;
    private Map<Long, Borrower> borrowers = new HashMap<Long, Borrower>();
    
    public Library() {}
    public Library(long id) { this.id = id; }
    
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @MapKey(name="id")
    public Map<Long, Borrower> getBorrowers() {
        return borrowers;
    }
    public void setBorrowers(Map<Long, Borrower> borrowers) {
        this.borrowers = borrowers;
    }
    @Id @GeneratedValue
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", id=" + id);
        text.append(", borrowers(" + borrowers.size() + ")={");
        for(long bid: borrowers.keySet()) {
            text.append("id=" + bid + "{" +
                    borrowers.get(bid).getName() +
                    "}, ");
        }
        text.append("}");
        return text.toString();
    }

}
