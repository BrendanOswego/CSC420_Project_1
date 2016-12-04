package mainpackage;

import javax.swing.*;
import java.awt.*;

/**
 * Created by brendan<brendan.goldberg@cainkade.com> on 12/4/16.
 */
public class AlbumPanel extends JPanel {


    public MainSwing mainSwing;
    public CustomJSON json;
    public String albumString;

    public AlbumPanel(MainSwing mainSwing) {
        this.mainSwing = mainSwing;
        this.json = this.mainSwing.getJSON();
        if (json != null) {
            System.out.println("JSON Not null");
        }
        setPreferredSize(new Dimension(100, 100));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (json != null) {
            System.out.println("JSON not null");
            if (json.getAlbumImage(getAlbumString()) != null) {
                System.out.println("JSON Album Image not null");
                g.drawImage(json.getAlbumImage(getAlbumString()), 0, 0,
                        (int) getPreferredSize().getWidth(),
                        (int) getPreferredSize().getHeight(), null);
            }
        }
    }

    public String getAlbumString() {
        return albumString;
    }

    public void setAlbumString(String albumString) {
        this.albumString = albumString;
    }
}
