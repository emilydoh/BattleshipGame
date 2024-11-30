package main;
import java.util.*;
import java.util.HashMap;

public class Game {

    // 0 is player 1 and 1 is player 2
    private int playerTurn;
    private boolean isgameInProgress;

    final int BOARD_COLUMNS = 9;
    final int BOARD_ROWS = 9;
    final int SQUARE_SIZE = 75;

    // for passing in offsets to coordinates to make sure they're drawn at correct offset to  align with board in attack mode boards
    final int OPPONENT_BOARD_OFFSET_X = 900;
    final int PLAYER_BOARD_OFFSET_X = 20;
    final int BOARD_OFFSET_Y = 80;

    AttackModeBoard playerBoard;
    AttackModeBoard opponentBoard;

    // MAYBE MAINTAIN A LIST OF SHIPS THAT REMAIN UNSUNK FOR EACH PLAYER - IF EITHER LIST IS EMPTY GAME ENDS
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

    // used in our heatmap method
    HashMap<ShipType,Integer> howManyOfPlayersShipTypeLeft;

    // used to check how many squares remain of each shipType - used to check if ship is sunk so we can mark all coordinates as sunk and update render
    HashMap<ShipType,Integer> opponentShipTypeToNumSquaresUnhit = new HashMap<>();
    HashMap<ShipType,Integer> playerShipTypeToNumSquaresUnhit = new HashMap<>();

    // keeps track of which coordinates each ship overlaps so we can lookup locations
    HashMap <ShipType, Coordinate[]> opponentShipTypeToCoordinates = new HashMap<>();;
    HashMap <ShipType, Coordinate[]> playerShipTypeToCoordinates = new HashMap<>();;

    // num of ships remaining - check if game is over
    int numberPlayersUnsunkShips = 5;
    int numberOpponentsUnsunkShips = 5;

    HashMap<ShipType, Integer> shipTypeToShipSizeMap;

    /*
     * Game class handles game logic once player has finished placing ships and entered attack mode
     */
    public Game(AttackModeBoard playerBoard, AttackModeBoard opponentBoard) {
        playerTurn = 0;
        isgameInProgress = false;
        this.playerBoard = playerBoard;
        this.opponentBoard = opponentBoard;

        shipTypeToShipSizeMap = new HashMap<ShipType, Integer>();
        shipTypeToShipSizeMap.put(ShipType.AIRCRAFT_CARRIER, 5);
        shipTypeToShipSizeMap.put(ShipType.BATTLESHIP, 4);
        shipTypeToShipSizeMap.put(ShipType.DESTROYER, 3);
        shipTypeToShipSizeMap.put(ShipType.SUBMARINE, 3);
        shipTypeToShipSizeMap.put(ShipType.PATROL_BOAT, 2);

        howManyOfPlayersShipTypeLeft = new HashMap<ShipType, Integer>();
        howManyOfPlayersShipTypeLeft.put(ShipType.AIRCRAFT_CARRIER, 1);
        howManyOfPlayersShipTypeLeft.put(ShipType.BATTLESHIP, 1);
        howManyOfPlayersShipTypeLeft.put(ShipType.DESTROYER, 1);
        howManyOfPlayersShipTypeLeft.put(ShipType.SUBMARINE, 1);
        howManyOfPlayersShipTypeLeft.put(ShipType.PATROL_BOAT, 1);

        opponentShipTypeToNumSquaresUnhit = new HashMap<ShipType, Integer>();
        opponentShipTypeToNumSquaresUnhit.put(ShipType.AIRCRAFT_CARRIER, 5);
        opponentShipTypeToNumSquaresUnhit.put(ShipType.BATTLESHIP, 4);
        opponentShipTypeToNumSquaresUnhit.put(ShipType.DESTROYER, 3);
        opponentShipTypeToNumSquaresUnhit.put(ShipType.SUBMARINE, 3);
        opponentShipTypeToNumSquaresUnhit.put(ShipType.PATROL_BOAT, 2);

        playerShipTypeToNumSquaresUnhit = new HashMap<ShipType, Integer>();
        playerShipTypeToNumSquaresUnhit.put(ShipType.AIRCRAFT_CARRIER, 5);
        playerShipTypeToNumSquaresUnhit.put(ShipType.BATTLESHIP, 4);
        playerShipTypeToNumSquaresUnhit.put(ShipType.DESTROYER, 3);
        playerShipTypeToNumSquaresUnhit.put(ShipType.SUBMARINE, 3);
        playerShipTypeToNumSquaresUnhit.put(ShipType.PATROL_BOAT, 2);

        playerShipTypeToCoordinates = new HashMap<ShipType, Coordinate[]>();
        playerShipTypeToCoordinates.put(ShipType.AIRCRAFT_CARRIER, new Coordinate[5]);
        playerShipTypeToCoordinates.put(ShipType.BATTLESHIP, new Coordinate[4]);
        playerShipTypeToCoordinates.put(ShipType.DESTROYER, new Coordinate[3]);
        playerShipTypeToCoordinates.put(ShipType.SUBMARINE, new Coordinate[3]);
        playerShipTypeToCoordinates.put(ShipType.PATROL_BOAT, new Coordinate[2]);

        opponentShipTypeToCoordinates = new HashMap<ShipType, Coordinate[]>();
        opponentShipTypeToCoordinates.put(ShipType.AIRCRAFT_CARRIER, new Coordinate[5]);
        opponentShipTypeToCoordinates.put(ShipType.BATTLESHIP, new Coordinate[4]);
        opponentShipTypeToCoordinates.put(ShipType.DESTROYER, new Coordinate[3]);
        opponentShipTypeToCoordinates.put(ShipType.SUBMARINE, new Coordinate[3]);
        opponentShipTypeToCoordinates.put(ShipType.PATROL_BOAT, new Coordinate[2]);
    }

