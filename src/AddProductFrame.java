import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddProductFrame extends JFrame {
    private JTextField nameField, categoryField, sizeField, priceField, quantityField;

    public AddProductFrame() {
        setTitle("Add New Product");
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components
        panel.add(new JLabel("Product Name:"));
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

        JButton addButton = new JButton("Add Product");
        addButton.addActionListener(new AddProductListener());
        panel.add(addButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        panel.add(cancelButton);

        add(panel);
        setVisible(true);
    }

    private class AddProductListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String category = categoryField.getText();
            String size = sizeField.getText();
            String priceText = priceField.getText();
            String quantityText = quantityField.getText();

            if (name.isEmpty() || category.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(AddProductFrame.this,
                        "Please fill all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double price = Double.parseDouble(priceText);
                int quantity = Integer.parseInt(quantityText);

                // Save to database
                try (Connection connection = DatabaseConnection.getConnection()) {
                    String sql = "INSERT INTO products (name, category, size, price, quantity) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, name);
                    statement.setString(2, category);
                    statement.setString(3, size);
                    statement.setDouble(4, price);
                    statement.setInt(5, quantity);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(AddProductFrame.this,
                                "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(AddProductFrame.this,
                            "Error saving product: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(AddProductFrame.this,
                        "Please enter valid numbers for price and quantity!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}