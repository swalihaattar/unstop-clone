<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  index.jsp - Landing / Home page
  If user is logged in → redirect to competitions
  If not → show hero landing page
--%>
<c:if test="${sessionScope.user != null}">
    <c:redirect url="/competitions"/>
</c:if>

<%@ include file="views/header.jsp" %>

<section class="hero">
    <div class="container">
        <h1>Find Your Next Challenge 🚀</h1>
        <p>Discover hackathons, quizzes, and competitions. Compete, learn, and grow.</p>
        <a href="${pageContext.request.contextPath}/competitions" class="btn btn-white btn-lg">
            Browse Competitions
        </a>
        &nbsp;
        <a href="${pageContext.request.contextPath}/register" class="btn btn-outline"
           style="color:white; border-color:white; margin-left:0.5rem">
            Sign Up Free
        </a>
    </div>
</section>

<div class="container" style="padding: 3rem 1.5rem; text-align:center;">
    <h2 style="margin-bottom:2rem;">Why Unstop?</h2>
    <div style="display:grid; grid-template-columns:repeat(auto-fit,minmax(220px,1fr)); gap:1.5rem;">
        <div class="info-card">
            <div style="font-size:2.5rem;margin-bottom:0.8rem;">🏆</div>
            <h3>Win Prizes</h3>
            <p style="color:var(--text-mid);font-size:0.9rem;margin-top:0.4rem;">
                Compete for cash prizes and recognition across multiple domains.
            </p>
        </div>
        <div class="info-card">
            <div style="font-size:2.5rem;margin-bottom:0.8rem;">🤝</div>
            <h3>Build Teams</h3>
            <p style="color:var(--text-mid);font-size:0.9rem;margin-top:0.4rem;">
                Form teams of up to 4 and collaborate with peers from across India.
            </p>
        </div>
        <div class="info-card">
            <div style="font-size:2.5rem;margin-bottom:0.8rem;">📈</div>
            <h3>Grow Your Profile</h3>
            <p style="color:var(--text-mid);font-size:0.9rem;margin-top:0.4rem;">
                Track your participation and showcase wins to recruiters.
            </p>
        </div>
    </div>
</div>

<footer>
    <p>Unstop Clone — Built with Java Servlets, JSP, ReactJS, and MySQL</p>
</footer>
</body>
</html>
