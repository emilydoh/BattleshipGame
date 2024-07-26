package main;

import javax.swing.JFrame;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame("Battleship");
        window.setSize(1000, 1000);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        GamePanel panel = new GamePanel(window);
        window.add(panel);
        window.pack();

        // centers window
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        panel.launchGame();
    }
}
