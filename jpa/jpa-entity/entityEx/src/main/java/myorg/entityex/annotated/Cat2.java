package myorg.entityex.annotated;

import java.util.Date;

@javax.persistence.Entity
@javax.persistence.Table(name="ENTITYEX_CAT")
public class Cat2 {
	@javax.persistence.Id
	@javax.persistence.Column(name="CAT_ID")
	private int id;
	@javax.persistence.Column(nullable=false, length=20)
	private String name;
	@javax.persistence.Temporal(javax.persistence.TemporalType.DATE)
	private Date dob;
	@javax.persistence.Column(precision=3, scale=1)  //010.2lbs
	private double weight;
	
	public Cat2() {} //must have default ctor
	public Cat2(String name, Date dob, double weight) {
		this.name = name;
		this.dob = dob;
		this.weight = weight;
	}
	
	public int getId() { return id; }
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getDob() { return dob; }
	public void setDob(Date dob) {
		this.dob = dob;
	}
	
	public double getWeight() { return weight; }
	public void setWeight(double weight) {
		this.weight = weight;
	}
}
