package myorg.relex.one2one;

import javax.persistence.*;

@Entity
@Table(name="RELATIONEX_PASSENGER")
public class Passenger {
    @Id @GeneratedValue
    private int id;
    
    @OneToOne(optional=false)
    private Ticket ticket;

    @Column(length=32)
    private String name;
}
