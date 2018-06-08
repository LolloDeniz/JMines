import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;

import javax.swing.*;

/**
 * Standalone Mines applet
 */
@SuppressWarnings("Duplicates")
public class GameTest extends JFrame {

    public MinesTheme currentTheme = new MinesTheme(Global.getgbl().light); //TODO sync with JMines's one
    public JPanel gridPanel = new JPanel();

    public GameTest(int DIM, double minesPercentage) {
        super("Test");
        this.setPreferredSize(new Dimension(500, 500));
        this.setSize(500, 500);

        MinesBox[][] Grid = MinesBox.matrix(DIM, DIM);     //matrix of box objs
        JLabel[][] topGrid = new JLabel[DIM][DIM];                //matrix of labels
        ArrayList<Integer> mines = new ArrayList<>();          //list of mines

        gridPanel.setLayout(new GridLayout(DIM, DIM));

        //random generation of mines
        Random r = new Random();
        Stream.generate(() -> r.nextInt(DIM * DIM)).distinct().limit((long) (DIM * DIM * minesPercentage)).forEach(mines::add);

        System.out.println("Generated " + mines.size() + " mines");

        for (Integer n : mines) {
            int x = n % DIM;
            int y = n / DIM;
            Grid[x][y].setBomb(true);

            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if ((i + x) >= 0 && (i + x) < DIM && (j + y) >= 0 && (j + y) < DIM && (!(i == 0 && j == 0)) && (!Grid[x + i][y + j].isBomb()))
                        Grid[x + i][y + j].addValue();
                }
            }
        }

        for (int x = 0; x < DIM; x++) {             //top
            for (int y = 0; y < DIM; y++) {
                topGrid[x][y] = new JLabel();
                topGrid[x][y].setIcon(new ImageIcon());
                topGrid[x][y].setOpaque(true);
                topGrid[x][y].setBackground(currentTheme.getHiddenBoxColor());
                topGrid[x][y].setHorizontalAlignment(JLabel.CENTER);
                topGrid[x][y].addMouseListener(new GridListener(x, y, topGrid, Grid));
                topGrid[x][y].setBorder(BorderFactory.createLineBorder(currentTheme.getBorderColor(), 2));
                gridPanel.add(topGrid[x][y]);
            }
        }
        //TODO Add bomb char

        //debug mode
        if (Global.getgbl().debug) {
            for (int x = 0; x < DIM; x++) {
                for (int y = 0; y < DIM; y++) {
                    if (Grid[x][y].getValue() != 0 || Grid[x][y].isBomb())
                        topGrid[x][y].setText("<html>" + ((Grid[x][y].isBomb()) ? "B" : String.valueOf(Grid[x][y].getValue()) + "</html>"));
                }
            }
        }

        this.add(gridPanel);
        //pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }


    private void ShowZeros(MinesBox[][] GridValues, JLabel[][] Grid, int x, int y) {
        GridValues[x][y].setShown();

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if ((i + x) >= 0 && (i + x) < GridValues[x].length && (j + y) >= 0 && (j + y) < GridValues[x].length && (!(i == 0 && j == 0))) {    //check not out of bounds
                    if (!GridValues[x + i][y + j].isShown() && (GridValues[x + i][y + j].getValue() == 0) && (!GridValues[x + i][y + j].isBomb()))
                        ShowZeros(GridValues, Grid, x + i, y + j);

                    //if there's a number I don't need ricorsion
                    if (!GridValues[x + i][y + j].isShown() && GridValues[x + i][y + j].getValue() != 0 && (!GridValues[x + i][y + j].isBomb())) {
                        GridValues[x + i][y + j].setShown();
                        Grid[x + i][y + j].setText("<html>" + String.valueOf(GridValues[x + i][y + j].getValue() + "<html>"));
                        Grid[x + i][y + j].setBackground(currentTheme.getShownBoxColor());
                    }
                }
            }
        }
        GridValues[x][y].setShown();
        Grid[x][y].setText(" ");
        Grid[x][y].setBackground(currentTheme.getShownBoxColor());
    }

    private class GridListener implements MouseListener {
        JLabel[][] grid;
        MinesBox[][] content;
        int x, y;
        boolean sureIsBomb = false;
        boolean maybeIsBomb = false;
        JLabel errorMsg=new JLabel("<html><center>Oh no! You hit a bomb!<br>Try again<center></html>");

        GridListener(int x, int y, JLabel[][] grid, MinesBox Content[][]) {
            this.grid = grid;
            this.grid[x][y].setHorizontalTextPosition(JLabel.CENTER);
            this.grid[x][y].setVerticalTextPosition(JLabel.CENTER);
            this.grid[x][y].setFont(new Font("Default", Font.PLAIN, 18));
            //TODO set dynamic font size
            this.grid[x][y].setForeground(currentTheme.getBoxFontColor());
            this.content = Content;
            this.x = x;
            this.y = y;
        }

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            //left click
            if (mouseEvent.getModifiers() == MouseEvent.BUTTON1_MASK) {
                if (content[x][y].getValue() == 0 && (!content[x][y].isBomb()))
                    ShowZeros(content, grid, x, y);
                else {
                    grid[x][y].setHorizontalAlignment(SwingConstants.CENTER);
                    grid[x][y].setVerticalAlignment(SwingConstants.CENTER);
                    content[x][y].setShown();
                    grid[x][y].setText((content[x][y].isBomb()) ? "<html>B</html>" : "<html>" + String.valueOf(content[x][y].getValue() + "</html>"));
                    grid[x][y].setBackground((content[x][y].isBomb()) ? currentTheme.getBombColor() : currentTheme.getShownBoxColor());

                    if (content[x][y].isBomb()){
                        JOptionPane.showMessageDialog(gridPanel, errorMsg,"GAME OVER", JOptionPane.ERROR_MESSAGE); //Game over popup
                    }
                }
            }

            //right click
            if (mouseEvent.getModifiers() == MouseEvent.BUTTON3_MASK)
                if (!content[x][y].isShown()) {
                    if (sureIsBomb) {
                        grid[x][y].setText("?");
                        maybeIsBomb = true;
                        sureIsBomb = false;
                    } else if (maybeIsBomb) {
                        grid[x][y].setText(" ");
                        maybeIsBomb = false;
                    } else {
                        grid[x][y].setText("!");
                        sureIsBomb = true;
                    }
                }
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
        }
    }

    public static void main(String[] args) {
        new GameTest(10,0.1);
    }

}
