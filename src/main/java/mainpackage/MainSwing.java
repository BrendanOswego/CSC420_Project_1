package mainpackage;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Created by brendan on 9/2/16.
 */
public class MainSwing {
    private JPanel MainPanel;
    private JPanel TitlePanel;
    private JLabel title;
    private JLabel artist;


    public MainSwing() {



    }

    public static void main(String[] args) {
        MainSwing swing = new MainSwing();
        JFrame jFrame = new JFrame("MainSwing");
        jFrame.setContentPane(new MainSwing().TitlePanel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(400, 200);
        swing.setImage();
        jFrame.setVisible(true);
    }

    protected ImageIcon createImageIcon(String path) {
        URL imageUrl = getClass().getResource(path);

        if (imageUrl != null) {
            System.out.println("Content Found");
            return new ImageIcon(path);
        } else {
            //Doesn't create NullPointerException if image URL is wrong
            System.out.println("No Content Found");
            return null;
        }

    }

    public void setImage(){
        //FIXME- Check why image is not loading, even though content (image file) is found
        ImageIcon icon = createImageIcon("/images/oswego_icon.gif");

        title.setIcon(icon);

    }



}
