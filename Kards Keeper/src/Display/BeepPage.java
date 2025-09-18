package Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.*;

public class BeepPage {
    private static JToggleButton toggleButton;

    public static void createAndShowGUI(JFrame mainFrame) {
        JFrame frame = new JFrame("Beep Screen");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(new BorderLayout());

        // Load the SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Calculate the total amount from the beep table
        String totalAmount = calculateTotalAmount();
        System.out.println("Total amount: " + totalAmount); // Print total amount in terminal

        // Create a custom panel for the Beep page with the total amount
        CustomPanel panel = new CustomPanel("Beep", totalAmount);
        frame.add(panel, BorderLayout.NORTH);

        // Create a panel for the text field and back button
        JPanel contentPanel = new JPanel(new BorderLayout());

        // Create a text field for inputting numbers
        JTextField textField = createNumberTextField();

        // Create a toggle button for Income/Expense
        toggleButton = new JToggleButton("Income");
        toggleButton.addActionListener(e -> {
            if (toggleButton.isSelected()) {
                toggleButton.setText("Expense");
            } else {
                toggleButton.setText("Income");
            }
        });

        // Create a panel for the text field, toggle button, and label
        JPanel textFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        textFieldPanel.add(new JLabel("Enter Amount:"));
        textFieldPanel.add(textField);
        textFieldPanel.add(toggleButton);

        // Add space between the custom panel and the text input
        contentPanel.add(Box.createVerticalStrut(100), BorderLayout.NORTH);
        contentPanel.add(textFieldPanel, BorderLayout.CENTER);

        // Create an "Add Transaction" button
        JButton addButton = new JButton("Add Transaction");
        addButton.setPreferredSize(new Dimension(450, 50));
        addButton.addActionListener(e -> {
            String amountText = textField.getText();
            if (!amountText.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                if (toggleButton.isSelected()) { // Expense
                    amount *= -1;
                }
                insertTransaction(amount);
                MainDisplay.updateTotalPanel(); // Update the total panel
            }
            frame.dispose();
            mainFrame.setVisible(true);
        });

        contentPanel.add(addButton, BorderLayout.SOUTH);

        // Add content panel to the Beep page frame
        frame.add(contentPanel, BorderLayout.CENTER);

        // Make the main frame invisible and the Beep page frame visible
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
                // Create the beep table if it doesn't exist
                String createTableSQL = "CREATE TABLE IF NOT EXISTS beep ("
                        + "amount REAL NOT NULL, "
                        + "category TEXT NOT NULL DEFAULT 'Beep'"
                        + ");";
                PreparedStatement pstmtCreate = conn.prepareStatement(createTableSQL);
                pstmtCreate.executeUpdate();

                // Check if the category column exists
                DatabaseMetaData dbMeta = conn.getMetaData();
                ResultSet rs = dbMeta.getColumns(null, null, "Card", "category");
                if (!rs.next()) {
                    // Add the category column to the beep table
                    String addColumnSQL = "ALTER TABLE beep ADD COLUMN category TEXT NOT NULL DEFAULT 'Beep';";
                    PreparedStatement pstmtAlter = conn.prepareStatement(addColumnSQL);
                    pstmtAlter.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insertTransaction(double amount) {
        String sql = "INSERT INTO beep(amount, category) VALUES(?, 'Card')";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:kards.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String calculateTotalAmount() {
        String sql = "SELECT SUM(amount) AS total FROM beep";
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
