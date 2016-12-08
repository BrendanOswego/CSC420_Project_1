package mainpackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by snoop_000 on 12/8/2016.
 */
public class OpaqueInfoPanel {
    JFrame f;

    OpaqueInfoPanel(String s, String ar, String al){
        f= new JFrame();
        f.setSize(200,100);
        JPanel view = new JPanel();

        JLabel song = new JLabel("SONG: "+s);
        song.setForeground(Color.WHITE);
        JLabel artist = new JLabel("ARTIST: "+ar);
        artist.setForeground(Color.WHITE);
        JLabel album = new JLabel("Album: " +al);
        album.setForeground(Color.WHITE);

        view.add(song);
        view.add(artist);
        view.add(album);
        view.setOpaque(false);
        f.setUndecorated(true);
        f.setBackground(new Color(.0f,.0f,.0f,.5f));
        f.add(view);
        Timer timer = new Timer(5000, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                f.dispose();
            }
        });

        timer.start();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        int x = (int) rect.getMaxX() - f.getWidth();
        int y = 0;
        f.setLocation(x, y);
        f.setVisible(true);
    }



}
