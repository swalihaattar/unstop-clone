# Unstop Clone

A Java MVC web application inspired by Unstop, built with Servlets, JSP, MySQL, and an optional React dashboard. The project includes competition browsing, login/registration, dashboards, leaderboards, quiz flows, and organizer functionality.

## Features

- Browse competitions and filters
- User registration and login
- Student dashboard and organizer dashboard
- Quiz participation and result tracking
- Team and submission management
- REST API for competition data
- Optional React dashboard UI for demo use

## Tech Stack

- Java 11+
- Maven
- Apache Tomcat 9
- MySQL
- Servlet + JSP + JSTL
- React (optional dashboard)

## Project Structure

```text
unstop-clone/
├── pom.xml
├── README.md
├── SETUP.md
├── LICENSE
├── .gitignore
├── .env.example
├── sql/
│   └── schema.sql
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/unstop/
│   │   ├── webapp/
│   │   └── resources/
│   └── test/
└── target/
```

## Prerequisites

Before running the project locally, install:

- JDK 11 or newer
- Maven
- Apache Tomcat 9
- MySQL Server
- Node.js 18+ (only for the optional React dashboard)

## Database Setup

1. Start MySQL.
2. Create the database schema from the SQL file:

```bash
mysql -u root -p
source /path/to/unstop-clone/sql/schema.sql
```

3. Set local database environment variables before starting the app:

Windows PowerShell:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/unstop_clone?useSSL=false&serverTimezone=UTC"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "your_mysql_password"
```

macOS/Linux:

```bash
export DB_URL="jdbc:mysql://localhost:3306/unstop_clone?useSSL=false&serverTimezone=UTC"
export DB_USERNAME="root"
export DB_PASSWORD="your_mysql_password"
```

## Run the App

### Build the project

```bash
mvn clean package
```

### Deploy to Tomcat

Copy the generated WAR file to your Tomcat webapps folder:

```bash
cp target/unstop-clone-1.0-SNAPSHOT.war /path/to/tomcat/webapps/unstop-clone.war
```

Then start Tomcat and open:

```text
http://localhost:8080/unstop-clone/
```

### Optional React dashboard

```bash
cd src/main/webapp/react-dashboard
npm install
npm start
```

The React app expects the Java backend to be running on Tomcat.

## Default Test Accounts

- Organizer: `organizer@techcorp.com` / `org123`
- Student: register a new account from the app

## GitHub Upload Steps

```bash
git init
git branch -M main
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/<your-username>/<your-repo-name>.git
git push -u origin main
```

If you want to keep local config values private, do not commit `.env` files or actual passwords.

## Notes

- The app uses environment variables for database configuration so credentials do not need to be hardcoded into the source code.
- For an academic project, this is a good baseline for demonstration and deployment.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
