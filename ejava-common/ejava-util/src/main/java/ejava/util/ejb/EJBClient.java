package ejava.util.ejb;

/**
 * This class provides utiltity functions for working with an EJB in the JNDI
 * tree. 
 */
public class EJBClient {
	private String earName;
	private String ejbModuleName;
	private String distinctName;
	private String ejbClassName;
	private String remoteInterface;
	private String version;
	
    public EJBClient() {}
    public EJBClient(String earName, String ejbModuleName, String distinctName,
			String ejbClassName, String remoteInterface, String version) {
		this.earName = earName;
		this.ejbModuleName = ejbModuleName;
		this.distinctName = distinctName;
		this.ejbClassName = ejbClassName;
		this.remoteInterface = remoteInterface;
		this.version = version;
	}
    public String getJNDIName() {
    	return getEJBLookupName(
    			earName, 
    			ejbModuleName, 
    			distinctName, 
    			ejbClassName, 
    			remoteInterface, 
    			version);
    }
    
	public static String getEJBLookupName(
    		String earName,
    		String ejbModuleName,
    		String distinctName,
    		String ejbClassName,
    		String remoteInterface,
    		String version) {
    	return getEJBLookupName(
    			String.format("%s-%s",earName,version), 
    			String.format("%s-%s", ejbModuleName, version), 
    			distinctName, 
    			ejbClassName, 
    			remoteInterface);
    }
    
    public static String getEJBLookupName(
    		String earNameVersion,
    		String ejbModuleNameVersion,
    		String distinctName,
    		String ejbClassName,
    		String remoteInterface) {

    	return new StringBuilder("ejb:")
    		.append(earNameVersion).append("/")
    		.append(ejbModuleNameVersion).append("/")
    		.append(distinctName).append("/")
    		.append(ejbClassName).append("!")
    		.append(remoteInterface)
    		.toString();
    }

}
