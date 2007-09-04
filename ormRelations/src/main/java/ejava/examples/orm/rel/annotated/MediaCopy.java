package ejava.examples.orm.rel.annotated;

import java.io.Serializable;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.orm.rel.MediaCopyPK;

/**
 * This class provides an example of a ManyToOne mapping. The media copy owns
 * the relationship to the media. Therefore the foreign key to media is within
 * the media copy database table.<p/>
 * 
 * This class has an unrelated technical issue worth mentioning. 
 * Everything would have worked out fine if we created a simple primary key
 * field for the media copy and a separate foreign key to the media. Instead, 
 * we pretended to encounter a composite primary key where one of the values
 * doubled as the foreign key to the media.<p/>
 * 
 * <pre>
 * MediaCopy (pk=MEDIACOPY_MID + COPYNO)
 *     MEDIACOPY_MID (fk to Media.MEDIA_ID) 
 *     COPYNO
 *     ...
 * Media (pk=MEDIA_ID)
 *     MEDIA_ID
 *     ...
 * </pre>
 * 
 * The composite primary key is based on mediaId and copyNo. However, the 
 * mediaId is derived from the assigned media object from the ManyToOne 
 * relationship. That means that the interface to this class wants to use
 * set(media, copyNo) and primary key processing wants to use 
 * set(mediaId, copyNo). The way around this was two mappings related to
 * media; mediaId is defined as an @Id field mapped to a MEDIACOPY_MID column.
 * media is defined as a @ManyToOne field and also mapped to the MEDIACOPY_MID
 * column. That means when one is set, both uses of the field are set. <p/>
 * 
 * This sounds fine, but there are two things that may go wrong. The first is
 * that we need to make sure that the dual setMediaId() and setMedia() don't 
 * try to modify the primary key field (with what would be the exact same 
 * value) after it has been initially set. The second is probably a side-effect 
 * of the first; JBoss ignores the common column mapping and creates a 
 * unique column name for MEDIAID, separate from MEDIACOPY_MID.<p/>
 * 
 * The net result is we do one of the following: honor a legacy DB schema of 
 * mediaId/copyNo or honor Java interface semantics of media/copyNo. The 
 * way to do the first is to mark media as @Transient. That means the value
 * has a chance of being null unless it is repaired by the DAO. The way to
 * do the second is to mark the field as @ManyToOne. That means we may get an
 * extra DB column for this field or stand a chance at an illegal double set
 * of a primary key field.<p/>
 * 
 * Since this is part of a @ManyToOne demo, we will allow the DB schema to
 * augmented. @JoinColumn versus @PrimaryKeyJoinColum was used to map the
 * local MEDIACOPY_MID to the primary key of the Media object. This was done
 * so we could have better control over the semantics of the MEDIACOPY_MID
 * column; which didn't seem to be set correct when using the other approach.
 * We want it to be required and not updatable.
 *
 * @author jcstaff
 */

@Entity @Table(name="ORMREL_MEDIACOPY")
@IdClass(MediaCopyPK.class)
public class MediaCopy implements Serializable {
    private static final Log log = LogFactory.getLog(MediaCopy.class);
    private static final long serialVersionUID = 1L;    
    private int copyNo;    
    private long mediaId;    
    private Media media;
    
    @SuppressWarnings("unused")
    private MediaCopy() { log.debug(super.toString() + ": ctor()"); }
    public MediaCopy(Media media, int copyNo) {
        log.debug(super.toString() + ": ctor() mediaId="
                + media.getId() + ", copyNo=" + copyNo);
        setMedia(media);
        setMediaId(media.getId());
        setCopyNo(copyNo);
    }    

    @Id @Column(name="COPYNO")
    public int getCopyNo()                { return copyNo; }    
    @SuppressWarnings("unused")
    private void setCopyNo(int copyNo)    { this.copyNo = copyNo; }
    
    //this property is used for the composite primary key
    @Id @Column(name="MEDIACOPY_MID")
    public long getMediaId()              { return mediaId; }
    @SuppressWarnings("unused")
    private void setMediaId(long mediaId) { this.mediaId = mediaId; }
    
    @ManyToOne(optional=false) //use m2o to have media automatically associated
    @JoinColumn(
            name="MEDIACOPY_MID", referencedColumnName="MEDIA_ID",
            nullable=false, updatable=false)
    //@Transient//mark transient to avoid extra col that violates legacy schema
    public Media getMedia()               { return media; }    
    @SuppressWarnings("unused")
    private void setMedia(Media media)    { this.media = media; }

    public String toString() {
        return super.toString() +
            ", mediaId=" + mediaId +
            ", copyNo=" + copyNo +
            ", media=" + media;
    }
}
