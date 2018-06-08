import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.management.MemoryNotificationInfo;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

@SuppressWarnings("Duplicates")

public class JMines extends JFrame {

    //fetch screen size
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static int DIMX = 500, DIMY = 500;
    private static double LOCX = (screenSize.getWidth() - DIMY) / 2, LOCY = (screenSize.getHeight() - DIMX) / 2;

    //set theme
    private MinesTheme currentTheme;
    private Global.themes themeFlag;

    //buttons//
    private JButton startButton;
    private JButton pauseButton;
    private JButton closeButton;

    //menu entries//
    private JMenuBar menubar;
    private JMenu fileMenu, editMenu;
    private JMenu helpMenu;
    private JMenuItem closeItem, newItem;    //file tab content
    private JMenuItem dimItem, themeItem, pauseItem, startItem; //edit file content
    private JMenuItem infoItem, rulesItem;

    private MinesTimer timer;
    private JPanel grid;
    private Game gameInstance;
    private JLabel pauseLabel = new JLabel("Game Paused");

    public JMines(Global.themes theme) {

        super("JMines");
        Global.getgbl().nWindows++;
        setLocation((int) LOCX, (int) LOCY);

        switch (theme) {
            case light:
                currentTheme = new MinesTheme(Global.getgbl().light);
                themeFlag = Global.themes.light;
                break;
            case dark:
                currentTheme = new MinesTheme(Global.getgbl().dark);
                themeFlag = Global.themes.dark;
                break;
        }

        setBackground(currentTheme.getShownBoxColor());

        //this.setPreferredSize(new Dimension(DIMX, DIMY));
        this.setSize(DIMX, DIMY);

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        //Set Grid position
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 1;
        c.weighty = 0.5;
        c.fill = GridBagConstraints.BOTH;
        add(SetGrid(), c);

        //Set SideBar position
        c.gridy = 0;
        c.gridx = 1;
        c.weightx = 0;
        c.weighty = 0.5;      //equal weight on grid and side for y
        c.fill = GridBagConstraints.VERTICAL;
        //c.anchor=GridBagConstraints.EAST;
        add(SetSide(), c);

        //Set Bottom Label position
        c.weightx = 1;
        c.weighty = 0;    //priority to grid and side
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.PAGE_END;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;

        add(SetBottom(), c);

        setJMenuBar(SetMenu());

        setVisible(true);
        addWindowListener(new OnWindowUpdate());
    }

    public JMines(Global.themes theme, int x, int y, int dimx, int dimy){
        JMines instance = new JMines(theme);
        instance.setLocation(x,y);
        instance.setSize(dimx, dimy);
    }

    private JPanel SetGrid() {

        grid = new JPanel();

        grid.setLayout(new BorderLayout());
        pauseLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pauseLabel.setVerticalAlignment(SwingConstants.CENTER);
        pauseLabel.setFont(new Font("Default", Font.PLAIN, 22));
        pauseLabel.setForeground(currentTheme.getBoxFontColor());

        grid.setBackground(currentTheme.getBorderColor());
        grid.setBorder(BorderFactory.createLineBorder(currentTheme.getShownBoxColor(), 5));
        //TODO use JScrollPane

        return grid;
    }

