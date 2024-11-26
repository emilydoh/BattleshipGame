package main;

import java.awt.*;

public class AttackModeBoard extends Board {
    public AttackModeBoard(int playerNum) {
        super(playerNum);
    }

    @Override public void draw(Graphics2D g2) {
        super.draw(g2);
    }

    public int getXCoordFromPositionOnGrid(int xPos) {
        return ((xPos - xOffset) / SQUARE_SIZE);
    }

    public int getYCoordFromPositionOnGrid(int yPos) {
        return (yPos - yOffset) / SQUARE_SIZE;
    }
}
