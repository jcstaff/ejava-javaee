package myorg.relex.collection;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

/**
 * This entity class provides an example of an ordered list of child entities ordered by a business property
 * in the child entity.
 */
@Entity
@Table(name="RELATIONEX_PATH")
public class Path {
	@Id @GeneratedValue
	private int id;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@OrderBy("number")
	private Collection<Segment> segments;
	
	@Column(length=16)
	private String name;

	public int getId() { return id; }

	public Collection<Segment> getSegments() {
		if (segments==null) { segments = new LinkedList<Segment>(); }
		return segments; 
	}
	public Path addSegment(Segment segment) {
		getSegments().add(segment);
//		Collections.sort(segments);
		return this;
	}

	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
}
