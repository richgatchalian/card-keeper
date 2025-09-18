package Display;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class MainDisplay {
    private static TotalPanel totalPanel;
    private static JFrame mainFrame;

    public static void main(String[] args) {
        // Create a new frame (window)
        mainFrame = new JFrame("Kards");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 700);
        mainFrame.setLayout(new BorderLayout());

        // Calculate the total sums of cash, gcash, beep
        double cashTotal = calculateTotalAmount("cash");
        double gcashTotal = calculateTotalAmount("gcash");
        double beepTotal = calculateTotalAmount("beep");

        // Rectangle Frame to display the total sum of cash, gcash, beep in an orderly manner
        totalPanel = new TotalPanel(cashTotal, gcashTotal, beepTotal);
        mainFrame.add(totalPanel, BorderLayout.NORTH);

        // Create a panel to hold the TotalPanel and Transaction History button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(totalPanel, BorderLayout.NORTH);

        // Create the "Transaction History" button
        JButton historyButton = new JButton("Transaction History");
        historyButton.setPreferredSize(new Dimension(150, 30));
        historyButton.addActionListener(e -> TransactionHistoryPage.createAndShowGUI(mainFrame));

        // Add the "Transaction History" button below the total panel
        JPanel historyButtonPanel = new JPanel();
        historyButtonPanel.add(historyButton);
        topPanel.add(historyButtonPanel, BorderLayout.SOUTH);

        // Add the top panel to the main frame
        mainFrame.add(topPanel, BorderLayout.NORTH);

        // Create a panel for the buttons and set its layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 0, 10)); // 3 rows, 1 column, 10px vertical gap

        // Create buttons with the desired labels
        JButton button1 = new JButton("Cash");
        JButton button2 = new JButton("GCash");
        JButton button3 = new JButton("Beep");

        // Set custom font and size for the buttons
        Font buttonFont = new Font("Serif", Font.BOLD, 20);
        button1.setFont(buttonFont);
        button2.setFont(buttonFont);
        button3.setFont(buttonFont);

        // Load icons
        ImageIcon cashIcon = new ImageIcon("src/images/money.png");
        ImageIcon gcashIcon = new ImageIcon("src/images/emoney.png");
        ImageIcon beepIcon = new ImageIcon("src/images/card.png");

        // Set icons beside text
        button1.setIcon(cashIcon);
        button2.setIcon(gcashIcon);
        button3.setIcon(beepIcon);

        // Set the preferred size of the buttons to match the width of the rectangle
        Dimension buttonSize = new Dimension(450, 100);
        button1.setPreferredSize(buttonSize);
        button2.setPreferredSize(buttonSize);
        button3.setPreferredSize(buttonSize);

        // Add action listeners to each button to open respective pages
        button1.addActionListener(e -> CashPage.createAndShowGUI(mainFrame));
        button2.addActionListener(e -> GCashPage.createAndShowGUI(mainFrame));
        button3.addActionListener(e -> BeepPage.createAndShowGUI(mainFrame));

        // Add buttons to the button panel
        buttonPanel.add(button1);
        buttonPanel.add(button2);
        buttonPanel.add(button3);

        // Add the button panel to the frame at the bottom
        mainFrame.add(buttonPanel, BorderLayout.CENTER);

        // Make the frame visible
        mainFrame.setVisible(true);
    }

    public static void updateTotalPanel() {
        double cashTotal = calculateTotalAmount("cash");
        double gcashTotal = calculateTotalAmount("gcash");
        double beepTotal = calculateTotalAmount("beep");
        totalPanel.updateTotals(cashTotal, gcashTotal, beepTotal);
        totalPanel.repaint();
    }

    private static double calculateTotalAmount(String tableName) {
        String sql = "SELECT SUM(amount) AS total FROM " + tableName;
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

        return total;
    }
}
