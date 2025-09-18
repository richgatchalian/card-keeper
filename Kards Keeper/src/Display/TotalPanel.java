package Display;

import javax.swing.*;
import java.awt.*;

public class TotalPanel extends JPanel {
    private double cashTotal;
    private double gcashTotal;
    private double beepTotal;

    public TotalPanel(double cashTotal, double gcashTotal, double beepTotal) {
        this.cashTotal = cashTotal;
        this.gcashTotal = gcashTotal;
        this.beepTotal = beepTotal;
        setPreferredSize(new Dimension(450, 200));
    }

    public void updateTotals(double cashTotal, double gcashTotal, double beepTotal) {
        this.cashTotal = cashTotal;
        this.gcashTotal = gcashTotal;
        this.beepTotal = beepTotal;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the rectangle border
        g2.drawRect(50, 20, 350, 150);

        // Set font and draw the text
        g2.setFont(new Font("Serif", Font.PLAIN, 20));
        g2.drawString("Total Cash: " + String.format("%.2f", cashTotal), 60, 60);
        g2.drawString("Total GCash: " + String.format("%.2f", gcashTotal), 60, 100);
        g2.drawString("Total Beep: " + String.format("%.2f", beepTotal), 60, 140);
    }
}
