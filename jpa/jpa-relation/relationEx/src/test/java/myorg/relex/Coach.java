package myorg.relex;

import javax.persistence.*;

import myorg.relex.one2one.Person;

@Entity
@Table(name="RELATIONEX_COACH")
public class Coach {
	public enum Type {HEAD, ASSISTANT };
	@Id 
	private int id;
	@Enumerated(EnumType.STRING) @Column(length=16)
	private Type type;
	@OneToOne(optional=false)
	@MapsId //informs provider the PK is derived from FK
	private Person person;
	
	public int getId() { return person==null ? 0 : person.getId(); }

	public Type getType() { return type; }
	public void setType(Type type) {
		this.type = type;
	}
	
	public Person getPerson() { return person; }
	public void setPerson(Person person) {
		this.person = person;
	}
}
