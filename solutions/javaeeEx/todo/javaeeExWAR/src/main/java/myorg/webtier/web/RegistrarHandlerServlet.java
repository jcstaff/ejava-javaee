package myorg.webtier.web;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.javaeeex.bl.Registrar;
import myorg.javaeeex.bo.Address;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.ejb.RegistrarLocal;
import myorg.javaeeex.ejb.RegistrarRemote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class RegistrarHandlerServlet extends HttpServlet {
    private static Log log = LogFactory.getLog(RegistrarHandlerServlet.class);
    private Map<String, Handler> handlers = new HashMap<String, Handler>();
    private Registrar registrar;

    public static final String COMMAND_PARAM = "command";
    public static final String EXCEPTION_PARAM = "exception";
    public static final String HANDLER_TYPE_KEY = "type";
    public static final String ADMIN_TYPE = "admin";
    public static final String CREATE_PERSON_COMMAND = "Create Person";
    public static final String GET_PEOPLE_COMMAND = "Get People";
    public static final String GET_PERSON_COMMAND = "Get Person";
    public static final String GET_PERSON_HYDRATED_COMMAND = "Get Person Hydrated";
    public static final String CHANGE_ADDRESS_COMMAND = "Change Address";
    private static final String UNKNOWN_COMMAND_URL = 
        "/WEB-INF/content/UnknownCommand.jsp";

    public void init() throws ServletException {
        log.debug("init() called ");
        try {
            ServletConfig config = getServletConfig();
            initRegistrar(config);
            
            //build a list of handlers for individual commands
            if (ADMIN_TYPE.equals(config.getInitParameter(HANDLER_TYPE_KEY))) {
                handlers.put(CREATE_PERSON_COMMAND, new CreatePerson());    
                handlers.put(GET_PEOPLE_COMMAND, new GetPeople());    
                handlers.put(GET_PERSON_COMMAND, new GetPerson());    
                handlers.put(GET_PERSON_HYDRATED_COMMAND, new GetPersonHydrated());    
                handlers.put(CHANGE_ADDRESS_COMMAND, new ChangeAddressCommand());    
            }            
        }
        catch (Exception ex) {
            log.fatal("error initializing handler", ex);
            throw new ServletException("error initializing handler", ex);
        }
    }

    private void initRegistrar(ServletConfig config) throws Exception {        
        log.debug("initTeller(), registrar=" + registrar);
        if (registrar == null) {
            //build an InitialContext from Servlet.init properties in web.xml
            Properties jndiProperties = new Properties();
            for(Enumeration<?> e=config.getInitParameterNames();
                e.hasMoreElements(); ) {
                String key = (String)e.nextElement();
                String value=(String)config.getInitParameter(key);
                if (key.startsWith("java.naming")) {
                    jndiProperties.put(key, value);
                }                    
            }
            log.debug("jndiProperties=" + jndiProperties);
            InitialContext jndi = new InitialContext(jndiProperties);
            String jndiName = config.getInitParameter("registrar.local");
            try { registrar = (Registrar)jndi.lookup(jndiName); }
            catch (Throwable ex) {
                log.debug(jndiName + " not found, trying remote");
                jndiName = config.getInitParameter("registrar.remote");
                registrar = (Registrar)jndi.lookup(jndiName);
            }
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
        protected static final String NEXT_INDEX_PARAM = "nextIndex";
        protected static final String COUNT_PARAM = "count";
        protected static final String FIRST_NAME_PARAM = "firstName";
        protected static final String LAST_NAME_PARAM = "lastName";
        protected static final String STREET_PARAM = "street";
        protected static final String CITY_PARAM = "city";
        protected static final String STATE_PARAM = "state";
        protected static final String ZIP_PARAM = "zip";
        protected static final String PERSON_PARAM = "person";
        protected static final String PEOPLE_PARAM = "people";
        protected static final String DISPLAY_EXCEPTION_URL = 
            "/WEB-INF/content/ErrorPage.jsp";
        protected static final String DISPLAY_PERSON_URL = 
            "/WEB-INF/content/DisplayPerson.jsp";
        protected static final String DISPLAY_PEOPLE_URL = 
            "/WEB-INF/content/DisplayPeople.jsp";
        public abstract void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException;        
    }
    
    private class CreatePerson extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {
            try {
                String firstName = 
                    (String)request.getParameter(FIRST_NAME_PARAM);                
                String lastName = 
                    (String)request.getParameter(LAST_NAME_PARAM);
                
                Person person = registrar.createPerson(firstName, lastName);
                
                request.setAttribute(PERSON_PARAM, person);                
                RequestDispatcher rd = 
                  getServletContext().getRequestDispatcher(DISPLAY_PERSON_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
            	log.error("error creating person", ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    DISPLAY_EXCEPTION_URL);
                rd.forward(request, response);
            }
        }
    }

    private class GetPeople extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {            
            try {
                String indexStr = (String)request.getParameter(INDEX_PARAM);
                String countStr = (String)request.getParameter(COUNT_PARAM);
                int index = Integer.parseInt(indexStr);
                int count = Integer.parseInt(countStr);
                
                Collection<Person> people = registrar.getAllPeople(index, count);
                
                int nextIndex = (people.size()==0) ? 
                        index : index + people.size();
                
                request.setAttribute(PEOPLE_PARAM, people);
                request.setAttribute(INDEX_PARAM, index);
                request.setAttribute(COUNT_PARAM, count);
                request.setAttribute(NEXT_INDEX_PARAM, nextIndex);
                
                RequestDispatcher rd = 
                 getServletContext().getRequestDispatcher(DISPLAY_PEOPLE_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
            	log.error("error getting people", ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    DISPLAY_EXCEPTION_URL);
                rd.forward(request, response);
            }
        }
    }
    
    private class GetPerson extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {            
            try {
                String idStr = (String)request.getParameter(ID_PARAM);
                long id = Long.parseLong(idStr);
                
                Person person = registrar.getPersonById(id);
                
                request.setAttribute(PERSON_PARAM, person);
                
                RequestDispatcher rd = 
                 getServletContext().getRequestDispatcher(DISPLAY_PERSON_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
            	log.error("error getting people", ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    DISPLAY_EXCEPTION_URL);
                rd.forward(request, response);
            }
        }
    }    

    private class GetPersonHydrated extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {            
            try {
                String idStr = (String)request.getParameter(ID_PARAM);
                long id = Long.parseLong(idStr);
                
                Person person = (registrar instanceof RegistrarRemote) ?
                	((RegistrarRemote)registrar).getPersonByIdHydrated(id) :
                    ((RegistrarLocal)registrar).getPersonByIdHydrated(id);
                
                request.setAttribute(PERSON_PARAM, person);
                
                RequestDispatcher rd = 
                 getServletContext().getRequestDispatcher(DISPLAY_PERSON_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
            	log.error("error getting people", ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    DISPLAY_EXCEPTION_URL);
                rd.forward(request, response);
            }
        }
    }
    
    private class ChangeAddressCommand extends Handler {
        public void handle(HttpServletRequest request, 
                HttpServletResponse response) 
                throws ServletException, IOException {            
            try {
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
                    ((RegistrarLocal)registrar).getPersonByIdHydrated(id);
               
                person = registrar.changeAddress(person, address);
                
                request.setAttribute(PERSON_PARAM, person);
                
                RequestDispatcher rd = 
                 getServletContext().getRequestDispatcher(DISPLAY_PERSON_URL);
                rd.forward(request, response);                
            }
            catch (Exception ex) {
            	log.error("error getting people", ex);
                RequestDispatcher rd = getServletContext().getRequestDispatcher(
                    DISPLAY_EXCEPTION_URL);
                rd.forward(request, response);
            }
        }
    }    

}
