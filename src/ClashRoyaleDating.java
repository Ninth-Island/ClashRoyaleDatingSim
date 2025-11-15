import javax.swing.*;
import java.awt.*;


public class ClashRoyaleDating {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Clash Royale Dating Simulator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 950);
            frame.setLocationRelativeTo(null);

            // Start with profile input
            frame.add(new ProfileInputPanel(frame));

            frame.setVisible(true);
        });
    }
}

