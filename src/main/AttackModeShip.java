package main;
import java.awt.*;

public class AttackModeShip extends Ship {

    public AttackModeShip(ShipType shipType, int playerNum, int orientation, int xCoordinate, int yCoordinate) {
        super(shipType, playerNum, orientation, xCoordinate, yCoordinate);
        // initialize and fill array of coordinates
        coordinateArray = new Coordinate[shipSize];
        if (playerNum == 0) {
            if (orientation==GamePanel.vertical) {
                for (int i=0; i<shipSize; i ++) {
                    coordinateArray[i] = new Coordinate(xCoordinate, yCoordinate+i, xCoordinate * SQUARE_SIZE + xOffset, (yCoordinate+i) * SQUARE_SIZE + yOffset, this, false);
                }
            }
            else {
                for (int i=0; i<shipSize; i ++) {
                    coordinateArray[i] = new Coordinate(xCoordinate+i, yCoordinate, (xCoordinate+i) * SQUARE_SIZE + xOffset, yCoordinate * SQUARE_SIZE + yOffset, this, false);
                }
            }
        }
        else {
            if (orientation==GamePanel.vertical) {
                for (int i=0; i<shipSize; i ++) {
                    coordinateArray[i] = new Coordinate(xCoordinate, yCoordinate+i, xCoordinate * SQUARE_SIZE + xOffset, (yCoordinate+i) * SQUARE_SIZE, this, true);
                }
            }
            else {
                for (int i=0; i<shipSize; i ++) {
                    coordinateArray[i] = new Coordinate(xCoordinate+i, yCoordinate, (xCoordinate+i) * SQUARE_SIZE + xOffset, yCoordinate * SQUARE_SIZE, this, true);
                }
            }
        }
    }

    @Override public void draw(Graphics2D g2) {
        for (Coordinate c: coordinateArray) {
            c.draw(g2);
        }
    }
}
