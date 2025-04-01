import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Clothing Store Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create menus
        JMenu productsMenu = new JMenu("Products");
        JMenu salesMenu = new JMenu("Sales");
        JMenu exitMenu = new JMenu("Exit");

        // Create menu items
        JMenuItem addProductItem = new JMenuItem("Add Product");
        JMenuItem viewProductsItem = new JMenuItem("View Products");
        JMenuItem addSaleItem = new JMenuItem("Add Sale");
        JMenuItem viewSalesItem = new JMenuItem("View Sales");
        JMenuItem exitItem = new JMenuItem("Exit");

        JMenuItem editProductItem = new JMenuItem("Edit Product");



        // Add action listeners
        addProductItem.addActionListener(e -> new AddProductFrame());
        viewProductsItem.addActionListener(e -> new ViewProductsFrame());
        addSaleItem.addActionListener(e -> new AddSaleFrame());
        viewSalesItem.addActionListener(e -> new ViewSalesFrame());
        exitItem.addActionListener(e -> System.exit(0));
        editProductItem.addActionListener(e -> new EditProductFrame());

        // Add items to menus
        productsMenu.add(addProductItem);
        productsMenu.add(viewProductsItem);
        salesMenu.add(addSaleItem);
        salesMenu.add(viewSalesItem);
        exitMenu.add(exitItem);
        productsMenu.add(editProductItem);

        // Add menus to menu bar
        menuBar.add(productsMenu);
        menuBar.add(salesMenu);
        menuBar.add(exitMenu);

        // Set menu bar
        setJMenuBar(menuBar);

        // Add welcome label
        JLabel welcomeLabel = new JLabel("Welcome to Clothing Store Management System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(welcomeLabel, BorderLayout.CENTER);
    }
}