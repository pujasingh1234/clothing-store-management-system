import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EditProductFrame extends JFrame {
    private JTextField nameField, categoryField, sizeField, priceField, quantityField;
    private JComboBox<Integer> idCombo;

    public EditProductFrame() {
        setTitle("Edit Product");
        setSize(400, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Product ID selection
        panel.add(new JLabel("Select Product ID:"));
        idCombo = new JComboBox<>();
        loadProductIds();
        idCombo.addActionListener(e -> loadProductData());
        panel.add(idCombo);

        // Other fields
        panel.add(new JLabel("Name:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        panel.add(categoryField);

        panel.add(new JLabel("Size:"));
        sizeField = new JTextField();
        panel.add(sizeField);

        panel.add(new JLabel("Price:"));
        priceField = new JTextField();
        panel.add(priceField);

        panel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        panel.add(quantityField);

        // Buttons
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateProduct());
        panel.add(updateButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        panel.add(cancelButton);

        add(panel);
        setVisible(true);
    }

    private void loadProductIds() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id FROM products";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                idCombo.addItem(rs.getInt("id"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading IDs: " + ex.getMessage());
        }
    }

    private void loadProductData() {
        Integer selectedId = (Integer) idCombo.getSelectedItem();
        if (selectedId == null) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM products WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, selectedId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                categoryField.setText(rs.getString("category"));
                sizeField.setText(rs.getString("size"));
                priceField.setText(String.valueOf(rs.getDouble("price")));
                quantityField.setText(String.valueOf(rs.getInt("quantity")));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading product: " + ex.getMessage());
        }
    }

    private void updateProduct() {
        Integer id = (Integer) idCombo.getSelectedItem();
        String name = nameField.getText();
        String priceText = priceField.getText();

        // Simple validation
        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and price are required!");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityField.getText());

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "UPDATE products SET name=?, category=?, size=?, price=?, quantity=? WHERE id=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, categoryField.getText());
                stmt.setString(3, sizeField.getText());
                stmt.setDouble(4, price);
                stmt.setInt(5, quantity);
                stmt.setInt(6, id);

                int updated = stmt.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Product updated!");
                    dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number in price/quantity!");
        }
    }
}