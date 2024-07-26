public class Board {
    // *** should size be 16 or 10 ? ***
    private final int BOARD_SIDE_LENGTH = 16;

    // represents the current state of the board - 0 is unoccupied, 1 is occupied by
    // P1, 2 is occupied by P2
    int[][] board;

    public Board() {
        // ** array starts out filled w 0's since its an empty board **
        board = new int[BOARD_SIDE_LENGTH][BOARD_SIDE_LENGTH];
    }

    public int getSideLength() {
        return BOARD_SIDE_LENGTH;
    }

    public void setBoardConfiguration() {

    }
}
