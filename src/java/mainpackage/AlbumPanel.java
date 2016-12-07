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
    public boolean hasImage = false;

    public AlbumPanel(MainSwing mainSwing) {
        this.mainSwing = mainSwing;
        this.json = this.mainSwing.getJSON();

        setSize(new Dimension(160, 140));
        setMinimumSize(new Dimension(180,140));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(hasImage()) {
            if (json != null) {
                if (json.getAlbumImage(getAlbumString()) != null) {
                    g.drawImage(json.getAlbumImage(getAlbumString()), 0, 0,
                            (int) getMinimumSize().getWidth(),
                            (int) getMinimumSize().getHeight(), null);
                }
            }
        }else {
            removeAll();
            updateUI();
        }
    }

    public String getAlbumString() {
        return albumString;
    }

    public void setAlbumString(String albumString) {
        this.albumString = albumString;
    }

    public void setHasImage(boolean hasImage){
        this.hasImage = hasImage;
    }

    public boolean hasImage(){
        return  this.hasImage;
    }


}
