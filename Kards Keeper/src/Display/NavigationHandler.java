package Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NavigationHandler {

    // Method to open a new window with a specific title
    public static void openNewWindow(String title, JFrame mainFrame) {
        JFrame newFrame = new JFrame(title);
        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newFrame.setSize(600, 800);
        newFrame.setLayout(new BorderLayout());

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        newFrame.add(label, BorderLayout.CENTER);

        // Create a back button
        JButton backButton = new JButton("Back to Main");
        backButton.setPreferredSize(new Dimension(450, 150));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newFrame.dispose();
                mainFrame.setVisible(true);
            }
        });

        // Add the back button to the bottom of the new frame
        JPanel backButtonPanel = new JPanel();
        backButtonPanel.setLayout(new BorderLayout());
        backButtonPanel.add(backButton, BorderLayout.SOUTH);
        newFrame.add(backButtonPanel, BorderLayout.SOUTH);

        // Make the main frame invisible and the new frame visible
        mainFrame.setVisible(false);
        newFrame.setVisible(true);
    }
}
