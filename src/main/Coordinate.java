package main;

import java.awt.*;

/*
Coordinate class is used for the AttackModePhase to render checked/unchecked squares potentially containing ships
 */
public class Coordinate {
    private int x;
    private int y;

    private int xPosition;
    private int yPosition;

    private AttackModeShip ship;

    private boolean hasBeenChecked;
    private boolean containsShip;
    private boolean containsSunkShip;

    private boolean playerShip;

    public final int SQUARE_SIZE=75;

    // Coordinate without ship constructor
    public Coordinate(int x, int y, int xPosition, int yPosition) {
        ship = null;
        this.x = x;
        this.y = y;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        containsShip = false;
        hasBeenChecked = false;
        containsSunkShip = false;
    }

    // Coordinate with ship constructor
    public Coordinate(int x, int y, int xPosition, int yPosition, AttackModeShip ship, boolean playerShip) {
        this.ship = ship;
        this.x = x;
        this.y = y;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        containsShip = true;
        hasBeenChecked = false;
        containsSunkShip = false;
        this.playerShip = playerShip;
    }

    // two coordinates are equal if their x, y are equal
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Coordinate)) {
            return false;
        }
        Coordinate c = (Coordinate) o;
        return ((x == c.x) && (y == c.y));
    }

    public boolean hasBeenChecked() {
        return hasBeenChecked;
    }

    // returns true if we sunk a ship
    public boolean updateHasBeenChecked() {
        hasBeenChecked = true;
        // check if we sunk a ship
        if (containsShip) {
            if (ship.decrementRemainingSquaresCount()) { // it sunk a ship
                //************** WE NEED TO UPDATE THE REST OF THE COORDINATES AS SUNK ? **********
                containsSunkShip = true;
                return true;
            }
            else { // no ship was sunk
                return false;
            }
        }
        return false;
    }

    public void setContainsSunkShip() {
        containsSunkShip = true;
    }

    public boolean containsShip() {
        return containsShip;
    }

    public AttackModeShip getShip() {
        if (containsShip) {
            return ship;
        }
        else {
            return null;
        }
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean containsSunkShip() { return containsSunkShip; }

    /* draw coordinate rectangle based on its current state:
        1) not yet checked -> draw nothing
        2) checked & contains ship ->
            2a) ship sunk -> green
            2b) ship unsunk -> red
        3) checked & no ship -> darker gray
     */

    /*
        we also want to draw our own ships?
     */
    public void draw(Graphics2D g2) {
        // only draw if already checked coordinate or its a player's ship
        if (playerShip) {
            if (containsSunkShip) {
                g2.setColor(new Color(6, 14, 20));
            }
            else if (hasBeenChecked) {
                g2.setColor(new Color(255, 0, 0));
            }
            else {
                g2.setColor(new Color(0, 28, 0));
            }
            g2.fillRect(xPosition, yPosition, SQUARE_SIZE, SQUARE_SIZE);
        }
        else if (hasBeenChecked) {
            if (containsShip) {
                if (containsSunkShip) {
                   g2.setColor(new Color(0, 28, 0));
                }
                else {
                    g2.setColor(new Color(255, 0, 0));
                }
            }
            else {
                g2.setColor(new Color(6, 14, 20));
            }
            g2.fillRect(xPosition, yPosition, SQUARE_SIZE, SQUARE_SIZE);
        }
        // ****************** for debugging on our board uncomment this **************
//        g2.fillRect(xPosition, yPosition, SQUARE_SIZE, SQUARE_SIZE);
    }
}
