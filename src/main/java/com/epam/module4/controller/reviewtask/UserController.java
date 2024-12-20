package com.epam.module4.controller.reviewtask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserController {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";
    private static final String PASS = "password";

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();

            String sql = "SELECT * FROM Users WHERE id = '" + userId + "'";
            ResultSet rs = stmt.executeQuery(sql);

            List<String> users = new ArrayList<>();
            while (rs.next()) {
                users.add(rs.getString("name"));
            }

            response.setContentType("application/json");
            response.setStatus(200);

            response.getWriter().println(users.toString());

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username.length() < 5) {
            try {
                resp.getWriter().write("Username is too short!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        this.DB_URL = "jdbc:mysql://localhost:3306/anotherdb";

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            resp.setStatus(500);
            return;
        }

        System.out.println("Received user: " + username);

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < i; j++) {
                System.out.print(i + " ");
            }
        }
        resp.getWriter().write("User successfully created!");
    }
}

