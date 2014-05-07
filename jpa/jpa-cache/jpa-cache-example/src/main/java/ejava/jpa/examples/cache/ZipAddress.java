package ejava.jpa.examples.cache;

import javax.persistence.*;

@Entity
@Table(name="JPACACHE_ZIPADDR")
@Cacheable(true)
public class ZipAddress {
	@Id
	@Column(length=10)
	private String zip;
	@Column(length=32, nullable=false)
	private String city;
	
	protected ZipAddress() {}
	public ZipAddress(String zip, String city) {
		this.zip=zip;
		this.city=city;
	}
	
	public String getZip() { return zip; }
	public String getCity() { return city; }
	
	@Override
	public String toString() {
		return zip + "=" + city;
	}
}
