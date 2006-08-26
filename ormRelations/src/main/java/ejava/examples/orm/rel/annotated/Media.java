package ejava.examples.orm.rel.annotated;

import java.io.Serializable;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class represents a media topic, for which there are many copies and
 * authors.<b/>
 * 
 * It is an example of a ManyToOne uni-directional relationship. There are
 * zero to MediaCopy(s) for each Media. However, there is no reference to 
 * the copies from Media. All access to the copies must come through querying
 * the MediaCopies. The EJB-QL examples for how this is done.
 * 
 * @author jcstaff
 * $Id:$
 */
@Entity @Table(name="ORMREL_MEDIA")
public class Media implements Serializable {
    private static Log log = LogFactory.getLog(Media.class);
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue @Column(name="MEDIA_ID")
    private long id;
    private String title;
    
    public Media() { log.debug(super.toString() + ": ctor()"); }

    public long getId() {
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String toString() {
        return super.toString() +
            ", id=" + id +
            ", title=" + title;
    }
}
