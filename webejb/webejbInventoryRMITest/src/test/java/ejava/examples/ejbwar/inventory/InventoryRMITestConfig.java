package ejava.examples.ejbwar.inventory;

import java.io.IOException;

import java.io.InputStream;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import ejava.examples.ejbwar.inventory.rmi.InventoryMgmtRemote;
import ejava.util.ejb.EJBClient;

/**
 * This class provides configuration and runtime objects used for testing
 * the inventory RMI interface.
 */
public class InventoryRMITestConfig {
	private String inventoryJNDIName;
	private InventoryMgmtRemote inventoryClient;	
	private Properties props = new Properties();
	
	/**
	 * Load in overrides for hard-coded defaults.
	 * @param resource
	 * @throws IOException
	 */
	public InventoryRMITestConfig(String resource) throws IOException {
		InputStream is = getClass().getResourceAsStream(resource);
		if (is!=null) {
			try {
				props.load(is);
			} finally {
				is.close();
			}
		}
	}
	
	/**
	 * Return the JNDI name for the inventory management EJB RMI facade.
	 * @return
	 */
	public String inventoryJNDIName() {
		if (inventoryJNDIName==null) {
			String warName = props.getProperty("war.name", "jaxrsInventoryWAR");
			String ejbName = props.getProperty("ejb.name", "InventoryMgmtRMIEJB");
			String remoteName = props.getProperty("remote.name", InventoryMgmtRemote.class.getName());
			inventoryJNDIName = EJBClient.getRemoteLookupName(warName, ejbName, remoteName);
		}
		return inventoryJNDIName;
	}
	
	/**
	 * Return the RMI stub that can be used to communicate directly with the 
	 * inventory management application.
	 * @return
	 * @throws NamingException
	 */
	public InventoryMgmtRemote inventoryClient() throws NamingException {
		if (inventoryClient==null) {
			InitialContext jndi = null;
			try {
				jndi=new InitialContext();
				inventoryClient = (InventoryMgmtRemote)jndi.lookup(inventoryJNDIName());
			} finally {
				//jndi.close(); CAN'T closed this or stub will fail!!!
			}
			 
		}
		return inventoryClient;
	}
}
