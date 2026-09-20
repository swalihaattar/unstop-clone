<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  login.jsp - Login form VIEW

  JSP CONCEPTS DEMONSTRATED:
  - ${param.registered}    → reads URL query param (?registered=true)
  - ${requestScope.error}  → reads attribute set by LoginServlet
  - <c:if>                 → JSTL conditional rendering
  - EL (Expression Language) to display dynamic data
--%>
<%@ include file="header.jsp" %>

<div class="form-card">
    <h2>Welcome back!</h2>
    <p class="subtitle">Log in to your Unstop account</p>

    <%-- Show success message after registration --%>
    <c:if test="${param.registered == 'true'}">
        <div class="alert alert-success">
            Account created! Please log in.
        </div>
    </c:if>

    <%-- Show success message after logout --%>
    <c:if test="${param.logout == 'true'}">
        <div class="alert alert-info">
            You have been logged out.
        </div>
    </c:if>

    <%-- Show error from LoginServlet (set via req.setAttribute) --%>
    <c:if test="${not empty requestScope.error}">
        <div class="alert alert-error">
            ${requestScope.error}
        </div>
    </c:if>

    <%--
      POST action → LoginServlet.doPost()
      The id="loginForm" is used by validation.js
    --%>
    <form id="loginForm" action="${pageContext.request.contextPath}/login" method="post">

        <div class="form-group">
            <label for="email">Email address</label>
            <input type="email" id="email" name="email"
                   placeholder="you@college.edu"
                   value="${param.email}"
                   required autocomplete="email">
            <span class="field-error"></span>
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password"
                   placeholder="Enter your password"
                   required autocomplete="current-password">
            <span class="field-error"></span>
        </div>

        <button type="submit" class="btn btn-primary btn-block btn-lg"
                style="margin-top:0.5rem;">
            Log In
        </button>
    </form>

    <p style="text-align:center; margin-top:1.5rem; color:var(--text-mid); font-size:0.9rem;">
        Don't have an account?
        <a href="${pageContext.request.contextPath}/register">Sign up free</a>
    </p>

    <%-- Demo credentials hint --%>
    <div style="margin-top:1.5rem; padding:1rem; background:var(--bg); border-radius:8px; font-size:0.83rem; color:var(--text-mid);">
        <strong>Demo accounts:</strong><br>
        Student: register a new account<br>
        Organizer: organizer@techcorp.com / org123
    </div>
</div>

<script src="${pageContext.request.contextPath}/static/js/validation.js"></script>
</body>
</html>
