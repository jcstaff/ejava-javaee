package myorg.entityex;

import static org.junit.Assert.*;


import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import myorg.entityex.annotated.Dog;
import myorg.entityex.mapped.Animal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AnimalTest {
    private static Log log = LogFactory.getLog(Auto.class);
    private static final String PERSISTENCE_UNIT = "entityEx-test";
    private static EntityManagerFactory emf;
    private EntityManager em;    

    @BeforeClass
    public static void setUpClass() {
        log.debug("creating entity manager factory");
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }
    
    @Before
    public void setUp() throws Exception {
        log.debug("creating entity manager");
        em = emf.createEntityManager();
        cleanup();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        try {
            log.debug("tearDown() started, em=" + em);
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                em.getTransaction().commit();            
            } else if (!em.getTransaction().getRollbackOnly()) {
                em.getTransaction().commit();                        	
            } else {
            	em.getTransaction().rollback();
            }
            em.close();
            log.debug("tearDown() complete, em=" + em);
        }
        catch (Exception ex) {
            log.fatal("tearDown failed", ex);
            throw ex;
        }
     }
    
    @AfterClass
    public static void tearDownClass() {
        log.debug("closing entity manager factory");
        if (emf!=null) { emf.close(); }
    }
    
    public void cleanup() {
        em.getTransaction().begin();
        //delete what we need to here
        em.getTransaction().commit();
    }

    @Test
    public void testCreateAnimal() {
        log.info("testCreateAnimal");
    	Animal animal = new Animal("bessie", 
    			new GregorianCalendar(1960, 1, 1).getTime(), 1400.2);
        em.persist(animal);        
        
        assertNotNull("animal not found", em.find(Animal.class,animal.getId()));
        
        em.flush(); //make sure all writes were issued to DB
        em.clear(); //purge the local entity manager entity cache to cause new instance
        assertNotNull("animal not found", em.find(Animal.class,animal.getId()));
    }

    @Test
    public void testCreateAnimalAnnotated() {
        log.info("testCreateAnimalAnnotated");
    	myorg.entityex.annotated.Animal2 animal = new myorg.entityex.annotated.Animal2("bessie", 
    			new GregorianCalendar(1960, 1, 1).getTime(), 1400.2);
        em.persist(animal);        
        
        assertNotNull("animal not found", em.find(myorg.entityex.annotated.Animal2.class,animal.getId()));
        
        em.flush(); //make sure all writes were issued to DB
        em.clear(); //purge the local entity manager entity cache to cause new instance
        assertNotNull("animal not found", em.find(myorg.entityex.annotated.Animal2.class,animal.getId()));
    }
    
    @Test
    public void testCreateCatMapped() {
    	log.info("testCreateCatMapped");
    	myorg.entityex.mapped.Cat cat = new myorg.entityex.mapped.Cat("fluffy", null, 99.9);
    	em.persist(cat);                                             //get provider to call getters
    	em.flush(); em.detach(cat);
    	cat = em.find(myorg.entityex.mapped.Cat.class, cat.getId()); //get provider to call setters
    }

    @Test
    public void testCreateCatAnnotated() {
    	log.info("testCreateCatAnnotated");
    	myorg.entityex.annotated.Cat2 cat = new myorg.entityex.annotated.Cat2("fluffy", null, 99.9);
    	em.persist(cat);                                                 //get provider to call getters
    	em.flush(); em.detach(cat);
    	cat = em.find(myorg.entityex.annotated.Cat2.class, cat.getId()); //get provider to call setters
    }
    
    @Test
    public void testEnums() {
    	log.info("testEnums");
    	Dog dog = new Dog()
    		.setGender(Dog.Sex.FEMALE)
    		.setColor(Dog.Color.MIX)
    		.setBreed(Dog.Breed.SAINT_BERNARD);
    	em.persist(dog);
    	em.flush();
    	
    	//check the raw value stored in the database
    	Object[] o = (Object[])em.createNativeQuery("select GENDER, COLOR, BREED from ENTITYEX_DOG where id=" + dog.getId())
    			.getSingleResult();
    	log.debug("cols=" + Arrays.toString(o));
    	assertEquals("unexpected gender", Dog.Sex.FEMALE.ordinal(), ((Number)o[0]).intValue());
    	assertEquals("unexpected color", Dog.Color.MIX.name(), ((String)o[1]));
    	assertEquals("unexpected breed", Dog.Breed.SAINT_BERNARD.prettyName, ((String)o[2]));
    	
    	//get a new instance
    	em.detach(dog);
    	Dog dog2 = em.find(Dog.class, dog.getId());
    	assertEquals("unexpected dog gender", dog.getGender(), dog2.getGender());
    	assertEquals("unexpected dog color", dog.getColor(), dog2.getColor());
    	assertEquals("unexpected dog breed", dog.getBreed(), dog2.getBreed());
    }
}