    public void start(ArrayList<Ship> playerShips) {
        isgameInProgress = true;
        placePlayerShips(playerShips);
    }

    public void placePlayerShips(ArrayList<Ship> playerShips) {
        // place player's ships on the opponent's board
        stateOfOpponentBoard = new int[BOARD_ROWS][BOARD_COLUMNS];
        for (Ship s : playerShips) {
            AttackModeShip newShip = new AttackModeShip(s.getShipType(), 1, s.getOrientation(), s.getXCoordinate(), s.getYCoordinate());
            // place each ship on opponents state board and coord grid and playerShipTypeToCoordinates
            if (newShip.getOrientation()==GamePanel.vertical) {
                for (int i = 0; i < s.getShipSize(); i++) {
                    int x = newShip.getXCoordinate();
                    int y = newShip.getYCoordinate() + i;
                    stateOfOpponentBoard[x][y] = 1;
                    opponentCoordinateGrid[x][y] = new Coordinate(x, y, newShip.xPosition , newShip.yPosition + (i * newShip.SQUARE_SIZE), newShip, true);
                    playerShipTypeToCoordinates.get(newShip.getShipType())[i] = opponentCoordinateGrid[x][y];
                }
            }
            else {
                for (int i = 0; i < s.getShipSize(); i++) {
                    int x = newShip.getXCoordinate() + i;
                    int y = newShip.getYCoordinate();
                    stateOfOpponentBoard[x][y] = 1;
                    opponentCoordinateGrid[x][y] = new Coordinate(x, y, newShip.xPosition + (i * newShip.SQUARE_SIZE), newShip.yPosition, newShip, true);
                    playerShipTypeToCoordinates.get(newShip.getShipType())[i] = opponentCoordinateGrid[x][y];
                }
            }
        }
        // after placing ships coords, fill null positions with non-ship containing coordinates
        for (int j = 0; j < BOARD_COLUMNS; j++) {
            for (int k = 0; k < BOARD_ROWS; k++) {
                if (opponentCoordinateGrid[k][j] == null) {
                    opponentCoordinateGrid[k][j] = new Coordinate(k, j, OPPONENT_BOARD_OFFSET_X + (k * SQUARE_SIZE), BOARD_OFFSET_Y + j*SQUARE_SIZE);
                }
            }
        }
        placeOpponentShips();
    }

