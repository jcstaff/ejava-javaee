package ejava.examples.orm.rel;

import java.io.Serializable;

import javax.persistence.Column;

public class MediaCopyPK implements Serializable {
    private static final long serialVersionUID = 1L;
    private long mediaId;
    private int copyNo;
    
    public MediaCopyPK() {}
    public MediaCopyPK(long mediaId, int copyNo) {
        this.mediaId = mediaId;
        this.copyNo = copyNo;
    }
    @Column(name="COPY_NO")    
    public int getCopyNo() {
        return copyNo;
    }
    public void setCopyNo(int copyNo) {
        this.copyNo = copyNo;
    }
    @Column(name="MEDIACOPY_MID")
    public long getMediaId() {
        return mediaId;
    }
    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }
    public boolean equals(Object obj) {
        try {
            if (obj == this) return true;
            return ((MediaCopyPK)obj).mediaId == mediaId &&
                   ((MediaCopyPK)obj).copyNo == copyNo;
        }
        catch (Throwable ex) {
            return false;
        }
    }
    public int hashCode() {
        return (int)mediaId + copyNo;
    }    
}
