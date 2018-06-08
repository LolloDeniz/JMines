import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MinesDimSet extends JPanel {

    private JSlider dimSlider, minesSlider;
    private JLabel dimLabel, minesLabel;
    private ActionListener listener;

    public MinesDimSet(ActionListener listener, MinesTheme currentTheme){

        UIDefaults defaults = UIManager.getDefaults();
        defaults.put("Slider.altTrackColor", currentTheme.getBoxFontColor());
       /* defaults.put("Slider.shadow", currentTheme.getShownBoxColor());
        defaults.put("Slider.border", currentTheme.getShownBoxColor());
        defaults.put("Slider.trackBorder", BorderFactory.createLineBorder(currentTheme.getShownBoxColor(), 2));
*/

        this.listener=listener;
        dimLabel=new JLabel("Select grid dimension");
        minesLabel=new JLabel("Select mines percentage (%)");

        dimSlider = new JSlider(0,5,100, 10);
        dimSlider.setMajorTickSpacing(15);
        dimSlider.setPaintLabels(true);
        minesSlider=new JSlider(0,10, 95, 10);
        minesSlider.setMajorTickSpacing(20);
        minesSlider.setPaintLabels(true);
        JButton setButton=new JButton("Set");

        //setting colors
        dimLabel.setForeground(currentTheme.getBoxFontColor());
        minesLabel.setForeground(currentTheme.getBoxFontColor());
        this.setBackground(currentTheme.getShownBoxColor());
        dimSlider.setBackground(currentTheme.getShownBoxColor());
        minesSlider.setBackground(currentTheme.getShownBoxColor());
        dimSlider.setForeground(currentTheme.getBoxFontColor());
        minesSlider.setForeground(currentTheme.getBoxFontColor());

        setButton.addActionListener(new MyButtonListener());

        this.setLayout(new GridBagLayout());

        GridBagConstraints c=new GridBagConstraints();

        c.gridx=0;
        c.gridy=0;
        c.insets=new Insets(50,0,0,0);
        c.gridwidth=3;

        c.weighty=1;
        this.add(dimLabel, c);

        c.insets=new Insets(0,0,0,0);
        c.gridy=1;
        c.weighty=1;
        this.add(dimSlider, c);

        c.gridy=2;
        c.weighty=1;
        this.add(minesLabel, c);

        c.weighty=1;
        c.gridy=3;
        this.add(minesSlider, c);

        c.insets=new Insets(0,0,50,0);
        c.gridy=4;
        c.gridx=2;
        c.gridwidth=1;
        c.weightx=0.5;
        this.add(setButton, c);

        this.setVisible(true);
    }

    private class MyButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getActionCommand().compareTo("Set")==0){
                Global.getgbl().gridDim=dimSlider.getValue();
                Global.getgbl().bombRate=((double) minesSlider.getValue()/100);
                setVisible(false);
                listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "RESTART"));//Trigger the RESTART event handled in JMines
            }
        }
    }
}

