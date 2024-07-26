package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;


/**
 * main.GameStartGUI creates the Swing GUI and starts the application
 */
public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    // redraws frame 60 times in one second
    final int FPS = 60;

    public int gameStage;
    public final int placeShipsGameStage = 0;
    public final int attackGameStage = 1;

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

    JFrame gameWindow;

    // player turn is 0, opponent turn is 1
    int playerTurn = 0;

    // maintain references to these components so we can remove them from the panel when entering new phase
    private JLabel instructionLabel;
    private JButton changeShipOrientationButton;
    private JButton finishPlacingShipButton;

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

        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        initializeGUI();
    }

    private void initializeGUI() {
        if (gameStage == placeShipsGameStage) {
            instructionLabel = new JLabel("Place your AIRCRAFT CARRIER, use the arrow keys to place and the space bar to change orientation. Press the button to finish");
            instructionLabel.setForeground(Color.WHITE);
            this.add(instructionLabel);

            changeShipOrientationButton = new JButton("Change orientation");
            changeShipOrientationButton.addActionListener(e -> currentlySelectedShip.changeOrientation());
            this.add(changeShipOrientationButton);

            finishPlacingShipButton = new JButton("Finished placing");
            finishPlacingShipButton.addActionListener(e -> {
                if (!shipsToBePlaced.isEmpty()) {
                    currentlySelectedShip = shipsToBePlaced.pop();
                    alreadyPlacedShips.add(currentlySelectedShip);
                } else {
                    // transition to attack mode
                    gameStage = attackGameStage;
                    // ************* TO DO : FOR PLAYER BOARD WE NEED TO PASS IN OUR RANDOMIZED SHIPS ******************

                    // pass in already placed ships to the Game object, it returns attack mode ships for player and opponenet.
                    game.start(alreadyPlacedShips);

                    removePlacementComponents();
                    gameWindow.setSize(1600, 1000);
                    gameWindow.setLocationRelativeTo(null);

                }
            });
            this.add(finishPlacingShipButton);
        }
    }

    private void removePlacementComponents() {
        this.remove(instructionLabel);
        this.remove(changeShipOrientationButton);
        this.remove(finishPlacingShipButton);
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

            // subtract off half square size to center pointer on ship
            currentlySelectedShip.changePosition(mouseXPosition-board.HALF_SQUARE_SIZE, mouseYPosition-board.HALF_SQUARE_SIZE);
            // ~~~~~~~~~~~~~~~~~~~~ IMPLEMENT CHECK FOR IN BOUNDS ~~~~~~~~~~~~~~~~~~

        }

        // in attack mode if its the users turn
        else if (mouse.pressed && gameStage==attackGameStage && playerTurn==0) {
            // ** map positions on player board to coordinates on grid **
            int x = playerBoard.getXCoordFromPositionOnGrid(mouse.x);
            int y = playerBoard.getYCoordFromPositionOnGrid(mouse.y);

            if (game.isGuessValid(x, y)) {
//                System.out.print("valid guess!");
                game.makePlayerTurn(x, y);
                opponentCoordGrid = game.getOpponentCoordinateGrid();
                playerCoordGrid = game.getPlayerCoordinateGrid();

//                playerTurn = 1;
                // THEN AFTER THIS WE NEED TO CALL OPPONENTS TURN AND MAKE SURE IT GETS UPDATED

//                playerTurn = 0;
            }
        }

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // ************* SHOULD WE HAVE THIS HERE OR ELSEWHERE? BC OUR ACTION LISTENER CHANGES THE THING HERE *********************
        // DO WE ADD THIS TO THE JPANEL OR THE FRAME?
        // add buttons and label
        // check game state and render conditionally
        if (gameStage == placeShipsGameStage) {
//            this.add(new JLabel("Place your AIRCRAFT CARRIER, use the arrow keys to place and the space bar to change orientation. Press the button to finish"));
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

        // ************* set opponent ships randomly ******************
        opponentShips.add(new Ship(ShipType.AIRCRAFT_CARRIER));
        opponentShips.add(new Ship(ShipType.BATTLESHIP));
        opponentShips.add(new Ship(ShipType.DESTROYER));
        opponentShips.add(new Ship(ShipType.SUBMARINE));
        opponentShips.add(new Ship(ShipType.PATROL_BOAT));


    }
}