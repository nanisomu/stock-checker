<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.example.stock.Product" %>
<!DOCTYPE html>
<html>
<head>
    <title>Product Stock Checker</title>
</head>
<body>
    <h2>Product Stock Checker</h2>

    <%
        String errorMessage = (String) request.getAttribute("errorMessage");
        if (errorMessage != null) {
    %>
        <p style="color:red;"><%= errorMessage %></p>
    <%
        }
    %>

    <form action="stock" method="post">
        <label for="productId">Select Product:</label>
        <select name="productId" id="productId" required>
            <option value="">-- Choose a product --</option>
            <%
                List<Product> products = (List<Product>) request.getAttribute("products");
                if (products != null) {
                    for (Product p : products) {
            %>
                <option value="<%= p.getId() %>"><%= p.getName() %></option>
            <%
                    }
                }
            %>
        </select>
        <button type="submit">Check Availability</button>
    </form>

    <%
        String selectedName = (String) request.getAttribute("selectedProductName");
        String status = (String) request.getAttribute("stockStatus");
        if (selectedName != null && status != null) {
    %>
        <h3>Result: <%= selectedName %> — <strong><%= status %></strong></h3>
    <%
        }
    %>
</body>
</html>