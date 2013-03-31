package myorg.relex.collection;


import javax.persistence.*;

/**
 * This class is provides an example of an entity that implements hashcode/equals 
 * using its business identity. Note that it is not always easy to derive a business Id
 * in all cases. 
 */
@Entity
@Table(name="RELATIONEX_SHIP")
public class ShipByBusinessId extends Ship {
	@Override
	public int peekHashCode() {
		return name==null ? 0 : name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		try {
			boolean equals = name.equals(((ShipByBusinessId)obj).name);
			return logEquals(obj, equals);
		} catch (Exception ex) {
			return logEquals(obj, false);
		}
	}
}
