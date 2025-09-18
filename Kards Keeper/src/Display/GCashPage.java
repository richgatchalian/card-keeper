package Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.*;

public class GCashPage {
    private static JToggleButton toggleButton;

    public static void createAndShowGUI(JFrame mainFrame) {
        JFrame frame = new JFrame("GCash Screen");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(new BorderLayout());

        // Load the SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Calculate the total amount from the gcash table
        String totalAmount = calculateTotalAmount();
        System.out.println("Total amount: " + totalAmount); // Print total amount in terminal

        // Create a custom panel for the GCash page with the total amount
        CustomPanel panel = new CustomPanel("GCash", totalAmount);
        frame.add(panel, BorderLayout.NORTH);

        // Create a panel for the text fields and back button
        JPanel contentPanel = new JPanel(new BorderLayout());

        // Create text fields for inputting account name, phone number, and amount
        JTextField accountNameField = new JTextField(15);
        JTextField phoneNumberField = new JTextField(15);
        JTextField amountField = createNumberTextField();

        // Create a toggle button for Income/Expense
        toggleButton = new JToggleButton("Income");
        toggleButton.addActionListener(e -> {
            if (toggleButton.isSelected()) {
                toggleButton.setText("Expense");
            } else {
                toggleButton.setText("Income");
            }
        });

        // Create a panel for the input fields and toggle button
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.add(new JLabel("Account Name:"));
        inputPanel.add(accountNameField);
        inputPanel.add(new JLabel("Phone Number:"));
        inputPanel.add(phoneNumberField);
        inputPanel.add(new JLabel("Enter Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel());
        inputPanel.add(toggleButton);

        // Create an "Add Transaction" button
        JButton addButton = new JButton("Add Transaction");
        addButton.setPreferredSize(new Dimension(450, 50));
        addButton.addActionListener(e -> {
            String accountName = accountNameField.getText();
            String phoneNumber = phoneNumberField.getText();
            String amountText = amountField.getText();

            if (!accountName.isEmpty() && !phoneNumber.isEmpty() && !amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                if (toggleButton.isSelected()) { // Expense
                    amount *= -1;
                }
                insertTransaction(accountName, phoneNumber, amount, "GCash");
                MainDisplay.updateTotalPanel(); // Update the total panel
            }
            frame.dispose();
            mainFrame.setVisible(true);
        });


        // Add components to the content panel
        contentPanel.add(inputPanel, BorderLayout.CENTER);
        contentPanel.add(addButton, BorderLayout.SOUTH);

        // Add content panel to the GCash page frame
        frame.add(contentPanel, BorderLayout.CENTER);

        // Make the main frame invisible and the GCash page frame visible
        mainFrame.setVisible(false);
        frame.setVisible(true);

        // Initialize the SQLite database
        initializeDatabase();
    }

    private static JTextField createNumberTextField() {
        JTextField textField = new JTextField(20); // Keep this as 20 characters
        textField.setHorizontalAlignment(JTextField.CENTER);

        // Limit input to digits and decimal point
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!(Character.isDigit(c) ||
                        (c == KeyEvent.VK_BACK_SPACE) ||
                        (c == KeyEvent.VK_DELETE) ||
                        (c == '.' && !textField.getText().contains(".")))) {
                    evt.consume();
                }
            }
        });

        return textField;
    }

    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:kards.db")) {
            if (conn != null) {
                String createTableSQL = "CREATE TABLE IF NOT EXISTS gcash ("
                        + "account_name TEXT NOT NULL, "
                        + "phone_number TEXT NOT NULL, "
                        + "amount REAL NOT NULL, "
                        + "category TEXT NOT NULL DEFAULT 'GCash'"
                        + ");";
                PreparedStatement pstmt = conn.prepareStatement(createTableSQL);
                pstmt.executeUpdate();

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insertTransaction(String accountName, String phoneNumber, double amount, String category) {
        String sql = "INSERT INTO gcash(account_name, phone_number, amount, category) VALUES(?, ?, ?, 'E-Money')";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:kards.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountName);
            pstmt.setString(2, phoneNumber);
            pstmt.setDouble(3, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    private static String calculateTotalAmount() {
        String sql = "SELECT SUM(amount) AS total FROM gcash";
        double total = 0.0;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:kards.db");
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return String.format("%.2f", total);
    }
}