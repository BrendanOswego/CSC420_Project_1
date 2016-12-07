package mainpackage;

import javazoom.jlgui.basicplayer.*;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static javazoom.jlgui.basicplayer.BasicPlayer.*;
import static javazoom.jlgui.basicplayer.BasicPlayerEvent.EOM;

/**
 * Created by brendan<brendan.goldberg@cainkade.com> on 12/7/16.
 */
public class PlayerThread extends BasicPlayer implements BasicPlayerListener, Runnable {

    private BasicPlayer player;
    private BasicController controller;
    private Long totalTime;
    private Long currentTime;
    private int totalMili;
    private int currentMili;
    private MainSwing mainSwing;
    private boolean changingValue = false;
    TimeWorker worker;
    BasicPlayerEvent event;

    public PlayerThread(String fileName, MainSwing mainSwing) {
        this.mainSwing = mainSwing;
        this.player = new BasicPlayer();
        this.controller = (BasicController)player;
        this.player.addBasicPlayerListener(this);
        currentMili = 0;
        currentTime = 0L;
        File f = new File("src/resources/music/" + fileName + ".mp3");
        try {
            controller.open(f);
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            controller.play();
            worker = new TimeWorker();
            worker.execute();
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        try {
            controller.pause();
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            controller.stop();
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resume() {
        try {
            controller.resume();
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void opened(Object o, Map map) {
        this.totalTime = (Long) map.get("duration");
        this.totalMili = (int) (totalTime / 1000);
        mainSwing.getMusicSlider().setMaximum(totalMili);
        System.out.println("Total Time: " + totalTime);
        System.out.println("Properties: " + map.toString());
    }

    @Override
    public void progress(int i, long l, byte[] bytes, Map map) {
        this.currentTime = l;
        this.currentMili = (int) (currentTime / 1000);
    }

    @Override
    public void stateUpdated(BasicPlayerEvent event) {
        this.event = event;
    }

    @Override
    public void setController(BasicController event) {
        this.controller = event;
    }

    public int getMaxInMili() {
        return this.totalMili;
    }

    public int getCurrentMili() {
        return this.currentMili;
    }

    class TimeWorker extends SwingWorker<Integer, Void> {

        @Override
        protected Integer doInBackground() throws Exception {
            if (mainSwing.isPlaying()) {
                while (getCurrentMili() < getMaxInMili() || event.getValue() != BasicPlayerEvent.STOPPED) {
                    System.out.println("Current Time in mili: " + getCurrentMili());
                    mainSwing.getMusicSlider().setValue(getCurrentMili());
                    System.out.println("Music Slider in mili: " + mainSwing.getMusicSlider().getValue());
                    Thread.sleep(1000);
                }
                return null;
            }


            return null;
        }

        @Override
        public void done() {
            System.out.println("Music Player Stopped");
            stop();
        }
    }

    public BasicPlayerEvent getEvent(){
        return this.event;
    }
}
