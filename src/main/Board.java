package main;

import java.awt.*;
import java.awt.geom.Line2D;

public class Board {

    /*
    Different boards for different stages - pass in which board we want to create
    1 place ships stage board is big
    2 attack stage ships are smaller
        player attacks board on left
        opponent moves show up on board on right
     */

    final int MAX_COLUMNS = 9;
    final int MAX_ROWS = 9;
    public int SQUARE_SIZE;
    public int HALF_SQUARE_SIZE;
    public int offset;

    /*
     place ships mode constructor
     */
    public Board() {
        SQUARE_SIZE = 90;
        offset = 0;
        HALF_SQUARE_SIZE = SQUARE_SIZE/2;
    }

    /*
    attack mode constructor
    params:
        gamestage: 0 if place ships stage; 1 if attack stage
        leftOrRight: 0 if want grid to appear on left side; 1 if want grid to appear on right side
     */
    public Board(int leftOrRight) {
        // attack mode
        SQUARE_SIZE = 75;
        if (leftOrRight==0) {
            offset = 20;
        }
        else {
            offset = 900;
        }
        HALF_SQUARE_SIZE = SQUARE_SIZE/2;

    }

    public void draw(Graphics2D g2) {

        // make pixels first
        g2.setColor(new Color(34, 84, 19));
        for (int row = 0; row < MAX_ROWS; row++) {
            for (int col = 0; col < MAX_COLUMNS; col++) {
                g2.fillRect(col*SQUARE_SIZE + offset, row*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }

        // then draw vertical + horizontal grid lines on top
        g2.setColor(new Color(201, 200, 195));
        for (int row = 0; row < MAX_ROWS; row++) {
            g2.draw(new Line2D.Double(row*SQUARE_SIZE + offset, 0, row*SQUARE_SIZE + offset, MAX_ROWS*SQUARE_SIZE));
            for (int col = 0; col < MAX_COLUMNS; col++) {
                g2.draw(new Line2D.Double(0 + offset, col*SQUARE_SIZE, MAX_ROWS*SQUARE_SIZE + offset, col*SQUARE_SIZE));
            }
        }
    }
}
