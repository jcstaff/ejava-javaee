package ejava.examples.orm.inheritance;

import java.util.List;
import java.util.Date;

import ejava.examples.orm.inheritance.annotated.Bread;
import ejava.examples.orm.inheritance.annotated.Product;
import ejava.examples.orm.inheritance.annotated.Soup;

/**
 * This class provides a demonstration of using a single table for the 
 * entire class hierachy.
 *
 * @author jcstaff
 */
public class SingleTablePerHierarchyDemo extends DemoBase {

    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();
        List<Product> products = 
            em.createQuery("select p from Product p").getResultList();
        for(Product p: products) {
            em.remove(p);
        }
        em.flush();
        em.getTransaction().commit();
        em.getTransaction().begin();
    }

    @SuppressWarnings("unchecked")
    public void testSingleTablePerHierarchyCreate() {
        log.info("testSingleTablePerHierarchyCreate");
        
        ejava.examples.orm.inheritance.annotated.Soup soup = new Soup();
        soup.setCost(2.12);
        final long lifetime = 365L*24*60*60*1000; 
        soup.setExpiration(new Date(System.currentTimeMillis() + lifetime)); 
        soup.setSoupType(Soup.SoupType.CHICKEN_NOODLE);
        em.persist(soup);
        
        ejava.examples.orm.inheritance.annotated.Bread bread = new Bread();
        bread.setBakedOn(new Date());
        bread.setCost(2.25);
        bread.setSlices(24);
        em.persist(bread);
        
        em.flush();
        em.clear();
        assertFalse("bread still managed", em.contains(bread));
        assertFalse("soup still managed", em.contains(soup));
        
        List<Product> products = 
            em.createQuery("select p from Product p").getResultList();
        assertTrue("unexpected number of products:" + products.size(),
                products.size() == 2);
        for(Product p: products) {
            log.info("product found:" + p);
        }        
        
        //query specific tables for columns
        int rows = em.createNativeQuery(
                "select ID, TYPE, COST, SOUPTYPE, EXPIRATION, BAKEDON, SLICES " +
                " from ORMINH_PRODUCT")
                .getResultList().size();
        assertEquals("unexpected number of product rows:" + rows, 2, rows);
    }
}
