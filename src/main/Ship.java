package main;

import java.awt.*;
import java.util.HashMap;

public class Ship {
    private ShipType shipType;
    public int shipSize;
    private String orientation;

    public int SQUARE_SIZE;
    public int HALF_SQUARE_SIZE;
    // offset used to position ships on attack mode boards
    // THIS IS BECOMING PROBLEMATIC, BECAUSE IN THE MAIN CLASS, WE HAVE A LIST OF SHIPS TO BE PLACED WITH NO BOOLEAN IN THE CONSTRUCTOR
    public int offset;


    // coordinate in array
    private int xCoordinate;
    private int yCoordinate;

    // position for graphics2D grid
    public int xPosition;
    public int yPosition;

    // coordinates that comprise the ship (attack mode)
    Coordinate[] coordinateArray;


    // constructor for PlaceShipShip
    public Ship(ShipType shipType) {

        // ****************************
        offset = 0;
        SQUARE_SIZE = 90;
        HALF_SQUARE_SIZE = SQUARE_SIZE/2;

        // ~~~~~~~~~~~~~~~~~~~~~` *** test this *** ~~~~~~~~~~~~~~~~~~~~~
        HashMap<ShipType, Integer> shipTypeToShipSizeMap = new HashMap<ShipType, Integer>();
        shipTypeToShipSizeMap.put(ShipType.AIRCRAFT_CARRIER, 5);
        shipTypeToShipSizeMap.put(ShipType.BATTLESHIP, 4);
        shipTypeToShipSizeMap.put(ShipType.DESTROYER, 3);
        shipTypeToShipSizeMap.put(ShipType.SUBMARINE, 3);
        shipTypeToShipSizeMap.put(ShipType.PATROL_BOAT, 2);


        this.shipType = shipType;
        this.shipSize = shipTypeToShipSizeMap.get(shipType);

        // default orientation is vertical
        this.orientation = "vertical";

        //default position in array is 0, 0
        this.xCoordinate=0;
        this.yCoordinate=0;

        this.xPosition = getXPositionOnGrid(xCoordinate);
        this.yPosition = getYPositionOnGrid(yCoordinate);
    }


    // constructor for AttackModeShips
    public Ship(ShipType shipType, int playerNum, String orientation, int xCoordinate, int yCoordinate) {

        // ****************************
        SQUARE_SIZE = 75;
        if (playerNum==0) {
            offset = 20;
        }
        else {
            offset = 900;
        }
        HALF_SQUARE_SIZE = SQUARE_SIZE/2;

        // ~~~~~~~~~~~~~~~~~~~~~` *** test this *** ~~~~~~~~~~~~~~~~~~~~~
        HashMap<ShipType, Integer> shipTypeToShipSizeMap = new HashMap<ShipType, Integer>();
        shipTypeToShipSizeMap.put(ShipType.AIRCRAFT_CARRIER, 5);
        shipTypeToShipSizeMap.put(ShipType.BATTLESHIP, 4);
        shipTypeToShipSizeMap.put(ShipType.DESTROYER, 3);
        shipTypeToShipSizeMap.put(ShipType.SUBMARINE, 3);
        shipTypeToShipSizeMap.put(ShipType.PATROL_BOAT, 2);


        this.shipType = shipType;
        this.shipSize = shipTypeToShipSizeMap.get(shipType);

        // pass in details about ship
        this.orientation = orientation;
        // position in array
        this.xCoordinate= xCoordinate;
        this.yCoordinate= yCoordinate;

        this.xPosition = getXPositionOnGrid(xCoordinate);
        this.yPosition = getYPositionOnGrid(yCoordinate);
    }

    public ShipType getShipType() {
        return shipType;
    }

    public int getShipSize() {
        return shipSize;
    }

    public String getOrientation() {
        return orientation;
    }

    public void changeOrientation() {
        if (orientation.equals("vertical")) {
            orientation = "horizontal";
        }
        else {
            orientation = "vertical";
        }
    }

    public void changePosition(int xpos, int ypos) {

        /**************************************************************
         need to make sure we are in a valid position before doing this
         */

        this.xPosition = xpos;
        this.yPosition = ypos;

        this.xCoordinate = getXCoordFromPositionOnGrid(xpos);
        this.yCoordinate = getYCoordFromPositionOnGrid(ypos);

        this.xPosition = getXPositionOnGrid(xCoordinate);
        this.yPosition = getYPositionOnGrid(yCoordinate);

    }

    public int getXPositionOnGrid(int col) {
        return col * SQUARE_SIZE + offset;
    }
    public int getYPositionOnGrid(int row) {
        return row * SQUARE_SIZE;
    }

    /* ################### does not work with attack ships - use attack ship board method ############################# */
    public int getXCoordFromPositionOnGrid(int xPos) {
        return ((xPos + HALF_SQUARE_SIZE) / SQUARE_SIZE);
    }

    public int getYCoordFromPositionOnGrid(int yPos) {
        return (yPos + HALF_SQUARE_SIZE) / SQUARE_SIZE;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }
    public int getYCoordinate() {
        return yCoordinate;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.black);

//        System.out.print("plain ship draw ");

        if(orientation.equals("vertical")) {
            // params are x, y, width, height
            g2.fillRect(xPosition + offset, yPosition, SQUARE_SIZE, shipSize*SQUARE_SIZE);
        }
        else {
            g2.fillRect(xPosition + offset, yPosition, shipSize*SQUARE_SIZE, SQUARE_SIZE);
        }
    }
}
