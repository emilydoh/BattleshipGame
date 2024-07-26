package main;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;

public class Game {

    // 0 is player 1 and 1 is player 2
    private int playerTurn;
    private boolean gameInProgress;

    final int BOARD_COLUMNS = 9;
    final int BOARD_ROWS = 9;
    final int SQUARE_SIZE = 75;
    final int OPPONENT_BOARD_OFFSET = 250;
    final int PLAYER_BOARD_OFFSET = 20;

    AttackModeBoard playerBoard;
    AttackModeBoard opponentBoard;

    // MAYBE MAINTAIN A LIST OF SHIPS THAT REMAIN UNSUNK FOR EACH PLAYER - IF EITHER LIST IS EMPTY GAME ENDS

    // make a 2d array of ships so we can easily check if ship is sunk ?

    // issue with this is we would need to create dummy ships where there's really none
    // what we would need is to just have coordiantes and then point to the ship object they contain

    // *** we should also easily be able to lookup if a square has been checked already - check Coordinate grid ***

    // 2d array of if the grid contains any ships O(1) lookups if a coordinate contains a sunk, active ship or no ship
    // 0 is no ship
    // 1 is ship not hit yet
    // 2 is ship hit

    private int[][] stateOfPlayerBoard;
    private int[][] stateOfOpponentBoard;

    // 2d array of coordinates for checking which ship is at that point
    private Coordinate[][] playerCoordinateGrid = new Coordinate[BOARD_ROWS][BOARD_COLUMNS];
    private Coordinate[][] opponentCoordinateGrid = new Coordinate[BOARD_ROWS][BOARD_COLUMNS];

    private int roundNumber;

    private ArrayList<AttackModeShip> playerBoardAttackShips = new ArrayList<AttackModeShip>();
    private ArrayList<AttackModeShip> opponentBoardAttackShips = new ArrayList<AttackModeShip>();

    /*
     * Game class handles game logic once player has finshed placing ships and entered attack mode
     */
    public Game(AttackModeBoard playerBoard, AttackModeBoard opponentBoard) {
        playerTurn = 0;
        gameInProgress = false;
        roundNumber = 1;
        this.playerBoard = playerBoard;
        this.opponentBoard = opponentBoard;
    }

    public void start(ArrayList<Ship> playerShips) {
        gameInProgress = true;
        placePlayerShips(playerShips);
    }

    public void placePlayerShips(ArrayList<Ship> playerShips) {
        // ** place player's ships on the opponent's board **
        stateOfOpponentBoard = new int[BOARD_ROWS][BOARD_COLUMNS];
        for (Ship s : playerShips) {
            AttackModeShip newShip = new AttackModeShip(s.getShipType(), 1, s.getOrientation(), s.getXCoordinate(), s.getYCoordinate());
            opponentBoardAttackShips.add(newShip);
            
            // place each ship on opponents state board and coord grid
            if (newShip.getOrientation().equals("vertical")) {
                for (int i = 0; i < s.getShipSize(); i++) {
                    int x = newShip.getXCoordinate();
                    int y = newShip.getYCoordinate() + i;
                    stateOfOpponentBoard[x][y] = 1;
                    opponentCoordinateGrid[x][y] = new Coordinate(x, y, newShip.xPosition , newShip.yPosition + (i * newShip.SQUARE_SIZE), newShip, true);
                }
            }
            else {
                for (int i = 0; i < s.getShipSize(); i++) {
                    int x = newShip.getXCoordinate() + i;
                    int y = newShip.getYCoordinate();
                    stateOfOpponentBoard[x][y] = 1;
                    opponentCoordinateGrid[x][y] = new Coordinate(x, y, newShip.xPosition + (i * newShip.SQUARE_SIZE), newShip.yPosition, newShip, true);
                }
            }
        }

        // ******* after we placed ships in coordinate grid, fill null positions with non-ship containing coordinates ************
        for (int j = 0; j < BOARD_COLUMNS; j++) {
            for (int k = 0; k < BOARD_ROWS; k++) {
                if (opponentCoordinateGrid[k][j] == null) {
                    opponentCoordinateGrid[k][j] = new Coordinate(k, j, OPPONENT_BOARD_OFFSET + (k * SQUARE_SIZE), j*SQUARE_SIZE);
                }
//                System.out.print(Arrays.deepToString(opponentCoordinateGrid));
            }
        }
        placeOpponentShips();
    }

    public void placeOpponentShips() {
        // place the opponents ships randomly on the player's board
        stateOfPlayerBoard = new int[BOARD_ROWS][BOARD_COLUMNS];
        // need to make sure its a valid position - in bounds and that it doesn't overlap

        // queue for ships opponent needs to place
        Deque<Ship> shipsToBePlaced = new ArrayDeque<>();
        shipsToBePlaced.push(new Ship(ShipType.AIRCRAFT_CARRIER));
        shipsToBePlaced.push(new Ship(ShipType.BATTLESHIP));
        shipsToBePlaced.push(new Ship(ShipType.DESTROYER));
        shipsToBePlaced.push(new Ship(ShipType.SUBMARINE));
        shipsToBePlaced.push(new Ship(ShipType.PATROL_BOAT));
        while (!shipsToBePlaced.isEmpty()) {
            Ship currentShip = shipsToBePlaced.pop();
            // place ships randomly and update states of boards
            placeRandomShip(currentShip);
        }

        // ******* after we placed ships in coordinate grid, fill null positions with non-ship containing coordinates ************
        for (int j = 0; j < BOARD_COLUMNS; j++) {
            for (int k = 0; k < BOARD_ROWS; k++) {
                if (playerCoordinateGrid[k][j] == null) {
                    playerCoordinateGrid[k][j] = new Coordinate(k, j, PLAYER_BOARD_OFFSET + (k * SQUARE_SIZE), j*SQUARE_SIZE);
                }
            }
        }
    }

