import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewProductsFrame extends JFrame {
    private JTable productsTable;
    private DefaultTableModel tableModel;

    public ViewProductsFrame() {
        setTitle("View Products");
        setSize(800, 400);
        setLocationRelativeTo(null);

        // Create table model
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Category");
        tableModel.addColumn("Size");
        tableModel.addColumn("Price");
        tableModel.addColumn("Quantity");

        // Create table
        productsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productsTable);

        // Add refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadProducts());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);

        // Add components to frame
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load products initially
        loadProducts();
        setVisible(true);
    }

    private void loadProducts() {
        // Clear existing data
        tableModel.setRowCount(0);

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM products";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                String size = resultSet.getString("size");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");

                tableModel.addRow(new Object[]{id, name, category, size, price, quantity});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading products: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}