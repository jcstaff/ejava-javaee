package myorg.javaeeex.web;

import java.io.IOException;

import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;

import myorg.javaeeex.ejb.RegistrarRemote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class RegistrarHandlerServlet extends HttpServlet {
    private static final Log log = LogFactory.getLog(RegistrarHandlerServlet.class);
    private static final String UNKNOWN_COMMAND_URL = 
        "/WEB-INF/content/UnknownCommand.jsp";
    public static final String ADMIN_TYPE = "admin";
    public static final String ANONYMOUS_TYPE = "anonymous";
    public static final String PING_COMMAND = "Ping";
    public static final String EXCEPTION_PARAM = "exception";
    public static final String COMMAND_PARAM = "command";
    public static final String HANDLER_TYPE_KEY = "type";

    @Inject
    private RegistrarRemote registrar;
    private Map<String, Handler> handlers = new HashMap<String, Handler>();

    public void init() throws ServletException {
        log.debug("init() called ");
        try {
            ServletConfig config = getServletConfig();
            initRegistrar(config);
            
            //build a list of handlers for individual commands
            String handlerType = config.getInitParameter(HANDLER_TYPE_KEY);
            if (ADMIN_TYPE.equals(handlerType)) {               
                //adminHandlers.put(XXX_COMMAND, new XXX());    
            } 
            else if (ANONYMOUS_TYPE.equals(handlerType)) {
                handlers.put(PING_COMMAND, new Ping());    
            }
            log.debug("configured handler type:" + handlerType +
                    " with " + handlers);
        }
        catch (Exception ex) {
            log.fatal("error initializing handler", ex);
            throw new ServletException("error initializing handler", ex);
        }
    }

    private void initRegistrar(ServletConfig config) throws Exception {
        log.debug("initRegistrar(), registrar=" + registrar);
        if (registrar == null) {
            //build an InitialContext from Servlet.init properties in web.xml
            InitialContext jndi = null;
            String ctxFactory = config.getServletContext()
                                      .getInitParameter(Context.INITIAL_CONTEXT_FACTORY);
            log.debug(Context.INITIAL_CONTEXT_FACTORY + "=" + ctxFactory);
            if (ctxFactory!=null) {
                    Properties env = new Properties();
                    env.put(Context.INITIAL_CONTEXT_FACTORY, ctxFactory);
                    jndi = new InitialContext(env);
            }
            else {
                    jndi = new InitialContext();
            }
            String jndiName = config.getServletContext().getInitParameter("registrar.remote");
            registrar = (RegistrarRemote)jndi.lookup(jndiName);
            log.debug("registrar initialized:" + registrar);
        }        
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
        throws ServletException, IOException {
        log.debug("doGet() called");
        String command = request.getParameter(COMMAND_PARAM);
        log.debug("command=" + command);
        try {
            if (command != null) {
                Handler handler = handlers.get(command);
                if (handler != null) {
                    handler.handle(request, response);
                }
                else {
                    RequestDispatcher rd = 
                        getServletContext().getRequestDispatcher(
                            UNKNOWN_COMMAND_URL);
                            rd.forward(request, response);
                }
            }
            else {
                throw new Exception("no " + COMMAND_PARAM + " supplied"); 
            }
        }
        catch (Exception ex) {
            log.error("error within GET", ex);
            request.setAttribute(EXCEPTION_PARAM, ex);
            RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    UNKNOWN_COMMAND_URL);
                    rd.forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
        throws ServletException, IOException {
        log.debug("doPost() called, calling doGet()");
        doGet(request, response);
    }

    public void destroy() {
        log.debug("destroy() called");
    }

    private abstract class Handler {
        protected static final String RESULT_PARAM = "result";
        protected static final String EXCEPTION_PARAM = "exception";
        protected static final String DISPLAY_EXCEPTION_URL = 
            "/WEB-INF/content/DisplayException.jsp";
        protected static final String DISPLAY_RESULT_URL = 
            "/WEB-INF/content/DisplayResult.jsp";
        protected String action;
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
                doHandle(request, response);
            }
            catch (Exception ex) {
                log.error("error in " + action, ex);
                request.setAttribute(EXCEPTION_PARAM, ex);                
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    DISPLAY_EXCEPTION_URL);
                rd.forward(request, response);
            }
        }
        
        public abstract void doHandle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException;
    }

    private class Ping extends Handler {
        public void doHandle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            action = "EJB.ping"; //describe action in case of exception
            registrar.ping();
                
            request.setAttribute(RESULT_PARAM, "ping() complete");                
            RequestDispatcher rd = 
              getServletContext().getRequestDispatcher(DISPLAY_RESULT_URL);
            rd.forward(request, response);                
        }
    }
}
