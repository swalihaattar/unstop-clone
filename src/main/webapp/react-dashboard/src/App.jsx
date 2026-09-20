/**
 * App.jsx - ReactJS Dashboard Component
 *
 * GROUP A REQUIREMENT: ReactJS client-side library
 *
 * This React app:
 *   1. Fetches competitions from our REST API (/api/competitions)
 *   2. Displays them as cards with filtering
 *   3. Demonstrates: components, useState, useEffect, props, conditional rendering
 *
 * HOW TO RUN:
 *   cd react-dashboard
 *   npm install
 *   npm start
 *   (Runs on port 3000, calls Java backend on port 8080)
 *
 * VIVA EXPLANATION:
 *   React uses a virtual DOM. When state changes (e.g., filter changes),
 *   React computes a diff and only updates changed DOM nodes — faster than
 *   full page reload or manual DOM manipulation.
 */

import React, { useState, useEffect } from 'react';
import CompetitionCard from './CompetitionCard';
import FilterBar from './FilterBar';
import './Dashboard.css';

// Base URL of your Java backend (change port if needed)
const API_BASE = 'http://localhost:8080/unstop-clone';

function App() {

    // ---- State variables ----
    const [competitions, setCompetitions]   = useState([]);  // all fetched data
    const [filtered, setFiltered]           = useState([]);  // displayed data
    const [categories, setCategories]       = useState([]);
    const [activeCategory, setActiveCategory] = useState(0);
    const [loading, setLoading]             = useState(true);
    const [error, setError]                 = useState(null);
    const [searchText, setSearchText]       = useState('');

    /**
     * useEffect with [] = runs once after component mounts (like componentDidMount).
     * Fetches competition data from our Java REST endpoint.
     */
    useEffect(() => {
        fetchCompetitions();
    }, []);

    /**
     * Whenever activeCategory or searchText changes, re-filter the list.
     */
    useEffect(() => {
        applyFilter();
    }, [activeCategory, searchText, competitions]);

    async function fetchCompetitions() {
        try {
            setLoading(true);
            const response = await fetch(`${API_BASE}/api/competitions`);

            if (!response.ok) {
                throw new Error(`HTTP error: ${response.status}`);
            }

            const data = await response.json();
            setCompetitions(data);

            // Extract unique categories from competitions
            const cats = [...new Set(data.map(c => c.categoryName).filter(Boolean))];
            setCategories(cats);

        } catch (err) {
            setError('Failed to load competitions. Make sure the Java server is running.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    }

    function applyFilter() {
        let result = competitions;

        // Filter by category
        if (activeCategory !== 0) {
            result = result.filter(c => c.categoryId === activeCategory);
        }

        // Filter by search text
        if (searchText.trim()) {
            const q = searchText.toLowerCase();
            result = result.filter(c =>
                c.title.toLowerCase().includes(q) ||
                (c.description || '').toLowerCase().includes(q)
            );
        }

        setFiltered(result);
    }

    // ---- Render ----
    return (
        <div className="react-dashboard">
            <header className="rd-header">
                <h1>🏆 Competitions Dashboard</h1>
                <p>Built with ReactJS — consuming Java REST API</p>
            </header>

            {/* Search bar */}
            <div className="rd-search">
                <input
                    type="text"
                    placeholder="Search competitions..."
                    value={searchText}
                    onChange={e => setSearchText(e.target.value)}
                    className="search-input"
                />
            </div>

            {/* Filter bar - passes callback as prop */}
            <FilterBar
                categories={categories}
                activeCategory={activeCategory}
                onSelect={setActiveCategory}
            />

            {/* Stats summary */}
            <div className="rd-stats">
                <span>{filtered.length} competitions</span>
                {searchText && <span> matching "{searchText}"</span>}
            </div>

            {/* Loading / error / results */}
            {loading && (
                <div className="rd-status">
                    <div className="spinner"></div>
                    <p>Loading from Java API...</p>
                </div>
            )}

            {error && (
                <div className="rd-error">
                    ⚠️ {error}
                    <button onClick={fetchCompetitions} className="retry-btn">Retry</button>
                </div>
            )}

            {!loading && !error && (
                <div className="rd-grid">
                    {filtered.length === 0 ? (
                        <div className="rd-empty">
                            <p>😕 No competitions found.</p>
                        </div>
                    ) : (
                        filtered.map(comp => (
                            <CompetitionCard
                                key={comp.id}
                                competition={comp}
                                apiBase={API_BASE}
                            />
                        ))
                    )}
                </div>
            )}
        </div>
    );
}

export default App;
