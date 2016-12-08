package mainpackage;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
//import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by brendan<brendan.goldberg@cainkade.com> on 11/10/16.
 */
public class MusicPlayer {

    private final static int NOTSTARTED = 0;
    private final static int PLAYING = 1;
    private final static int PAUSED = 2;
    private final static int FINISHED = 3;

    private final FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("MP3 Files", "mp3");

    private AdvancedPlayer player;


    public MusicPlayer(InputStream stream) {
        try {
            player = new AdvancedPlayer(stream);
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
    }

    String getDuration(String name){
        File f = new File("src/resources/music/" + name);
        AudioFileFormat baseFileFormat= null;// = new MpegAudioFileReader().getAudioFileFormat(f);
        Map properties = baseFileFormat.properties();
        System.out.println(properties.toString());
        Long microseconds = (Long) properties.get("duration");
        int mili = (int) (microseconds / 1000);
        int sec = (mili / 1000) % 60;
        int min = (mili / 1000) / 60;
        return String.format("%d:%02d", min, sec);
    }


    public  AdvancedPlayer playMp3(final InputStream is, final int start, final int end, PlaybackListener listener) {
        final AdvancedPlayer player;
        try {
            player = new AdvancedPlayer(is);
            player.setPlayBackListener(listener);
            // run in new thread
            new Thread() {
                public void run() {
                    try {
                        player.play(start, end);
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }.start();
            return player;
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void play(String filename) {
        InfoListener lst = new InfoListener();
        playMp3(new File(filename), lst);
    }

    public AdvancedPlayer playMp3(File mp3, PlaybackListener listener) {
        try {
            return playMp3(mp3, 0, Integer.MAX_VALUE, listener);
        } catch (IOException | JavaLayerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AdvancedPlayer playMp3(File mp3, int start, int end, PlaybackListener listener) throws IOException, JavaLayerException {
        return playMp3(new BufferedInputStream(new FileInputStream(mp3)), start, end, listener);
    }

    public class InfoListener extends PlaybackListener {
        public void playbackStarted(PlaybackEvent evt) {
            System.out.println("Play started from frame " + evt.getFrame());
        }

        public void playbackFinished(PlaybackEvent evt) {
            System.out.println("Play completed at frame " + evt.getFrame());
            System.exit(0);
        }
    }


}

