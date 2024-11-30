package main;
import java.awt.*;
import java.util.HashMap;

public class Ship {
    public ShipType shipType;
    public int shipSize;
    private int orientation;

    public int SQUARE_SIZE;
    public int HALF_SQUARE_SIZE;
    // x position offset used to position ships on attack mode boards
    public int xOffset;
    public int yOffset;

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
        xOffset = 0;
        // leaving space at the top for the instructions and buttons
        yOffset = 0;
        SQUARE_SIZE = 70;
        HALF_SQUARE_SIZE = SQUARE_SIZE/2;

        HashMap<ShipType, Integer> shipTypeToShipSizeMap = new HashMap<ShipType, Integer>();
        shipTypeToShipSizeMap.put(ShipType.AIRCRAFT_CARRIER, 5);
        shipTypeToShipSizeMap.put(ShipType.BATTLESHIP, 4);
        shipTypeToShipSizeMap.put(ShipType.DESTROYER, 3);
        shipTypeToShipSizeMap.put(ShipType.SUBMARINE, 3);
        shipTypeToShipSizeMap.put(ShipType.PATROL_BOAT, 2);


        this.shipType = shipType;
        this.shipSize = shipTypeToShipSizeMap.get(shipType);

        // default orientation is vertical
        this.orientation = GamePanel.vertical;

        //default position in array is 0, 0
        this.xCoordinate=0;
        this.yCoordinate=0;

        this.xPosition = getXPositionOnGrid(xCoordinate);
        this.yPosition = getYPositionOnGrid(yCoordinate);
    }

    // constructor for AttackModeShips
    public Ship(ShipType shipType, int playerNum, int orientation, int xCoordinate, int yCoordinate) {

        // ****************************
        SQUARE_SIZE = 75;
        if (playerNum==0) {
            xOffset = 20;
        }
        else {
            xOffset = 900;
        }
        yOffset = 80;
        HALF_SQUARE_SIZE = SQUARE_SIZE/2;

        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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

    public int getOrientation() {
        return orientation;
    }

    public void changeOrientation() {
        if (orientation==GamePanel.vertical) {
            orientation = GamePanel.horizontal;
        }
        else {
            orientation = GamePanel.vertical;
        }
    }

    /*
    * this method is used to update position of the ship
    * before this method is called, make sure position is valid
    * */
    public void changePosition(int xpos, int ypos) {

        this.xPosition = xpos;
        this.yPosition = ypos;

        this.xCoordinate = getXCoordFromPositionOnGrid(xpos);
        this.yCoordinate = getYCoordFromPositionOnGrid(ypos);

//        System.out.println("(X: " + xCoordinate + " Y: " + yCoordinate + ")");

        this.xPosition = getXPositionOnGrid(xCoordinate);
        this.yPosition = getYPositionOnGrid(yCoordinate);
    }

    public int getXPositionOnGrid(int col) {
        return col * SQUARE_SIZE + xOffset;
    }
    public int getYPositionOnGrid(int row) {
        return row * SQUARE_SIZE + yOffset;
    }

    /* this method is for placement ships only ; for attack ships use attack ship board method instead */
    public int getXCoordFromPositionOnGrid(int xPos) {
        return (xPos + HALF_SQUARE_SIZE) / SQUARE_SIZE;
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
        g2.setColor(new Color(20, 20, 20));
        if(orientation==GamePanel.vertical) {
            // params are x, y, width, height
            g2.fillRect(xPosition + xOffset, yPosition + yOffset, SQUARE_SIZE, shipSize*SQUARE_SIZE);
        }
        else {
            g2.fillRect(xPosition + xOffset, yPosition, shipSize*SQUARE_SIZE, SQUARE_SIZE);
        }
    }
}
