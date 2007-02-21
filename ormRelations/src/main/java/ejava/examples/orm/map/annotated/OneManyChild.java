package ejava.examples.orm.map.annotated;

import javax.persistence.*;

/**
 * This class represents a child in a One-to-Many, uni-directional
 * relationship, relative to the parent. This class makes no reference
 * to the relationship. It is only known to the parent.
 * 
 * @author jcstaff
 *
 */
@Entity @Table(name="ORMMAP_ONEMANY_CHILD")
public class OneManyChild {
	private String name;
	
	protected OneManyChild() {}
	public OneManyChild(String name) {
		super();
		this.name = name;
	}

	@Id @Column(name="ID")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		StringBuilder text = new StringBuilder();
		text.append(getClass().getName());
		text.append(", name=" + name);
		return text.toString();
	}
}