    private JPanel SetSide() {

        //dimension is given by buttons dimension (with padding)

        //panels settings and look
        JPanel sidePanel = new JPanel();
        JPanel toolsPanel = new JPanel();
        JPanel timerPanel = new JPanel();

        sidePanel.setBorder(BorderFactory.createLineBorder(currentTheme.getShownBoxColor(), 4));
        sidePanel.setBackground(currentTheme.getShownBoxColor());
        toolsPanel.setBackground(currentTheme.getShownBoxColor());
        timerPanel.setBackground(currentTheme.getShownBoxColor());

        //timer look and settings
        JLabel timerLabel = new JLabel();

        timerLabel.setForeground(currentTheme.getBoxFontColor());
        timer = SetTimer(timerLabel);
        timerPanel.add(timerLabel);
        timerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(currentTheme.getBoxFontColor()),
                "Timer", 2, 0, new Font("Default", Font.PLAIN, 12), currentTheme.getBoxFontColor()));
        //


        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        closeButton = new JButton("Close");

        //Setting Button look
        startButton.setBackground(currentTheme.getShownBoxColor());
        startButton.setForeground(currentTheme.getBoxFontColor());
        startButton.setFocusPainted(false);

        pauseButton.setBackground(currentTheme.getShownBoxColor());
        pauseButton.setForeground(currentTheme.getBoxFontColor());
        pauseButton.setFocusPainted(false);

        closeButton.setBackground(currentTheme.getShownBoxColor());
        closeButton.setForeground(currentTheme.getBoxFontColor());
        closeButton.setFocusPainted(false);
        //
        //Setting Button commands
        startButton.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        startButton.addActionListener(new ButtonListener(timer, this));
        startButton.setActionCommand("RESTART");

        pauseButton.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        pauseButton.addActionListener(new ButtonListener(timer, this));
        pauseButton.setActionCommand("PAUSE");
        pauseButton.setEnabled(false);

        closeButton.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        closeButton.setBackground(currentTheme.getShownBoxColor());
        closeButton.addActionListener(new ButtonListener(timer, this));
        closeButton.setActionCommand("CLOSE");
        //

        //setting tools panel layout
        toolsPanel.setLayout(new GridLayout(3, 1));
        toolsPanel.add(startButton);
        toolsPanel.add(pauseButton);
        toolsPanel.add(closeButton);
        //

        //setting side panel layout
        GridBagConstraints c = new GridBagConstraints();
        sidePanel.setLayout(new GridBagLayout());

        //Set timer Panel position
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.7;
        c.weightx = 1;
        c.ipadx = 20;
        c.gridx = 0;
        c.gridy = 0;
        sidePanel.add(timerPanel, c);

        //Set ToolsPanel position
        c.fill = GridBagConstraints.BOTH;
        c.ipadx = 0;
        c.weighty = 0.3;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 2;
        sidePanel.add(toolsPanel, c);
        //


        return sidePanel;
    }

    private JPanel SetBottom() {

        JLabel Version = new JLabel("v1.2.1 (Alpha)");
        JPanel BottomPanel = new JPanel();

        BottomPanel.setLayout(new GridBagLayout());
        Version.setFont(new Font("Default", Font.PLAIN, 12));

        //Set Version label position
        GridBagConstraints c = new GridBagConstraints();
        c.ipady = 15;
        c.ipadx = 10;
        c.weightx = 1;
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.EAST;
        BottomPanel.add(Version, c);

        //static colors
        BottomPanel.setBackground(Color.black);
        Version.setForeground(Color.lightGray);

        return BottomPanel;
    }

    private MinesTimer SetTimer(JLabel timer) {
        return new MinesTimer(timer);
    }

    private JMenuBar SetMenu() {

        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(currentTheme.getBoxFontColor(), 1));
        JSeparator[] separators;

        menubar = new JMenuBar();
        //-----//
        fileMenu = new JMenu("File");
        closeItem = new JMenuItem("Close");
        newItem = new JMenuItem("New");
        //-----//
        editMenu = new JMenu("Edit");
        dimItem = new JMenuItem("Set Dimension");
        switch (this.themeFlag) {
            case light:
                themeItem = new JMenuItem("Set Dark Theme");
                break;
            case dark:
                themeItem = new JMenuItem("Set Light Theme");
                break;
        }
        startItem = new JMenuItem("Start");
        pauseItem = new JMenuItem("Pause");
        //-----//
        helpMenu = new JMenu("Help");
        infoItem = new JMenuItem("Info");
        rulesItem = new JMenuItem("Rules");

        //building menus
        //main bar
        menubar.setBackground(currentTheme.getShownBoxColor());
        menubar.setBorder(BorderFactory.createLineBorder(currentTheme.getBorderColor()));

        menubar.add(fileMenu);
        menubar.add(editMenu);
        menubar.add(Box.createHorizontalGlue());        //to move helpMenu to the right
        menubar.add(helpMenu);

        separators = new JSeparator[menubar.getMenuCount() - 1];      //subtracting 1 due to the horizontal glue
        for (int i = 0; i < separators.length; i++) {
            separators[i] = new JSeparator();
            separators[i].setBackground(currentTheme.getBorderColor());
            separators[i].setBorder(BorderFactory.createLineBorder(currentTheme.getBoxFontColor()));
        }
        /////

        //file menu
        fileMenu.setForeground(currentTheme.getBoxFontColor());
        fileMenu.setBorderPainted(false);

        fileMenu.add(closeItem);
        fileMenu.add(separators[0]);
        fileMenu.add(newItem);

        for (Component c : fileMenu.getMenuComponents()) {
            c.setForeground(currentTheme.getBoxFontColor());
            c.setBackground(currentTheme.getShownBoxColor());
            if (!(c instanceof JSeparator))
                ((JMenuItem) c).setBorderPainted(false);
        }
        /////

        //edit menu
        editMenu.setForeground(currentTheme.getBoxFontColor());
        editMenu.setBorderPainted(false);

        editMenu.add(dimItem);
        editMenu.add(themeItem);
        editMenu.add(separators[1]);
        editMenu.add(startItem);
        editMenu.add(pauseItem);

        for (Component c : editMenu.getMenuComponents()) {
            c.setForeground(currentTheme.getBoxFontColor());
            c.setBackground(currentTheme.getShownBoxColor());
            if (!(c instanceof JSeparator))
                ((JMenuItem) c).setBorderPainted(false);
        }
        //setting tooltips
        dimItem.setToolTipText("<html>Change the dimension of the grid.<br>Require a game restart!<html>");
        themeItem.setToolTipText("Require a game restart!");
        startItem.setToolTipText("Start/Restart a game");
        pauseItem.setToolTipText("Pause/Resume a game");
        /////

        //help menu
        helpMenu.setForeground(currentTheme.getBoxFontColor());
        helpMenu.setBorderPainted(false);

        helpMenu.add(rulesItem);
        helpMenu.add(separators[2]);
        helpMenu.add(infoItem);

        for (Component c : helpMenu.getMenuComponents()) {
            c.setForeground(currentTheme.getBoxFontColor());
            c.setBackground(currentTheme.getShownBoxColor());
            if (!(c instanceof JSeparator))
                ((JMenuItem) c).setBorderPainted(false);
        }
        //setting tooltips
        rulesItem.setToolTipText("See the game rules");
        infoItem.setToolTipText("See version and credits");
        /////

        //setting listeners
        closeItem.addActionListener(new MenuListener(ButtonListenerFactory())); //The listener is used to restart the game once the dimension is set
        newItem.addActionListener(new MenuListener(ButtonListenerFactory()));

        dimItem.addActionListener(new MenuListener(ButtonListenerFactory()));
        themeItem.addActionListener(new MenuListener(ButtonListenerFactory()));

        startItem.setActionCommand("RESTART");
        pauseItem.setActionCommand("PAUSE");
        startItem.addActionListener(new ButtonListener(this.timer, this));    //Button Listeners used because of same function
        pauseItem.addActionListener(new ButtonListener(this.timer, this));

        infoItem.addActionListener(new MenuListener(ButtonListenerFactory()));

        return menubar;
    }

    private void UpdateTheme(Global.themes t) {
        switch (t) {
            case dark:
                new JMines(Global.themes.dark, this.getX(), this.getY(), this.getWidth(), this.getHeight());
                break;
            case light:
                new JMines(Global.themes.light, this.getX(), this.getY(), this.getWidth(), this.getHeight());
                break;
        }
        new OnWindowUpdate().windowClosing(null);
    }

    ActionListener ButtonListenerFactory() {
        return new ButtonListener(this.timer, this);
    }

    private class MenuListener implements ActionListener {

        ActionListener listener;

        public MenuListener(ActionListener listener){
            this.listener=listener;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getActionCommand().compareTo("Close") == 0) {
                new OnWindowUpdate().windowClosing(null);
            }
            if (actionEvent.getActionCommand().compareTo("New") == 0) {
                new JMines(Global.themes.light);
            }

            if (actionEvent.getActionCommand().compareTo("Set Dark Theme") == 0) {
                themeItem.setActionCommand("Set Bright Theme");
                themeItem.setText("Set Bright Theme");
                UpdateTheme(Global.themes.dark);
            }

            if (actionEvent.getActionCommand().compareTo("Set Light Theme") == 0) {
                themeItem.setActionCommand("Set Dark Theme");
                themeItem.setText("Set Dark Theme");
                UpdateTheme(Global.themes.light);
            }

            if (actionEvent.getActionCommand().compareTo("Set Dimension") == 0) {

                if(grid.getComponentCount()!=0)
                    grid.removeAll();

                grid.add(new MinesDimSet(listener, currentTheme), BorderLayout.CENTER);
                setVisible(true);
            }

            if (actionEvent.getActionCommand().compareTo("Info") == 0) {
                System.out.println("Info requested");
                new InfoPopup((int) LOCX + DIMX / 2, (int) LOCY + DIMY / 2);
            }
        }
    }

    private class ButtonListener implements ActionListener {

        MinesTimer timer;
        JMines frame;

        ButtonListener(MinesTimer timer, JMines frame) {
            this.timer = timer;
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getActionCommand().compareTo("RESTART") == 0) {
                System.out.println("Game restarting");

                this.timer.resetTimer();
                this.timer.startTimer();

                //new instance of the Game
                if (gameInstance != null) {
                    gameInstance.RemoveFromPanel();
                    grid.remove(pauseLabel);
                }

                gameInstance = new Game(frame, grid, Global.getgbl().gridDim, Global.getgbl().bombRate, frame.currentTheme);

                pauseButton.setText("Pause");
                pauseButton.setActionCommand("PAUSE");
                startButton.setEnabled(false);
                pauseButton.setEnabled(true);

                pauseItem.setText("Pause");
                pauseItem.setActionCommand("PAUSE");
                startItem.setEnabled(false);
                pauseItem.setEnabled(true);
            }
            if (actionEvent.getActionCommand().compareTo("PAUSE") == 0) {
                System.out.println("Game paused");
                this.timer.stopTimer();

                gameInstance.HideGrid();

                grid.add(pauseLabel);

                startButton.setText("Restart"); //the first time I pause the label is set to "restart" instead of "start"
                pauseButton.setText("Resume");
                pauseButton.setActionCommand("RESUME");
                startButton.setEnabled(true);

                startItem.setText("Restart");
                startItem.setActionCommand("RESTART");
                pauseItem.setText("Resume");
                pauseItem.setActionCommand("RESUME");
                startItem.setEnabled(true);
            }
            if (actionEvent.getActionCommand().compareTo("RESUME") == 0) {
                System.out.println("Resuming");
                this.timer.startTimer();

                grid.remove(pauseLabel);

                gameInstance.ShowGrid();

                pauseButton.setText("Pause");
                pauseButton.setActionCommand("PAUSE");

                pauseItem.setText("Pause");
                pauseItem.setActionCommand("PAUSE");
            }
            if (actionEvent.getActionCommand().compareTo("GAMEOVER") == 0) {
                System.out.println("Game Over");
                this.timer.stopTimer();

                pauseButton.setEnabled(false);
                pauseItem.setEnabled(false);
                startButton.setEnabled(true);
                startItem.setEnabled(true);
                startButton.setText("Try Again");
                startItem.setText("Try Again");

            }
            if (actionEvent.getActionCommand().compareTo("CLOSE") == 0) {
                new OnWindowUpdate().windowClosing(null);
            }
        }
    }

    public class OnWindowUpdate implements WindowListener {

        @Override
        public void windowOpened(WindowEvent windowEvent) {
            System.out.println("Window opened");
        }

        @Override
        public void windowClosing(WindowEvent windowEvent) {
            System.out.println("Window closing");
            if (Global.getgbl().nWindows < 2)
                System.exit(-1);
            else {
                dispose();
                Global.getgbl().nWindows--;
            }
        }


        @Override
        public void windowClosed(WindowEvent windowEvent) {
            System.out.println("Window opened");
        }

        @Override
        public void windowIconified(WindowEvent windowEvent) {
            System.out.println("Window iconified");
        }

        @Override
        public void windowDeiconified(WindowEvent windowEvent) {
            System.out.println("Window deiconified");
        }

        @Override
        public void windowActivated(WindowEvent windowEvent) {
            System.out.println("Window activated");
        }

        @Override
        public void windowDeactivated(WindowEvent windowEvent) {
            System.out.println("Window dectivated");
        }
    }

}