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
	//@Id
	//private int id;
	@Temporal(TemporalType.DATE)
	private Date hireDate;
	
	@OneToOne(optional=false,fetch=FetchType.LAZY)
	//@PrimaryKeyJoinColumn
	//@MapsId
	@Id
	private Person person;
	
	//public int getId() { return id; }

	public Person getPerson() { return person; }
	public Employee setPerson(Person person) {
		this.person = person; return this;
	}

	public Date getHireDate() { return hireDate; }
	public Employee setHireDate(Date hireDate) {
		this.hireDate = hireDate; return this;
	}
}
