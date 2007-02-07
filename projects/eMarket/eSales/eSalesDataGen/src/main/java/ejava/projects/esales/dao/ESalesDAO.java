package ejava.projects.esales.dao;

import java.util.List;

import ejava.projects.esales.dto.Account;
import ejava.projects.esales.dto.Auction;
import ejava.projects.esales.dto.Image;

public interface ESalesDAO {	
	public List<Account> getAccounts(int index, int count);
	public List<Auction> getAuctions(int index, int count);
	public List<Image> getImagesForAuctions(List<Integer> auctions, 
			int index, int count);
	public void clear(); 
}
