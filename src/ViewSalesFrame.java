import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewSalesFrame extends JFrame {
    private JTable salesTable;
    private DefaultTableModel tableModel;

    public ViewSalesFrame() {
        setTitle("View Sales");
        setSize(800, 400);
        setLocationRelativeTo(null);

        // Create table model
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Sale ID");
        tableModel.addColumn("Product ID");
        tableModel.addColumn("Product Name");
        tableModel.addColumn("Sale Date");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Total Price");

        // Create table
        salesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(salesTable);

        // Add refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadSales());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);

        // Add components to frame
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load sales initially
        loadSales();
        setVisible(true);
    }

    private void loadSales() {
        // Clear existing data
        tableModel.setRowCount(0);

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT s.id, s.product_id, p.name as product_name, s.sale_date, s.quantity, s.total_price " +
                    "FROM sales s JOIN products p ON s.product_id = p.id";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int productId = resultSet.getInt("product_id");
                String productName = resultSet.getString("product_name");
                String saleDate = resultSet.getDate("sale_date").toString();
                int quantity = resultSet.getInt("quantity");
                double totalPrice = resultSet.getDouble("total_price");

                tableModel.addRow(new Object[]{id, productId, productName, saleDate, quantity, totalPrice});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading sales: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}