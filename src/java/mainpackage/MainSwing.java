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

    private JLabel title;
    private JLabel artist;
    private JLabel artwork;
    private JLabel play;
    private JLabel pause;
    private JLabel refresh;

    public MainSwing() {
        createDesign();
    }

    public static void main(String[] args) {
        MainSwing swing = new MainSwing();
    }

    private void createDesign(){
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(600,600);

        JPanel infoPanel = new JPanel();
        JPanel mainPanel = new JPanel();
        JPanel soundControlPanel = new JPanel();
        JSlider musicSlider = new JSlider(JSlider.HORIZONTAL);
        play = new JLabel();
        pause = new JLabel();
        refresh = new JLabel();


        BoxLayout verticalLayout = new BoxLayout(infoPanel,BoxLayout.Y_AXIS);
        BorderLayout mainLayout = new BorderLayout();
        FlowLayout playerLayout = new FlowLayout();

        infoPanel.setLayout(verticalLayout);
        infoPanel.setPreferredSize(new Dimension(jFrame.getWidth(),150));
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        mainPanel.setLayout(mainLayout);
        mainPanel.setSize(jFrame.getWidth(),jFrame.getHeight());
        mainPanel.setPreferredSize(mainPanel.getPreferredSize());

        soundControlPanel.setLayout(playerLayout);
        soundControlPanel.setSize(600,20);
        soundControlPanel.setPreferredSize(soundControlPanel.getSize());

        title = new JLabel("Title");
        artist = new JLabel("Artist");

        artwork = new JLabel();
        title.setFont(title.getFont().deriveFont(15f));
        artist.setFont(artist.getFont().deriveFont(15f));
        artist.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        artwork.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        refresh.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        artist.setAlignmentX(Component.CENTER_ALIGNMENT);
        artwork.setAlignmentX(Component.CENTER_ALIGNMENT);

        createIcon(artwork,"oswego_icon",70,70);
        createIconPNG(play,"play_button",20,20);
        createIconPNG(pause,"pause_button",20,20);
        createIconPNG(refresh,"replay_button",20,20);


        infoPanel.add(artwork);
        infoPanel.add(title);
        infoPanel.add(artist);

        soundControlPanel.add(play);
        soundControlPanel.add(musicSlider);
        soundControlPanel.add(pause);
        soundControlPanel.add(refresh);

        mainPanel.add(infoPanel,BorderLayout.NORTH);
        mainPanel.add(soundControlPanel,BorderLayout.CENTER);

        jFrame.setContentPane(mainPanel);
        jFrame.setVisible(true);

    }

    private ImageIcon findImagePath(String path) {
        //Relative URL path to project Resource Folder created
        //Creates an ImageIcon out of the URL declared
        URL imgUrl = MainSwing.class.getResource(path);
        if (imgUrl != null) {
            return new ImageIcon(imgUrl);
        } else {
            System.err.println("No content found");
            return null;
        }

    }

    private void createIcon(JLabel label,String name,int width,int height) {
        //Uses above method and sets the icon to the local JLabel
        ImageIcon icon = findImagePath("/images/" + name + ".gif");
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(width,height,Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImage);
        label.setIcon(icon);
        if (icon != null) {
            System.out.println("Image Found");
        } else {
            System.out.println("Image not found");
        }
    }
    private void createIconPNG(JLabel label,String name,int width,int height) {
        //Uses above method and sets the icon to the local JLabel
        ImageIcon icon = findImagePath("/images/" + name + ".png");
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(width,height,Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImage);
        label.setIcon(icon);
        if (icon != null) {
            System.out.println("Image Found");
        } else {
            System.out.println("Image not found");
        }
    }






}
