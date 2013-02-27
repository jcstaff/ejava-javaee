package ejava.examples.orm.rel.annotated;

import java.io.Serializable;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.orm.rel.MediaCopyPK;

/**
 * This version of MediaCopy uses the @MapsId annotatioon to link the 
 * relationship and primary key properties together. This technique 
 * was added in JPA 2.0 and is stated to be preferred over the JPA 1.0
 * indirect technique.
 */

@Entity @Table(name="ORMREL_MEDIACOPY2")
@IdClass(MediaCopyPK.class)
public class MediaCopy2 implements Serializable {
    private static final Log log = LogFactory.getLog(MediaCopy2.class);
    private static final long serialVersionUID = 1L;    
    @Id //mapped to COPY_NO by IdClass
    private int copyNo;    
    @Id //mapped to MEDIACOPY_MID by IdClass
    private long mediaId;    
    @ManyToOne 
    @MapsId("mediaId") //maps mediaId property to relationship column
    @JoinColumn(name="MEDIACOPY_MID")
    private Media media;
    
    @SuppressWarnings("unused")
    private MediaCopy2() { log.debug(super.toString() + ": ctor()"); }
    public MediaCopy2(Media media, int copyNo) {
        log.debug(super.toString() + ": ctor() mediaId="
                + media.getId() + ", copyNo=" + copyNo);
        setMedia(media);
        setMediaId(media.getId());
        setCopyNo(copyNo);
    }    

    public int getCopyNo()                { return copyNo; }    
    private void setCopyNo(int copyNo)    { this.copyNo = copyNo; }
    
    public long getMediaId()              { return media.getId();} //mediaId; }
    private void setMediaId(long mediaId) { this.mediaId = mediaId; }
    
    public Media getMedia()               { return media; }    
    private void setMedia(Media media)    { this.media = media; }

    public String toString() {
        return super.toString() +
            ", mediaId=" + getMediaId() +
            ", copyNo=" + getCopyNo() +
            ", media=" + getMedia();
    }
}
