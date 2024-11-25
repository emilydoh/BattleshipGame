package main;
import java.awt.*;

public class AttackModeShip extends Ship {
    private int remainingSquaresCount;
    private boolean isSunk;

    public AttackModeShip(ShipType shipType, int playerNum, int orientation, int xCoordinate, int yCoordinate) {
        super(shipType, playerNum, orientation, xCoordinate, yCoordinate);
        isSunk = false;
        // initialize and fill array of coordinates
        coordinateArray = new Coordinate[shipSize];
        if (playerNum == 0) {
            if (orientation==GamePanel.vertical) {
                for (int i=0; i<shipSize; i ++) {
                    coordinateArray[i] = new Coordinate(xCoordinate, yCoordinate+i, xCoordinate * SQUARE_SIZE + xOffset, (yCoordinate+i) * SQUARE_SIZE, this, false);
                }
            }
            else {
                for (int i=0; i<shipSize; i ++) {
                    coordinateArray[i] = new Coordinate(xCoordinate+i, yCoordinate, (xCoordinate+i) * SQUARE_SIZE + xOffset, yCoordinate * SQUARE_SIZE, this, false);
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

    public boolean isSunk() { return isSunk; }
    public void setIsSunk() {
        isSunk = true;
    }


    /******** ISSUE IS THAT WE CAN'T INDEX BY X, Y SINCE WE DIDN'T DO THAT WE NEED TO ITERATE THROUGH ALL AND MANUALLY SET IT *********/
    public void setCoordinateArrayAsChecked(int x, int y) {
        for (Coordinate c : coordinateArray) {
            if (c.getX() == x && c.getY()==y) {
                c.updateHasBeenChecked();
            }
        }
    }

    /* used when a ship is hit - returns true if it sunk a ship and false if no ship sunk */
    public boolean decrementRemainingSquaresCount() {
        remainingSquaresCount--;
        if (remainingSquaresCount == 0) {
            setIsSunk();
            for (Coordinate c: coordinateArray) {
                c.setContainsSunkShip();
            }
            return true;
        }
        return false;
    }

    @Override public void draw(Graphics2D g2) {
        for (Coordinate c: coordinateArray) {
            c.draw(g2);
        }
    }
}
