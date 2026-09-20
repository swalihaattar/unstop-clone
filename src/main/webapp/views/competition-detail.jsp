<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jsp" %>

<div class="container">
    <div style="padding:1.5rem 0 .5rem;">
        <a href="${pageContext.request.contextPath}/competitions"
           style="color:var(--text-mid); font-size:.9rem;">← Back to competitions</a>
    </div>

    <c:if test="${param.registered == 'true'}">
        <div class="alert alert-success">🎉 Registered successfully!</div>
    </c:if>
    <c:if test="${param.alreadyRegistered == 'true'}">
        <div class="alert alert-info">You are already registered.</div>
    </c:if>

    <div class="detail-layout">
        <%-- ── Main content ── --%>
        <div class="detail-main">
            <div style="display:flex; align-items:center; gap:1rem; margin-bottom:.8rem; flex-wrap:wrap;">
                <h1>${competition.title}</h1>
                <span class="badge badge-open">${competition.status}</span>
                <%-- Type badge --%>
                <c:choose>
                    <c:when test="${competition.quiz}">
                        <span class="badge badge-coding">Quiz</span>
                    </c:when>
                    <c:when test="${competition.hackathon}">
                        <span class="badge badge-hackathon">Hackathon</span>
                    </c:when>
                </c:choose>
            </div>
            <p style="color:var(--text-light); margin-bottom:1.5rem; font-size:.9rem;">
                By <strong>${competition.organizerName}</strong>
            </p>
            <div style="background:var(--bg-white); border-radius:var(--radius-lg);
                        padding:2rem; border:1px solid var(--border);">
                <h3 style="margin-bottom:1rem;">About</h3>
                <p class="detail-body">${competition.description}</p>
            </div>

            <%-- Leaderboard link (always visible once registered) --%>
            <c:if test="${sessionScope.user != null and alreadyRegistered}">
                <div style="margin-top:1rem;">
                    <a href="${pageContext.request.contextPath}/leaderboard?competitionId=${competition.id}"
                       class="btn btn-outline btn-sm">📊 View Leaderboard</a>
                </div>
            </c:if>
        </div>

        <%-- ── Sidebar ── --%>
        <aside class="detail-sidebar">

            <div class="info-card" style="text-align:center;">
                <p style="color:var(--text-light); font-size:.85rem; margin-bottom:.3rem;">Prize Pool</p>
                <div class="prize-amount">${competition.prizePool}</div>
            </div>

            <div class="info-card">
                <h3>Details</h3>
                <div class="info-row">
                    <span class="label">Category</span>
                    <span class="value">${competition.categoryName}</span>
                </div>
                <div class="info-row">
                    <span class="label">Type</span>
                    <span class="value" style="text-transform:capitalize;">
                        ${competition.competitionType}
                    </span>
                </div>
                <div class="info-row">
                    <span class="label">Team size</span>
                    <span class="value">${competition.teamSizeDisplay}</span>
                </div>
                <div class="info-row">
                    <span class="label">Deadline</span>
                    <span class="value">${competition.lastDate}</span>
                </div>
                <div class="info-row">
                    <span class="label">Registered</span>
                    <span class="value">${competition.registrationCount}</span>
                </div>
            </div>

            <%-- ── Action card ── --%>
            <div class="info-card">
                <c:choose>

                    <%-- Not logged in --%>
                    <c:when test="${sessionScope.user == null}">
                        <p style="color:var(--text-mid); margin-bottom:1rem; font-size:.9rem;">
                            Log in to participate.
                        </p>
                        <a href="${pageContext.request.contextPath}/login"
                           class="btn btn-primary btn-block">Login to Register</a>
                    </c:when>

                    <%-- Organizer / admin --%>
                    <c:when test="${sessionScope.user.organizer or sessionScope.user.admin}">
                        <p style="color:var(--text-mid); font-size:.9rem;">
                            Organizers cannot participate.
                        </p>
                    </c:when>

                    <%-- Registered student — show type-specific actions --%>
                    <c:when test="${alreadyRegistered}">
                        <p style="font-weight:600; margin-bottom:1rem; color:var(--success);">
                            ✅ You're registered
                        </p>

                        <c:choose>
                            <%-- Quiz actions --%>
                            <c:when test="${competition.quiz}">
                                <c:choose>
                                    <c:when test="${quizAttempt != null and quizAttempt.status == 'submitted'}">
                                        <p style="margin-bottom:.8rem; color:var(--text-mid); font-size:.9rem;">
                                            You scored <strong>${quizAttempt.score}/${quizAttempt.totalMarks}</strong>
                                        </p>
                                        <a href="${pageContext.request.contextPath}/quiz/result?attemptId=${quizAttempt.id}"
                                           class="btn btn-outline btn-block">View My Result</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/quiz?competitionId=${competition.id}"
                                           class="btn btn-primary btn-block btn-lg">
                                            ▶ Start Quiz
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                                <a href="${pageContext.request.contextPath}/leaderboard?competitionId=${competition.id}"
                                   class="btn btn-outline btn-block" style="margin-top:.6rem;">
                                    📊 Leaderboard
                                </a>
                            </c:when>

                            <%-- Hackathon actions --%>
                            <c:when test="${competition.hackathon}">
                                <div style="display:flex; flex-direction:column; gap:.6rem;">
                                    <a href="${pageContext.request.contextPath}/teams?competitionId=${competition.id}"
                                       class="btn btn-outline btn-block">👥 Team Formation</a>
                                    <a href="${pageContext.request.contextPath}/submit?competitionId=${competition.id}"
                                       class="btn btn-primary btn-block">🚀 Submit Project</a>
                                    <a href="${pageContext.request.contextPath}/leaderboard?competitionId=${competition.id}"
                                       class="btn btn-outline btn-block">📊 Leaderboard</a>
                                </div>
                            </c:when>

                            <%-- General competition --%>
                            <c:otherwise>
                                <p style="color:var(--text-mid); font-size:.9rem;">
                                    Registration confirmed. Await further details from the organizer.
                                </p>
                            </c:otherwise>
                        </c:choose>
                    </c:when>

                    <%-- Student, not yet registered --%>
                    <c:when test="${competition.status == 'open'}">
                        <p style="color:var(--text-mid); margin-bottom:1rem; font-size:.9rem;">
                            Registering as <strong>${sessionScope.user.name}</strong>
                        </p>
                        <form action="${pageContext.request.contextPath}/competitions" method="post">
                            <input type="hidden" name="competitionId" value="${competition.id}">
                            <button type="submit" class="btn btn-primary btn-block btn-lg">
                                🚀 Register Now
                            </button>
                        </form>
                    </c:when>

                    <c:otherwise>
                        <p style="color:var(--danger); font-weight:600;">Registrations closed.</p>
                    </c:otherwise>

                </c:choose>
            </div>

        </aside>
    </div>
</div>

<footer><p>Unstop Clone</p></footer>
</body></html>
