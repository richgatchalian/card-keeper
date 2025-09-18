package Display;

import javax.swing.*;
import java.awt.*;

class CustomPanel extends JPanel {
    private String pageTitle;
    private String totalAmount;

    public CustomPanel(String pageTitle, String totalAmount) {
        this.pageTitle = pageTitle;
        this.totalAmount = totalAmount;
    }

    public void setTitle(String title) {
        this.pageTitle = title;
        repaint(); // Ensure panel repaints to show updated title immediately
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
        repaint(); // Ensure panel repaints to show updated total amount immediately
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smoother edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set the color for the rectangle
        g2d.setColor(Color.LIGHT_GRAY);

        // Determine the dimensions and position of the rounded rectangle
        int x = 50;  // Adjust x position as needed
        int y = 50;  // Adjust y position as needed
        int width = getWidth() - 100;  // Width of the panel minus some padding
        int height = 150;  // Height of the rounded rectangle
        int arcWidth = 50;  // Arc width for rounded corners
        int arcHeight = 50;  // Arc height for rounded corners
        g2d.fillRoundRect(x, y, width, height, arcWidth, arcHeight);

        // Set the color for the text
        g2d.setColor(Color.BLACK);

        // Draw the page title and total amount at the center of the rectangle
        String fullTitle = pageTitle + ": " + totalAmount;
        Font titleFont = new Font("Serif", Font.BOLD, 24);
        g2d.setFont(titleFont);
        FontMetrics titleMetrics = g2d.getFontMetrics(titleFont);
        int titleX = x + (width - titleMetrics.stringWidth(fullTitle)) / 2;
        int titleY = y + (height - titleMetrics.getHeight()) / 2 + titleMetrics.getAscent();
        g2d.drawString(fullTitle, titleX, titleY);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 150);  // Adjust width and height as needed
    }
}
