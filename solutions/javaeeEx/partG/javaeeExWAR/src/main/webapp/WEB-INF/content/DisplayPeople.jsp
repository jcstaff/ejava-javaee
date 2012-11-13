<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
            "http://www.w3.org/TR/html4/strict.dtd">

<jsp:directive.page errorPage="/WEB-INF/content/ErrorPage.jsp"/>
<jsp:directive.page import="java.util.*"/>
<jsp:directive.page import="myorg.javaeeex.bo.*"/>
<html>
    <title>People Display</title>
    <body>
        <h2>People Display</h2>

        <jsp:scriptlet>
            List people = (List)request.getAttribute("result");
            int index = ((Integer)request.getAttribute("index")).intValue();
            int count = ((Integer)request.getAttribute("count")).intValue();
            int nextIndex = ((Integer)request.getAttribute("nextIndex")).intValue();
            String handler = request.getContextPath() + "/model/admin/handler";
        </jsp:scriptlet>

        <ul>
            <jsp:scriptlet>
                for(Object o: people) {
                    Person p = (Person)o;
                    String firstName = p.getFirstName();
                    String lastName = p.getLastName();
                    String url = "?id=" + p.getId() + "&amp;command=Get+Person";
            </jsp:scriptlet>
                <li><a href="<%= url %>"><%= firstName %> <%= lastName %> </a></li>
            <jsp:scriptlet>
                }
            </jsp:scriptlet>
        </ul>

        <form method="GET"
            action="<%=request.getContextPath()%>/model/admin/handler">
            Index: <%= index %><p/>
            Count: <%= count %><p/>
            <input type="hidden" name="index" value="<%= nextIndex %>"/>
            <input type="hidden" name="count" value="<%= count %>"/>
            <input type="submit" name="command" value="Get All People"/>
        </form>

        <p/><a href="<%=request.getContextPath()%>/index.jsp">Go to Main Page</a>
    </body>
</html>
