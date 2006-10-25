package ejava.examples.ejbsessionbank.web;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JNDIHelper {
    private static Log log = LogFactory.getLog(JNDIHelper.class);

    public void dump() throws NamingException {
        dump(new InitialContext(),"");
    }

    public void dump(Context context, String name) {
        StringBuilder text = new StringBuilder();
        try {
            doDump(0, text, context, name);
        }
        catch (NamingException ex) {}
        log.debug("\nlisting " + name + "\n" + text.toString());
    }

    private void doDump(int level, StringBuilder text, Context context, String name) 
        throws NamingException {
        for (NamingEnumeration ne = context.list(name); ne.hasMore();) {
            NameClassPair ncp = (NameClassPair) ne.next();
            String objectName = ncp.getName();
            String className = ncp.getClassName();
            String classText = (true) ? " :" + className : "";
            if (isContext(className)) {
                text.append(getPad(level) + "+" + objectName + classText +"\n");
                doDump(level + 1, text, context, name + "/" + objectName);
            } else {
                text.append(getPad(level) + "-" + objectName + classText + "\n");
            }
        }
    }
    
    protected boolean isContext(String className) {
        try {
            Class objectClass = Thread.currentThread().getContextClassLoader()
                    .loadClass(className);
            return Context.class.isAssignableFrom(objectClass);
        }
        catch (ClassNotFoundException ex) {
            //object is probably not a context, report as non-context
            return false;
        }
    }

    protected String getPad(int level) {
        StringBuffer pad = new StringBuffer();
        for (int i = 0; i < level; i++) {
            pad.append(" ");
        }
        return pad.toString();
    }


}
