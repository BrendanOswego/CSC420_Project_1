package mainpackage;

import javazoom.jlgui.basicplayer.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
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

    private BasicController controller;
    MainSwing mainSwing;
    private int totalBytes;
    private int currentBytes;
    BasicPlayer player;
    TimeWorker worker;
    BasicPlayerEvent event;
    private int m_status = -1;
    private AudioInputStream m_audioInputStream;
    private File m_dataSource;
    public boolean test = false;

    public PlayerThread(String fileName, MainSwing mainSwing) {
        this.mainSwing = mainSwing;
        player = new BasicPlayer();
        controller = (BasicController) player;
        player.addBasicPlayerListener(this);
        File file = new File("src/resources/music/" + fileName + ".mp3");
        m_dataSource = file;
        try {
            m_audioInputStream = AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        try {
            controller.open(file);
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            controller.play();
            //player.play();
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
            worker.cancel(true);
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
    public long seek(long k) {
        try {
            return controller.seek(k);
        } catch (BasicPlayerException e) {
            e.printStackTrace();
        }
        return -1;
    }


    @Override
    public void opened(Object o, Map map) {
        totalBytes = (int) map.get("mp3.length.bytes");
        System.out.println("Total Bytes: " + totalBytes);
        mainSwing.getMusicSlider().setMaximum(totalBytes);
        System.out.println("Properties: " + map.toString());
    }

    @Override
    public void progress(int i, long l, byte[] bytes, Map map) {
        this.currentBytes = i;
    }

    @Override
    public long skipBytes(long k) {
        return this.skipBytes(k);
    }


    @Override
    public void stateUpdated(BasicPlayerEvent event) {
        this.event = event;
    }

    @Override
    public void setController(BasicController event) {
        this.controller = event;
    }

    class TimeWorker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws Exception {
            while (currentBytes < totalBytes) {
                System.out.println("Current Time in bytes: " + currentBytes);
                mainSwing.getMusicSlider().setValue(currentBytes);
                Thread.sleep(1000);
            }

            return null;
        }

        @Override
        public void done() {
            System.out.println("Music Player Stopped");
            if (currentBytes < totalBytes) {
                stop();
            } else {
                mainSwing.playNextSong();
            }
        }
    }

    public BasicPlayerEvent getEvent() {
        return this.event;
    }


}
