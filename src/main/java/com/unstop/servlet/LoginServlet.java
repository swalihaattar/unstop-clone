package com.unstop.servlet;

import com.unstop.dao.UserDAO;
import com.unstop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * LoginServlet - Controller for /login
 *
 * GET  → show the login JSP page
 * POST → validate credentials, create session, redirect by role
 *
 * MVC ROLE: This is the Controller (C).
 *   - It receives HTTP request
 *   - Calls UserDAO (Model) to check credentials
 *   - Redirects to appropriate View (JSP) based on result
 *
 * SESSION TRACKING: On successful login, we store the User object
 * in the HttpSession. Every other Servlet checks session.getAttribute("user")
 * to know who is logged in.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    /**
     * GET /login → forward to login.jsp
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // If already logged in, redirect to home
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            res.sendRedirect(req.getContextPath() + "/competitions");
            return;
        }

        req.getRequestDispatcher("/views/login.jsp").forward(req, res);
    }

    /**
     * POST /login → authenticate user
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String email    = req.getParameter("email").trim();
        String password = req.getParameter("password");

        // Basic null check
        if (email.isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Email and password are required.");
            req.getRequestDispatcher("/views/login.jsp").forward(req, res);
            return;
        }

        // Fetch user from DB
        User user = userDAO.getUserByEmail(email);

        if (user == null || !password.equals(user.getPassword())) {
            // NOTE: In production, use BCrypt.checkpw(password, user.getPassword())
            req.setAttribute("error", "Invalid email or password.");
            req.getRequestDispatcher("/views/login.jsp").forward(req, res);
            return;
        }

        // ---- Login successful: create session ----
        HttpSession session = req.getSession(true);   // create new session
        session.setAttribute("user", user);           // store User bean in session
        session.setAttribute("userId", user.getId()); // convenience attribute
        session.setMaxInactiveInterval(30 * 60);      // session expires in 30 min

        // Redirect based on role
        if (user.isOrganizer()) {
            res.sendRedirect(req.getContextPath() + "/organizer/dashboard");
        } else if (user.isAdmin()) {
            res.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } else {
            res.sendRedirect(req.getContextPath() + "/competitions");
        }
    }
}
