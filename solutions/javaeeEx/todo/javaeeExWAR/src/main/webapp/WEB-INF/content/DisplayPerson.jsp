<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:directive.page import="myorg.javaeeex.bo.*"/>
<html>
    <title>Display Person</title>
    <body>
        
        <jsp:scriptlet>
            Person person = (Person)request.getAttribute("person");
        </jsp:scriptlet>
        
        Id: <%= person.getId() %><p/>
        First Name: <%=person.getFirstName()%><p/>        
        Last Name: <%=person.getLastName()%><p/>
        SSN: <%=person.getSsn()%></p>
        <jsp:scriptlet>
        for(Address address : person.getAddresses()) {
        </jsp:scriptlet>
            <li><%=address.getStreet() %>, <%=address.getCity() %>, 
                <%=address.getState() %> <%=address.getZip()%></li>
        <jsp:scriptlet>
        }
        </jsp:scriptlet>
        
	    <form method="GET" 
	        action="<%=request.getContextPath()%>/model/admin/handler">
	        Street: <input type="text" name="street" size="25"/><p/>   
	        City: <input type="text" name="city" size="25"/><p/>         
	        State: <input type="text" name="state" size="2"/><p/>         
	        Zip: <input type="text" name="zip" size="5"/><p/>         
	        <input type="hidden" name="id" value="<%= person.getId() %>"/>
	        <input type="submit" name="command" value="Change Address"/>
	    </form>
                
        
        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>        
    </body>
</html>
            