    public void placeOpponentShips() {
        // place the opponents ships randomly on the player's board
        stateOfPlayerBoard = new int[BOARD_ROWS][BOARD_COLUMNS];
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

        // after we place ships in coordinate grid, fill null positions with non-ship containing coordinates
        for (int j = 0; j < BOARD_COLUMNS; j++) {
            for (int k = 0; k < BOARD_ROWS; k++) {
                if (playerCoordinateGrid[k][j] == null) {
                    playerCoordinateGrid[k][j] = new Coordinate(k, j, PLAYER_BOARD_OFFSET_X + (k * SQUARE_SIZE), BOARD_OFFSET_Y + j*SQUARE_SIZE);
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
            if (isShipPlacementValid(xGuess, yGuess, orientation, s.getShipSize(), stateOfPlayerBoard)) {
                // mark on the board that all of these spaces are occupied
                AttackModeShip newShip;
                if (orientation==GamePanel.vertical) {  // vertical
                    newShip = new AttackModeShip(s.getShipType(), 0, GamePanel.vertical, xGuess, yGuess);
                    for (int i = 0; i < s.getShipSize(); i++) {
                        stateOfPlayerBoard[xGuess][yGuess+i] = 1;
                        playerCoordinateGrid[xGuess][yGuess+i] = new Coordinate(xGuess, yGuess+i, xGuess*SQUARE_SIZE + PLAYER_BOARD_OFFSET_X, (yGuess+i)*SQUARE_SIZE + BOARD_OFFSET_Y, newShip, false);
                        opponentShipTypeToCoordinates.get(newShip.getShipType())[i] = playerCoordinateGrid[xGuess][yGuess+i];
                    }
                }
                else { // horizontal
                    newShip = new AttackModeShip(s.getShipType(), 0, GamePanel.horizontal, xGuess, yGuess);
                    for (int i = 0; i < s.getShipSize(); i++) {
                        stateOfPlayerBoard[xGuess+i][yGuess] = 1;
                        playerCoordinateGrid[xGuess+i][yGuess] = new Coordinate(xGuess+i, yGuess, (xGuess+i)*SQUARE_SIZE + PLAYER_BOARD_OFFSET_X, yGuess*SQUARE_SIZE + BOARD_OFFSET_Y, newShip, false);
                        opponentShipTypeToCoordinates.get(newShip.getShipType())[i] = playerCoordinateGrid[xGuess+i][yGuess];
                    }
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


    /*
     this method is used in placeRandomShips when placing random ships for bot player
     checks if a ship placement is valid in terms of 1) in bounds of grid 2) not overlapping
     */
    public boolean isShipPlacementValid(int x, int y, int orientation, int size, int[][] boardSquaresOccupied) {
        // vertical
        if (orientation==GamePanel.vertical) {
            // check in bounds
            if (y+size > BOARD_ROWS) {
                return false;
            }
            // check all spots are unoccupied
            for (int i = 0; i < size; i++) {
                if (boardSquaresOccupied[x][y+i] != 0) {
                    return false;
                }
            }
        }
        // horizontal
        else {
            // check in bounds
            if (x+size > BOARD_COLUMNS) {
                return false;
            }
            // check all spots are unoccupied
            for (int i = 0; i < size; i++) {
                if (boardSquaresOccupied[x+i][y] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isGuessValid(int xCoord, int yCoord) {
        // check x and y in bounds and square not checked already
        return (xCoord < BOARD_COLUMNS && xCoord >= 0 && yCoord < BOARD_ROWS && !playerCoordinateGrid[xCoord][yCoord].hasBeenChecked());
    }

    /* params: x, y already have been validated; the player has not yet checked this coord and coord in bounds
    *       returns false if game is over */
    public boolean makePlayerTurn(int x, int y) {
        // hit previously unknown square that contains a ship
        if (stateOfPlayerBoard[x][y] == 1) {
            // update state to show its been hit
            stateOfPlayerBoard[x][y] = 2;
            //check to see if whole ship has been sunk
            ShipType typeOfShipHit = playerCoordinateGrid[x][y].getShip().shipType;
            int numSquaresRemainingForShipHitBeforeHit = opponentShipTypeToNumSquaresUnhit.get(typeOfShipHit);
            opponentShipTypeToNumSquaresUnhit.put(typeOfShipHit, numSquaresRemainingForShipHitBeforeHit-1);
            // if 0 squares remain for that ship, we need to update it as sunk - update opponentHowManyOfEachShipTypeLeft
            if (numSquaresRemainingForShipHitBeforeHit-1==0) {
                // we need to set each coordinate overlapping ship as containing sunk ship so it will be drawn as dark red
                setShipCoordinatesAsSunk(typeOfShipHit, 0);
                // check if game over - if so player won
                numberOpponentsUnsunkShips--;
                if (numberOpponentsUnsunkShips==0) {
                    return false;
                }
            }
        }
        playerCoordinateGrid[x][y].updateHasBeenChecked(); // update coordinate to show its hit
        // change turn
        playerTurn=1;
        return true;
    }

    /* this method makes the turn for the CPU - it chooses the next square to hit based on a heatmap of likely squares
    *   returns false if game is over and no ships left to attack
    * */
    public boolean makeCPUTurn() {
        // choose next coordinate to attack
        Coordinate coordinateToAttack = getNextCoordinateToAttack();
        //  if returns null no ships remain we need to end the game ***
        if (coordinateToAttack!=null) { // hit a square
            opponentCoordinateGrid[coordinateToAttack.getX()][coordinateToAttack.getY()].updateHasBeenChecked();
            playerTurn = 0;
            // if we hit a ship:
            if (opponentCoordinateGrid[coordinateToAttack.getX()][coordinateToAttack.getY()].containsShip()) {
                ShipType typeOfShipHit = opponentCoordinateGrid[coordinateToAttack.getX()][coordinateToAttack.getY()].getShip().shipType;
                int numSquaresRemainingForShipHitBeforeHit = playerShipTypeToNumSquaresUnhit.get(typeOfShipHit);
                // decrement squares remaining for ship to be sunk
                playerShipTypeToNumSquaresUnhit.put(typeOfShipHit, numSquaresRemainingForShipHitBeforeHit-1);
                // if 0 squares remain for that ship, we need to update it as sunk
                if (numSquaresRemainingForShipHitBeforeHit-1==0) {
                    howManyOfPlayersShipTypeLeft.put(typeOfShipHit, 0);
                    // we need to set each coordinate overlapping ship as containing sunk ship so it will render dark red
                    setShipCoordinatesAsSunk(typeOfShipHit, 1);
                    // check if game over - if so cpu won
                    numberPlayersUnsunkShips--;
                    if (numberPlayersUnsunkShips==0) {
                        return false;
                    }
                }
            }
            return true;
        }
        else { // end game
            isgameInProgress=false;
            return false;
        }
    }

    /*
    lookup coordinates for a ship on specified board and mark all coords as sunk
     params:
        ShipType typeOfShipHit
        playerOrOpponentBoard: tells which grid to mark on - 0 for opponents board 1 for players board
     */
    private void setShipCoordinatesAsSunk(ShipType typeOfShipHit, int playerOrOpponentBoard) {
        if (playerOrOpponentBoard == 1){
            // playing as CPU on opponent board
            for (Coordinate coord : playerShipTypeToCoordinates.get(typeOfShipHit)) {
                opponentCoordinateGrid[coord.getX()][coord.getY()].setContainsSunkShip();
            }
        }
        else {
            for (Coordinate coord : opponentShipTypeToCoordinates.get(typeOfShipHit)) {
                playerCoordinateGrid[coord.getX()][coord.getY()].setContainsSunkShip();
            }
        }

    }

    // returns null if no ships remain
    public Coordinate getNextCoordinateToAttack() {
        double[][] heatMap = makeHeatMap();
        if (heatMap!=null) {
            // iterate across heatmap and choose max
            double maxProbability = 0;
            // choose the coordinate with the max probability of a ship being placed there
            // if theres a tie on probabilities, choose a random square thats not MISS or HIT or SUNK
            Coordinate nextCoordinateToAttack = null;
            for (int m = 0; m < BOARD_ROWS; m++) {
                for (int n = 0; n < BOARD_COLUMNS; n++) {
                    double curr = heatMap[m][n];
                    if (curr >= maxProbability && !opponentCoordinateGrid[m][n].hasBeenChecked()) {
                        maxProbability = curr;
                        nextCoordinateToAttack = opponentCoordinateGrid[m][n];
                    }
                }
            }
            return nextCoordinateToAttack;
        }
        else {
            return null;
        }
    }

    /* this method creates a heatmap for the current state of the board and remaining ships based on probabilities of ship configurations */
    /* event is a single ship placement  */
    /* look at the probability that a coordinate contains a ship */
    public double[][] makeHeatMap() {
        boolean atLeastOneShipRemains = false;
        double[][] cumulativeHeatMap = new double[BOARD_ROWS][BOARD_COLUMNS];

        /* make heatmap for each remaining shiptype on the opponents board */
        for (ShipType shipType : howManyOfPlayersShipTypeLeft.keySet()) {

            int numLeft = howManyOfPlayersShipTypeLeft.get(shipType);
            if (numLeft != 0) {
                atLeastOneShipRemains = true;
                Integer size = shipTypeToShipSizeMap.get(shipType);
                size = size.intValue();
                double[][] shipHeatMap = makeShipHeatMap(shipType);
                // if numLeft more than 1 we need to add heatmap vals to the heatmap for every ship
                if (numLeft > 1) {
                    // *******************/
                    for (int i = 0; i < BOARD_ROWS; i++) {
                        for (int j = 0; j < BOARD_COLUMNS; j++) {
                            cumulativeHeatMap[i][j] += numLeft * shipHeatMap[i][j];
                        }
                    }
                } else {
                    // just add it to cumulative
                    for (int i = 0; i < BOARD_ROWS; i++) {
                        for (int j = 0; j < BOARD_COLUMNS; j++) {
                            cumulativeHeatMap[i][j] += shipHeatMap[i][j];
                        }
                    }
                }
            }
        }
        // * if theres no ships left return null and end game *
        if (!atLeastOneShipRemains) {
            return null;
        }
        return cumulativeHeatMap;
    }

    /* makes a heatmap for a single ship */
    public double[][] makeShipHeatMap(ShipType s) {
        // int arrays are default initialized to all zeros
        double[][] heatMap = new double[BOARD_ROWS][BOARD_COLUMNS];
        int totalNumConfigsForThisShipType = 0;

        boolean haveBiggerShipsBeenFound = false;
        int num1 = howManyOfPlayersShipTypeLeft.get(ShipType.AIRCRAFT_CARRIER);
        int num2 = howManyOfPlayersShipTypeLeft.get(ShipType.BATTLESHIP);
        if (num1 == 0 && num2 == 0) {
            haveBiggerShipsBeenFound = true;
        }

        for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLUMNS; j++) {
                Coordinate coord = opponentCoordinateGrid[i][j];

                // null means we haven't guessed the square yet - we need to check if this configuration works vertically and horizonally
                // if its a MISS square or a SUNK (already fully discovered ship) do nothing
                if (!coord.hasBeenChecked() || (coord.hasBeenChecked() && coord.containsShip() && !coord.containsSunkShip()) ) {

                    // if we have a hit & a small ship, we should prioritize searching area around it
                    if (haveBiggerShipsBeenFound && (coord.hasBeenChecked() && coord.containsShip() && !coord.containsSunkShip())) {
                        // check all four directions

                        if (i + 1 < BOARD_ROWS && !opponentCoordinateGrid[i + 1][j].hasBeenChecked()) {
                            double[][] newArr = new double[BOARD_COLUMNS][BOARD_ROWS];
                            newArr[i + 1][j] = 1;
                            return newArr;
                        } else if (j + 1 < BOARD_COLUMNS && !opponentCoordinateGrid[i][j + 1].hasBeenChecked()) {
                            double[][] newArr = new double[BOARD_ROWS][BOARD_COLUMNS];
                            newArr[i][j + 1] = 1;
                            return newArr;
                        } else if (i - 1 < BOARD_ROWS && !opponentCoordinateGrid[i - 1][j].hasBeenChecked()) {
                            double[][] newArr = new double[BOARD_ROWS][BOARD_COLUMNS];
                            newArr[i - 1][j] = 1;
                            return newArr;
                        } else {
                            if (j - 1 < BOARD_COLUMNS && !opponentCoordinateGrid[i][j - 1].hasBeenChecked()) {
                                double[][] newArr = new double[BOARD_ROWS][BOARD_COLUMNS];
                                newArr[i][j - 1] = 1;
                                return newArr;
                            }
                        }
                    }
                    // horizontal check if the last square of a ship placement would be in bounds
                    if (checkIndexInBounds(i + shipTypeToShipSizeMap.get(s) - 1, j)) {
                        boolean foundSunkOrMiss = false;
                        for (int a = 0; a < shipTypeToShipSizeMap.get(s); a++) {

                            Coordinate squareInPossShipConfig = opponentCoordinateGrid[i + a][j];
                            if (squareInPossShipConfig.containsSunkShip()
                                    || (squareInPossShipConfig.hasBeenChecked() && !squareInPossShipConfig.containsShip())) {
                                foundSunkOrMiss = true;
                                break;
                            }

                        }
                        // if all the squares are NOT SUNK and NOT MISS we can consider the configuration and increment it for all of them besides ones already hit
                        if (!foundSunkOrMiss) {
                            for (int c = 0; c < shipTypeToShipSizeMap.get(s); c++) {
                                Coordinate squareInPossShipConfig = opponentCoordinateGrid[i + c][j];
                                if ((!squareInPossShipConfig.hasBeenChecked() || (squareInPossShipConfig.hasBeenChecked() && squareInPossShipConfig.containsSunkShip()))) {
                                    heatMap[i + c][j] += 1;
                                }
                            }
                            totalNumConfigsForThisShipType++;
                        }
                    }
                    // vertical check
                    if (checkIndexInBounds(i, j+ shipTypeToShipSizeMap.get(s) - 1)) {
                        boolean foundSunkOrMiss = false;
                        for (int b = 0; b < shipTypeToShipSizeMap.get(s); b++) {
                            Coordinate squareInPossShipConfig = opponentCoordinateGrid[i][j+b];
                            if (squareInPossShipConfig.containsSunkShip()
                                    || (squareInPossShipConfig.hasBeenChecked() && !squareInPossShipConfig.containsShip())) {
                                foundSunkOrMiss = true;
                                break;
                            }
                        }
                        // if all the squares are NOT SUNK and NOT MISS we can consider the configuration and increment it for all of them besides ones already hit
                        if (!foundSunkOrMiss) {
                            for (int d = 0; d < shipTypeToShipSizeMap.get(s); d++) {
                                Coordinate squareInPossShipConfig = opponentCoordinateGrid[i][j+d];
                                if (!squareInPossShipConfig.hasBeenChecked() || (squareInPossShipConfig.hasBeenChecked() && squareInPossShipConfig.containsSunkShip())) {
                                    heatMap[i][j + d] += 1;
                                }
                            }
                            totalNumConfigsForThisShipType++;
                        }
                    }
                }
            }
        }

        // we use num of configs to calc probability; iterate over heatMap, and for each cell, replace it with its value / numConfigs
        if (totalNumConfigsForThisShipType != 0) {
            for (int y = 0; y < BOARD_ROWS; y++) {
                for (int z = 0; z < BOARD_COLUMNS; z++) {
                    heatMap[y][z] = heatMap[y][z] / totalNumConfigsForThisShipType;
                }
            }
        }
        return heatMap;
    }

    public boolean checkIndexInBounds(int x, int y) {
        return (x<=BOARD_COLUMNS-1 && x>0 && y<=BOARD_ROWS-1 && y>0);
    }
    public Coordinate[][] getPlayerCoordinateGrid() {
        return playerCoordinateGrid;
    }
    public Coordinate[][] getOpponentCoordinateGrid() {
        return opponentCoordinateGrid;
    }
    public int getCurrentTurn() {
        return playerTurn;
    }
}
