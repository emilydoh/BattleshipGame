package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TitlePanel extends JPanel {
    private JFrame window;
    private GamePanel gamePanel;

    public TitlePanel(JFrame window) {
        this.window = window;
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Battleship", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 60));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.CENTER);

        // start button
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.PLAIN, 30));
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGameScreen();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.add(startButton);
        add(buttonPanel, BorderLayout.SOUTH);
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
