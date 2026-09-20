/**
 * CompetitionCard.jsx - Reusable React component for one competition
 *
 * REACT CONCEPT: Props
 *   This component receives `competition` as a prop from App.jsx
 *   It is a "dumb" / presentational component — just renders data,
 *   no state of its own.
 *
 * VIVA: "CompetitionCard is a reusable component. App.jsx passes each
 *        competition object as a prop. The card renders its own UI
 *        based on those props. If we need the card anywhere else in the
 *        app, we just reuse this component."
 */

import React from 'react';

function CompetitionCard({ competition, apiBase }) {

    const {
        id, title, description, categoryName,
        prizePool, lastDate, teamSizeMin, teamSizeMax,
        registrationCount, status
    } = competition;

    // Helper: team size display
    const teamDisplay = teamSizeMin === teamSizeMax
        ? `${teamSizeMin}`
        : `${teamSizeMin}–${teamSizeMax}`;

    // Helper: truncate description
    const shortDesc = description && description.length > 100
        ? description.substring(0, 100) + '...'
        : description;

    // Link back to the Java JSP detail page
    const detailUrl = `${apiBase}/competitions?id=${id}`;

    return (
        <div className="rc-card">
            <div className="rc-card-top">
                <h3>{title}</h3>
                <span className={`rc-badge rc-badge-${(categoryName || '').toLowerCase().replace(' ','-')}`}>
                    {categoryName}
                </span>
            </div>

            <p className="rc-desc">{shortDesc}</p>

            <div className="rc-meta">
                <span>🏆 {prizePool || 'N/A'}</span>
                <span>👥 {teamDisplay}</span>
                <span>📅 {lastDate}</span>
                <span>👤 {registrationCount || 0} registered</span>
            </div>

            <div className="rc-footer">
                <span className="rc-badge rc-badge-open">{status}</span>
                <a href={detailUrl} className="rc-btn" target="_top">
                    View Details →
                </a>
            </div>
        </div>
    );
}

export default CompetitionCard;
