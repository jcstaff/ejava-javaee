<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:directive.page import="myorg.webtier.data.*"/>
<html>
    <title>Display Name</title>
    <body>
        
        <jsp:scriptlet>
            Person person = (Person)request.getAttribute("person");
        </jsp:scriptlet>
        
        
        First Name: <%=person.getFirstName()%><p/>        
        Last Name: <%=person.getLastName()%><p/>
        
        
        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>        
    </body>
</html>
            