    private void placeRandomShip(Ship s) {
        // randomly generate x, y within bounds & orientation
        // x and y can both go from index 0 to side length -1
        int xGuess = (int) (Math.random() * BOARD_COLUMNS);
        int yGuess = (int) (Math.random() * BOARD_ROWS);
        // either 0 or 1
        int orientation = (int) (Math.random() * 2);

        boolean shipPlaced = false;
        while (!shipPlaced) {
            if (isShipPlacementValid(xGuess, yGuess, orientation, s, stateOfPlayerBoard)) {
                // mark on the board that all of these spaces are occupied
                AttackModeShip newShip;
                if (orientation==1) {  // vertical
                    newShip = new AttackModeShip(s.getShipType(), 0, "vertical", xGuess, yGuess);
                    for (int i = 0; i < s.getShipSize(); i++) {
                        stateOfPlayerBoard[xGuess][yGuess+i] = 1;
                        playerCoordinateGrid[xGuess][yGuess+i] = new Coordinate(xGuess, yGuess+i, xGuess*SQUARE_SIZE + PLAYER_BOARD_OFFSET, (yGuess+i)*SQUARE_SIZE, newShip, false);
                    }
                    playerBoardAttackShips.add(newShip);
                }
                else { // horizontal
                    newShip = new AttackModeShip(s.getShipType(), 0, "horizontal", xGuess, yGuess);
                    for (int i = 0; i < s.getShipSize(); i++) {
                        stateOfPlayerBoard[xGuess+i][yGuess] = 1;
                        playerCoordinateGrid[xGuess+i][yGuess] = new Coordinate(xGuess+i, yGuess, (xGuess+i)*SQUARE_SIZE + PLAYER_BOARD_OFFSET, yGuess*SQUARE_SIZE, newShip, false);
                    }
                    playerBoardAttackShips.add(newShip);
                }
                shipPlaced = true;
            }
            // make a new guess
            else {
                xGuess = (int) (Math.random() * BOARD_COLUMNS);
                yGuess = (int) (Math.random() * BOARD_ROWS);
                orientation = (int) (Math.random() * 2);
            }
        }
    }

    // this method checks if a ship placement is valid
    public boolean isShipPlacementValid(int x, int y, int orientation, Ship s, int[][] board) {
        // vertical
        if (orientation==1) {
            // check in bounds
            if (y+s.getShipSize() > BOARD_ROWS) {
                return false;
            }
            // check all spots are unoccupied
            for (int i = 0; i < s.getShipSize(); i++) {
                if (board[x][y+i] != 0) {
                    return false;
                }
            }
        }
        // horizontal
        else {
            // check in bounds
            if (x+s.getShipSize() > BOARD_COLUMNS) {
                return false;
            }
            // check all spots are unoccupied
            for (int i = 0; i < s.getShipSize(); i++) {
                if (board[x+i][y] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    // we will return these arrays to be rendered on the front end
    public int[][] getStateOfPlayerBoard() {
        return stateOfPlayerBoard;
    }
    public int[][] getStateOfOpponentBoard() {
        return stateOfOpponentBoard;
    }

    public boolean isGuessValid(int xCoord, int yCoord) {
        // check x and y in bounds and square not checked already
        return (xCoord < BOARD_COLUMNS && xCoord >= 0 && yCoord < BOARD_ROWS && !playerCoordinateGrid[xCoord][yCoord].hasBeenChecked());
    }

    /* params: x, y should already have been validated - player has not yet checked this coord and coord in bounds */
    public void makePlayerTurn(int x, int y) {
        // if we hit a ship - we must check to see if its been sunk - this will be difficult without knowing orientation or type of ship
        // maybe it would be good to maintain a grid of coordinates that say which ship occupies each square and which direction its in

        // hit previously unknown square of a ship
        if (stateOfPlayerBoard[x][y] != 0) {
            // update state to show its been hit
            stateOfPlayerBoard[x][y] = 2;

            playerCoordinateGrid[x][y].getShip().setCoordinateArrayAsChecked(x, y);

            // *********** check if it sunk a ship and do something with it *********
            boolean wasShipSunk = checkIfShipSunk(x, y);
        }

        // ************8 MAJOR ISSUE - BECAUSE WE ARE RENDERING ON THE FRONT END - WE DON'T UPDATE A COORDINATE IF IT DOESNT CONTAIN A SHIP ********
        // EVEN THOUGH WE WANT TO DISPLAY THAT WE HIT THE COORDIANTE AND IT WAS A MISS
        playerCoordinateGrid[x][y].updateHasBeenChecked(); // update arrays to show its hit

        // change turn

        // make the CPU turn
    }

    public boolean checkIfShipSunk(int x, int y) {
        AttackModeShip s = playerCoordinateGrid[x][y].getShip();
        return s.decrementRemainingSquaresCount(); // if this returns true, we sunk ship
    }

    /************************************/
    public Coordinate[][] getPlayerCoordinateGrid() {
        return playerCoordinateGrid;
    }
    public Coordinate[][] getOpponentCoordinateGrid() {
        return opponentCoordinateGrid;
    }

    public void makeCPUTurn() {

    }




    public void setShipConfiguration() {

    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public void playerPlayTurn(int playerNum) {

    }

    public int getWhoseTurn() {
        return playerTurn;
    }

    // alternates playerTurn between 0 and 1
    public void updateTurn() {
        playerTurn = 1 - playerTurn;
    }
}
