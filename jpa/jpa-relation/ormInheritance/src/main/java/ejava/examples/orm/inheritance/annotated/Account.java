package ejava.examples.orm.inheritance.annotated;

import javax.persistence.*;

/**
 * This class provides a base class for table-per-class inheritance strategy
 * example. The derived classes will squash the information from this entity
 * within their tables.<p/>
 * 
 * Note too that since there is no physical base table, something external 
 * must be used to create id values or allow the ids of each sub-type to 
 * be allowed to be locally generated (thus getting overlaps in id values).
 * The approach taken here was to define a sequence generator.
 *
 * @author jcstaff
 */
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@SequenceGenerator(
        name="orminhSeq", //required logical name
        sequenceName="ORMINH_SEQ" //name in database
)
public abstract class Account {
    private long id;
    private double balance;

    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="orminhSeq")
    public long getId() {
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }
    
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public void deposit(double amount) throws AccountException {
        setBalance(getBalance() + amount);
    }
    public abstract void withdraw(double amount) throws AccountException;
    public void processInterest() {}
    
    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", id=" + id);
        text.append(", balance=" + balance);
        return text.toString();
    }
}
