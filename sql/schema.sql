-- ============================================================
-- Unstop Clone - Database Schema
-- Run this file in MySQL to set up the database
-- ============================================================

CREATE DATABASE IF NOT EXISTS unstop_clone;
USE unstop_clone;

-- ------------------------------------------------------------
-- USERS table - stores students, organizers, admins
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,   -- store hashed password
    role         ENUM('student','organizer','admin') DEFAULT 'student',
    college      VARCHAR(200),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- CATEGORIES table - e.g. Hackathon, Quiz, Case Study
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id    INT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100) NOT NULL UNIQUE
);

-- ------------------------------------------------------------
-- COMPETITIONS table - events posted by organizers
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS competitions (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(200) NOT NULL,
    description     TEXT,
    category_id     INT,
    organizer_id    INT NOT NULL,
    prize_pool      VARCHAR(100),
    last_date       DATE,
    team_size_min   INT DEFAULT 1,
    team_size_max   INT DEFAULT 1,
    status          ENUM('open','closed','completed') DEFAULT 'open',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id)  REFERENCES categories(id),
    FOREIGN KEY (organizer_id) REFERENCES users(id)
);

-- ------------------------------------------------------------
-- REGISTRATIONS table - student registers for a competition
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS registrations (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL,
    competition_id  INT NOT NULL,
    registered_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_reg (user_id, competition_id),   -- no duplicate registration
    FOREIGN KEY (user_id)        REFERENCES users(id),
    FOREIGN KEY (competition_id) REFERENCES competitions(id)
);

-- ------------------------------------------------------------
-- SEED DATA - categories and sample competitions
-- ------------------------------------------------------------
INSERT IGNORE INTO categories (name) VALUES
    ('Hackathon'), ('Quiz'), ('Case Study'), ('Coding'), ('Design');

-- Admin user (password = "admin123" - plain for demo, hash in production)
INSERT IGNORE INTO users (name, email, password, role) VALUES
    ('Admin', 'admin@unstop.com', 'admin123', 'admin');

-- Sample organizer
INSERT IGNORE INTO users (name, email, password, role, college) VALUES
    ('TechCorp HR', 'organizer@techcorp.com', 'org123', 'organizer', 'TechCorp Inc.');

-- Sample competitions
INSERT IGNORE INTO competitions (title, description, category_id, organizer_id, prize_pool, last_date, team_size_min, team_size_max, status) VALUES
    ('HackCetra 2024', 'Build innovative solutions in 24 hours. Open to all engineering students.', 1, 2, '₹50,000', '2024-12-31', 1, 4, 'open'),
    ('Quizzard National Quiz', 'Test your knowledge across science, tech, and culture.', 2, 2, '₹10,000', '2024-11-30', 1, 2, 'open'),
    ('Case Cracker 2024', 'Solve real business problems presented by top companies.', 3, 2, '₹25,000', '2024-12-15', 2, 3, 'open'),
    ('Code Sprint', 'Competitive programming contest. DSA focused.', 4, 2, '₹15,000', '2024-12-01', 1, 1, 'open');
