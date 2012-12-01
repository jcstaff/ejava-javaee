<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">

<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:directive.page import="myorg.javaeeex.bo.*"/>
<html>
    <title>Display Person</title>
    <body>

        <jsp:scriptlet>
            Person person = (Person)request.getAttribute("result");
            Address address = (person.getAddresses() != null) ?
                (Address)person.getAddresses().iterator().next() :
                new Address();
        </jsp:scriptlet>

        Id: <%= person.getId() %><p/>
        First Name: <%=person.getFirstName()%><p/>
        Last Name: <%=person.getLastName()%><p/>
        SSN: <%=person.getSsn()%><p/>

        <form method="GET"
            action="<%=request.getContextPath()%>/model/admin/handler">
            Street: <input type="text" name="street" size="25" value="<%= address.getStreet()%>"/><p/>
            City: <input type="text" name="city" size="25" value="<%= address.getCity()%>"/><p/>
            State: <input type="text" name="state" size="2" value="<%= address.getState()%>"/><p/>
            Zip: <input type="text" name="zip" size="5" value="<%= address.getZip()%>"/><p/>
            <input type="hidden" name="id" value="<%= person.getId() %>"/>
            <input type="submit" name="command" value="Change Address"/>
        </form>


        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>
    </body>
</html>
