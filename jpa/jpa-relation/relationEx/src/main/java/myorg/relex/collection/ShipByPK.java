package myorg.relex.collection;


import javax.persistence.*;

/**
 * This class is provides an example of an entity that implements hashcode/equals 
 * using its database assigned primary key. Note the PK is not assigned until the 
 * entity is inserted into the database. 
 */
@Entity
@Table(name="RELATIONEX_SHIP")
public class ShipByPK extends Ship {
	@Override
	public int peekHashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			boolean equals = id==((ShipByPK)obj).id;
			return logEquals(obj, equals);
		} catch (Exception ex) {
			return logEquals(obj, false);
		}
	}
}
