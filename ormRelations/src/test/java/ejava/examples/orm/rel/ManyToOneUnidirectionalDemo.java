package ejava.examples.orm.rel;

import static org.junit.Assert.*;

import org.junit.Test;

import ejava.examples.orm.rel.annotated.Media;
import ejava.examples.orm.rel.annotated.MediaCopy;

/**
 * 
 * @author jcstaff
 * $Id:$
 */
public class ManyToOneUnidirectionalDemo extends DemoBase {
    
    /**
     */
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     */
	@Test
    public void testCreateManyToOne() {
        log.info("testCreateManyToOne");
        ejava.examples.orm.rel.annotated.Media media = new Media();
        media.setTitle("EJB3");
        
        //add media to DB
        assertTrue(media.getId() == 0);
        em.persist(media);
        log.info("created media:" + media);
        assertTrue(media.getId() != 0);
        
        //create some copies 
        for(int i=0; i<5; i++) {
            ejava.examples.orm.rel.annotated.MediaCopy mc = 
                new MediaCopy(media, i);
            assertNotNull(mc.getMedia());
            assertEquals(i, mc.getCopyNo());
            em.persist(mc);
            log.info("created copy:" + mc);
        }
        
        em.flush();
        em.clear();
        
        for(int i=0; i<5; i++) {
            ejava.examples.orm.rel.MediaCopyPK pk =
                new MediaCopyPK(media.getId(), i);
            MediaCopy mc = em.find(MediaCopy.class, pk);
            log.info("found media copy:" + mc);
            assertNotNull("media copy not found:" + pk, mc);
            assertEquals("unexpected mediaId:" + mc.getMediaId(),
                    mc.getMediaId(), media.getId());
            assertEquals("unexpected copyNo:" + mc.getCopyNo(), 
                    mc.getCopyNo(), i);
            assertNotNull("no media resolved", mc.getMedia());
        }
   }    
}