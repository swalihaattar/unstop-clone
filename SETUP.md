# Unstop Clone — Complete Setup Guide (VS Code)
# ====================================================
# Follow these steps exactly to run the project

## WHAT YOU NEED TO INSTALL FIRST
# ─────────────────────────────────────────────────────
# 1. JDK 11+         → https://adoptium.net
# 2. Apache Maven    → https://maven.apache.org/download.cgi
# 3. Apache Tomcat 9 → https://tomcat.apache.org/download-90.cgi
# 4. MySQL Server    → https://dev.mysql.com/downloads/
# 5. MySQL Workbench → (optional, for GUI)
# 6. Node.js 18+     → https://nodejs.org  (for React only)

## VS CODE EXTENSIONS TO INSTALL
# ─────────────────────────────────────────────────────
# - Extension Pack for Java  (Microsoft)
# - Maven for Java           (Microsoft)
# - Tomcat for Java          (Wei Shen)
# - MySQL                    (Weijan Chen)

## ─────────────────────────────────────────────────────
## STEP 1: SET UP THE DATABASE
## ─────────────────────────────────────────────────────

# Open MySQL Workbench or MySQL terminal and run:
mysql -u root -p

# Then inside MySQL:
source /path/to/unstop-clone/sql/schema.sql;

# Or copy-paste the contents of sql/schema.sql into MySQL Workbench and run it.
# This creates the database, all tables, and seed data.

## ─────────────────────────────────────────────────────
## STEP 2: CONFIGURE YOUR DB PASSWORD
## ─────────────────────────────────────────────────────

# Open this file:
#   src/main/java/com/unstop/util/DBConnection.java

# Change line:
#   private static final String PASSWORD = "root";
# To your actual MySQL root password.

## ─────────────────────────────────────────────────────
## STEP 3: BUILD THE JAVA PROJECT
## ─────────────────────────────────────────────────────

# In VS Code terminal, navigate to the project root:
cd unstop-clone

# Build with Maven (downloads all dependencies from pom.xml):
mvn clean package

# This creates: target/unstop-clone-1.0-SNAPSHOT.war

## ─────────────────────────────────────────────────────
## STEP 4: DEPLOY TO TOMCAT
## ─────────────────────────────────────────────────────

# Option A: Using Tomcat for Java VS Code extension
#   1. Click the Tomcat icon in the VS Code sidebar
#   2. Click "+" to add a Tomcat server → select your Tomcat folder
#   3. Right-click the .war file in target/ → "Run on Tomcat Server"

# Option B: Manual copy
#   Copy target/unstop-clone-1.0-SNAPSHOT.war
#   to    [TOMCAT_HOME]/webapps/unstop-clone.war
#   Start Tomcat: [TOMCAT_HOME]/bin/startup.sh (Mac/Linux)
#                 [TOMCAT_HOME]/bin/startup.bat (Windows)

## ─────────────────────────────────────────────────────
## STEP 5: ACCESS THE APPLICATION
## ─────────────────────────────────────────────────────

# Open browser:
#   http://localhost:8080/unstop-clone/

# Pages:
#   Home:          http://localhost:8080/unstop-clone/
#   Competitions:  http://localhost:8080/unstop-clone/competitions
#   Login:         http://localhost:8080/unstop-clone/login
#   Register:      http://localhost:8080/unstop-clone/register
#   Dashboard:     http://localhost:8080/unstop-clone/dashboard  (student)
#   Organizer:     http://localhost:8080/unstop-clone/organizer/dashboard
#   REST API:      http://localhost:8080/unstop-clone/api/competitions

# Test accounts:
#   Organizer → email: organizer@techcorp.com   password: org123
#   Student   → register a new account at /register

## ─────────────────────────────────────────────────────
## STEP 6: RUN REACT DASHBOARD (OPTIONAL - for demo)
## ─────────────────────────────────────────────────────

cd src/main/webapp/react-dashboard

npm install          # install React dependencies (first time only)
npm start            # starts React dev server on http://localhost:3000

# The React app calls the Java REST API at localhost:8080
# Make sure Tomcat is running BEFORE starting React

## ─────────────────────────────────────────────────────
## PROJECT STRUCTURE QUICK REFERENCE
## ─────────────────────────────────────────────────────

# unstop-clone/
# ├── pom.xml                            Maven config + dependencies
# ├── sql/schema.sql                     Database setup script
# ├── src/main/java/com/unstop/
# │   ├── util/DBConnection.java         JDBC connection factory
# │   ├── model/                         JavaBeans (User, Competition, Registration)
# │   ├── dao/                           Database operations (JDBC)
# │   ├── servlet/                       MVC Controllers
# │   └── rest/                          REST API → returns JSON
# └── src/main/webapp/
#     ├── WEB-INF/web.xml                Deployment descriptor
#     ├── index.jsp                      Landing page
#     ├── views/                         All JSP view pages
#     │   ├── header.jsp                 Shared navbar (included in all pages)
#     │   ├── login.jsp
#     │   ├── register.jsp
#     │   ├── competitions.jsp           Browse page (AJAX filter here)
#     │   ├── competition-detail.jsp     Detail + register form
#     │   ├── dashboard.jsp              Student dashboard
#     │   ├── organizer-dashboard.jsp    Organizer panel
#     │   └── error.jsp
#     ├── static/
#     │   ├── css/style.css              All styles
#     │   ├── js/main.js                 AJAX competition filter
#     │   └── js/validation.js          Client-side form validation
#     └── react-dashboard/               ReactJS app (standalone)

## ─────────────────────────────────────────────────────
## VIVA QUICK ANSWERS
## ─────────────────────────────────────────────────────

# Q: What is MVC in your project?
# A: Servlet = Controller (receives request, calls DAO, sets attributes)
#    JSP = View (renders HTML using data from request attributes)
#    JavaBean = Model (plain Java object that holds data)

# Q: How does session tracking work?
# A: On login, LoginServlet calls req.getSession(true) and stores the User
#    object with session.setAttribute("user", user).
#    Every other Servlet checks session.getAttribute("user") to know who is logged in.
#    On logout, session.invalidate() destroys the session.

# Q: How does AJAX work in your project?
# A: The filter bar buttons have data-category attributes. main.js listens
#    for clicks and creates an XMLHttpRequest to /api/competitions?category=X.
#    The Java REST servlet returns JSON. JS parses it and rebuilds the card grid
#    without reloading the page.

# Q: What is the role of the DAO layer?
# A: Data Access Object. It isolates all SQL from the Servlet layer.
#    If we switch from JDBC to Hibernate, only the DAO classes change.
#    Follows Single Responsibility Principle.

# Q: Why ReactJS?
# A: React uses a virtual DOM for efficient updates. The dashboard component
#    uses useState for state management and useEffect to fetch from the REST
#    API on mount. Components (CompetitionCard, FilterBar) are reusable.
