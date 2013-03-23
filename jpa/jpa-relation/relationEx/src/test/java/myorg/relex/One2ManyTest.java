package myorg.relex;

import static org.junit.Assert.*;


import java.util.ArrayList;
import java.util.List;

import myorg.relex.one2many.Bus;
import myorg.relex.one2many.Rider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

public class One2ManyTest extends JPATestBase {
    private static Log log = LogFactory.getLog(One2ManyTest.class);

    /**
     * This method provides a demonstration of a one-to-many, uni-directional relationship
     * mapped using a join table.
     */
    @Test
    public void testOneToManyUniJoinTable() {
    	log.info("*** testOneToManyUniJoinTable ***");
    	Bus bus = new Bus(302);
    	em.persist(bus);
    	List<Rider> riders = new ArrayList<Rider>();
    	for (int i=0; i<2; i++) {
    		Rider rider = new Rider();
    		rider.setName("rider" + i);
    		em.persist(rider);
    		riders.add(rider);
    	}
    	log.debug("relating entities");
    	bus.getPassengers().addAll(riders);
    	em.flush(); em.clear();
    	
    	log.debug("verify we have expected objects");
    	Bus bus2 = em.find(Bus.class, bus.getNumber());
    	assertNotNull("bus not found", bus2);
    	for (Rider r: bus.getPassengers()) {
    		assertNotNull("rider not found", em.find(Rider.class, r.getId()));
    	}
    	log.debug("verify they are related");
    	assertEquals("unexpected number of riders", bus.getPassengers().size(), bus2.getPassengers().size());
    }
}
