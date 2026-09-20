-- ============================================================
-- Unstop Clone - Database Schema
-- Run this file in MySQL to set up the database
-- Run each section separately during setup
-- ============================================================

CREATE DATABASE IF NOT EXISTS unstop_clone;

USE unstop_clone;


-- ------------------------------------------------------------
-- USERS
-- Stores students, organizers, and admins
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('student', 'organizer', 'admin') DEFAULT 'student',
    college     VARCHAR(200),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ------------------------------------------------------------
-- CATEGORIES
-- E.g. Hackathon, Quiz, Case Study
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS categories (
    id    INT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100) NOT NULL UNIQUE
);


-- ------------------------------------------------------------
-- COMPETITIONS
-- Events posted by organizers
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS competitions (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(200) NOT NULL,
    description      TEXT,
    category_id      INT,
    organizer_id     INT NOT NULL,
    prize_pool       VARCHAR(100),
    last_date        DATE,
    team_size_min    INT DEFAULT 1,
    team_size_max    INT DEFAULT 1,
    status           ENUM('open', 'closed', 'completed') DEFAULT 'open',
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    competition_type ENUM('general', 'quiz') DEFAULT 'general',
    quiz_locked      TINYINT DEFAULT 0,

    FOREIGN KEY (category_id)
        REFERENCES categories(id),

    FOREIGN KEY (organizer_id)
        REFERENCES users(id)
);


-- ------------------------------------------------------------
-- REGISTRATIONS
-- Students register for competitions
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS registrations (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL,
    competition_id  INT NOT NULL,
    registered_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY unique_reg (user_id, competition_id),

    FOREIGN KEY (user_id)
        REFERENCES users(id),

    FOREIGN KEY (competition_id)
        REFERENCES competitions(id)
);


-- ------------------------------------------------------------
-- QUIZ QUESTIONS
-- Questions belonging to a quiz competition
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS quiz_questions (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    competition_id   INT NOT NULL,
    question_text    TEXT NOT NULL,
    option_a         VARCHAR(255) NOT NULL,
    option_b         VARCHAR(255) NOT NULL,
    option_c         VARCHAR(255) NOT NULL,
    option_d         VARCHAR(255) NOT NULL,
    correct_option   ENUM('A', 'B', 'C', 'D') NOT NULL,
    marks            INT DEFAULT 1,
    order_num        INT DEFAULT 0,

    FOREIGN KEY (competition_id)
        REFERENCES competitions(id)
);


-- ------------------------------------------------------------
-- QUIZ ATTEMPTS
-- Stores a user's attempt for a quiz competition
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS quiz_attempts (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    user_id          INT NOT NULL,
    competition_id   INT NOT NULL,
    score            INT DEFAULT 0,
    total_marks      INT DEFAULT 0,
    started_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    submitted_at     TIMESTAMP NULL,
    status           ENUM('in_progress', 'submitted') DEFAULT 'in_progress',

    FOREIGN KEY (user_id)
        REFERENCES users(id),

    FOREIGN KEY (competition_id)
        REFERENCES competitions(id)
);


-- ------------------------------------------------------------
-- QUIZ ANSWERS
-- Stores answers submitted during a quiz attempt
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS quiz_answers (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id       INT NOT NULL,
    question_id      INT NOT NULL,
    chosen           ENUM('A', 'B', 'C', 'D'),
    is_correct       TINYINT DEFAULT 0,

    FOREIGN KEY (attempt_id)
        REFERENCES quiz_attempts(id),

    FOREIGN KEY (question_id)
        REFERENCES quiz_questions(id)
);


-- ------------------------------------------------------------
-- TEAMS
-- Teams created for competitions
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS teams (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    competition_id   INT NOT NULL,
    leader_id        INT NOT NULL,
    invite_code      VARCHAR(255) NOT NULL UNIQUE,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (competition_id)
        REFERENCES competitions(id),

    FOREIGN KEY (leader_id)
        REFERENCES users(id)
);


-- ------------------------------------------------------------
-- TEAM MEMBERS
-- Users belonging to teams
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS team_members (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    team_id     INT NOT NULL,
    user_id     INT NOT NULL,
    joined_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (team_id)
        REFERENCES teams(id),

    FOREIGN KEY (user_id)
        REFERENCES users(id)
);


-- ------------------------------------------------------------
-- SUBMISSIONS
-- Competition project submissions
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS submissions (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    competition_id   INT NOT NULL,
    user_id          INT NOT NULL,
    team_id          INT,
    project_title    VARCHAR(255) NOT NULL,
    description      TEXT,
    github_url       VARCHAR(500),
    demo_url         VARCHAR(500),
    submitted_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    score            INT,
    feedback         TEXT,

    FOREIGN KEY (competition_id)
        REFERENCES competitions(id),

    FOREIGN KEY (user_id)
        REFERENCES users(id),

    FOREIGN KEY (team_id)
        REFERENCES teams(id)
);


-- ============================================================
-- SEED DATA
-- ============================================================

-- ------------------------------------------------------------
-- Categories
-- ------------------------------------------------------------

INSERT IGNORE INTO categories (name) VALUES
    ('Hackathon'),
    ('Quiz'),
    ('Case Study'),
    ('Coding'),
    ('Design');


-- ------------------------------------------------------------
-- Admin user
-- Password: admin123
-- Plain text for demo only; hash in production
-- ------------------------------------------------------------

INSERT IGNORE INTO users
    (name, email, password, role)
VALUES
    ('Admin', 'admin@unstop.com', 'admin123', 'admin');


-- ------------------------------------------------------------
-- Sample organizer
-- ------------------------------------------------------------

INSERT IGNORE INTO users
    (name, email, password, role, college)
VALUES
    (
        'TechCorp HR',
        'organizer@techcorp.com',
        'org123',
        'organizer',
        'TechCorp Inc.'
    );


-- ------------------------------------------------------------
-- Sample competitions
-- ------------------------------------------------------------

INSERT IGNORE INTO competitions
    (
        title,
        description,
        category_id,
        organizer_id,
        prize_pool,
        last_date,
        team_size_min,
        team_size_max,
        status
    )
VALUES
    (
        'HackCetra 2024',
        'Build innovative solutions in 24 hours. Open to all engineering students.',
        1,
        2,
        '₹50,000',
        '2024-12-31',
        1,
        4,
        'open'
    ),
    (
        'Quizzard National Quiz',
        'Test your knowledge across science, tech, and culture.',
        2,
        2,
        '₹10,000',
        '2024-11-30',
        1,
        2,
        'open'
    ),
    (
        'Case Cracker 2024',
        'Solve real business problems presented by top companies.',
        3,
        2,
        '₹25,000',
        '2024-12-15',
        2,
        3,
        'open'
    ),
    (
        'Code Sprint',
        'Competitive programming contest. DSA focused.',
        4,
        2,
        '₹15,000',
        '2024-12-01',
        1,
        1,
        'open'
    );
