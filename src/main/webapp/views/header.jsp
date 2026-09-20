<%-- 
  header.jsp - Reusable navigation bar included by all JSP pages.

  WHY INCLUDE: Instead of copying the navbar HTML into every page,
  we use <%@ include file="header.jsp" %> in each page.
  One change here updates every page — DRY principle.

  JSP CONCEPTS USED:
  - ${sessionScope.user}  → EL (Expression Language) reads from HttpSession
  - <c:if>               → JSTL conditional tag
  - <c:choose>           → JSTL switch-like tag
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Unstop Clone</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>

<nav class="navbar">
    <div class="nav-container">
        <a href="${pageContext.request.contextPath}/competitions" class="nav-brand">
            Unstop Clone
        </a>

        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/competitions">Competitions</a>

            <%-- Show different links based on login status --%>
            <c:choose>
                <c:when test="${sessionScope.user != null}">
                    <%-- Logged in: show dashboard link and user name --%>
                    <c:if test="${sessionScope.user.student}">
                        <a href="${pageContext.request.contextPath}/dashboard">My Dashboard</a>
                    </c:if>
                    <c:if test="${sessionScope.user.organizer or sessionScope.user.admin}">
                        <a href="${pageContext.request.contextPath}/organizer/dashboard">Dashboard</a>
                    </c:if>
                    <span class="nav-user">${sessionScope.user.name}</span>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline">Logout</a>
                </c:when>
                <c:otherwise>
                    <%-- Not logged in --%>
                    <a href="${pageContext.request.contextPath}/login">Login</a>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-primary text-white">Register</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>
