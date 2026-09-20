package com.unstop.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * LogoutServlet - Invalidates the current session and redirects to login.
 *
 * SESSION TRACKING demo point: session.invalidate() destroys the session
 * and all attributes stored in it. The user must log in again.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();  // destroys session + all stored attributes
        }
        res.sendRedirect(req.getContextPath() + "/login?logout=true");
    }
}
