package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class TitlePanel extends JPanel {
    private JFrame window;
    private GamePanel gamePanel;

    private String font;

    public TitlePanel(JFrame window) {
        this.window = window;
        setBackground(new Color(6, 57, 112));
        setLayout(new BorderLayout());

        initializeFonts();

        JLabel titleLabel = new JLabel("BATTLESHIP", SwingConstants.CENTER);
        titleLabel.setFont(new Font(font, Font.PLAIN, 60));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.CENTER);

        // start button
        JButton startButton = new JButton("START");
        startButton.setFont(new Font(font, Font.PLAIN, 28));
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGameScreen();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(6, 57, 112, 0));
        buttonPanel.add(startButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initializeFonts() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            // Danger Zone Warning font only works for capital letters
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/main/font/Danger Zone Warning.ttf")));
            font = "Danger Zone Warning";
        } catch (IOException | FontFormatException e) {
            // if it can't properly read font, just use arial instead
            font = "Arial";
        }
    }

    public void showGameScreen() {
        // Initialize the game panel and transition to it
        gamePanel = new GamePanel(window);
        // remove this JPanel from JFrame and add GamePanel
        window.remove(this);
        window.add(gamePanel);
        window.pack();
        gamePanel.launchGame();
        // centers window
        window.setLocationRelativeTo(null);
        window.revalidate();
        window.repaint();
    }
}
