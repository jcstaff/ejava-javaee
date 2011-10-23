package myorg.javaeeex.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JPAFilter implements Filter {
    private static Log log = LogFactory.getLog(JPAFilter.class);

    private InitialContext jndi;
    public void init(FilterConfig config) throws ServletException {
        log.debug("*** JPAFilter.init() ***");
        try {
            jndi = new InitialContext();
        }
        catch (NamingException ex) {
            log.error("error in Filter JNDI:", ex);
            config.getServletContext().log("error in Filter JNDI" + ex);
        }
    }   

    public void doFilter(ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        log.debug("*** JPAFilter.doFilter() ENTER ***");

        UserTransaction tx = null;
        boolean ownTx = false;

        try {
            tx = (UserTransaction)jndi.lookup("/UserTransaction");
            if (tx.getStatus() != Status.STATUS_ACTIVE) {
                tx.begin();
                ownTx = true;
            }
        } catch (NamingException ex) {
            log.info("no transaction available, moving on without it");
        } catch (Exception ex) {
            log.error("error starting transaction:", ex);
            throw new ServletException("error starting transaction", ex);
        }

        chain.doFilter(request, response);

        try {
            if (ownTx) {
                if (tx.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                    tx.rollback();
                }
                else if (tx.getStatus() == Status.STATUS_ACTIVE) {
                    tx.commit();
                }
            }
        } catch (Exception ex) {
            log.error("error ending transaction:", ex);
            throw new ServletException("error ending transaction", ex);
        }

        log.debug("*** JPAFilter.doFilter() EXIT ***");
    }


    public void destroy() {}//required by interface
}
