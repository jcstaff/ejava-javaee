package myorg.relex.many2one;

import java.io.Serializable;

import myorg.relex.many2one.LifePhase.Phase;

/**
 * This class represents a primary key for a PersonPhase class,
 * which is an example of a one-to-one relationship where the 
 * dependent (PersonPhase) is derived from the parent table.
 */
public class LifePhasePK implements Serializable { //PK class must be Serializable
	private static final long serialVersionUID = 1L;
	//access to properties determined by entity class for non-embedded
	private int person;          //matches name and type in parent entity 
	private Phase phase; //matches property and type in dependent entity  
	
	public LifePhasePK() {} //PK class must have default ctor
	public LifePhasePK(int id, Phase phase) {
		this.person = id;
		this.phase = phase;
	}
	
	@Override //PK class must define hashCode()
	public int hashCode() { return person + phase.hashCode(); }
	@Override //PK class must define equals()
	public boolean equals(Object obj) {
		try {
			return person==((LifePhasePK)obj).person && 
				   phase.equals(((LifePhasePK)obj).phase);
		} catch (Exception ex) { return false; }
	}
}