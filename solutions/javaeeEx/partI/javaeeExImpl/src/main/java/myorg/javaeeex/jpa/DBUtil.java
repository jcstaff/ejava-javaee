package myorg.javaeeex.jpa;

import java.io.File;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import myorg.javaeeex.cdi.JavaeeEx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class will execute a set of drop and create DDL scripts against
 * a supplied entity manager.
 * @author jcstaff
 *
 */
public class DBUtil {
    static Log log = LogFactory.getLog(DBUtil.class);
    protected EntityManager em;
    protected String dropPath;
    protected String createPath;
    
    public DBUtil() {}
    public DBUtil(EntityManager em, String dropPath, String createPath) {
        setEntityManager(em);
        setDropPath(dropPath);
        setCreatePath(createPath);
    }

    public void setEntityManager(EntityManager em) {
        this.em = em; 
    }
    public void setDropPath(String dropPath) {
        this.dropPath = dropPath;
    }
    public void setCreatePath(String createPath) {
        this.createPath = createPath;
    }
    
    /**
     * Locate the input stream either as a file path or a resource path.
     * @param path
     * @return
     * @throws Exception
     */
    protected InputStream getInputStream(String path) throws Exception {
        InputStream is = null;
        
        File pathFile = new File(path);
        if (pathFile.exists()) {
            is = new FileInputStream(pathFile);
        }
        else {
           is = Thread.currentThread()
                      .getContextClassLoader()
                      .getResourceAsStream(path);
        }
        
        return is; //let a null is be returned
    }
    
    /**
     * Turn the InputStream into a String for easier parsing for SQL statements.
     * @param is
     * @return
     * @throws Exception
     */
    protected String getString(InputStream is) throws Exception {
        StringBuilder text = new StringBuilder();
        byte[] buffer = new byte[4096];
        for (int n; (n = is.read(buffer)) != -1;) {
            text.append(new String(buffer, 0,n));
        }
        return text.toString();
    }
        
    /**
     * Get list of SQL statements from the supplied string.
     * @param contents
     * @return
     * @throws Exception
     */
    protected List<String> getStatements(String contents) throws Exception {
        List<String> statements = new ArrayList<String>();

        for (String tok: contents.split(";")) {
            statements.add(tok);
        }
        return statements;
    }
    
    /**
     * Execute the SQL statements contained within the resource that is located
     * by the path supplied. This path can be either a file path or resource 
     * path.
     * @param path
     * @return
     * @throws Exception
     */
    public int executeScript(String path) throws Exception {
        if (path == null || path.length() == 0) {
            throw new Exception("no path provided");
        }
        
        InputStream is = getInputStream(path);
        if (is == null) {
            throw new Exception("path not found:" + path);
        }
        
        String sql = getString(is);
        
        List<String> statements = getStatements(sql);
        log.debug("found " + statements.size() + " statements");
        
        for (String statement : statements) {
            log.debug("executing:" + statement);
            em.createNativeQuery(statement).executeUpdate();    
        }

        return statements.size(); 
    }
    
    /**
     * Execute the drop script against the DB.
     * @return
     * @throws Exception
     */
    public int dropAll() throws Exception {
        return executeScript(dropPath);
    }

    /**
     * Execute the create script against the DB.
     * @return
     * @throws Exception
     */
    public int createAll() throws Exception {
        return executeScript(createPath);
    }    
}
