package mainpackage;

import sun.jvm.hotspot.utilities.WorkerThread;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

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
    private JLabel currentTime;
    private JLabel totalTime;
    private ArrayList<String> libraryList;
    private ArrayList<JLabel> songLabels;


    public MainSwing() {
        createDesign();
    }

    public static void main(String[] args) {
        MainSwing swing = new MainSwing();
    }

    private void createDesign() {
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(700, 700);

        JPanel infoPanel = new JPanel();
        JPanel mainPanel = new JPanel();
        JPanel westPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension(100, 100));
        westPanel.setPreferredSize(new Dimension(120, jFrame.getHeight()));
        JPanel eastPanel = new JPanel();
        eastPanel.setPreferredSize(new Dimension(120, jFrame.getHeight()));
        JPanel soundControlPanel = new JPanel();

        JLabel libraryHeader = new JLabel("Library");
        JLabel artistHeader = new JLabel("Artist");

        libraryList = new ArrayList<>();
        libraryList.add("Test");
        libraryList.add("Another One");
        libraryList.add("Damn Daniel");
        for (String aLibraryList : libraryList) {
            JLabel label = new JLabel(aLibraryList);
            label.setBorder(BorderFactory.createEmptyBorder(2,0,2,0));
            centerPanel.add(label);
            centerPanel.revalidate();
            centerPanel.repaint();
        }
        libraryHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        artistHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSlider musicSlider = new JSlider(JSlider.HORIZONTAL);
        musicSlider.setValue(0);
        play = new JLabel();
        pause = new JLabel();
        refresh = new JLabel();

        currentTime = new JLabel("0:00");
        totalTime = new JLabel("Set Time");

        BoxLayout centerLayout = new BoxLayout(centerPanel, BoxLayout.Y_AXIS);
        BoxLayout verticalLayout = new BoxLayout(infoPanel, BoxLayout.Y_AXIS);
        BorderLayout mainLayout = new BorderLayout();
        FlowLayout playerLayout = new FlowLayout();
        BoxLayout westLayout = new BoxLayout(westPanel, BoxLayout.Y_AXIS);
        BoxLayout eastLayout = new BoxLayout(eastPanel, BoxLayout.Y_AXIS);
        infoPanel.setLayout(verticalLayout);
        infoPanel.setPreferredSize(new Dimension(jFrame.getWidth(), 140));
        //infoPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        mainPanel.setLayout(mainLayout);
        mainPanel.setPreferredSize(new Dimension(jFrame.getWidth(), jFrame.getHeight()));

        soundControlPanel.setLayout(playerLayout);
        soundControlPanel.setPreferredSize(new Dimension(100, 10));

        westPanel.setLayout(westLayout);
        eastPanel.setLayout(eastLayout);
        centerPanel.setLayout(centerLayout);

        title = new JLabel("Title");
        artist = new JLabel("Artist");

        artwork = new JLabel();
        title.setFont(title.getFont().deriveFont(15f));
        artist.setFont(artist.getFont().deriveFont(15f));

        artwork.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        //westPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //eastPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        artist.setAlignmentX(Component.CENTER_ALIGNMENT);
        artwork.setAlignmentX(Component.CENTER_ALIGNMENT);

        createIcon(artwork, "oswego_icon", 50, 50);
        createIconPNG(play, "play_button", 20, 20);
        createIconPNG(pause, "pause_button", 20, 20);
        createIconPNG(refresh, "replay_button", 20, 20);

        infoPanel.add(artwork);
        infoPanel.add(title);
        infoPanel.add(artist);

        soundControlPanel.add(play);
        soundControlPanel.add(currentTime);
        soundControlPanel.add(musicSlider);
        soundControlPanel.add(totalTime);
        soundControlPanel.add(pause);
        soundControlPanel.add(refresh);

        infoPanel.add(soundControlPanel);

        westPanel.add(libraryHeader);
        eastPanel.add(artistHeader);

        centerPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(westPanel, BorderLayout.LINE_START);
        mainPanel.add(eastPanel, BorderLayout.LINE_END);

        jFrame.setContentPane(mainPanel);
        jFrame.setVisible(true);

    }

    private void setUpLibrary() {

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

    private void createIcon(JLabel label, String name, int width, int height) {
        //Uses above method and sets the icon to the local JLabel
        ImageIcon icon = findImagePath("/images/" + name + ".gif");
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImage);
        label.setIcon(icon);
        if (icon != null) {
            System.out.println("Image Found");
        } else {
            System.out.println("Image not found");
        }
    }

    private void createIconPNG(JLabel label, String name, int width, int height) {
        //Uses above method and sets the icon to the local JLabel
        ImageIcon icon = findImagePath("/images/" + name + ".png");
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImage);
        label.setIcon(icon);
        if (icon != null) {
            System.out.println("Image Found");
        } else {
            System.out.println("Image not found");
        }
    }


    public void setTitle(String title) {
        this.title.setText(title);
    }


}
