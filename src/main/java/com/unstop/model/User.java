package com.unstop.model;

import java.sql.Timestamp;

/**
 * User - JavaBean representing a row in the `users` table.
 *
 * WHY JavaBean: Follows the JavaBean convention (no-arg constructor,
 * private fields, public getters/setters). This lets JSP pages access
 * properties via EL (Expression Language): ${user.name}
 *
 * ROLES:
 * student - can browse and register for competitions
 * organizer - can post competitions
 * admin - can manage everything
 */
public class User {

    private int id;
    private String name;
    private String email;
    private String password; // hashed
    private String role; // student | organizer | admin
    private String college;
    private Timestamp createdAt;

    // ---- No-argument constructor (required for JavaBean) ----
    public User() {
    }

    // ---- Constructor for creating new users ----
    public User(String name, String email, String password, String role, String college) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.college = college;
    }

    // ---- Getters and Setters ----
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // Helper: used in JSP to check role easily
    public boolean isStudent() {
        return "student".equals(role);
    }

    public boolean isOrganizer() {
        return "organizer".equals(role);
    }

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name=" + name + ", role=" + role + "}";
    }
}
