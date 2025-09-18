package Display;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class TransactionHistoryPage {

    public static void createAndShowGUI(JFrame mainFrame) {
        JFrame frame = new JFrame("Transaction History");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(new BorderLayout());

        // Create a table model and set column names
        String[] columnNames = {"Category", "Value", "Mode of Transaction", "Income/Expense"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        // Load transaction history and populate the table model
        populateTableModel(tableModel);

        // Create a JTable with the table model
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);  // Adjust row height for better visibility

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Create a back button to return to the main page
        JButton backButton = new JButton("Back to Main");
        backButton.setPreferredSize(new Dimension(200, 50));
        backButton.addActionListener(e -> {
            frame.dispose();
            mainFrame.setVisible(true);
        });
        buttonPanel.add(backButton);

        // Create a reset button to clear all values
        JButton resetButton = new JButton("Reset All");
        resetButton.setPreferredSize(new Dimension(200, 50));
        resetButton.addActionListener(e -> {
            clearAllTransactions();
            tableModel.setRowCount(0); // Clear the table model
        });
        buttonPanel.add(resetButton);

        // Add the button panel to the frame
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Make the main frame invisible and the Transaction History page visible
        mainFrame.setVisible(false);
        frame.setVisible(true);
    }

    private static void populateTableModel(DefaultTableModel tableModel) {
        String[] tables = {"cash", "gcash", "beep"};
        String[] modes = {"Cash", "GCash", "Beep"};

        for (int i = 0; i < tables.length; i++) {
            String table = tables[i];
            String mode = modes[i];
            loadTransactions(tableModel, table, mode);
        }
    }

    private static void loadTransactions(DefaultTableModel tableModel, String table, String mode) {
        String sql = "SELECT amount, category FROM " + table;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:kards.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String category = rs.getString("category");
                double amount = rs.getDouble("amount");
                String incomeExpense = amount > 0 ? "Income" : "Expense";
                String formattedAmount = String.format("%.2f", amount);

                tableModel.addRow(new Object[]{category, formattedAmount, mode, incomeExpense});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void clearAllTransactions() {
        String[] tables = {"cash", "gcash", "beep"};

        for (String table : tables) {
            String sql = "DELETE FROM " + table;

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:kards.db");
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
