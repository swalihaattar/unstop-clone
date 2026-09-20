<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jsp" %>

<div class="container" style="max-width:700px; padding:2rem 1.5rem;">

    <a href="${pageContext.request.contextPath}/competitions?id=${competitionId}"
       style="color:var(--text-mid); font-size:.9rem;">← Back to competition</a>
    <h1 style="margin:1rem 0 .3rem;">Team Formation</h1>
    <p style="color:var(--text-light); margin-bottom:1.5rem;">
        Create a team and share your invite code, or join an existing one.
    </p>

    <c:if test="${param.created == 'true'}">
        <div class="alert alert-success">✅ Team created! Share your invite code below.</div>
    </c:if>
    <c:if test="${param.joined == 'true'}">
        <div class="alert alert-success">✅ You joined the team!</div>
    </c:if>
    <c:if test="${not empty requestScope.error}">
        <div class="alert alert-error">${requestScope.error}</div>
    </c:if>

    <c:choose>
        <%-- Already in a team --%>
        <c:when test="${myTeam != null}">
            <div class="info-card">
                <div style="display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:1rem;">
                    <div>
                        <h2 style="margin-bottom:.2rem;">${myTeam.name}</h2>
                        <p style="color:var(--text-light); font-size:.9rem;">
                            Led by ${myTeam.leaderName} · ${myTeam.memberCount} member(s)
                        </p>
                    </div>
                    <%-- Invite code with copy button --%>
                    <div class="invite-box">
                        <span class="invite-label">Invite Code</span>
                        <span class="invite-code" id="inviteCode">${myTeam.inviteCode}</span>
                        <button class="btn btn-outline btn-sm" onclick="copyCode()">Copy</button>
                    </div>
                </div>

                <%-- Member list --%>
                <div style="margin-top:1.2rem; border-top:1px solid var(--border); padding-top:1rem;">
                    <h4 style="margin-bottom:.6rem; font-size:.9rem; color:var(--text-mid); text-transform:uppercase; letter-spacing:.04em;">Members</h4>
                    <div style="display:flex; flex-wrap:wrap; gap:.5rem;">
                        <c:forEach var="member" items="${myTeam.memberNames}">
                            <span style="background:var(--primary-light); color:var(--primary-dark);
                                         padding:.3rem .8rem; border-radius:999px; font-size:.88rem; font-weight:500;">
                                👤 ${member}
                            </span>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:when>

        <%-- Not in a team yet --%>
        <c:otherwise>
            <div style="display:grid; grid-template-columns:1fr 1fr; gap:1.5rem;">

                <%-- Create team --%>
                <div style="background:var(--bg-white); border:1px solid var(--border);
                            border-radius:var(--radius-lg); padding:1.5rem;">
                    <h3 style="margin-bottom:1rem;">Create a Team</h3>
                    <form action="${pageContext.request.contextPath}/teams" method="post">
                        <input type="hidden" name="action"        value="create">
                        <input type="hidden" name="competitionId" value="${competitionId}">
                        <div class="form-group">
                            <label for="teamName">Team Name</label>
                            <input type="text" id="teamName" name="teamName"
                                   placeholder="e.g. NullPointers" required>
                        </div>
                        <button type="submit" class="btn btn-primary btn-block">
                            Create Team
                        </button>
                    </form>
                </div>

                <%-- Join team --%>
                <div style="background:var(--bg-white); border:1px solid var(--border);
                            border-radius:var(--radius-lg); padding:1.5rem;">
                    <h3 style="margin-bottom:1rem;">Join a Team</h3>
                    <form action="${pageContext.request.contextPath}/teams" method="post">
                        <input type="hidden" name="action"        value="join">
                        <input type="hidden" name="competitionId" value="${competitionId}">
                        <div class="form-group">
                            <label for="inviteCode">Invite Code</label>
                            <input type="text" id="inviteCode" name="inviteCode"
                                   placeholder="e.g. A1B2C3" maxlength="6"
                                   style="text-transform:uppercase; letter-spacing:.15em; font-weight:700;"
                                   required>
                        </div>
                        <button type="submit" class="btn btn-outline btn-block">
                            Join Team
                        </button>
                    </form>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<style>
.invite-box   { display:flex; align-items:center; gap:.6rem; background:var(--bg);
                border:1.5px dashed var(--border); border-radius:var(--radius); padding:.5rem 1rem; }
.invite-label { font-size:.78rem; color:var(--text-light); font-weight:600; text-transform:uppercase; }
.invite-code  { font-size:1.3rem; font-weight:800; color:var(--primary); letter-spacing:.1em; }
</style>

<script>
function copyCode() {
    const code = document.getElementById('inviteCode').textContent;
    navigator.clipboard.writeText(code).then(() => {
        alert('Invite code copied: ' + code);
    });
}
</script>

<footer><p>Unstop Clone</p></footer>
</body></html>
