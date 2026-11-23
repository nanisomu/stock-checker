package com.example.stock;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/stock")
public class StockServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/inventory_db?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "app_user";
    private static final String DB_PASS = "AppPass@123";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        loadProductsAndForward(req, resp, null, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String selectedIdParam = req.getParameter("productId");
        String selectedProductName = null;
        String stockStatus = null;

        if (selectedIdParam != null && !selectedIdParam.isEmpty()) {
            int productId = Integer.parseInt(selectedIdParam);
            String sql = "SELECT product_name, stock_quantity FROM products WHERE id = ?";

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, productId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            selectedProductName = rs.getString("product_name");
                            int qty = rs.getInt("stock_quantity");
                            stockStatus = (qty > 0) ? "In Stock" : "Out of Stock";
                        }
                    }
                }
            } catch (Exception e) {
                stockStatus = "Error checking stock: " + e.getMessage();
            }
        }

        loadProductsAndForward(req, resp, selectedProductName, stockStatus);
    }

    private void loadProductsAndForward(HttpServletRequest req, HttpServletResponse resp,
                                          String selectedProductName, String stockStatus)
            throws ServletException, IOException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, product_name, stock_quantity FROM products ORDER BY product_name";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("product_name"),
                        rs.getInt("stock_quantity")
                    ));
                }
            }
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
        }

        req.setAttribute("products", products);
        req.setAttribute("selectedProductName", selectedProductName);
        req.setAttribute("stockStatus", stockStatus);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}