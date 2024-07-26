//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//
//public class PlayButtonListener extends JFrame implements ActionListener {
//    JFrame frame;
//    Game game;
//
//    public PlayButtonListener(JFrame theFrame, Game theGame) {
//        this.frame = theFrame;
//        this.game = theGame;
//    }
//
//    public void actionPerformed(ActionEvent e) {
//        // when we click on the button, we want to change the panel to go to the place your ships page
//        JPanel placeYourShipsPanel = new JPanel();
//
//        // *** for the layout manager we'll align label and grid in a column ***
//
//        // MAYBE A BETTER WAY TO DO THIS IS TO CREATE A NEW CLASS FOR THIS PAGE AND THEN JUST RETURN AN INSTANCE OF IT
//
//        JLabel label = new JLabel("Place your ships:");
//        label.setFont(new Font("Sans Serif", Font.PLAIN, 60));
//        placeYourShipsPanel.add(label);
//
//        placeYourShipsPanel.add(new GridPane(14, 14));
//        placeYourShipsPanel.add(new GridPane(14, 14));
//
//
//        // here I think what i want to do is add let the person add the ships one at a time for each ship type
//
//        // finished placing singular ship button -> think about the mechanics of this
//        // first we would need to loop over each ship
//        // then it would have to loop where they can move it around until they place it in a valid square [and they can rotate it] and when they press the button
//
//        JButton finishedButton = new JButton("Finish placing ships");
//        placeYourShipsPanel.add(finishedButton);
//        finishedButton.addActionListener(new FinishedPlacingShipsButtonListener());
//
//        frame.setSize(2000, 1000);
//        frame.setContentPane(placeYourShipsPanel);
//        frame.setVisible(true);
//    }
//
//    public class FinishedPlacingShipsButtonListener extends JFrame implements ActionListener {
//
//        public void actionPerformed(ActionEvent e) {
//            // once all the ships are placed, we want to take the grid as it is and then iterate over it
//            // and add the ships to the game object
//            // we could pass in the game object to this class?
//
//            // we need to fill this with the user's input from the jpanels
//            int boardDimension = game.getPlayerOneBoard().getSideLength();
//            int[][] userBoardShipConfiguration = new int[boardDimension][boardDimension];
//
//            // we should also go to the next page once we click this button and there we should start the game
////            game.startGame();
//
//        }
//    }
//
//
//    // from https://gist.github.com/cemremengu/1555805
//    public static class GridPane extends JPanel {
//
//        public GridPane(int row, int col) {
//
//            int count = 0 ; // use to give a name to each box so that you can refer to them later
//            setLayout(new GridLayout(row, col));
//            setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
//
//            for (int i = 1; i <= (row * col); i++) {
//                JPanel pan = new JPanel();
//
//                pan.setEnabled(true);
//                pan.setBackground(Color.WHITE);
//                pan.setPreferredSize(new Dimension(40, 40));
//                pan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//                pan.addMouseListener(new BoxListener()); // add a mouse listener to make the panels clickable
//                pan.setName(count+"");
//                ++count;
//                add(pan);
//            }
//        }
//    }
//
//    // listener for when user clicks on a panel, let them place their ships
//    public static class BoxListener extends MouseAdapter
//    {
//        public void mouseClicked(MouseEvent me)
//        {
//            JPanel clickedBox =(JPanel)me.getSource(); // get the reference to the box that was clicked
//
//            // insert here the code defining what happens when a grid is clicked
//            // place a ship => we need to loop through all types of ships or have currently selected ship and render that
//        }
//    }
//}
//
