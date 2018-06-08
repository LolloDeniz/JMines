import java.util.concurrent.TimeUnit;

import javax.swing.*;

public class MinesTimer {

    private JLabel label;
    private long time;
    private Thread Update = null;

    public MinesTimer(JLabel label) {
        this.label = label;
        label.setHorizontalAlignment(JLabel.CENTER);
        this.setTimer(0);
    }

    public void startTimer() {
        this.stopTimer();
        Update = new Thread(new TimerUpdater(this));
        Update.start();
    }

    public void stopTimer() {
        if (Update != null && Update.isAlive()) {
            Update.interrupt();
        }
    }

    public void setTimer(long sec) {
        this.time = sec;

        //corversion
        int hh = (int) (this.time / 3600);
        int mm = (int) (this.time - hh * 3600) / 60;
        int ss = (int) (this.time - hh * 3600 - mm * 60);
        label.setText((hh / 10) + "" + hh % 10 + ":" + mm / 10 + "" + mm % 10 + ":" + ss / 10 + "" + ss % 10);
    }

    public void resetTimer() {
        this.setTimer(0);
    }

    public long getTime() {
        return this.time;
    }

    private class TimerUpdater implements Runnable {
        MinesTimer timer;

        public TimerUpdater(MinesTimer timer) {
            this.timer = timer;
        }

        public void run() {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
                this.timer.setTimer(this.timer.getTime() + 1);
            }
        }
    }

}
