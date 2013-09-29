package ejava.examples.orm.rel.annotated;

import java.io.Serializable;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides an example of the "inverse side" of a OneToOne 
 * Uni-directional relationship. This object will be "owned" by the Person
 * object.
 * 
 * @author jcstaff
 * $Id:$
 */
@Entity
@Table(name="ORMREL_PHOTO")
public class Photo implements Serializable {
    private static Log log = LogFactory.getLog(Photo.class);
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue @Column(name="PHOTO_ID")
    private long id;
    @Lob
    private byte[] image;
    
    
    public Photo() { log.debug(super.toString() + ": ctor()"); }
    public Photo(byte[] image) { 
        log.debug(super.toString() + ": ctor() image=" + image); 
        this.image = image;
    }
    
    public long getId() {
        log.debug(super.toString() + ": getId()=" + id);
        return id;
    }
    
    public byte[] getImage() { return image; }
    public void setImage(byte[] image) {
        this.image = image;
    }          

    public String toString() {
        long size = (image == null) ? 0 : image.length;
        String sizeText = 
            (image == null) ? "null" : new Long(size).toString() + " bytes";
        return super.toString() +
            ", id=" + id +
            ". image=" + sizeText;
    }
}
