package ejava.projects.esales.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import ejava.projects.esales.dto.Account;
import ejava.projects.esales.dto.Auction;
import ejava.projects.esales.dto.Image;

public class JPAESalesDAO implements ESalesDAO {
	public static final String PERSISTENCE_UNIT = "eSalesData";
	private EntityManager em;

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	public void clear() {
		em.clear();
	}
	
	@SuppressWarnings("unchecked")
	public List<Account> getAccounts(int index, int count) {
		Query query = em.createQuery("select a from Account a")
		                .setFirstResult(index);
		if (count >= 0) {
		    query.setMaxResults(count);
		}
		return (List<Account>)query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Auction> getAuctions(int index, int count) {
		Query query = em.createQuery("select au from Auction au")
		                .setFirstResult(index);
		if (count >= 0) {
		    query.setMaxResults(count);
		}
		return (List<Auction>)query.getResultList();
	}


	@SuppressWarnings("unchecked")
	public List<Image> getImagesForAuctions(
			List<Integer> auctions, int index, int count) {
		/*
		List<Image> images = new ArrayList();
		int key=0;
		for (Integer id : auctions) {
			Auction auction = em.find(Auction.class, id);
			Image image = new Image();
			image.setAuction(auction);
			image.setId(key++);
			images.add(image);
		}
		return images;
		*/
		Query query = em.createQuery(
				"select i from Image i " +
				"where i.auction in (" + createSubselectInt(auctions) + ")")
				.setFirstResult(index);
		if (count >= 0) {
			query.setMaxResults(count);
		}
		return (List<Image>)query.getResultList();
	}

	@SuppressWarnings("unused")
	private static String createSubselectString(List<String> vals) {
		StringBuilder text = new StringBuilder();
		for (String s : vals) {
			if (text.length() > 0) {
				text.append(",");
			}
			text.append("'" + s + "'");
		}
		return text.toString();
	}
	
	private static String createSubselectInt(List<Integer> vals) {
		StringBuilder text = new StringBuilder();
		for (Integer val : vals) {
			if (text.length() > 0) {
				text.append(",");
			}
			text.append(val);
		}
		return text.toString();
	}
}
