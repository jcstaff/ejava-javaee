package ejava.examples.orm.core.mapped;

import javax.persistence.*;

/**
 * This class provides an example of embedding a primary key class within
 * the conatining class.
 */
@Entity
@Table(name="ORMCORE_NAPSACK")
public class Napsack {
    private NapsackPK pk;
    private int size;
    
    public Napsack() {}
    public Napsack(String make, String model) {
        this.pk = new NapsackPK(make, model);
    }
    
    @EmbeddedId
    public NapsackPK getPk() { return pk; }
    public void setPk(NapsackPK pk) {
        this.pk = pk;
    }
    
    public int getSize() { return size; }
    public void setSize(int size) {
        this.size = size;
    }
    
    public String toString() {
        return super.toString() +
           "pk=" + pk +
           ", size=" + size;
    }
}
