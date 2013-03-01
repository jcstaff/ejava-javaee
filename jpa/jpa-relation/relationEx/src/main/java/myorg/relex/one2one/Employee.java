package myorg.relex.one2one;

import java.util.Date;

import javax.persistence.*;

/**
 * Provides example of one-to-one unidirectional relationship 
 * using a primary key join.
 */
@Entity
@Table(name="RELATIONEX_EMPLOYEE")
public class Employee {
	@Id
	private int id;
	@Temporal(TemporalType.DATE)
	private Date hireDate;
	
	@OneToOne(optional=false,fetch=FetchType.LAZY)
	@PrimaryKeyJoinColumn //informs provider the FK is derived from PK
	private Person person;
	
	public int getId() { return person.getId(); }

	public Person getPerson() { return person; }
	public void setPerson(Person person) {
		this.person = person;
		if (person != null) { id = person.getId(); }
	}

	public Date getHireDate() { return hireDate; }
	public void setHireDate(Date hireDate) {
		this.hireDate = hireDate;
	}
}
