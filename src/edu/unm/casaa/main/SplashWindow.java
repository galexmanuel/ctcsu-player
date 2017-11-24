package edu.unm.casaa.main;

import javax.swing.*;
import java.awt.*;

public class SplashWindow extends JWindow {

    private static final long serialVersionUID = 1L;

    public SplashWindow() {
        JPanel content = (JPanel) getContentPane();

        content.setBackground(Color.white);

        int width = 800;
        int height = 600;

        setSize(width, height);
        setLocationRelativeTo(null); // Center on screen.

        // Build the splash screen.
        JLabel image = new JLabel(new ImageIcon("images/Splash.jpg"));

        content.add(image, BorderLayout.CENTER);
    }
}
