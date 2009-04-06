<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">
            
<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:directive.page import="myorg.javaeeex.bo.*"/>
<html>
    <title>Result</title>
    <body>
        
        <jsp:scriptlet>
            Object result = request.getAttribute("result");
        </jsp:scriptlet>
        Result: <%= result %>
                
        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>        
    </body>
</html>
            
