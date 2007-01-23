package myorg.webtier.web;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.registry.infomodel.PersonName;

import myorg.webtier.data.Person;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class HandlerServlet extends HttpServlet {
    Log log = LogFactory.getLog(HandlerServlet.class);

    public void init() {
        log.debug("init() called ");
    }

    protected void doGet(HttpServletRequest request, 
                         HttpServletResponse response) 
        throws ServletException, IOException {
        log.debug("doGet() called");

        Person person = new Person();
        person.setFirstName(request.getParameter("firstName"));
        person.setLastName(request.getParameter("lastName"));
        
        request.setAttribute("person", person);

        RequestDispatcher rd = getServletContext().getRequestDispatcher(
            "/WEB-INF/content/DisplayName.jsp");
            rd.forward(request, response);

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
}
