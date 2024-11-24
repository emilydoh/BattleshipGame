package main;

import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame("Battleship");
        window.setSize(800, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // add title JPanel to window JFrame
        TitlePanel titlePanel = new TitlePanel(window);
        titlePanel.setBorder(new EmptyBorder(10, 10, 40, 10));
        window.add(titlePanel);

        // centers window
        window.setLocationRelativeTo(null);
        window.setVisible(true);

    }
}
