/**
 * FilterBar.jsx - Category filter buttons component
 *
 * REACT CONCEPTS DEMONSTRATED:
 *   - Props: receives categories array and onSelect callback from parent
 *   - Callback props: parent passes a function; child calls it on click
 *     This is the React pattern for child→parent communication
 *
 * VIVA: "FilterBar doesn't manage which category is selected — App does.
 *        FilterBar just calls the onSelect prop function when a button
 *        is clicked, and App updates the state. This keeps state in one
 *        place (App) and avoids inconsistency — single source of truth."
 */

import React from 'react';

function FilterBar({ categories, activeCategory, onSelect }) {
    return (
        <div className="rd-filter-bar">
            {/* "All" button always shown first */}
            <button
                className={`filter-pill ${activeCategory === 0 ? 'active' : ''}`}
                onClick={() => onSelect(0)}
            >
                All
            </button>

            {/* One button per unique category */}
            {categories.map((cat, index) => (
                <button
                    key={index}
                    className={`filter-pill ${activeCategory === index + 1 ? 'active' : ''}`}
                    onClick={() => onSelect(index + 1)}
                >
                    {cat}
                </button>
            ))}
        </div>
    );
}

export default FilterBar;
