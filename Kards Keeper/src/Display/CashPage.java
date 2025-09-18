package Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.*;

public class CashPage {
    private static JToggleButton toggleButton;
    private static JPanel cardPanel;
    private static JPanel incomePanel;
    private static JPanel expensePanel;
    private static CardLayout cardLayout;
    private static JButton selectedButton = null;
    private static String selectedCategory = "";

    public static void createAndShowGUI(JFrame mainFrame) {
        JFrame frame = new JFrame("Cash Screen");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 700);
        frame.setLayout(new BorderLayout());

        // Load the SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Calculate the total amount from the cash table
        String totalAmount = calculateTotalAmount();
        System.out.println("Total amount: " + totalAmount); // Print total amount in terminal

        // Create a custom panel for the Cash page with the total amount
        CustomPanel panel = new CustomPanel("Cash", totalAmount);
        frame.add(panel, BorderLayout.NORTH);

        // Create a panel for the text field and back button
        JPanel contentPanel = new JPanel(new BorderLayout());

        // Create the card panel and add income and expense panels to it
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        incomePanel = createIncomePanel();
        expensePanel = createExpensePanel();
        cardPanel.add(incomePanel, "Income");
        cardPanel.add(expensePanel, "Expense");

        // Create a text field for inputting numbers
        JTextField textField = createNumberTextField();

        // Create a toggle button for Income/Expense
        toggleButton = new JToggleButton("Income");
        toggleButton.addActionListener(e -> {
            if (toggleButton.isSelected()) {
                toggleButton.setText("Expense");
                showExpensePanel();
            } else {
                toggleButton.setText("Income");
                showIncomePanel();
            }
        });

        // Create a panel for the text field, toggle button, and label
        JPanel textFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        textFieldPanel.add(new JLabel("Enter Amount:"));
        textFieldPanel.add(textField);
        textFieldPanel.add(toggleButton);

        // Add components to the content panel
        contentPanel.add(cardPanel, BorderLayout.NORTH);
        contentPanel.add(textFieldPanel, BorderLayout.CENTER);

        // Show income panel by default
        showIncomePanel();

        // Create an "Add Transaction" button
        JButton addButton = new JButton("Add Transaction");
        addButton.setPreferredSize(new Dimension(450, 50));
        addButton.addActionListener(e -> {
            String amountText = textField.getText();
            if (!amountText.isEmpty() && !selectedCategory.isEmpty()) {
                double amount = Double.parseDouble(amountText);
                if (toggleButton.isSelected()) { // Expense
                    amount *= -1;
                }
                insertTransaction(amount, selectedCategory);
                MainDisplay.updateTotalPanel(); // Update the total panel
            }
            frame.dispose();
            mainFrame.setVisible(true);
        });

        // Add the "Add Transaction" button to the content panel
        contentPanel.add(addButton, BorderLayout.SOUTH);

        // Add content panel to the Cash page frame
        frame.add(contentPanel, BorderLayout.CENTER);

        // Make the main frame invisible and the Cash page frame visible
        mainFrame.setVisible(false);
        frame.setVisible(true);

        // Initialize the SQLite database
        initializeDatabase();
    }

    private static JTextField createNumberTextField() {
        JTextField textField = new JTextField(20);
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

    private static JPanel createIncomePanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10)); // 2 columns with 10px gaps
        panel.add(createCategoryButton("Salary", "src/images/salary.png"));
        panel.add(createCategoryButton("Gift", "src/images/gift.png"));
        panel.add(createCategoryButton("Refund", "src/images/refund.png"));
        panel.add(createCategoryButton("Others", "src/images/others.png"));
        return panel;
    }

    private static JPanel createExpensePanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10)); // 2 columns with 10px gaps
        panel.add(createCategoryButton("Bills/Fees", "src/images/bills.png"));
        panel.add(createCategoryButton("Transport", "src/images/transpo.png"));
        panel.add(createCategoryButton("Food", "src/images/food.png"));
        panel.add(createCategoryButton("Lend", "src/images/lend.png"));
        panel.add(createCategoryButton("Wants", "src/images/wants.png"));
        panel.add(createCategoryButton("Date", "src/images/date.png"));
        panel.add(createCategoryButton("Gift", "src/images/gift.png"));
        panel.add(createCategoryButton("Others", "src/images/others.png"));
        return panel;
    }

    private static JButton createCategoryButton(String text, String iconPath) {
        JButton button = new JButton(text);
        ImageIcon icon = new ImageIcon(iconPath);
        button.setIcon(icon);
        button.setHorizontalAlignment(SwingConstants.LEFT); // Align text to the left
        button.setHorizontalTextPosition(SwingConstants.RIGHT); // Position text to the right of the icon

        // Add action listener to change the button color when clicked
        button.addActionListener(e -> {
            if (selectedButton != null) {
                selectedButton.setBackground(null); // Reset the previous selected button color
            }
            button.setBackground(Color.LIGHT_GRAY); // Change the current button color
            selectedButton = button;
            selectedCategory = text; // Set the selected category
        });

        return button;
    }

    private static void showIncomePanel() {
        cardLayout.show(cardPanel, "Income");
    }

    private static void showExpensePanel() {
        cardLayout.show(cardPanel, "Expense");
    }

    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:kards.db")) {
            if (conn != null) {
                // Create the table if it doesn't exist
                String createTableSQL = "CREATE TABLE IF NOT EXISTS cash ("
                        + "amount REAL NOT NULL"
                        + ");";
                PreparedStatement pstmt = conn.prepareStatement(createTableSQL);
                pstmt.executeUpdate();

                // Add the category column if it doesn't exist
                if (!doesColumnExist(conn, "cash", "category")) {
                    String addColumnSQL = "ALTER TABLE cash ADD COLUMN category TEXT;";
                    PreparedStatement addColStmt = conn.prepareStatement(addColumnSQL);
                    addColStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static boolean doesColumnExist(Connection conn, String tableName, String columnName) {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName)) {
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private static void insertTransaction(double amount, String category) {
        String sql = "INSERT INTO cash(amount, category) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:kards.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, category);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String calculateTotalAmount() {
        String sql = "SELECT SUM(amount) AS total FROM cash";
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
