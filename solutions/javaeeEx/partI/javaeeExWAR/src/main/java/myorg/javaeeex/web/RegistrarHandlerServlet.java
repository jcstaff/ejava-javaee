package myorg.javaeeex.web;

import java.io.IOException;

import java.util.Collection;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;

import myorg.javaeeex.bo.Address;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.ejb.RegistrarLocal;
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
    public static final String GET_ALL_PEOPLE_COMMAND = "Get All People";
    public static final String GET_PERSON_COMMAND = "Get Person";
    public static final String CHANGE_ADDRESS_COMMAND = "Change Address";
    public static final String LOGOUT_COMMAND = "logout";
    public static final String EXCEPTION_PARAM = "exception";
    public static final String COMMAND_PARAM = "command";
    public static final String HANDLER_TYPE_KEY = "type";
    @Resource(name="httpPort")
    Integer httpPort = 8080;

    @EJB(beanInterface=RegistrarLocal.class)
    private Object registrar;
    private Map<String, Handler> handlers = new HashMap<String, Handler>();

    public void init() throws ServletException {
        log.debug("init() called ");
        try {
            ServletConfig config = getServletConfig();
            initRegistrar(config);
            
            //build a list of handlers for individual commands
            String handlerType = config.getInitParameter(HANDLER_TYPE_KEY);
            if (ADMIN_TYPE.equals(handlerType)) {               
                handlers.put(GET_ALL_PEOPLE_COMMAND, new GetAllPeople());
                handlers.put(GET_PERSON_COMMAND, new GetPerson());
                handlers.put(CHANGE_ADDRESS_COMMAND, new ChangeAddress());
            } 
            else if (ANONYMOUS_TYPE.equals(handlerType)) {
                handlers.put(PING_COMMAND, new Ping());    
            }
            handlers.put(LOGOUT_COMMAND, new Logout());
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
        protected static final String ID_PARAM = "id";
        protected static final String INDEX_PARAM = "index";
        protected static final String COUNT_PARAM = "count";
        protected static final String RESULT_PARAM = "result";
        protected static final String EXCEPTION_PARAM = "exception";
        protected static final String NEXT_INDEX_PARAM = "nextIndex";
        protected static final String STREET_PARAM = "street";
        protected static final String CITY_PARAM = "city";
        protected static final String STATE_PARAM = "state";
        protected static final String ZIP_PARAM = "zip";
        protected static final String DISPLAY_EXCEPTION_URL = 
            "/WEB-INF/content/DisplayException.jsp";
        protected static final String DISPLAY_RESULT_URL = 
            "/WEB-INF/content/DisplayResult.jsp";
        protected static final String DISPLAY_PEOPLE_URL = 
            "/WEB-INF/content/DisplayPeople.jsp";
        protected static final String DISPLAY_PERSON_URL = 
            "/WEB-INF/content/DisplayPerson.jsp";
        protected static final String MAIN_MENU_URL =
            "/index.jsp";
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
                throws ServletException, IOException, Exception;
    }

    private class Ping extends Handler {
        public void doHandle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            action = "EJB.ping"; //describe action in case of exception

            if (registrar instanceof RegistrarRemote) {
               ((RegistrarRemote)registrar).ping(); 
            } else {
               ((RegistrarLocal)registrar).ping();
            }
                
            request.setAttribute(RESULT_PARAM, "ping() complete");                
            RequestDispatcher rd = 
              getServletContext().getRequestDispatcher(DISPLAY_RESULT_URL);
            rd.forward(request, response);                
        }
    }

    private class GetAllPeople extends Handler {
        public void doHandle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws Exception {
            action = "EJB.getAllPeople"; //describe action in case of exception
            
            String indexStr = (String)request.getParameter(INDEX_PARAM);
            String countStr = (String)request.getParameter(COUNT_PARAM);
            int index = Integer.parseInt(indexStr);
            int count = Integer.parseInt(countStr);
            
            Collection<Person> people = (registrar instanceof RegistrarRemote) ?
                ((RegistrarRemote)registrar).getAllPeopleHydrated(index, count) :
                //((RegistrarLocal)registrar).getAllPeopleHydrated(index, count);
                ((RegistrarLocal)registrar).getAllPeople(index, count);
            int nextIndex = (people.size()==0) ?
                index : index + people.size();

            //request.setAttribute(RESULT_PARAM, people);
            request.setAttribute(RESULT_PARAM, people);
            request.setAttribute(INDEX_PARAM, index);
            request.setAttribute(COUNT_PARAM, count);
            request.setAttribute(NEXT_INDEX_PARAM, nextIndex);

            //RequestDispatcher rd =
            //  getServletContext().getRequestDispatcher(DISPLAY_RESULT_URL);
            RequestDispatcher rd =
              getServletContext().getRequestDispatcher(DISPLAY_PEOPLE_URL);
            rd.forward(request, response);
        }
    }

    private class GetPerson extends Handler {
        public void doHandle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws Exception {
            action = "EJB.getPerson"; //describe action in case of exception
            
            String idStr = (String)request.getParameter(ID_PARAM);
            long id = Long.parseLong(idStr);

            Person person = (registrar instanceof RegistrarRemote) ?
                ((RegistrarRemote)registrar).getPersonByIdHydrated(id) :
                //((RegistrarLocal)registrar).getPersonByIdHydrated(id);
                ((RegistrarLocal)registrar).getPersonById(id);

            request.setAttribute(RESULT_PARAM, person);

            RequestDispatcher rd =
             //getServletContext().getRequestDispatcher(DISPLAY_RESULT_URL);
             getServletContext().getRequestDispatcher(DISPLAY_PERSON_URL);
            rd.forward(request, response);
        }
    }

    private class ChangeAddress extends Handler {
        public void doHandle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws Exception {
            action = "EJB.changeAddress"; //describe action in case of exception
            
            log.debug("Change Address: id=" + request.getParameter(ID_PARAM) +
                    ", uri=" + request.getRequestURI());
            String idStr = (String)request.getParameter(ID_PARAM);
            long id = Long.parseLong(idStr);
        
            String street = (String)request.getParameter(STREET_PARAM);
            String city = (String)request.getParameter(CITY_PARAM);
            String state = (String)request.getParameter(STATE_PARAM);
            String zip = (String)request.getParameter(ZIP_PARAM);
        
            Address address = new Address();
            address.setStreet(street);
            address.setCity(city);
            address.setState(state);
            address.setZip(zip);
        
            Person person = (registrar instanceof RegistrarRemote) ?
                ((RegistrarRemote)registrar).getPersonByIdHydrated(id) :
                //((RegistrarLocal)registrar).getPersonByIdHydrated(id);
                ((RegistrarLocal)registrar).getPersonById(id);
            person = (registrar instanceof RegistrarRemote) ?
                ((RegistrarRemote)registrar).changeAddress(person, address) :
                ((RegistrarLocal)registrar).changeAddress(person, address);
            
            request.setAttribute(RESULT_PARAM, person);
        
            RequestDispatcher rd =
             getServletContext().getRequestDispatcher(DISPLAY_PERSON_URL);
            rd.forward(request, response);
        }
    }

    private class Logout extends Handler {
        @Override
        public void doHandle(HttpServletRequest request,
                HttpServletResponse response) throws Exception {
            request.getSession().invalidate();
            
            //switch back to straight HTTP            
            String contextPath = new StringBuilder()
                .append("http://")
                .append(request.getServerName())
                .append(":")
                .append(httpPort)
                .append(request.getContextPath())
                .toString();            
            
            response.sendRedirect(contextPath + MAIN_MENU_URL);
        }
    }
}
