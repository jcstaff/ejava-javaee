package ejava.examples.orm.rel.annotated;

import java.io.Serializable;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.orm.rel.MediaCopyPK;

@Entity @Table(name="ORMREL_MEDIACOPY")
@IdClass(MediaCopyPK.class)
public class MediaCopy implements Serializable {
    private static final Log log = LogFactory.getLog(MediaCopy.class);
    private static final long serialVersionUID = 1L;
    
    private int copyNo;    
    private Media media;
    
    private MediaCopy() { log.debug(super.toString() + ": ctor()"); }
    public MediaCopy(Media media, int copyNo) {
        log.debug(super.toString() + ": ctor() mediaId=" 
                + media.getId() + ", copyNo=" + copyNo);
        this.media = media;
        this.copyNo = copyNo;
    }    

    //@Id
    //@Column(name="MEDIACOPY_MIDY")
    //public long getMediaId() { return media.getId(); }
    //public void setMediaId(long mediaId) {}

    @Id @Column(name="COPYNO")
    @SuppressWarnings("unused")
    private void setCopyNo(int copyNo) {
        this.copyNo = copyNo;
    }
    public int getCopyNo() {
        return copyNo;
    }
    
    @Id
    @AttributeOverride(name="mediaId", column=@Column(name="MEDIACOPY_MID"))
    @ManyToOne(optional=false)
    @JoinColumn(name="MEDIACOPY_MID")
    public Media getMedia() {
        return media;
    }    
    public void setMedia(Media media) {
        this.media = media;
    }
}
