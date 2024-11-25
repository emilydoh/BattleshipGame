package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;


/**
 * main.GameStartGUI creates the Swing GUI and starts the application
 */
public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 675;
    public static final int HEIGHT = 675;
    // redraws frame 60 times in one second
    final int FPS = 60;

    public int gameStage;
    public final int placeShipsGameStage = 0;
    public final int attackGameStage = 1;

    public static final int vertical = 0;
    public static final int horizontal = 1;

    Thread gameThread;
    PlaceShipsBoard board = new PlaceShipsBoard();
    AttackModeBoard playerBoard = new AttackModeBoard(0);
    AttackModeBoard opponentsBoard = new AttackModeBoard(1);
    Mouse mouse = new Mouse();

    ArrayList<Ship> playerShips = new ArrayList<Ship>();
    ArrayList<Ship> opponentShips = new ArrayList<Ship>();

    // queue for ships user still needs to place
    Deque<Ship> shipsToBePlaced = new ArrayDeque<>();
    ArrayList<Ship> alreadyPlacedShips = new ArrayList<Ship>();
    Ship currentlySelectedShip;

    Game game = new Game(playerBoard, opponentsBoard);
    Coordinate[][] opponentCoordGrid;
    Coordinate[][] playerCoordGrid;

    int[][] containsShipGrid; // used to make sure ships don't overlap when user places ships

    JFrame gameWindow;

    // string constants displayed in instruction phase
    String instructionBeginString = "Click any square on the board to place your ";
    String instructionEndString = ". Use the buttons to rotate your ship and finalize placement.";

    // player turn is 0, opponent turn is 1
    int playerTurn = 0;

    // maintain references to these components so we can remove them from the panel when entering new phase
    private JTextArea instructionTextArea;
    private JButton rotateButton;
    private JButton placeButton;

    public GamePanel(JFrame window) {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);

        setInitialShips();

        gameWindow = window;
        gameStage = placeShipsGameStage;
        currentlySelectedShip = shipsToBePlaced.pop();
        alreadyPlacedShips.add(currentlySelectedShip);

        containsShipGrid = new int[board.MAX_ROWS][board.MAX_COLUMNS];

        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        initializeGUI();
    }

    private void initializeGUI() {
        if (gameStage == placeShipsGameStage) {
            instructionTextArea = new JTextArea(instructionBeginString + "PATROL_BOAT" + instructionEndString);
//          ** NEW 11/25 set the text color, background color, and font
//              *** still working on layout / design
//          instructionLabel.setForeground(Color.WHITE);
//            instructionTextArea.setLineWrap(true);
            instructionTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
            this.add(instructionTextArea);

            rotateButton = new JButton("Rotate");
            rotateButton.addActionListener(e -> currentlySelectedShip.changeOrientation());
            this.add(rotateButton);

            placeButton = new JButton("Place");
            placeButton.addActionListener(e -> {
                if (!shipsToBePlaced.isEmpty()) {
                    // button should not place the ship if placing it would overlap other ships
                    if (!isShipPlacementOverlapping(currentlySelectedShip.getXCoordinate(), currentlySelectedShip.getYCoordinate(), currentlySelectedShip.getOrientation(), currentlySelectedShip.getShipSize())) {
                        if (currentlySelectedShip.getOrientation()==GamePanel.vertical) {
                            for (int i = 0; i < currentlySelectedShip.getShipSize(); i++) {
                                containsShipGrid[currentlySelectedShip.getYCoordinate()+i][currentlySelectedShip.getXCoordinate()] = 1;
                            }
                        }
                        else {
                            for (int i = 0; i < currentlySelectedShip.getShipSize(); i++) {
                                containsShipGrid[currentlySelectedShip.getYCoordinate()][currentlySelectedShip.getXCoordinate()+i] = 1;
                            }
                        }
//                        System.out.println("Called when press finish button. State of containsShipGrid:\n" + Arrays.deepToString(containsShipGrid).replace("],", "],\n"));
                        currentlySelectedShip = shipsToBePlaced.pop();
                        // display instructions based on current ship we're placing
                        instructionTextArea.setText(instructionBeginString + currentlySelectedShip.shipType + instructionEndString);
                        alreadyPlacedShips.add(currentlySelectedShip);
                    }
                } else {
                    // transition to attack mode upon clicking finish button when no more ships to be placed
                    gameStage = attackGameStage;
                    // pass in already placed ships to the Game object
                    game.start(alreadyPlacedShips);

                    // clear components from GamePanel JPanel then resize window for attack mode
                    removePlacementComponents();
                    gameWindow.setSize(1600, 1000);
                    gameWindow.setLocationRelativeTo(null);

                    // ** NEW 11/25 add JLabels for each board -
                        //  TO DO : still need to center them above respective boards and figure out layout
                    JLabel yourBoardLabel = new JLabel("Your Board");
                    yourBoardLabel.setForeground(Color.WHITE);
                    this.add(yourBoardLabel);
                    JLabel opponentsBoardLabel = new JLabel("Opponent's board");
                    opponentsBoardLabel.setForeground(Color.WHITE);
                    this.add(opponentsBoardLabel);
                }
            });
            this.add(placeButton);
        }
    }

    private void removePlacementComponents() {
        this.remove(instructionTextArea);
        this.remove(rotateButton);
        this.remove(placeButton);
        this.revalidate();
        this.repaint();
    }

    @Override
    public void run() {
        // game loop

        // convert to the interval of how often in nanoseconds we need to redraw the frame and update
        double drawInterval = 1000000000 / FPS;
        long lastTime = System.nanoTime();
        long currentTime;
        double deltaTime = 0;

        while (gameThread!=null) {
            currentTime = System.nanoTime();
            deltaTime += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (deltaTime >= 1) {
                update();
                repaint();
                deltaTime--;
            }
        }
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {

        // in place ships mode
        if (mouse.pressed && gameStage == placeShipsGameStage) {
            // map the x to the square its in, then set ship's coordinate to that
            int mouseXPosition = mouse.x;
            int mouseYPosition = mouse.y;

            // make sure entire ship will be in bounds of board
            if (board.checkShipPlacementInBounds(mouseXPosition, mouseYPosition, currentlySelectedShip.getOrientation(),currentlySelectedShip.getShipSize())) {
                // subtract off half square size to center pointer on ship
                currentlySelectedShip.changePosition(mouseXPosition - board.HALF_SQUARE_SIZE, mouseYPosition - board.HALF_SQUARE_SIZE);
            }
        }

        // in attack mode if its the users turn
        else if (mouse.pressed && gameStage==attackGameStage && playerTurn==0) {
            // ** map positions on player board to coordinates on grid **
            int x = playerBoard.getXCoordFromPositionOnGrid(mouse.x);
            int y = playerBoard.getYCoordFromPositionOnGrid(mouse.y);

            if (game.isGuessValid(x, y)) {
                game.makePlayerTurn(x, y);
                opponentCoordGrid = game.getOpponentCoordinateGrid();
                playerCoordGrid = game.getPlayerCoordinateGrid();

                playerTurn = game.getCurrentTurn();
                game.makeCPUTurn();
                opponentCoordGrid = game.getOpponentCoordinateGrid();
                playerCoordGrid = game.getPlayerCoordinateGrid();
                playerTurn = game.getCurrentTurn();
            }
        }

    }

    /*
    * this method is used in placeShipsPhase
    * called when user presses PLACE button to ensure ship placement doesn't overlap with an occupied square
    *   relies upon updating containsShipGrid
    *   returns true if finds ship overlaps on a square, returns false if no overlap
    */
    private boolean isShipPlacementOverlapping(int xCoordinate, int yCoordinate, int orientation, int shipSize) {
        if (orientation==GamePanel.vertical) {
            // check all spots are unoccupied
            for (int i = 0; i < shipSize; i++) {
                if (containsShipGrid[yCoordinate+i][xCoordinate] != 0) {
                    return true;
                }
            }
        }
        else {
            // check all spots are unoccupied
            for (int i = 0; i < shipSize; i++) {
                if (containsShipGrid[yCoordinate][xCoordinate+i] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (gameStage == placeShipsGameStage) {
            board.draw(g2);

            for (Ship s : alreadyPlacedShips) {
                s.draw(g2);
            }
        }
        else {
            // display player and opponent boards
            playerBoard.draw(g2);
            opponentsBoard.draw(g2);

            // draw enemy and player ships
//           for (AttackModeShip s : attackModeShips) {
//               s.draw(g2);
//           }

           // draw each coordinate based on the coordinate arrays
            for (Coordinate[] row : game.getPlayerCoordinateGrid()) {
                for (Coordinate c : row) {
                    c.draw(g2);
                }
            }
            for (Coordinate[] row : game.getOpponentCoordinateGrid()) {
                for (Coordinate c : row) {
                    c.draw(g2);
                }
            }
        }

    }

    public void setInitialShips() {
        shipsToBePlaced.push(new Ship(ShipType.AIRCRAFT_CARRIER));
        shipsToBePlaced.push(new Ship(ShipType.BATTLESHIP));
        shipsToBePlaced.push(new Ship(ShipType.DESTROYER));
        shipsToBePlaced.push(new Ship(ShipType.SUBMARINE));
        shipsToBePlaced.push(new Ship(ShipType.PATROL_BOAT));

        // player
        playerShips.add(new Ship(ShipType.AIRCRAFT_CARRIER));
        playerShips.add(new Ship(ShipType.BATTLESHIP));
        playerShips.add(new Ship(ShipType.DESTROYER));
        playerShips.add(new Ship(ShipType.SUBMARINE));
        playerShips.add(new Ship(ShipType.PATROL_BOAT));

        // opponent ships will be set randomly
        opponentShips.add(new Ship(ShipType.AIRCRAFT_CARRIER));
        opponentShips.add(new Ship(ShipType.BATTLESHIP));
        opponentShips.add(new Ship(ShipType.DESTROYER));
        opponentShips.add(new Ship(ShipType.SUBMARINE));
        opponentShips.add(new Ship(ShipType.PATROL_BOAT));
    }
}