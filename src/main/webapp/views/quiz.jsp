<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jsp" %>

<div class="container" style="max-width:760px; padding:2rem 1.5rem;">
    <c:if test="${not quizLocked}">
        <div class="alert alert-error" style="margin-bottom:1rem;">
            ⚠️ This quiz is not live yet. Please try again later.
        </div>
    </c:if>

    <c:if test="${quizLocked}">
        
    <%-- Timer bar --%>
    <div class="quiz-header">
        <h2>Live Quiz</h2>
        <div class="timer-box" id="timer">--:--</div>
    </div>
    <div class="timer-track"><div class="timer-fill" id="timerFill"></div></div>

    <form id="quizForm" action="${pageContext.request.contextPath}/quiz" method="post">
        <input type="hidden" name="attemptId"     value="${attemptId}">
        <input type="hidden" name="competitionId" value="${competitionId}">

        <c:forEach var="q" items="${questions}" varStatus="loop">
            <div class="question-card" id="qcard-${loop.index}">
                <p class="q-number">Question ${loop.index + 1} of ${questionCount}</p>
                <p class="q-text">${q.questionText}</p>
                <div class="options">
                    <c:forEach var="opt" items="${['A','B','C','D']}">
                        <label class="option-label">
                            <input type="radio" name="q_${q.id}" value="${opt}">
                            <span class="option-key">${opt}</span>
                            <span class="option-text">
                                <c:choose>
                                    <c:when test="${opt == 'A'}">${q.optionA}</c:when>
                                    <c:when test="${opt == 'B'}">${q.optionB}</c:when>
                                    <c:when test="${opt == 'C'}">${q.optionC}</c:when>
                                    <c:when test="${opt == 'D'}">${q.optionD}</c:when>
                                </c:choose>
                            </span>
                        </label>
                    </c:forEach>
                </div>
            </div>
        </c:forEach>

        <div style="margin-top:2rem; display:flex; justify-content:space-between; align-items:center;">
            <p style="color:var(--text-light); font-size:0.9rem;">
                All questions carry equal marks. Unanswered = 0.
            </p>
            <button type="submit" class="btn btn-primary btn-lg" id="submitBtn">
                Submit Quiz
            </button>
        </div>
    </form>
    </c:if>
</div>

<style>
.quiz-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:.5rem; }
.timer-box   { background:var(--primary); color:white; padding:.4rem 1.2rem;
               border-radius:999px; font-weight:700; font-size:1.1rem; min-width:80px; text-align:center; }
.timer-box.warning { background:var(--danger); }
.timer-track { height:6px; background:var(--border); border-radius:3px; margin-bottom:2rem; overflow:hidden; }
.timer-fill  { height:100%; background:var(--primary); transition:width 1s linear; border-radius:3px; }

.question-card { background:var(--bg-white); border:1px solid var(--border);
                 border-radius:var(--radius-lg); padding:1.5rem; margin-bottom:1.2rem; }
.q-number  { font-size:.82rem; color:var(--text-light); margin-bottom:.5rem; }
.q-text    { font-weight:600; font-size:1.05rem; margin-bottom:1rem; line-height:1.5; }
.options   { display:flex; flex-direction:column; gap:.6rem; }

.option-label { display:flex; align-items:center; gap:.8rem; padding:.7rem 1rem;
                border:1.5px solid var(--border); border-radius:var(--radius);
                cursor:pointer; transition:all .15s; }
.option-label:hover { border-color:var(--primary); background:var(--primary-light); }
.option-label:has(input:checked) { border-color:var(--primary); background:var(--primary-light); }
.option-key  { background:var(--primary); color:white; width:28px; height:28px;
               border-radius:50%; display:flex; align-items:center; justify-content:center;
               font-weight:700; font-size:.85rem; flex-shrink:0; }
.option-label input[type=radio] { display:none; }
</style>

<script>
const TOTAL_SECONDS = <c:out value="${questionCount}" /> * 60;
const timerEl   = document.getElementById('timer');
const fillEl    = document.getElementById('timerFill');
const form      = document.getElementById('quizForm');
let remaining   = TOTAL_SECONDS;

fillEl.style.width = '100%';

const interval = setInterval(() => {
    remaining--;
    const m = Math.floor(remaining / 60).toString().padStart(2,'0');
    const s = (remaining % 60).toString().padStart(2,'0');
    timerEl.textContent = m + ':' + s;
    fillEl.style.width  = ((remaining / TOTAL_SECONDS) * 100) + '%';

    if (remaining <= 60) timerEl.classList.add('warning');
    if (remaining <= 0)  { clearInterval(interval); form.submit(); }
}, 1000);


const beforeUnloadHandler = (e) => {
    e.preventDefault();
    e.returnValue = '';
};

// attach
window.addEventListener('beforeunload', beforeUnloadHandler);

// remove on submit
form.addEventListener('submit', () => {
    window.removeEventListener('beforeunload', beforeUnloadHandler);
    clearInterval(interval);
});
</script>
</body></html>
