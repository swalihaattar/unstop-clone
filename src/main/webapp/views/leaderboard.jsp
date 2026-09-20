<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="header.jsp" %>

<div class="container" style="padding:2rem 1.5rem;">

    <a href="${pageContext.request.contextPath}/competitions?id=${competition.id}"
       style="color:var(--text-mid); font-size:.9rem;">← Back</a>

    <div style="display:flex; justify-content:space-between; align-items:center;
                margin:1rem 0 1.5rem; flex-wrap:wrap; gap:1rem;">
        <div>
            <h1 style="margin-bottom:.2rem;">Leaderboard</h1>
            <p style="color:var(--text-light);">${competition.title}</p>
        </div>
        <span class="badge ${competition.quiz ? 'badge-coding' : 'badge-hackathon'}">
            ${competition.quiz ? 'Quiz' : 'Hackathon'}
        </span>
    </div>

    <c:choose>
        <c:when test="${empty rankings}">
            <div style="text-align:center; padding:4rem; background:var(--bg-white);
                        border-radius:var(--radius-lg); border:1px solid var(--border);">
                <p style="font-size:2.5rem;">🏆</p>
                <p style="margin-top:.8rem; color:var(--text-light);">
                    No results yet. Check back after the competition ends.
                </p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="registrations-table">
                <table>
                    <thead>
                    <tr>
                        <th style="width:60px;">Rank</th>
                        <th>Participant</th>
                        <c:choose><c:when test="${competition.quiz}">
                            <th>Score</th>
                            <th>Marks</th>
                            <th>Submitted At</th>
                        </c:when><c:otherwise>
                            <th>Project</th>
                            <th>Score</th>
                            <th>Feedback</th>
                        </c:otherwise></c:choose>
                    </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="row" items="${rankings}">
                            <tr class="${row.rank == 1 ? 'rank-gold' : row.rank == 2 ? 'rank-silver' : row.rank == 3 ? 'rank-bronze' : ''}">
                                
                                <td style="text-align:center; font-weight:700;">
                                    ${row.rank}
                                </td>

                                
                                <td>
                                    <strong>
                                        <c:choose>
                                            <c:when test="${not empty row.teamName}">
                                                👥 ${row.teamName}
                                            </c:when>
                                            <c:otherwise>
                                                👤 ${row.name}
                                            </c:otherwise>
                                        </c:choose>
                                    </strong>
                                </td>

                                
                                <c:choose>

                                    <c:when test="${competition.quiz}">
                                        <td>${row.score}</td>

                                        <td>
                                            ${row.score}
                                            <span style="color:var(--text-light);">/ ${row.total}</span>
                                        </td>

                                        <td style="color:var(--text-light); font-size:.85rem;">
                                            ${row.detail}
                                        </td>
                                    </c:when>


                                    <c:otherwise>
                                        <td>${row.projectTitle}</td>

                                        <td>
                                            <span style="font-weight:700; color:var(--primary);">
                                                ${row.score}
                                            </span>
                                            <span style="color:var(--text-light);">/ 100</span>
                                        </td>

                                        <td style="color:var(--text-light); font-size:.85rem;">
                                            <c:choose>
                                                <c:when test="${not empty row.feedback}">
                                                    ${row.feedback}
                                                </c:when>
                                                <c:otherwise>
                                                    —
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </c:otherwise>

                                </c:choose>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<style>
.rank-gold   td { background:#fef9c3; }
.rank-silver td { background:#f3f4f6; }
.rank-bronze td { background:#fdf4ee; }
</style>

<footer><p>Unstop Clone</p></footer>
</body></html>
