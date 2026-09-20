<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%--
  competitions.jsp - Competition browse/listing page VIEW

  JSP CONCEPTS:
  - <c:forEach>     → loops over the List<Competition> set by CompetitionServlet
  - <c:choose>      → conditional badge color based on category name
  - ${competition.X} → EL reads Competition JavaBean properties
  - fn:length()     → JSTL function to get list size

  AJAX INTEGRATION:
  - The filter buttons have data-category attributes
  - main.js reads these and makes XHR calls to /api/competitions
  - The #competitions-grid div is replaced with new cards from JS
  - On first load, cards are rendered server-side (JSP); after filtering, JS takes over
--%>
<%@ include file="header.jsp" %>

<div class="container">
    <div class="page-header">
        <h1>Competitions</h1>
        <p>Discover and register for competitions, hackathons, and quizzes</p>
    </div>

    <%-- ---- Category Filter Bar (AJAX triggers) ---- --%>
    <div class="filter-bar">
        <button class="filter-btn active" data-category="0">All</button>
        <c:forEach var="cat" items="${categories}">
            <%-- cat is String[]{id, name} --%>
            <button class="filter-btn" data-category="${cat[0]}">${cat[1]}</button>
        </c:forEach>
    </div>

    <p id="result-count" style="color:var(--text-light); font-size:0.9rem; margin-bottom:1rem;">
        ${fn:length(competitions)} competition<c:if test="${fn:length(competitions) != 1}">s</c:if> found
    </p>

    <%-- Loading spinner shown by JS during AJAX fetch --%>
    <div id="loading-spinner">Loading competitions...</div>

    <%-- ---- Competition Cards Grid ---- --%>
    <%-- This div is the target that main.js replaces on filter --%>
    <div class="competitions-grid" id="competitions-grid">

        <c:choose>
            <c:when test="${empty competitions}">
                <div style="grid-column:1/-1; text-align:center; padding:3rem; color:#9ca3af;">
                    <p style="font-size:3rem">📭</p>
                    <p>No competitions available right now. Check back soon!</p>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="c" items="${competitions}">
                    <div class="comp-card">
                        <div class="comp-card-header">
                            <h3>${c.title}</h3>
                            <%-- Dynamic badge color based on category --%>
                            <c:choose>
                                <c:when test="${c.categoryName == 'Hackathon'}">
                                    <span class="badge badge-hackathon">${c.categoryName}</span>
                                </c:when>
                                <c:when test="${c.categoryName == 'Quiz'}">
                                    <span class="badge badge-quiz">${c.categoryName}</span>
                                </c:when>
                                <c:when test="${c.categoryName == 'Case Study'}">
                                    <span class="badge badge-case">${c.categoryName}</span>
                                </c:when>
                                <c:when test="${c.categoryName == 'Coding'}">
                                    <span class="badge badge-coding">${c.categoryName}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-default">${c.categoryName}</span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <p class="desc">${c.description}</p>

                        <div class="comp-meta">
                            <span>🏆 ${c.prizePool}</span>
                            <span>👥 Team: ${c.teamSizeDisplay}</span>
                            <span>📅 ${c.lastDate}</span>
                            <span>👤 ${c.registrationCount} registered</span>
                        </div>

                        <div class="comp-card-footer">
                            <span class="badge badge-open">Open</span>
                            <a href="${pageContext.request.contextPath}/competitions?id=${c.id}"
                               class="btn btn-primary btn-sm">
                                View Details →
                            </a>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>

    </div><%-- end #competitions-grid --%>
</div>

<footer>
    <p>Unstop Clone — Built with Java Servlets, JSP, ReactJS & MySQL</p>
</footer>

<%--
  Pass the context path to JavaScript so it can build correct API URLs.
  This is the standard way to bridge JSP server variables into JS.
--%>
<script>
    const contextPath = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/static/js/main.js"></script>
</body>
</html>
