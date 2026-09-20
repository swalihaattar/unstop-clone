package com.unstop.servlet;

import com.unstop.dao.UserDAO;
import com.unstop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * RegisterServlet - Controller for /register
 *
 * GET  → show registration form (register.jsp)
 * POST → validate input, check duplicate email, insert user, redirect to login
 *
 * VALIDATION STRATEGY:
 *   - Server-side validation here (important: never trust client-side only)
 *   - Client-side validation in register.js (for good UX)
 *   - Both are required; server side is the real guard
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.getRequestDispatcher("/views/register.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        // Collect form parameters
        String name     = req.getParameter("name").trim();
        String email    = req.getParameter("email").trim().toLowerCase();
        String password = req.getParameter("password");
        String confirm  = req.getParameter("confirmPassword");
        String role     = req.getParameter("role");
        String college  = req.getParameter("college").trim();

        // ---- Server-side Validation ----
        String error = null;

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            error = "All fields are required.";
        } else if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            error = "Invalid email format.";
        } else if (password.length() < 6) {
            error = "Password must be at least 6 characters.";
        } else if (!password.equals(confirm)) {
            error = "Passwords do not match.";
        } else if (userDAO.emailExists(email)) {
            error = "An account with this email already exists.";
        }

        if (error != null) {
            // Put error and form values back so user doesn't retype everything
            req.setAttribute("error", error);
            req.setAttribute("name", name);
            req.setAttribute("email", email);
            req.setAttribute("college", college);
            req.getRequestDispatcher("/views/register.jsp").forward(req, res);
            return;
        }

        // ---- Create User ----
        // NOTE: In production, hash password: BCrypt.hashpw(password, BCrypt.gensalt())
        User user = new User(name, email, password, role, college);
        boolean created = userDAO.registerUser(user);

        if (created) {
            // Pass success message to login page
            res.sendRedirect(req.getContextPath() + "/login?registered=true");
        } else {
            req.setAttribute("error", "Registration failed. Please try again.");
            req.getRequestDispatcher("/views/register.jsp").forward(req, res);
        }
    }
}
