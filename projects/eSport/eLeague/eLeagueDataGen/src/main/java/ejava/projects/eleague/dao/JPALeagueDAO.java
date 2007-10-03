package ejava.projects.eleague.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.projects.eleague.dto.Club;
import ejava.projects.eleague.dto.Season;

public class JPALeagueDAO implements ELeagueDAO {
	private static final Log log = LogFactory.getLog(JPALeagueDAO.class);
	public static final String PERSISTENCE_UNIT = "eLeagueData";
	private EntityManager em;

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	public void clear() {
		em.clear();
	}

    public List<Club> getClubsForSeason(List<Season> seasons) {
        // TODO Auto-generated method stub
        return null;
    }

    public ELeagueDAO getLeague() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Season> getSeasons() {
        // TODO Auto-generated method stub
        return null;
    }
}
