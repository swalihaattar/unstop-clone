<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="header.jsp" %>

<div class="container" style="max-width:950px; padding:2rem 1.5rem;">

    <%-- Header --%>
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:1.5rem; flex-wrap:wrap;">
        <div>
            <h1>Submissions</h1>
            <p style="color:var(--text-light); font-size:.9rem;">
                Total submissions: ${submissions != null ? fn:length(submissions) : 0}
            </p>
        </div>

        <a href="${pageContext.request.contextPath}/organizer/dashboard"
           class="btn btn-outline btn-sm">← Back</a>
    </div>

    <%-- Empty state --%>
    <c:if test="${empty submissions}">
        <div class="info-card" style="text-align:center; padding:2rem;">
            <p style="color:var(--text-light);">No submissions yet.</p>
        </div>
    </c:if>

    <%-- Submissions list --%>
    <c:forEach var="sub" items="${submissions}">
        <div class="info-card" style="margin-bottom:1.3rem;">

            <%-- Top Row --%>
            <div style="display:flex; justify-content:space-between; align-items:flex-start; gap:1rem; flex-wrap:wrap;">
                
                <div style="flex:1;">
                    <h3 style="margin-bottom:.2rem;">${sub.projectTitle}</h3>

                    <p style="font-size:.85rem; color:var(--text-light);">
                        👤 ${sub.userName}
                        <c:if test="${not empty sub.teamName}">
                            &nbsp;|&nbsp; 👥 ${sub.teamName}
                        </c:if>
                    </p>
                </div>

                <%-- Score Badge --%>
                <c:choose>
                    <c:when test="${sub.score != null}">
                        <span class="badge badge-open">${sub.score}/100</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-default">Pending</span>
                    </c:otherwise>
                </c:choose>
            </div>

            <%-- Description (collapsible for cleanliness) --%>
            <details style="margin-top:.8rem;">
                <summary style="cursor:pointer; font-size:.9rem; color:var(--primary); font-weight:600;">
                    View Description
                </summary>
                <p style="margin-top:.6rem; color:var(--text-mid); font-size:.92rem;">
                    ${sub.description}
                </p>
            </details>

            <%-- Links --%>
            <div style="margin-top:.8rem; display:flex; gap:.6rem; flex-wrap:wrap;">
                <c:if test="${not empty sub.githubUrl}">
                    <a href="${sub.githubUrl}" target="_blank"
                       class="btn btn-outline btn-sm">GitHub</a>
                </c:if>
                <c:if test="${not empty sub.demoUrl}">
                    <a href="${sub.demoUrl}" target="_blank"
                       class="btn btn-outline btn-sm">Demo</a>
                </c:if>
            </div>

            <%-- Feedback (if exists) --%>
            <c:if test="${not empty sub.feedback}">
                <div style="margin-top:.9rem; padding:.8rem 1rem;
                            background:var(--primary-light);
                            border-left:3px solid var(--primary);
                            border-radius:var(--radius);">
                    <strong>Feedback:</strong>
                    <p style="margin-top:.3rem; color:var(--text-mid);">
                        ${sub.feedback}
                    </p>
                </div>
            </c:if>

            <%-- Grade Form --%>
            <c:if test="${sub.score == null}">
                <form action="${pageContext.request.contextPath}/organizer/grade"
                    method="post"
                    style="margin-top:1rem;">

                    <input type="hidden" name="submissionId" value="${sub.id}">

                    <div class="form-row">

                        <div class="form-group" style="max-width:120px;">
                            <label>Score</label>
                            <input type="number" name="score"
                                min="0" max="100"
                                placeholder="0-100"
                                required>
                        </div>

                        <div class="form-group" style="flex:1;">
                            <label>Feedback</label>
                            <input type="text" name="feedback"
                                placeholder="Write feedback (optional)">
                        </div>

                    </div>

                    <button type="submit" class="btn btn-primary btn-sm">
                        Submit Grade
                    </button>

                </form>
            </c:if>

        </div>
    </c:forEach>

</div>