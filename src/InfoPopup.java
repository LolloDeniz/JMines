import java.awt.*;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

class InfoPopup extends JFrame {
    private String text = "<html><body><center>Mines Game, inspired by Windows Minesweeper<br>" +
            "<br>Credits: Lorenzo De Nisi<br>Last Release: 30 May 2018</center></body></html>";

    InfoPopup(int x, int y) {
        super("Informations");
        setLocation(x, y);
        setSize(300, 150);

        setPreferredSize(new Dimension(300, 150));

        JLabel info1 = new JLabel(text);
        info1.setFont(new Font("Default", Font.PLAIN, 11));
        info1.setHorizontalAlignment(JLabel.CENTER);

        this.add(info1);

        //pack();
        setVisible(true);
        addWindowListener(new OnWindowUpdate());

    }

    public class OnWindowUpdate implements WindowListener {

        @Override
        public void windowOpened(WindowEvent windowEvent) {
            System.out.println("Info opened");
        }

        @Override
        public void windowClosing(WindowEvent windowEvent) {
            System.out.println("Window closing");
            dispose();
        }

        @Override
        public void windowClosed(WindowEvent windowEvent) {
            System.out.println("Info closed");
        }

        @Override
        public void windowIconified(WindowEvent windowEvent) {
            System.out.println("Info iconified");
        }

        @Override
        public void windowDeiconified(WindowEvent windowEvent) {
            System.out.println("Info deiconified");
        }

        @Override
        public void windowActivated(WindowEvent windowEvent) {
            System.out.println("Info activated");
        }

        @Override
        public void windowDeactivated(WindowEvent windowEvent) {
            System.out.println("Info dectivated");
        }
    }

}
