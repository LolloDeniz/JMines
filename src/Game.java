import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Stream;

@SuppressWarnings("Duplicates")

        /*package-private*/ class Game {

    public static int boxDimension = 38;
    private int DIM;
    private JPanel innerPanel = new JPanel();
    private JPanel outerPanel;
    private MinesTheme currentTheme;

    private MinesBox[][] Grid;          //matrix of box objs
    private JLabel[][] topGrid;          //matrix of labels

    private ArrayList<Integer> mines;   //list of mines

    private double bombRate;
    double blankBox;

    private boolean isGameOver = false;
    private boolean firstClick = true;


    //frame required for the ActionListener
    public Game(JMines frame, JPanel panel, int DIM, double bombRate, MinesTheme theme) {
        this.DIM = DIM;
        outerPanel = panel;
        this.currentTheme = theme;

        this.bombRate = bombRate;

        Grid = MinesBox.matrix(DIM, DIM);
        topGrid = new JLabel[DIM][DIM];

        mines = new ArrayList<>();

        if (DIM < 10)
            innerPanel.setLayout(new GridLayout(DIM, DIM));
        else {
            innerPanel.setLayout(null);
            innerPanel.setPreferredSize(new Dimension(Game.boxDimension * DIM, Game.boxDimension * DIM));    //dimension set to activate scrolling
        }
        innerPanel.setBackground(theme.getBorderColor());

        for (int x = 0; x < DIM; x++) {             //top
            for (int y = 0; y < DIM; y++) {
                topGrid[x][y] = new JLabel();
                topGrid[x][y].setIcon(new ImageIcon());
                topGrid[x][y].setOpaque(true);
                topGrid[x][y].setBackground(currentTheme.getHiddenBoxColor());
                topGrid[x][y].setHorizontalAlignment(JLabel.CENTER);
                topGrid[x][y].addMouseListener(new Game.GridListener(x, y, topGrid, Grid, frame.ButtonListenerFactory()));
                topGrid[x][y].setBorder(BorderFactory.createLineBorder(currentTheme.getBorderColor(), 2));
                topGrid[x][y].setPreferredSize(new Dimension(Game.boxDimension, Game.boxDimension));

                innerPanel.add(topGrid[x][y]);   //add single box
                if (DIM >= 10)
                    topGrid[x][y].setBounds(topGrid[x][y].getPreferredSize().height * x,
                            topGrid[x][y].getPreferredSize().width * y,
                            topGrid[x][y].getPreferredSize().height,
                            topGrid[x][y].getPreferredSize().width);
            }
        }

        JScrollPane scrollInnerPanel = new JScrollPane(innerPanel);
        scrollInnerPanel.getHorizontalScrollBar().setBackground(currentTheme.getShownBoxColor());
        scrollInnerPanel.getVerticalScrollBar().setBackground(currentTheme.getShownBoxColor());
        scrollInnerPanel.getHorizontalScrollBar().setPreferredSize(new Dimension(8, 8));
        scrollInnerPanel.getVerticalScrollBar().setPreferredSize(new Dimension(8, 8));
        scrollInnerPanel.setLayout(new ScrollPaneLayout());

        outerPanel.add(scrollInnerPanel);  //local innerPanel is added to external panel



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
                        this.blankBox--;
                        Grid[x + i][y + j].setText("<html>" + String.valueOf(GridValues[x + i][y + j].getValue() + "<html>"));
                        Grid[x + i][y + j].setBackground(currentTheme.getShownBoxColor());
                    }
                }
            }
        }
        GridValues[x][y].setShown();
        this.blankBox--;
        Grid[x][y].setText(" ");
        Grid[x][y].setBackground(currentTheme.getShownBoxColor());
    }

    public void HideGrid() {
        this.innerPanel.setVisible(false);
    }

    public void ShowGrid() {
        this.innerPanel.setVisible(true);
    }

    public void RemoveFromPanel() {
        outerPanel.removeAll();
    }

    private void showBombs() {

        //char[] bomb={(char)0xF0,(char)0x9F,(char)0x92,(char)0xA3};
        for (Integer n : mines) {
            int x = n % DIM;
            int y = n / DIM;
            topGrid[x][y].setText("B");
            topGrid[x][y].setBackground(currentTheme.getBombColor());
        }
    }

    private void BombsGenerator() {
        Random r = new Random();

        /*Stream.generate(() -> r.nextInt(DIM * DIM)).distinct()
                .filter((i) -> !(Grid[i % DIM][i / DIM].isShown()))           //check is not the first clicked obj
                .limit((long) (DIM * DIM * bombRate))                    //generation random number
                .forEach(mines::add); */                                   //add to array


        java.util.List<Integer> positions = new LinkedList<>();
        long before = System.nanoTime(); //tic

        for (int i = 0; i < DIM * DIM; i++) {
            if (!(Grid[i % DIM][i / DIM].isShown()))         //check is not the first clicked obj
                positions.add(i);
        }

        Stream.generate(() -> r.nextInt(positions.size())).limit((long) (DIM * DIM * bombRate))
                .forEach((i) -> {
                    mines.add(positions.get(i));
                    positions.remove((int) i);
                });

        long after = System.nanoTime();     //toc

        System.out.println("Generated " + mines.size() + " mines in " + (((float)after - (float)before)/1000000) + " milliseconds");

        this.blankBox = (DIM * DIM) - mines.size();  //number of boxes without bomb

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

    }

    private class GridListener extends MouseAdapter {
        ActionListener listener;
        JLabel[][] grid;
        MinesBox[][] content;
        int x, y;
        boolean sureIsBomb = false;
        boolean maybeIsBomb = false;
        JLabel gameOverMsg = new JLabel("<html><center>Oh no! You hit a bomb!<br>Try again</center></html>");
        JLabel winMsg = new JLabel("<html><center>You did it!<br>Clap! Clap!</center></html>");


        GridListener(int x, int y, JLabel[][] grid, MinesBox Content[][], ActionListener listener) {

            this.listener = listener;
            this.grid = grid;
            this.grid[x][y].setHorizontalTextPosition(JLabel.CENTER);
            this.grid[x][y].setVerticalTextPosition(JLabel.CENTER);
            this.grid[x][y].setFont(new Font("Default", Font.PLAIN, 18));
            this.grid[x][y].setForeground(currentTheme.getBoxFontColor());
            this.content = Content;
            this.x = x;
            this.y = y;
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            if (!isGameOver) {      //disabling click event on game over

                //left click
                if (mouseEvent.getModifiers() == MouseEvent.BUTTON1_MASK) {
                    content[x][y].setShown();

                    if (firstClick) {
                        BombsGenerator();       //bombs generated on first click
                        firstClick = false;
                    }
                    if (content[x][y].getValue() == 0 && (!content[x][y].isBomb()))
                        ShowZeros(content, grid, x, y);
                    else {
                        grid[x][y].setHorizontalAlignment(SwingConstants.CENTER);
                        grid[x][y].setVerticalAlignment(SwingConstants.CENTER);
                        grid[x][y].setText((content[x][y].isBomb()) ? "<html>B</html>" : "<html>" + String.valueOf(content[x][y].getValue() + "</html>"));
                        grid[x][y].setBackground((content[x][y].isBomb()) ? currentTheme.getBombColor() : currentTheme.getShownBoxColor());
                        content[x][y].setShown();
                        blankBox--;

                        if (content[x][y].isBomb()) {
                            listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "GAMEOVER"));//Trigger the GAMEOVER event handled in JMines
                            showBombs();
                            JOptionPane.showMessageDialog(innerPanel, gameOverMsg, "GAME OVER", JOptionPane.ERROR_MESSAGE); //Game over popup
                            isGameOver = true;
                        }
                    }
                    if (blankBox == 0) {
                        listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "GAMEOVER"));
                        showBombs();
                        winMsg.setHorizontalAlignment(SwingConstants.CENTER);
                        JOptionPane.showMessageDialog(innerPanel, winMsg, "GREAT JOB", JOptionPane.PLAIN_MESSAGE); //Game over popup
                        isGameOver = true;
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
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            if(!content[x][y].isShown())
                grid[x][y].setBorder(BorderFactory.createLineBorder(currentTheme.getShownBoxColor(), 2));
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            grid[x][y].setBorder(BorderFactory.createLineBorder(currentTheme.getBorderColor(), 2));
        }
    }
}
