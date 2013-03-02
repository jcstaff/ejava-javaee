package myorg.relex.one2one;

import java.util.Date;

import javax.persistence.*;

/**
 * This class provides an example of a the owning entity of a
 * one-to-one, uni-directional relationship where the dependent's
 * primary key is derived from the parent and the parent uses
 * a composite primary key.
 */
@Entity
@Table(name="RELATIONEX_BOXOFFICE")
public class BoxOffice {
	@EmbeddedId 
	private ShowEventPK pk; //will be set by provider with help of @MapsId
	
	@OneToOne(optional=false)
	@MapsId //provider maps this composite FK to @EmbeddedId PK value
	private ShowEvent show;
	
	@Column(name="TICKETS")
	private int ticketsLeft;

	public BoxOffice() {}
	public BoxOffice(ShowEvent show) {
		this.show = show;
	}

	public Date getDate() { return show==null ? null : show.getDate(); }
	public Date getTime() { return show==null ? null : show.getTime(); }
	public ShowEvent getShow() { return show; }

	public int getTicketsLeft() { return ticketsLeft; }
	public void setTicketsLeft(int ticketsLeft) {
		this.ticketsLeft = ticketsLeft;
	}
}
