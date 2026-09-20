<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="header.jsp" %>

<div class="container">
    <div class="dashboard-header">
        <div>
            <h1>Organizer Dashboard</h1>
            <p style="color:var(--text-light);">Posting as <strong>${sessionScope.user.name}</strong></p>
        </div>
    </div>

    <c:if test="${param.posted    == 'true'}"><div class="alert alert-success">Competition posted!</div></c:if>
    <c:if test="${param.question  == 'true'}"><div class="alert alert-success">Question added!</div></c:if>
    <c:if test="${param.graded    == 'true'}"><div class="alert alert-success">Submission graded!</div></c:if>
    <c:if test="${not empty requestScope.error}"><div class="alert alert-error">${requestScope.error}</div></c:if>

    <div class="organizer-layout">

        <%-- ── LEFT: competitions list ── --%>
        <div>
            <h2 style="margin-bottom:1rem;">My Competitions (${fn:length(myCompetitions)})</h2>

            <c:forEach var="c" items="${myCompetitions}">
                <div class="info-card" style="margin-bottom:1rem;">
                    <div style="display:flex; justify-content:space-between; align-items:flex-start; gap:1rem; flex-wrap:wrap;">
                        <div>
                            <div style="display:flex; align-items:center; gap:.6rem; margin-bottom:.3rem; flex-wrap:wrap;">
                                <h3>${c.title}</h3>
                                <span class="badge badge-open">${c.status}</span>
                                <c:choose>
                                    <c:when test="${c.quiz}">
                                        <span class="badge badge-coding">Quiz</span>
                                    </c:when>
                                    <c:when test="${c.hackathon}">
                                        <span class="badge badge-hackathon">Hackathon</span>
                                    </c:when>
                                </c:choose>
                            </div>
                            <div style="font-size:.85rem; color:var(--text-light); display:flex; gap:1rem; flex-wrap:wrap;">
                                <span>${c.lastDate}</span>
                                <span>${c.registrationCount} registered</span>
                                <span>${c.prizePool}</span>
                            </div>
                        </div>
                        <div style="display:flex; gap:.5rem; flex-wrap:wrap;">
                            <a href="${pageContext.request.contextPath}/leaderboard?competitionId=${c.id}"
                               class="btn btn-outline btn-sm">Leaderboard</a>
                            <a href="${pageContext.request.contextPath}/competitions?id=${c.id}"
                               class="btn btn-outline btn-sm">View</a>
                            <c:if test="${c.hackathon}">
                                <a href="${pageContext.request.contextPath}/organizer/submissions?competitionId=${c.id}"
                                class="btn btn-outline btn-sm">
                                    View Submissions
                                </a>
                            </c:if>
                        </div>
                    </div>

                    <%-- Quiz builder + finalize --%>
                        <c:if test="${c.quiz}">
                            <div style="margin-top:1rem; padding-top:1rem; border-top:1px solid var(--border);">

                                <%-- IF NOT LOCKED → allow adding questions + show finalize button --%>
                                <c:if test="${not c.quizLocked}">
                                    <details>
                                        <summary style="cursor:pointer; font-weight:600; font-size:.9rem; color:var(--primary);">
                                            Add Quiz Question
                                        </summary>
                                        <form action="${pageContext.request.contextPath}/organizer/question"
                                            method="post" style="margin-top:1rem;">
                                            <input type="hidden" name="competitionId" value="${c.id}">
                                            <div class="form-group">
                                                <label>Question Text</label>
                                                <textarea name="questionText" rows="2" required
                                                        placeholder="Enter your question..."></textarea>
                                            </div>
                                            <div class="form-row">
                                                <div class="form-group">
                                                    <label>Option A</label>
                                                    <input type="text" name="optionA" required placeholder="Option A">
                                                </div>
                                                <div class="form-group">
                                                    <label>Option B</label>
                                                    <input type="text" name="optionB" required placeholder="Option B">
                                                </div>
                                            </div>
                                            <div class="form-row">
                                                <div class="form-group">
                                                    <label>Option C</label>
                                                    <input type="text" name="optionC" required placeholder="Option C">
                                                </div>
                                                <div class="form-group">
                                                    <label>Option D</label>
                                                    <input type="text" name="optionD" required placeholder="Option D">
                                                </div>
                                            </div>
                                            <div class="form-row">
                                                <div class="form-group">
                                                    <label>Correct Answer</label>
                                                    <select name="correctOption" required>
                                                        <option value="A">A</option>
                                                        <option value="B">B</option>
                                                        <option value="C">C</option>
                                                        <option value="D">D</option>
                                                    </select>
                                                </div>
                                                <div class="form-group">
                                                    <label>Marks</label>
                                                    <input type="number" name="marks" value="1" min="1" max="10">
                                                </div>
                                            </div>
                                            <button type="submit" class="btn btn-primary btn-sm">Add Question</button>
                                        </form>
                                    </details>

                                    <form action="${pageContext.request.contextPath}/organizer/finalizeQuiz" method="post" style="margin-top:.8rem;">
                                        <input type="hidden" name="competitionId" value="${c.id}">
                                        <button class="btn btn-success btn-sm">Finalize Quiz</button>
                                    </form>

                                </c:if>

                                <%-- IF LOCKED → show status --%>
                                <c:if test="${c.quizLocked}">
                                    <div class="alert alert-success" style="margin-top:.8rem;">
                                        Quiz finalized (Live)
                                    </div>
                                </c:if>

                            </div>
                        </c:if> 

                    <%-- Hackathon submissions grading --%>
                    <!-- <c:if test="${c.hackathon and not empty submissionsMap[c.id]}">
                        <div style="margin-top:1rem; padding-top:1rem; border-top:1px solid var(--border);">
                            <details>
                                <summary style="cursor:pointer; font-weight:600; font-size:.9rem; color:var(--primary);">
                                    Submissions (${fn:length(submissionsMap[c.id])})
                                </summary>
                                <div style="margin-top:1rem; display:flex; flex-direction:column; gap:.8rem;">
                                    <c:forEach var="sub" items="${submissionsMap[c.id]}">
                                        <div style="background:var(--bg); border-radius:var(--radius);
                                                    padding:1rem; border:1px solid var(--border);">
                                            <div style="display:flex; justify-content:space-between; flex-wrap:wrap; gap:.5rem;">
                                                <div>
                                                    <strong>${sub.projectTitle}</strong>
                                                    <span style="color:var(--text-light); font-size:.85rem;"> by ${sub.userName}</span>
                                                </div>
                                                <c:if test="${sub.graded}">
                                                    <span class="badge badge-open">Score: ${sub.score}</span>
                                                </c:if>
                                            </div>
                                            <div style="display:flex; gap:.5rem; margin-top:.5rem; flex-wrap:wrap;">
                                                <c:if test="${not empty sub.githubUrl}">
                                                    <a href="${sub.githubUrl}" target="_blank"
                                                       class="btn btn-outline btn-sm">GitHub</a>
                                                </c:if>
                                                <c:if test="${not empty sub.demoUrl}">
                                                    <a href="${sub.demoUrl}" target="_blank"
                                                       class="btn btn-outline btn-sm">Demo</a>
                                                </c:if>
                                            </div>
                                            <%-- Grade form --%>
                                            <c:if test="${not sub.graded}">
                                                <form action="${pageContext.request.contextPath}/organizer/grade"
                                                      method="post" style="margin-top:.8rem; display:flex; gap:.5rem; flex-wrap:wrap;">
                                                    <input type="hidden" name="submissionId" value="${sub.id}">
                                                    <input type="number" name="score" placeholder="Score /100"
                                                           min="0" max="100" style="width:120px;"
                                                           class="form-group input" required>
                                                    <input type="text" name="feedback" placeholder="Feedback (optional)"
                                                           style="flex:1; min-width:150px;" class="form-group input">
                                                    <button type="submit" class="btn btn-primary btn-sm">Grade</button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </c:forEach>
                                </div>
                            </details>
                        </div>
                    </c:if> -->

                </div>
            </c:forEach>

            <c:if test="${empty myCompetitions}">
                <div style="text-align:center; padding:3rem; background:var(--bg-white);
                            border-radius:var(--radius-lg); border:1px solid var(--border);">
                    <p style="color:var(--text-light);">No competitions yet. Post one →</p>
                </div>
            </c:if>
        </div>

        <%-- ── RIGHT: post new competition form ── --%>
        <div>
            <div style="background:var(--bg-white); border-radius:var(--radius-lg);
                        padding:2rem; box-shadow:var(--shadow-md); border:1px solid var(--border);">
                <h2 style="margin-bottom:1.5rem;">Post a Competition</h2>

                <form id="compForm" action="${pageContext.request.contextPath}/organizer/dashboard" method="post">

                    <div class="form-group">
                        <label for="title">Title *</label>
                        <input type="text" id="title" name="title"
                               placeholder="HackCetra 2025" required>
                        <span class="field-error"></span>
                    </div>

                    <div class="form-group">
                        <label for="description">Description *</label>
                        <textarea id="description" name="description" rows="3"
                                  placeholder="Describe the competition..." required></textarea>
                        <span class="field-error"></span>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="categoryId">Category *</label>
                            <select id="categoryId" name="categoryId" required>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat[0]}">${cat[1]}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="competitionType">Type *</label>
                            <select id="competitionType" name="competitionType" required>
                                <option value="general">General (registration only)</option>
                                <option value="quiz">MCQ (Quiz)</option>
                                <option value="hackathon">Submission</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="prizePool">Prize Pool</label>
                            <input type="text" id="prizePool" name="prizePool" placeholder="₹50,000">
                        </div>
                        <div class="form-group">
                            <label for="lastDate">Deadline *</label>
                            <input type="date" id="lastDate" name="lastDate" required>
                            <span class="field-error"></span>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="teamSizeMin">Min Team</label>
                            <input type="number" id="teamSizeMin" name="teamSizeMin" value="1" min="1" max="10">
                        </div>
                        <div class="form-group">
                            <label for="teamSizeMax">Max Team</label>
                            <input type="number" id="teamSizeMax" name="teamSizeMax" value="4" min="1" max="10">
                            <span class="field-error"></span>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block btn-lg" style="margin-top:.5rem;">
                        🚀 Post Competition
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<footer><p>Unstop Clone</p></footer>
<script src="${pageContext.request.contextPath}/static/js/validation.js"></script>
</body></html>
