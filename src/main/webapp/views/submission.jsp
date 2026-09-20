<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jsp" %>

<div class="container" style="max-width:700px; padding:2rem 1.5rem;">

    <a href="${pageContext.request.contextPath}/competitions?id=${competitionId}"
       style="color:var(--text-mid); font-size:.9rem;">← Back to competition</a>

    <h1 style="margin:1rem 0 .3rem;">Submit Your Project</h1>
    <p style="color:var(--text-light); margin-bottom:1.5rem;">
        Share your GitHub repo and a live demo link.
    </p>

    <c:if test="${param.submitted == 'true'}">
        <div class="alert alert-success">✅ Project submitted successfully!</div>
    </c:if>
    <c:if test="${not empty requestScope.error}">
        <div class="alert alert-error">${requestScope.error}</div>
    </c:if>

    <%-- Already submitted: show existing submission --%>
    <c:choose>
        <c:when test="${alreadySubmitted}">
            <div class="info-card" style="margin-bottom:1.5rem;">
                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:1rem;">
                    <h3>${submission.projectTitle}</h3>

                    <c:if test="${not empty submission.teamName}">
                        <p style="font-size:.85rem; color:var(--text-light);">
                            Submitted by team: <strong>${submission.teamName}</strong>
                        </p>
                    </c:if>
                    <c:choose>
                        <c:when test="${submission.graded}">
                            <span class="badge badge-open">Score: ${submission.score}/100</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge badge-default">Awaiting review</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                <p style="color:var(--text-mid); font-size:.92rem; margin-bottom:1rem;">
                    ${submission.description}
                </p>
                <div style="display:flex; gap:1rem; flex-wrap:wrap;">
                    <c:if test="${not empty submission.githubUrl}">
                        <a href="${submission.githubUrl}" target="_blank" class="btn btn-outline btn-sm">
                            🔗 GitHub Repo
                        </a>
                    </c:if>
                    <c:if test="${not empty submission.demoUrl}">
                        <a href="${submission.demoUrl}" target="_blank" class="btn btn-outline btn-sm">
                            🚀 Live Demo
                        </a>
                    </c:if>
                </div>
                <c:if test="${not empty submission.feedback}">
                    <div style="margin-top:1rem; padding:1rem; background:var(--primary-light);
                                border-radius:var(--radius); border-left:3px solid var(--primary);">
                        <strong>Organizer Feedback:</strong>
                        <p style="margin-top:.3rem; color:var(--text-mid);">${submission.feedback}</p>
                    </div>
                </c:if>
            </div>
        </c:when>
        <c:otherwise>
            <%-- Team info banner --%>
            <c:if test="${team != null}">
                <div class="alert alert-info" style="margin-bottom:1.5rem;">
                    👥 Submitting as team <strong>${team.name}</strong>
                    (${team.memberCount} members · Code: <code>${team.inviteCode}</code>)
                </div>
            </c:if>

            <div style="background:var(--bg-white); border:1px solid var(--border);
                        border-radius:var(--radius-lg); padding:2rem;">
                <form action="${pageContext.request.contextPath}/submit" method="post"
                      id="submitForm">
                    <input type="hidden" name="competitionId" value="${competitionId}">
                    <c:if test="${team != null}">
                        <input type="hidden" name="teamId" value="${team.id}">
                    </c:if>

                    <div class="form-group">
                        <label for="projectTitle">Project Name *</label>
                        <input type="text" id="projectTitle" name="projectTitle"
                               placeholder="e.g. EcoTrack — Carbon Footprint Analyzer" required>
                        <span class="field-error"></span>
                    </div>

                    <div class="form-group">
                        <label for="description">Project Description *</label>
                        <textarea id="description" name="description" rows="4"
                                  placeholder="Describe what your project does, tech stack used, and what problem it solves."
                                  required></textarea>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="githubUrl">GitHub Repository URL</label>
                            <input type="url" id="githubUrl" name="githubUrl"
                                   placeholder="https://github.com/you/project">
                        </div>
                        <div class="form-group">
                            <label for="demoUrl">Live Demo / Video URL</label>
                            <input type="url" id="demoUrl" name="demoUrl"
                                   placeholder="https://your-demo.vercel.app">
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block btn-lg"
                            style="margin-top:.5rem;">
                        🚀 Submit Project
                    </button>
                </form>
            </div>
        </c:otherwise>
    </c:choose>

    <%-- Team formation prompt --%>
    <c:if test="${team == null and not alreadySubmitted}">
        <div style="margin-top:1.5rem; padding:1rem 1.2rem; background:var(--bg);
                    border-radius:var(--radius); border:1px solid var(--border);
                    font-size:.9rem; color:var(--text-mid);">
            No team yet?
            <a href="${pageContext.request.contextPath}/teams?competitionId=${competitionId}">
                Create or join a team
            </a>
            before submitting if your competition requires it.
        </div>
    </c:if>
</div>

<footer><p>Unstop Clone</p></footer>
<script src="${pageContext.request.contextPath}/static/js/validation.js"></script>
</body></html>
