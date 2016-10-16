package main;


import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by brendan on 9/2/16.
 */
public class MainSwing {

    private JLabel title;
    public JFrame jFrame;
    private JLabel artist;
    private JLabel artwork;
    private JTable songTable = new JTable();
    private JButton play;
    private JButton pause;
    private JButton refresh;
    private JLabel currentTime;
    private JLabel totalTime;
    private ArrayList<String> libraryList;
    private ArrayList<JLabel> songLabels;
    private Map<String, Integer> mapping;
    private Dimension screenSize;
    private String[] colNames = {"Song", "Artist","Album","Duration"};
    private JPopupMenu optionMenu;

    public static void main(String[] args) {
        new MainSwing().createDesign();
    }


    private void createDesign() {
        jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        screenSize = Toolkit.getDefaultToolkit().getScreenSize();


        jFrame.setSize(screenSize.width , screenSize.height);

        JPanel infoPanel = new JPanel();
        JPanel mainPanel = new JPanel();
        JPanel westPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel eastPanel = new JPanel();
        JPanel soundControlPanel = new JPanel();

        JScrollBar scrollBar = new JScrollBar(Adjustable.VERTICAL);

        westPanel.setPreferredSize(new Dimension(120, jFrame.getHeight()));
        eastPanel.setPreferredSize(new Dimension(120, jFrame.getHeight()));
        infoPanel.setPreferredSize(new Dimension(jFrame.getWidth(), 160));
        mainPanel.setPreferredSize(new Dimension(jFrame.getWidth(), jFrame.getHeight()));
        soundControlPanel.setPreferredSize(new Dimension(100, 10));

        BoxLayout centerLayout = new BoxLayout(centerPanel, BoxLayout.Y_AXIS);
        BorderLayout mainLayout = new BorderLayout();
        FlowLayout playerLayout = new FlowLayout();
        BoxLayout westLayout = new BoxLayout(westPanel, BoxLayout.Y_AXIS);
        BoxLayout eastLayout = new BoxLayout(eastPanel, BoxLayout.Y_AXIS);
        BoxLayout verticalLayout = new BoxLayout(infoPanel, BoxLayout.Y_AXIS);

        mainPanel.setLayout(mainLayout);
        soundControlPanel.setLayout(playerLayout);
        westPanel.setLayout(westLayout);
        eastPanel.setLayout(eastLayout);
        centerPanel.setLayout(centerLayout);
        infoPanel.setLayout(verticalLayout);

        JLabel libraryHeader = new JLabel("Library");
        JLabel artistHeader = new JLabel("Artist");

        libraryList = new ArrayList<>();
        libraryList.add("Test");
        libraryList.add("Another One");
        libraryList.add("Song 3");
        libraryList.add("Song 4");
        libraryList.add("Song 5");
        libraryList.add("Song 6");

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(findLibJSON("/json/library.json")));
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } catch (ParseException e){
            e.printStackTrace();
        }


        mapping = new HashMap<String, Integer>();
        DefaultTableModel dataModel = new DefaultTableModel(colNames, 0);

        for (int i = 0; i < libraryList.size(); i++) {
            JLabel label = new JLabel(libraryList.get(i));
            label.setToolTipText(libraryList.get(i));
            mapping.put(label.getText(), i);

            Object[] songRow = {libraryList.get(i), i};
            dataModel.addRow(songRow);

            label.addMouseListener(new MouseListener());

        }
        songTable.setFocusable(false);
        songTable.setRowSelectionAllowed(true);
        songTable.setAutoCreateRowSorter(true);
        songTable.setModel(dataModel);
        songTable.setDefaultRenderer(Object.class,new CustomCellRender());

        setPopupMenu();



        centerPanel.add(new JScrollPane(songTable));

        JSlider musicSlider = new JSlider(JSlider.HORIZONTAL);
        musicSlider.setValue(0);
        play = new JButton();
        pause = new JButton();
        refresh = new JButton();

        currentTime = new JLabel("0:00");
        totalTime = new JLabel("Set Time");

        title = new JLabel("Title");
        artist = new JLabel("Artist");

        artwork = new JLabel();
        title.setFont(title.getFont().deriveFont(15f));
        artist.setFont(artist.getFont().deriveFont(15f));

        artwork.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        westPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        eastPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //infoPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        artist.setAlignmentX(Component.CENTER_ALIGNMENT);
        artwork.setAlignmentX(Component.CENTER_ALIGNMENT);
        libraryHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        artistHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        createIcon(artwork, "oswego_icon", 60, 60);
        createIconPNG(play, "play_button", 20, 20);
        createIconPNG(pause, "pause_button", 20, 20);
        createIconPNG(refresh, "replay_button", 20, 20);

        play.setOpaque(true);
        refresh.setOpaque(true);
        pause.setOpaque(true);

        soundControlPanel.add(play);
        soundControlPanel.add(currentTime);
        soundControlPanel.add(musicSlider);
        soundControlPanel.add(totalTime);
        soundControlPanel.add(pause);
        soundControlPanel.add(refresh);

        infoPanel.add(title);
        infoPanel.add(artist);
        infoPanel.add(soundControlPanel);

        westPanel.add(libraryHeader);
        eastPanel.add(artistHeader);

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(westPanel, BorderLayout.LINE_START);
        mainPanel.add(eastPanel, BorderLayout.LINE_END);

        jFrame.setContentPane(mainPanel);
        jFrame.setVisible(true);

    }

    private void setUpLibrary() {

    }

    private void setPopupMenu(){
        optionMenu = new JPopupMenu();
        optionMenu.add("Play");
        optionMenu.add("Get Info");
        optionMenu.add("Copy");
        optionMenu.add("Delete");
    }


    private ImageIcon findImagePath(String path) {

        URL imgUrl = MainSwing.class.getResource(path);
        if (imgUrl != null) {
            return new ImageIcon(imgUrl);
        } else {
            System.err.println("No content found");
            return null;
        }

    }

    private File findLibJSON(String path){

        URL jsonUrl = MainSwing.class.getResource(path);
        if(jsonUrl != null){
            return new File(String.valueOf(jsonUrl));
        }else {
            System.err.println("JSON Not Fount");
            return null;
        }

    }


    private void createIcon(JLabel label, String name, int width, int height) {
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

    private void createIconPNG(JButton label, String name, int width, int height) {
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

    class MouseListener implements java.awt.event.MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (jFrame != null) {
                if (System.getProperty("os.name").contains("Mac OS X")) {
                    if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0 && (e.getModifiers() & InputEvent.CTRL_MASK) != 0) {
                        JOptionPane.showMessageDialog(jFrame, "Test Right" + mapping.toString());
                    } else if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
                        JOptionPane.showMessageDialog(jFrame, "Test Left");
                    }
                } else {
                    if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
                        if (e.getClickCount() == 2) {

                        }
                        JOptionPane.showMessageDialog(jFrame, "Test Left" + mapping.toString());
                    } else if ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) {
                        JOptionPane.showMessageDialog(jFrame, "Test Right");
                    }
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }


}
