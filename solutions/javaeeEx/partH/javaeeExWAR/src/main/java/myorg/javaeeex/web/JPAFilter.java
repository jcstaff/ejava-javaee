package myorg.javaeeex.web;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JPAFilter implements Filter {
	private static final Log log = LogFactory.getLog(JPAFilter.class);
	@Inject
	private static UserTransaction tx;

	public void init(FilterConfig config) throws ServletException {
		log.debug("*** JPAFilter.init() ***");
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
            log.debug("*** JPAFilter.doFilter() ENTER ***");

            boolean ownTx = false;

            if (tx != null) {
                try {
                    if (tx.getStatus() != Status.STATUS_ACTIVE) {
                        tx.begin();
                        ownTx = true;
                    }
                } catch (Exception ex) {
                    log.error("error starting transaction:", ex);
                    throw new ServletException("error starting transaction", ex);
                }
            } else {
                    log.debug("no UserTransaction injected -- moving on without one");
            }

            chain.doFilter(request, response);

            if (tx != null) {
                try {
                    if (ownTx) {
                        if (tx.getStatus() == Status.STATUS_MARKED_ROLLBACK) {
                            tx.rollback();
                        } else if (tx.getStatus() == Status.STATUS_ACTIVE) {
                            tx.commit();
                        }
                    }
                } catch (Exception ex) {
                    log.error("error ending transaction:", ex);
                    throw new ServletException("error ending transaction", ex);
                }
            }
            
            log.debug("*** JPAFilter.doFilter() EXIT ***");
	}

	public void destroy() {
	}// required by interface
}
