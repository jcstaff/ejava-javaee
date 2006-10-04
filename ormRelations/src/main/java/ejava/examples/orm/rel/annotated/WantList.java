package ejava.examples.orm.rel.annotated;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;

/**
 * This class is an example of a uni-directional ManyToMany relationship. 
 * This class both "owns" the relationship to Media and is the only direction
 * in which it can be navigated. There are many Media in a WantList and
 * many WantLists associated with a Media.
 *
 * @author jcstaff
 */
@Entity @Table(name="ORMREL_WANTED")
public class WantList {
    private long id;
    private Collection<Media> media = new ArrayList<Media>();
    
    @Id @GeneratedValue
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    
    @ManyToMany
    @JoinTable(name="ORMREL_WANTED_MEDIA") //define table, 
                                           //but let columns use default names
    public Collection<Media> getMedia() {
        return media;
    }
    public void setMedia(Collection<Media> media) {
        this.media = media;
    }
}
