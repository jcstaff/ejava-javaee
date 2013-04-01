package myorg.relex;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import javax.persistence.*;

import myorg.relex.collection.Fleet;
import myorg.relex.collection.Ship;
import myorg.relex.collection.ShipByBusinessId;
import myorg.relex.collection.ShipByDefault;
import myorg.relex.collection.ShipByPK;
import myorg.relex.collection.ShipBySwitch;

import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;

import org.junit.*;


public class CollectionTest extends JPATestBase {
    private static Log log = LogFactory.getLog(CollectionTest.class);

    /**
     * This test will demonstrate how instances that use the default java.lang.Object
     * hashCode/equals will only be equal is when they are the same instance.
     */
    @Test
    public void testDefault() {
    	log.info("*** testDefault ***");
    	
    	Ship ship1 = new ShipByDefault();
    	Ship ship2 = new ShipByDefault();
    	assertFalse("unexpected hashCode", ship1.hashCode() == ship2.hashCode());
    	assertFalse("unexpected equality", ship1.equals(ship2));
    	
    	log.debug("persisting entity");
    	em.persist(ship1);
    	em.flush();
    	Ship ship3 = em.find(ShipByDefault.class, ship1.getId());
    	assertTrue("unexpected hashCode", ship1.hashCode() == ship3.hashCode());
    	assertTrue("unexpected inequality", ship1.equals(ship3));

    	log.debug("getting new instance of entity");
    	em.clear();
    	Ship ship4 = em.find(ShipByDefault.class, ship1.getId());
    	assertFalse("unexpected hashCode", ship1.hashCode() == ship4.hashCode());
    	assertFalse("unexpected equality", ship1.equals(ship4));
    	
    	/*
    	log.debug("persisting entity within collection");
    	Fleet fleet = new Fleet();
        fleet.getShipsListByDefault().add((ShipByDefault)ship1);
        assertTrue(fleet.getShipsListByDefault().contains(ship1));
        em.persist(fleet);
        em.flush();

        assertTrue(fleet.getShipsListByDefault().contains(ship1));
        assertFalse(fleet.getShipsListByDefault().contains(ship2));
        assertTrue(em.contains(ship1));
        assertFalse(em.contains(ship2));
        */
    }
}