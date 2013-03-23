package myorg.relex.one2one;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name="RELATIONEX_TICKET")
public class Ticket {
	@Id @GeneratedValue
	private int id;
	
	@OneToOne(mappedBy="ticket", fetch=FetchType.EAGER,
			cascade={
			CascadeType.PERSIST,
			CascadeType.DETACH,
			CascadeType.REMOVE,
			CascadeType.REFRESH,
			CascadeType.MERGE
		})
	private Passenger passenger;
	
	@Temporal(TemporalType.DATE)
	Date date;
}
