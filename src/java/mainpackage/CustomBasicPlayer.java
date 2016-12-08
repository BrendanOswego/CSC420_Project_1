package mainpackage;

import com.sun.media.jfxmedia.events.PlayerStateEvent;
import com.sun.media.jfxmedia.logging.Logger;
import javazoom.jlgui.basicplayer.*;
import javazoom.spi.PropertiesContainer;
import org.tritonus.share.sampled.TAudioFormat;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.media.Player;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Created by brendan<brendan.goldberg@cainkade.com> on 12/7/16.
 */

public class CustomBasicPlayer {

    private PlayerThread thread;

    private boolean isPlaying = false;
    private MainSwing mainSwing;

    public CustomBasicPlayer(MainSwing mainSwing) {
        this.mainSwing = mainSwing;
    }



    public void play(String fileName) {
        if(thread != null) {
        }

        thread = new PlayerThread(fileName,mainSwing);
        thread.run();
        isPlaying = true;

    }

    public void stop() {
        isPlaying = false;
        thread.stop();

    }

    public void pause() {
        isPlaying = false;
        thread.pause();
    }

    public void resume() {
        isPlaying = true;
        thread.resume();
    }

    private boolean isPlaying() {
        return this.isPlaying;
    }

    public PlayerThread getThread() {
        return this.thread;
    }


}