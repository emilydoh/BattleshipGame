package main;

public class PlaceShipsBoard extends Board {
    public PlaceShipsBoard() {
        super();
    }
    // ********************************* 8/2/2024 IMPLEMENT  *********************************************************8
    // used for placing ships phase
    public boolean checkShipPlacementInBounds(int xPos, int yPos, int orientation, int shipSize) {
        if (orientation==GamePanel.vertical) {
            return (yPos - HALF_SQUARE_SIZE + SQUARE_SIZE*shipSize < MAX_ROWS*SQUARE_SIZE && xPos < MAX_COLUMNS*SQUARE_SIZE && xPos - HALF_SQUARE_SIZE > 0);
        }
        else {
            return (xPos - HALF_SQUARE_SIZE + SQUARE_SIZE*shipSize < MAX_COLUMNS*SQUARE_SIZE && yPos - HALF_SQUARE_SIZE < MAX_ROWS*SQUARE_SIZE && xPos - HALF_SQUARE_SIZE > 0);
        }
    }
}
