<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jsp" %>

<div class="container" style="max-width:760px; padding:2rem 1.5rem;">

    <%-- Score summary card --%>
    <div class="result-hero">
        <div class="result-score-ring">
            <span class="score-num">${attempt.percentage}%</span>
        </div>
        <div class="result-summary">
            <h2>Quiz Complete!</h2>
            <p style="color:var(--text-mid); margin:.3rem 0 .8rem;">
                ${attempt.competitionTitle}
            </p>
            <div style="display:flex; gap:1.5rem; flex-wrap:wrap;">
                <span class="result-stat">
                    <strong>${attempt.score}</strong> / ${attempt.totalMarks} marks
                </span>
                <span class="result-stat">
                    <strong>${attempt.submittedAt}</strong>
                </span>
            </div>
            <div style="margin-top:1rem; display:flex; gap:.7rem; flex-wrap:wrap;">
                <a href="${pageContext.request.contextPath}/leaderboard?competitionId=${attempt.competitionId}"
                   class="btn btn-primary btn-sm">View Leaderboard</a>
                <a href="${pageContext.request.contextPath}/competitions"
                   class="btn btn-outline btn-sm">Browse More</a>
            </div>
        </div>
    </div>

    <%-- Answer breakdown --%>
    <h3 style="margin:2rem 0 1rem;">Answer Breakdown</h3>
    <c:forEach var="q" items="${attempt.questions}" varStatus="loop">
        <div class="answer-card ${q.correct ? 'correct' : 'wrong'}">
            <div class="answer-header">
                <span class="q-num">Q${loop.index + 1}</span>
                <span class="answer-badge ${q.correct ? 'badge-open' : 'badge-closed'}">
                    ${q.correct ? '✓ Correct' : '✗ Wrong'}
                </span>
                <span style="margin-left:auto; color:var(--text-light); font-size:.85rem;">
                    ${q.marks} mark${q.marks != 1 ? 's' : ''}
                </span>
            </div>
            <p class="answer-question">${q.questionText}</p>
            <div class="answer-options">
                <c:forEach var="opt" items="${['A','B','C','D']}">
                    <div class="answer-opt
                        ${opt == q.correctOption ? 'opt-correct' : ''}
                        ${opt == q.chosenOption and opt != q.correctOption ? 'opt-wrong' : ''}">
                        <span class="opt-key">${opt}</span>
                        <span>
                            <c:choose>
                                <c:when test="${opt == 'A'}">${q.optionA}</c:when>
                                <c:when test="${opt == 'B'}">${q.optionB}</c:when>
                                <c:when test="${opt == 'C'}">${q.optionC}</c:when>
                                <c:when test="${opt == 'D'}">${q.optionD}</c:when>
                            </c:choose>
                        </span>
                        <c:if test="${opt == q.correctOption}">
                            <span class="opt-tag">Correct</span>
                        </c:if>
                        <c:if test="${opt == q.chosenOption and opt != q.correctOption}">
                            <span class="opt-tag opt-tag-wrong">Your answer</span>
                        </c:if>
                    </div>
                </c:forEach>
                <c:if test="${empty q.chosenOption}">
                    <p style="color:var(--text-light); font-size:.85rem; margin-top:.4rem;">
                        ⚠ Not answered
                    </p>
                </c:if>
            </div>
        </div>
    </c:forEach>
</div>

<style>
.result-hero { display:flex; gap:2rem; align-items:center; background:var(--bg-white);
               border:1px solid var(--border); border-radius:var(--radius-lg);
               padding:2rem; margin-bottom:1.5rem; flex-wrap:wrap; }
.result-score-ring { width:110px; height:110px; border-radius:50%;
                     border:6px solid var(--primary); display:flex; align-items:center;
                     justify-content:center; flex-shrink:0; }
.score-num  { font-size:1.6rem; font-weight:800; color:var(--primary); }
.result-stat { font-size:.9rem; color:var(--text-mid); }

.answer-card { background:var(--bg-white); border:1px solid var(--border);
               border-radius:var(--radius-lg); padding:1.3rem; margin-bottom:1rem;
               border-left:4px solid var(--border); }
.answer-card.correct { border-left-color: var(--success); }
.answer-card.wrong   { border-left-color: var(--danger); }
.answer-header { display:flex; align-items:center; gap:.7rem; margin-bottom:.6rem; }
.q-num       { background:var(--bg); border:1px solid var(--border); border-radius:6px;
               padding:.1rem .5rem; font-size:.8rem; font-weight:700; }
.answer-question { font-weight:600; margin-bottom:.8rem; line-height:1.4; }
.answer-options  { display:flex; flex-direction:column; gap:.4rem; }
.answer-opt { display:flex; align-items:center; gap:.7rem; padding:.5rem .8rem;
              border-radius:8px; font-size:.9rem; background:var(--bg); }
.opt-correct { background:#dcfce7; }
.opt-wrong   { background:#fee2e2; }
.opt-key  { width:24px; height:24px; border-radius:50%; background:var(--border);
            display:flex; align-items:center; justify-content:center;
            font-weight:700; font-size:.78rem; flex-shrink:0; }
.opt-correct .opt-key { background:#22c55e; color:white; }
.opt-wrong   .opt-key { background:var(--danger); color:white; }
.opt-tag { margin-left:auto; font-size:.75rem; font-weight:600;
           color:#166534; background:#dcfce7; padding:.1rem .5rem; border-radius:4px; }
.opt-tag-wrong { color:#991b1b; background:#fee2e2; }
</style>
</body></html>
