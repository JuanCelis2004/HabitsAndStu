<%-- 
    Document   : index
    Created on : 23/04/2025, 10:46:49 p. m.
    Author     : juand
--%>

<%@page import="Persistence.UserJpaController"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        
        <%  
            UserJpaController userJPA = new UserJpaController();
        %>
    </body>
</html>
