package ejava.jpa.examples.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;

@Entity @Table(name="JPAQL_CLERK")
public class Clerk {
    @Id @GeneratedValue @Column(name="CLERK_ID")
    private long id;

    @Column(length=16, nullable=false)
    private String firstName;
    @Column(length=16, nullable=false)
    private String lastName;
    @Temporal(TemporalType.DATE)
    private Date hireDate;
    @Temporal(TemporalType.DATE)
    private Date termDate;

    @ManyToMany(mappedBy="clerks", fetch=FetchType.LAZY)
    private Collection<Sale> sales = new ArrayList<Sale>();

    public long getId() { return id; }

    public Collection<Sale> getSales() { return sales; }
    public Clerk setSales(Collection<Sale> sales) {
        this.sales = sales;
        return this;
    }
    public Clerk addSale(Sale...sale) {
    	if (sale != null) {
    		for (Sale s : sale) {
    			if (s != null) { sales.add(s); }
    		}
    	}
    	return this;
    }
    
    public Date getHireDate() { return hireDate; }
    public Clerk setHireDate(Date hireDate) {
        this.hireDate = hireDate;
        return this;
    }
    
    public Date getTermDate() { return termDate; }
    public Clerk setTermDate(Date termDate) {
        this.termDate = termDate;
        return this;
    }    

    public String getFirstName() { return firstName; }
    public Clerk setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }    
    
    public String getLastName() { return lastName; }
    public Clerk setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("firstName=" + firstName);
        text.append(", lastName=" + lastName);
        text.append(", hireDate=" + hireDate);
        text.append(", termDate=" + termDate);
        text.append(", sales(" + sales.size() + ")={");
        for(Sale s : sales) {
            text.append(s.getId() + ", ");
        }
        text.append("}");
        return text.toString();
    }
}
