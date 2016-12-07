package mainpackage;

import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.FileInputStream;

/**
 * Created by brendan<brendan.goldberg@cainkade.com> on 12/7/16.
 */
public class NewAudioPlayer extends Thread {

    private String fileLocation;
    private boolean loop;
    private AdvancedPlayer prehravac;



    public NewAudioPlayer(String fileLocation, boolean loop) {
        this.fileLocation = fileLocation;
        this.loop = loop;
    }

    public void run() {

        try {
            do {
                FileInputStream buff = new FileInputStream(fileLocation);
                prehravac = new AdvancedPlayer(buff);
                prehravac.play();

            } while (loop);
        } catch (Exception ioe) {
            // TODO error handling
        }
    }


    public void playFrom(int start) {
        try {
            do {
                FileInputStream buff = new FileInputStream(fileLocation);
                prehravac = new AdvancedPlayer(buff);
                prehravac.play(start, Integer.MAX_VALUE);
            } while (loop);
        } catch (Exception ioe) {
            // TODO error handling
        }
    }

    public void close() {
        loop = false;
        prehravac.close();
        this.interrupt();
    }

}
