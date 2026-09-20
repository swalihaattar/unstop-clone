<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  register.jsp - New user registration form VIEW

  Note how form field values are pre-filled on error:
  value="${requestScope.name}" → if server sends back the name attribute,
  the user doesn't need to retype it (good UX).
--%>
<%@ include file="header.jsp" %>

<div class="form-card" style="max-width:520px;">
    <h2>Create an account 🎓</h2>
    <p class="subtitle">Join thousands of students on Unstop</p>

    <c:if test="${not empty requestScope.error}">
        <div class="alert alert-error">${requestScope.error}</div>
    </c:if>

    <form id="registerForm" action="${pageContext.request.contextPath}/register" method="post">

        <div class="form-group">
            <label for="name">Full Name</label>
            <input type="text" id="name" name="name"
                   placeholder="Priya Sharma"
                   value="${requestScope.name}"
                   required>
            <span class="field-error"></span>
        </div>

        <div class="form-group">
            <label for="email">Email address</label>
            <input type="email" id="email" name="email"
                   placeholder="priya@college.edu"
                   value="${requestScope.email}"
                   required>
            <span class="field-error"></span>
        </div>

        <div class="form-row">
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password"
                       placeholder="Min. 6 characters" required>
                <span class="field-error"></span>
            </div>
            <div class="form-group">
                <label for="confirmPassword">Confirm Password</label>
                <input type="password" id="confirmPassword" name="confirmPassword"
                       placeholder="Repeat password" required>
                <span class="field-error"></span>
            </div>
        </div>

        <div class="form-group">
            <label for="college">College / Institution</label>
            <input type="text" id="college" name="college"
                   placeholder="Cummins College of Engineering"
                   value="${requestScope.college}"
                   required>
            <span class="field-error"></span>
        </div>

        <div class="form-group">
            <label for="role">I am registering as a</label>
            <select id="role" name="role" required>
                <option value="student">Student (looking to participate)</option>
                <option value="organizer">Organizer (looking to host events)</option>
            </select>
        </div>

        <button type="submit" class="btn btn-primary btn-block btn-lg"
                style="margin-top:0.5rem;">
            Create Account
        </button>
    </form>

    <p style="text-align:center; margin-top:1.5rem; color:var(--text-mid); font-size:0.9rem;">
        Already have an account?
        <a href="${pageContext.request.contextPath}/login">Log in</a>
    </p>
</div>

<script src="${pageContext.request.contextPath}/static/js/validation.js"></script>
</body>
</html>
