package myorg.relex.one2one;

import javax.persistence.*;

/**
 * Provides example of one-to-one unidirectional relationship 
 * using foreign key.
 */
@Entity
@Table(name="RELATIONEX_PLAYER")
public class Player {
	public enum Position { DEFENSE, OFFENSE, SPECIAL_TEAMS};
	@Id
	private int id;
	@Enumerated(EnumType.STRING)
	private Position position;
	
	@OneToOne(optional=false,fetch=FetchType.EAGER)
	@JoinColumn(name="PERSON_ID", unique=true)
	private Person person;
	
	public int getId() { return id; }

	public Person getPerson() { return person; }
	public Player setPerson(Person person) {
		this.person = person; return this;
	}

	public Position getPosition() { return position; }
	public Player setPosition(Position position) {
		this.position = position; return this;
	}
}
