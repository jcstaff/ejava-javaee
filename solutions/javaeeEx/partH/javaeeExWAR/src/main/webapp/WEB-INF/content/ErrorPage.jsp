<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">

<%-- ErrorPage.jsp
     This page is registered to handle errors in JSP files.
     --%>
<%@ page isErrorPage="true" %>
<html>
<header>
   <title>General Exception Page</title>
</header>
<body>
   <center><h1>General Exception Page</h1></center>
   <p>An error was reported by the application. More detailed information
   may follow.</p>.

   <p><%
      java.io.PrintWriter writer = new java.io.PrintWriter(out);
      /*if (exception == null) { //check if this is a redirect versus errorPage
    	  exception = (Exception)request.getAttribute("exception");
      }*/
      exception.printStackTrace(writer);
   %></p>

   <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>
</body>
</html>

