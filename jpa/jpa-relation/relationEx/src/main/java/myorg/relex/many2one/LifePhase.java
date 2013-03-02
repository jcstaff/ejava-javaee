package myorg.relex.many2one;

import javax.persistence.*;

import myorg.relex.one2one.Person;


/**
 * This class represents a dependent in a one-to-one uni-directional
 * relationship who derives part of its compound primary key from the 
 * identity in the parent.
 */
@Entity
@Table(name="RELATIONEX_LIFEPHASE")
@IdClass(LifePhasePK.class)
public class LifePhase {
	public static enum Phase { CHILD, TEEN, ADULT };
	@Id
	@OneToOne
	private Person person;
	@Id 
	@Enumerated(EnumType.STRING) @Column(length=8)
	private Phase phase;
	
	protected LifePhase() {}
	public LifePhase(Person person, Phase phase) {
		this.person = person;
		this.phase = phase;
	}
	
	public Person getPerson() { return person; }
	public Phase getPhase() { return phase; }
}
