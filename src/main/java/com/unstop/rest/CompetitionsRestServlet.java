package com.unstop.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unstop.dao.CompetitionDAO;
import com.unstop.model.Competition;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * CompetitionsRestServlet - REST API endpoint returning JSON.
 *
 * URL: /api/competitions
 *
 * WHY REST + JSON:
 *   - The competition filter bar uses AJAX to call this endpoint
 *   - The ReactJS dashboard also fetches from here
 *   - JSON is lightweight and works natively with JavaScript
 *
 * This is the "Group B: REST Web Service" requirement of the syllabus.
 *
 * RESPONSE FORMAT:
 *   GET /api/competitions              → all open competitions as JSON array
 *   GET /api/competitions?category=1   → filtered by category
 *   GET /api/competitions?id=5         → single competition as JSON object
 */
@WebServlet("/api/competitions")
public class CompetitionsRestServlet extends HttpServlet {

    private final CompetitionDAO competitionDAO = new CompetitionDAO();

    // Gson converts Java objects → JSON strings
    // setPrettyPrinting() makes it readable; remove in production for speed
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd")
            .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        // Set response type to JSON + allow AJAX from same origin
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String idParam       = req.getParameter("id");
        String categoryParam = req.getParameter("category");

        String json;

        if (idParam != null) {
            // Return single competition
            Competition c = competitionDAO.getById(Integer.parseInt(idParam));
            json = (c != null) ? gson.toJson(c) : "{\"error\": \"Not found\"}";

        } else if (categoryParam != null) {
            // Return filtered list
            int categoryId = Integer.parseInt(categoryParam);
            List<Competition> list = competitionDAO.getByCategory(categoryId);
            json = gson.toJson(list);

        } else {
            // Return all open competitions
            List<Competition> list = competitionDAO.getAllOpen();
            json = gson.toJson(list);
        }

        res.getWriter().write(json);
    }
}
