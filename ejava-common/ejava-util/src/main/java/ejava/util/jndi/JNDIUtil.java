package ejava.util.jndi;

import java.io.IOException;

import java.io.InputStream;
import java.util.Properties;

import javax.naming.Context;

import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * This class helps work with the JNDI tree.
 */
public class JNDIUtil {
	/**
	 * This method will return a jndi.properties object that is based on the
	 * properties found in ejava-jndi.properties that start with the provided
	 * prefix. This method is useful when examples are using different JNDI
	 * mechanisms and want to keep them separate by forming the jndi.properties
	 * in memory.
	 * @param prefix
	 * @return
	 * @throws IOException
	 */
    public static Properties getJNDIProperties(String prefix) throws IOException {
    	InputStream is=JNDIUtil.class.getResourceAsStream("/ejava-jndi.properties");
    	Properties props = new Properties();
    	props.load(is);
    	is.close();
    	
    	Properties env = new Properties();
    	for (String key : props.stringPropertyNames()) {
    		String value = props.getProperty(key);
    		if (key.startsWith(prefix) && value != null && !value.isEmpty()) {
    			String name=key.substring(prefix.length(),key.length());
    			env.put(name, value);
    		}
    	}
    	return env;
    }
	
	
	
	
	
	
    public String dump() throws NamingException {
        return dump(new InitialContext(),"");
    }

    public String dump(Context context, String name) {
        StringBuilder text = new StringBuilder();
        try {
            text.append("listing ").append(name);
            doDump(0, text, context, name);
        }
        catch (NamingException ex) {}
        return text.toString();
    }

	private void doDump(int level, StringBuilder text, Context context, String name) 
        throws NamingException {
        for (NamingEnumeration<NameClassPair> ne = context.list(name); ne.hasMore();) {
            NameClassPair ncp = (NameClassPair) ne.next();
            String objectName = ncp.getName();
            String className = ncp.getClassName();
            if (isContext(className)) {
            	text.append(getPad(level))
            	    .append("+")
            	    .append(objectName)
            	    .append(":")
            	    .append(className)
            	    .append('\n');
                doDump(level + 1, text, context, name + "/" + objectName);
            } else {
            	text.append(getPad(level))
	        	    .append("-")
	        	    .append(objectName)
	        	    .append(":")
	        	    .append(className)
	        	    .append('\n');
            }
        }
    }
    
	protected boolean isContext(String className) {
        try {
			Class<?> objectClass = Thread.currentThread().getContextClassLoader()
                    .loadClass(className);
            return Context.class.isAssignableFrom(objectClass);
        }
        catch (ClassNotFoundException ex) {
            //object is probably not a context, report as non-context
            return false;
        }
    }

    protected String getPad(int level) {
        StringBuilder pad = new StringBuilder();
        for (int i = 0; i < level; i++) {
            pad.append(" ");
        }
        return pad.toString();
    }
}
