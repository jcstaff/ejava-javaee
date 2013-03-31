package myorg.relex.collection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides an example one/parent entity with a relationship to many child/dependent
 * objects -- with the members in each collection based on a different hashCode/equals method.
 */
@Entity
public class Fleet {
	private static Log log = LogFactory.getLog(Fleet.class);
	
	@Id @GeneratedValue
	private int id;
	@Column(length=16)
	private String name;
	@OneToMany(cascade=CascadeType.PERSIST) 
	@JoinColumn
	private List<ShipByDefault> shipsListByDefault = new ArrayList<ShipByDefault>();
	
	@OneToMany(cascade=CascadeType.PERSIST)  
	@JoinColumn
	private Set<ShipByDefault> shipsSetByDefault = new HashSet<ShipByDefault>();

	@OneToMany(cascade=CascadeType.PERSIST)  
	@JoinColumn
	private Set<ShipByDefault> shipsSortedSetByDefault = new TreeSet<ShipByDefault>(
			new Comparator<ShipByDefault>() {
				public int compare(ShipByDefault o1, ShipByDefault o2) {
	        		int result = o1.hashCode() - o2.hashCode();
					log.debug("compare(" + o1 + ", " + o2 + ")=" + result);
					return result;
				}
			});

	@OneToMany(cascade=CascadeType.PERSIST) 
	@JoinColumn
	private List<ShipByPK> shipsListByPK = new ArrayList<ShipByPK>();
	
	@OneToMany(cascade=CascadeType.PERSIST)  
	@JoinColumn
	private Set<ShipByPK> shipsSetByPK = new HashSet<ShipByPK>();

	@OneToMany(cascade=CascadeType.PERSIST)  
	@JoinColumn
	@OrderBy("id")
	private Set<ShipByPK> shipsSortedSetByPK = new TreeSet<ShipByPK>(			
			new Comparator<ShipByPK>() {
			public int compare(ShipByPK o1, ShipByPK o2) {
	    		int result = o1.id - o2.id;
				log.debug("compare(" + o1 + ", " + o2 + ")=" + result);
				return result;
			}
		});

	@OneToMany(cascade=CascadeType.PERSIST) 
	@JoinColumn
	private List<ShipBySwitch> shipsListBySwitch = new ArrayList<ShipBySwitch>();
	
	@OneToMany(cascade=CascadeType.PERSIST)  
	@JoinColumn
	private Set<ShipBySwitch> shipsSetBySwitch = new HashSet<ShipBySwitch>();

	@OneToMany(cascade=CascadeType.PERSIST)  
	@JoinColumn
	@OrderBy("id")
	private Set<ShipBySwitch> shipsSortedSetBySwitch = new TreeSet<ShipBySwitch>(			
			new Comparator<ShipBySwitch>() {
			public int compare(ShipBySwitch o1, ShipBySwitch o2) {
				int result = (o1.id != 0 && o2.id != 0) ? 
						o1.id - o2.id :
						o1.hashCode() - o2.hashCode();
				log.debug("compare(" + o1 + ", " + o2 + ")=" + result);
				return result;
			}
		});


	@OneToMany(cascade=CascadeType.PERSIST) 
	@JoinColumn
	private List<ShipByBusinessId> shipsListByBusinessId = new ArrayList<ShipByBusinessId>();
	
	@OneToMany(cascade=CascadeType.PERSIST)  
	@JoinColumn
	private Set<ShipByBusinessId> shipsSetByBusinessId = new HashSet<ShipByBusinessId>();

	@OneToMany(cascade=CascadeType.PERSIST)  
	@JoinColumn
	@OrderBy("name")
	private Set<ShipByBusinessId> shipsSortedSetByBusinessId = new TreeSet<ShipByBusinessId>(			
			new Comparator<ShipByBusinessId>() {
			public int compare(ShipByBusinessId o1, ShipByBusinessId o2) {
				int result=0;
				try { result=o1.getName().compareTo(o2.getName()); }
				catch (NullPointerException npe) {}
				
				log.debug("compare(" + o1 + ", " + o2 + ")=" + result);
				return result;
}
		});

	public int getId() { return id; }
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() { return name;}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<ShipByDefault> getShipsListByDefault() { return shipsListByDefault; }
	public Set<ShipByDefault> getShipsSetByDefault() { return shipsSetByDefault; }
	public Set<ShipByDefault> getShipsSortedSetByDefault() { return shipsSortedSetByDefault; }

	public List<ShipByPK> getShipsListByPK() { return shipsListByPK; }
	public Set<ShipByPK> getShipsSetByPK() { return shipsSetByPK; }
	public Set<ShipByPK> getShipsSortedSetByPK() { return shipsSortedSetByPK; }

	public List<ShipBySwitch> getShipsListBySwitch() { return shipsListBySwitch; }
	public Set<ShipBySwitch> getShipsSetBySwitch() { return shipsSetBySwitch; }
	public Set<ShipBySwitch> getShipsSortedSetBySwitch() { return shipsSortedSetBySwitch; }

	public List<ShipByBusinessId> getShipsListByBusinessId() { return shipsListByBusinessId; }
	public Set<ShipByBusinessId> getShipsSetByBusinessId() { return shipsSetByBusinessId; }
	public Set<ShipByBusinessId> getShipsSortedSetByBusinessId() { return shipsSortedSetByBusinessId; }
}
