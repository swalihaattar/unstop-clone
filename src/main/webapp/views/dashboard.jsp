<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="header.jsp" %>

<div class="container">
    <div class="dashboard-header">
        <div>
            <h1>My Dashboard</h1>
            <p style="color:var(--text-light);">
                Welcome, <strong>${sessionScope.user.name}</strong>
                &nbsp;·&nbsp; ${sessionScope.user.college}
            </p>
        </div>
        <a href="${pageContext.request.contextPath}/competitions" class="btn btn-primary">Browse More</a>
    </div>

    <%-- Stats row --%>
    <div class="stats-row">
        <div class="stat-card">
            <div class="stat-number">${fn:length(myRegistrations)}</div>
            <div class="stat-label">Competitions Joined</div>
        </div>
        <div class="stat-card">
            <div class="stat-number">${fn:length(mySubmissions)}</div>
            <div class="stat-label">Projects Submitted</div>
        </div>
        <div class="stat-card">
            <div class="stat-number">${fn:length(myAttempts)}</div>
            <div class="stat-label">Quizzes Taken</div>
        </div>
    </div>

    <%-- Registrations table --%>
    <h2 style="margin-bottom:1rem;">My Registrations</h2>
    <c:choose>
        <c:when test="${empty myRegistrations}">
            <div style="text-align:center; padding:3rem; background:var(--bg-white);
                        border-radius:var(--radius-lg); border:1px solid var(--border); margin-bottom:2rem;">
                <p style="color:var(--text-light);">No registrations yet.</p>
                <a href="${pageContext.request.contextPath}/competitions"
                   class="btn btn-primary" style="margin-top:1rem;">Browse Competitions</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="registrations-table" style="margin-bottom:2rem;">
                <table>
                    <thead>
                        <tr><th>#</th><th>Competition</th><th>Type</th><th>Status</th><th>Actions</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="reg" items="${myRegistrations}" varStatus="loop">
                            <tr>
                                <td>${loop.index + 1}</td>
                                <td><strong>${reg.competitionTitle}</strong></td>
                                <td><span class="badge badge-default" style="text-transform:capitalize;">${reg.competitionType}</span></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${reg.competitionStatus == 'open'}">
                                            <span class="badge badge-open">Open</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-closed">${reg.competitionStatus}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/competitions?id=${reg.competitionId}"
                                       class="btn btn-outline btn-sm">View</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>

    <%-- Quiz attempts --%>
    <c:if test="${not empty myAttempts}">
        <h2 style="margin-bottom:1rem;">My Quiz Results</h2>
        <div class="registrations-table" style="margin-bottom:2rem;">
            <table>
                <thead>
                    <tr><th>Competition</th><th>Score</th><th>Percentage</th><th>Status</th><th></th></tr>
                </thead>
                <tbody>
                    <c:forEach var="attempt" items="${myAttempts}">
                        <tr>
                            <td><strong>${attempt.competitionTitle}</strong></td>
                            <td>${attempt.score} / ${attempt.totalMarks}</td>
                            <td>
                                <div style="display:flex; align-items:center; gap:.6rem;">
                                    <div style="flex:1; height:8px; background:var(--border); border-radius:4px; overflow:hidden;">
                                        <div style="height:100%; width:${attempt.percentage}%;
                                                    background:var(--primary); border-radius:4px;"></div>
                                    </div>
                                    <span style="font-size:.85rem; font-weight:600;">${attempt.percentage}%</span>
                                </div>
                            </td>
                            <td><span class="badge badge-open">${attempt.status}</span></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/quiz/result?attemptId=${attempt.id}"
                                   class="btn btn-outline btn-sm">Details</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>

    <%-- Hackathon submissions --%>
    <c:if test="${not empty mySubmissions}">
        <h2 style="margin-bottom:1rem;">My Submissions</h2>
        <div class="registrations-table" style="margin-bottom:2rem;">
            <table>
                <thead>
                    <tr><th>Project</th><th>Competition</th><th>Score</th><th>Links</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="sub" items="${mySubmissions}">
                        <tr>
                            <td><strong>${sub.projectTitle}</strong></td>
                            <td>${sub.competitionTitle}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${sub.graded}">
                                        <span class="badge badge-open">${sub.score}/100</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-default">Pending</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td style="display:flex; gap:.4rem; flex-wrap:wrap;">
                                <c:if test="${not empty sub.githubUrl}">
                                    <a href="${sub.githubUrl}" target="_blank"
                                       class="btn btn-outline btn-sm">GitHub</a>
                                </c:if>
                                <c:if test="${not empty sub.demoUrl}">
                                    <a href="${sub.demoUrl}" target="_blank"
                                       class="btn btn-outline btn-sm">Demo</a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>

</div>

<footer><p>Unstop Clone</p></footer>
</body></html>
