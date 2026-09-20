/**
 * main.js - AJAX-powered competition filter
 *
 * GROUP A REQUIREMENT: AJAX technology for rich user experience
 *
 * HOW IT WORKS:
 *   1. User clicks a category filter button (e.g. "Hackathon")
 *   2. JavaScript intercepts the click (no page reload)
 *   3. XMLHttpRequest (XHR) sends GET to /api/competitions?category=1
 *   4. Server returns JSON array of competitions
 *   5. JavaScript parses JSON and re-renders the cards in the DOM
 *
 * WHY XMLHttpRequest instead of fetch():
 *   - Demonstrates AJAX fundamentally as taught in the syllabus
 *   - Shows readyState, onreadystatechange concepts
 *   - fetch() is modern but XHR is what the syllabus covers
 */

document.addEventListener('DOMContentLoaded', function () {

    const filterButtons = document.querySelectorAll('.filter-btn');
    const grid          = document.getElementById('competitions-grid');
    const spinner       = document.getElementById('loading-spinner');
    const countEl       = document.getElementById('result-count');

    if (!grid) return;  // not on the competitions page

    // ---- Filter button click handler ----
    filterButtons.forEach(function(btn) {
        btn.addEventListener('click', function() {

            // Update active state
            filterButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            const categoryId = btn.getAttribute('data-category');
            loadCompetitions(categoryId);
        });
    });

    /**
     * loadCompetitions - makes an AJAX call to the REST API
     * and re-renders the competition cards.
     *
     * @param {string} categoryId - "0" for all, or category id number
     */
    function loadCompetitions(categoryId) {
        const url = contextPath + '/api/competitions' +
                    (categoryId && categoryId !== '0' ? '?category=' + categoryId : '');

        // Show spinner while loading
        if (spinner) spinner.style.display = 'block';
        grid.style.opacity = '0.4';

        // ---- AJAX using XMLHttpRequest ----
        const xhr = new XMLHttpRequest();

        // Step 1: Open the request (method, url, async=true)
        xhr.open('GET', url, true);

        // Step 2: Set request headers
        xhr.setRequestHeader('Accept', 'application/json');

        // Step 3: Handle response state changes
        xhr.onreadystatechange = function () {

            // readyState 4 = request complete
            if (xhr.readyState === 4) {

                if (spinner) spinner.style.display = 'none';
                grid.style.opacity = '1';

                if (xhr.status === 200) {
                    // Parse JSON response
                    const competitions = JSON.parse(xhr.responseText);
                    renderCards(competitions);
                } else {
                    grid.innerHTML = '<p class="alert alert-error">Failed to load competitions. Please refresh.</p>';
                }
            }
        };

        // Step 4: Send the request
        xhr.send();
    }

    /**
     * renderCards - builds HTML for each competition and injects into DOM.
     * This replaces the server-rendered JSP cards dynamically.
     */
    function renderCards(competitions) {

        if (countEl) {
            countEl.textContent = competitions.length + ' competition' +
                                  (competitions.length !== 1 ? 's' : '') + ' found';
        }

        if (competitions.length === 0) {
            grid.innerHTML = `
                <div style="grid-column:1/-1; text-align:center; padding:3rem; color:#9ca3af;">
                    <p style="font-size:3rem">🔍</p>
                    <p>No competitions found in this category.</p>
                </div>`;
            return;
        }

        // Build card HTML for each competition object
        const html = competitions.map(function(c) {
            return `
            <div class="comp-card">
                <div class="comp-card-header">
                    <h3>${escapeHtml(c.title)}</h3>
                    <span class="badge badge-default">${escapeHtml(c.categoryName || '')}</span>
                </div>
                <p class="desc">${escapeHtml(c.description || '')}</p>
                <div class="comp-meta">
                    <span>🏆 ${escapeHtml(c.prizePool || 'N/A')}</span>
                    <span>👥 Team: ${c.teamSizeMin === c.teamSizeMax
                                      ? c.teamSizeMin
                                      : c.teamSizeMin + '-' + c.teamSizeMax}</span>
                    <span>📅 ${c.lastDate ? formatDate(c.lastDate) : 'N/A'}</span>
                    <span>👤 ${c.registrationCount || 0} registered</span>
                </div>
                <div class="comp-card-footer">
                    <span class="badge badge-open">Open</span>
                    <a href="${contextPath}/competitions?id=${c.id}" class="btn btn-primary btn-sm">
                        View Details
                    </a>
                </div>
            </div>`;
        }).join('');

        grid.innerHTML = html;
    }

    /**
     * escapeHtml - SECURITY: prevents XSS by escaping special characters
     * before inserting user-provided data into the DOM as innerHTML.
     */
    function escapeHtml(str) {
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    /**
     * formatDate - convert "2024-12-31" to "31 Dec 2024"
     */
    function formatDate(dateStr) {
        const d = new Date(dateStr);
        return d.toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
    }
});
