import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class AddSaleFrame extends JFrame {
    private JComboBox<Integer> productIdCombo;
    private JTextField quantityField;

    public AddSaleFrame() {
        setTitle("Add New Sale");
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components
        panel.add(new JLabel("Product ID:"));
        productIdCombo = new JComboBox<>();
        loadProductIds();
        panel.add(productIdCombo);

        panel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        panel.add(quantityField);

        JButton addButton = new JButton("Add Sale");
        addButton.addActionListener(new AddSaleListener());
        panel.add(addButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        panel.add(cancelButton);

        add(panel);
        setVisible(true);
    }

    private void loadProductIds() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT id FROM products";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                productIdCombo.addItem(resultSet.getInt("id"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading product IDs: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class AddSaleListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String quantityText = quantityField.getText();

            if (quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(AddSaleFrame.this,
                        "Please enter quantity!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int productId = (Integer) productIdCombo.getSelectedItem();
                int quantity = Integer.parseInt(quantityText);

                // First check if product exists and has enough quantity
                try (Connection connection = DatabaseConnection.getConnection()) {
                    // Check product quantity
                    String checkSql = "SELECT price, quantity FROM products WHERE id = ?";
                    PreparedStatement checkStatement = connection.prepareStatement(checkSql);
                    checkStatement.setInt(1, productId);
                    ResultSet resultSet = checkStatement.executeQuery();

                    if (resultSet.next()) {
                        int availableQuantity = resultSet.getInt("quantity");
                        double price = resultSet.getDouble("price");

                        if (quantity > availableQuantity) {
                            JOptionPane.showMessageDialog(AddSaleFrame.this,
                                    "Not enough quantity available! Available: " + availableQuantity,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Calculate total price
                        double totalPrice = price * quantity;

                        // Insert sale record
                        String insertSql = "INSERT INTO sales (product_id, sale_date, quantity, total_price) VALUES (?, ?, ?, ?)";
                        PreparedStatement insertStatement = connection.prepareStatement(insertSql);
                        insertStatement.setInt(1, productId);
                        insertStatement.setDate(2, new java.sql.Date(new Date().getTime()));
                        insertStatement.setInt(3, quantity);
                        insertStatement.setDouble(4, totalPrice);

                        int rowsInserted = insertStatement.executeUpdate();

                        if (rowsInserted > 0) {
                            // Update product quantity
                            String updateSql = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
                            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                            updateStatement.setInt(1, quantity);
                            updateStatement.setInt(2, productId);
                            updateStatement.executeUpdate();

                            JOptionPane.showMessageDialog(AddSaleFrame.this,
                                    "Sale recorded successfully! Total: $" + totalPrice,
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(AddSaleFrame.this,
                                "Product not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(AddSaleFrame.this,
                        "Please enter a valid quantity!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(AddSaleFrame.this,
                        "Error recording sale: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}