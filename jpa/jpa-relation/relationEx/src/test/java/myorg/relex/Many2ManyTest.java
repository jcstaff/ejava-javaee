package myorg.relex;

import static org.junit.Assert.*;


import javax.persistence.*;

import myorg.relex.many2many.Group;
import myorg.relex.many2many.Individual;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

public class Many2ManyTest extends JPATestBase {
    private static Log log = LogFactory.getLog(Many2ManyTest.class);

    /**
     * This test demonstrates the capability to form and work with a many-to-many,
     * uni-directional relationship.
     */
    @Test
    public void testManyToManyUni() {
    	log.info("*** testManyToManyUni ***");
    	
    	log.debug("persisting owner");
    	Group group = new Group("board");
    	em.persist(group);
    	em.flush();
    	log.debug("persisting inverse");
    	Individual individual = new Individual("manny");
    	em.persist(individual);
    	em.flush();
    	log.debug("relating parent to inverse");
    	group.getMembers().add(individual);
    	em.flush();
    	
    	log.debug("getting new instances");
    	em.clear();
    	Group group2 = em.find(Group.class, group.getId());
    	log.debug("checking owner");
    	assertEquals("unexpected group.name", group.getName(), group2.getName());
    	log.debug("checking inverse");
    	assertEquals("unexpected size", 1, group2.getMembers().size());
    	assertEquals("unexpected member.name", individual.getName(), group2.getMembers().iterator().next().getName());
    	
    	log.debug("adding inverse members");
    	Individual individualB = new Individual("moe");    	
    	Individual individualC = new Individual("jack");    	
    	group2.getMembers().add(individualB);
    	group2.getMembers().add(individualC);
    	em.persist(individualB);
    	em.persist(individualC);
    	em.flush();
    	
    	log.debug("adding owning members");
    	Group groupB = new Group("night shift");
    	groupB.getMembers().add(individualB);
    	groupB.getMembers().add(individualC);
    	em.persist(groupB);
    	em.flush();
    	
    	log.debug("checking relations");
    	assertEquals("unexpected relations for member 1", 1, em.createQuery(
    			"select count(g) from Group g where :individual member of g.members", Number.class)
    			.setParameter("individual", individual)
    			.getSingleResult().intValue());
    	assertEquals("unexpected relations for member 2", 2, em.createQuery(
    			"select count(g) from Group g where :individual member of g.members", Number.class)
    			.setParameter("individual", individualB)
    			.getSingleResult().intValue());
    	assertEquals("unexpected relations for member 3", 2, em.createQuery(
    			"select count(g) from Group g where :individual member of g.members", Number.class)
    			.setParameter("individual", individualC)
    			.getSingleResult().intValue());
    	
    	log.debug("removing relations");
    	assertTrue(group2.getMembers().remove(individualB));
    	assertTrue(groupB.getMembers().remove(individualB));
    	log.debug("verifying relation removal");
    	assertEquals("unexpected relations for member 1", 0, em.createQuery(
    			"select count(g) from Group g, IN (g.members) m where m = :individual", Number.class)
    			.setParameter("individual", individualB)
    			.getSingleResult().intValue());

    	log.debug("verifying inverse was not removed");
    	em.flush(); em.clear();
    	assertNotNull(em.find(Individual.class, individualB.getId()));
    	log.debug("removing initial owner");
    	em.remove(em.find(Group.class, group.getId()));
    	em.flush();
    }
}
