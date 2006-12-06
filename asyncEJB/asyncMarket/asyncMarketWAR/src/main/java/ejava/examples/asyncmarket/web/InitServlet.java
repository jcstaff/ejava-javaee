package ejava.examples.asyncmarket.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.asyncmarket.AuctionMgmt;
import ejava.examples.asyncmarket.ejb.AuctionMgmtLocal;

@SuppressWarnings("serial")
public class InitServlet extends HttpServlet {
    private static Log log = LogFactory.getLog(InitServlet.class);
    private AuctionMgmt auctionMgmt;

    public void init() throws ServletException {
        log.debug("init() called ");
        try {
            if (auctionMgmt == null) {
                auctionMgmt = new JNDIHelper()
                    .getAuctionMgmt(getServletContext());
            }        

            if (auctionMgmt instanceof AuctionMgmtLocal) {
                ((AuctionMgmtLocal)auctionMgmt).initTimers();
            }
        }
        catch (Exception ex) {
            log.fatal("error initializing", ex);
            throw new ServletException("error initializing", ex);
        }
    }
}
