package main;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;

public class MainSwing {
    private static final File file = new File("src/resources/json/library.json");

    private JTable songTable = new JTable();
    private String[] colNames = {"Song", "Artist", "Album", "Duration"};

    private HashMap<String, Song> songList;

    public static void main(String[] args) {
        new MainSwing().createDesign();
    }


    private void createDesign() {
        JFrame jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        jFrame.setSize(screenSize.width / 2, screenSize.height);

        JPanel infoPanel = new JPanel();
        JPanel mainPanel = new JPanel();
        JPanel westPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel soundControlPanel = new JPanel();

        westPanel.setPreferredSize(new Dimension(120, jFrame.getHeight()));
        infoPanel.setPreferredSize(new Dimension(jFrame.getWidth(), 100));
        mainPanel.setPreferredSize(new Dimension(jFrame.getWidth(), jFrame.getHeight()));
        soundControlPanel.setPreferredSize(new Dimension(100, 10));

        BoxLayout centerLayout = new BoxLayout(centerPanel, BoxLayout.Y_AXIS);
        BorderLayout mainLayout = new BorderLayout();
        FlowLayout playerLayout = new FlowLayout();
        BoxLayout westLayout = new BoxLayout(westPanel, BoxLayout.Y_AXIS);
        BoxLayout verticalLayout = new BoxLayout(infoPanel, BoxLayout.Y_AXIS);

        mainPanel.setLayout(mainLayout);
        soundControlPanel.setLayout(playerLayout);
        westPanel.setLayout(westLayout);
        centerPanel.setLayout(centerLayout);
        infoPanel.setLayout(verticalLayout);

        westPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel libraryHeader = new JLabel("Library");
        JLabel artistHeader = new JLabel("Artist");
        JLabel currentTime = new JLabel("0:00");
        JLabel totalTime = new JLabel("Set Time");
        JLabel title = new JLabel("Title");
        JLabel artist = new JLabel("Artist");
        JSlider musicSlider = new JSlider(JSlider.HORIZONTAL);
        JButton play = new JButton();
        JButton pause = new JButton();
        JButton refresh = new JButton();

        CustomMenuBar topMenu = new CustomMenuBar();

        musicSlider.setValue(0);

        title.setFont(title.getFont().deriveFont(15f));
        artist.setFont(artist.getFont().deriveFont(15f));

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        artist.setAlignmentX(Component.CENTER_ALIGNMENT);
        libraryHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
        artistHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

        createIconPNG(play, "play_button", 20, 20);
        createIconPNG(pause, "pause_button", 20, 20);
        createIconPNG(refresh, "replay_button", 20, 20);

        play.setOpaque(true);
        refresh.setOpaque(true);
        pause.setOpaque(true);

        //This has to be called before the songTable is added to the center panel
        //And anything that changes the songTable information as well i.e changing the name of a song
        initializeJson();

        centerPanel.add(new JScrollPane(songTable));

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

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(westPanel, BorderLayout.LINE_START);

        jFrame.setJMenuBar(topMenu.showMenuBar());

        jFrame.setContentPane(mainPanel);
        jFrame.setVisible(true);

        //loadPlaylistToTable("test");

    }

    private void initializeJson() {

        
        ArrayList<String> jsonIdList = new ArrayList<>();
        songList = new HashMap<>();

        DefaultTableModel dataModel = new DefaultTableModel(colNames, 0);

        JSONParser parser = new JSONParser();
        Song tempSong;
        try {
            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject library = (JSONObject) jsonObject.get("library");
            JSONArray playlistArr = (JSONArray) library.get("playlist");


            for (int i = 0; i < playlistArr.size(); i++) {
                JSONObject playElement = (JSONObject) playlistArr.get(i);
                String playlistName = (String) playElement.get("name");
                if (playlistName.equalsIgnoreCase("default")) {
                    JSONArray songArr = (JSONArray) playElement.get("song");
                    for (int j = 0; j < songArr.size(); j++) {
                        JSONObject songElement = (JSONObject) songArr.get(j);
                        if (songElement != null) {
                            String title = (String) songElement.get("title");
                            String id = (String) songElement.get("id");
                            jsonIdList.add(id);
                            String artist = (String) songElement.get("artist");
                            String duration = (String) songElement.get("duration");
                            tempSong = new Song(id, title, artist, null, duration);
                            songList.put(id, tempSong);
                            Object[] rowObj = {tempSong.getTitle(), tempSong.getArtist(), tempSong.getDuration()};
                            dataModel.addRow(rowObj);
                        }

                    }
                }

            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        //System.out.println(jsonSongList.toString());

        songTable.setFocusable(false);
        songTable.setRowSelectionAllowed(true);
        songTable.setAutoCreateRowSorter(true);
        songTable.setFillsViewportHeight(true);
        songTable.setModel(dataModel);
        songTable.setDefaultRenderer(Object.class, new CustomCellRender());
        songTable.setComponentPopupMenu(showPopupMenu());
    }

    private void loadPlaylistToTable(String name) {

        DefaultTableModel dataModel = new DefaultTableModel(colNames, 0);

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject library = (JSONObject) jsonObject.get("library");
            JSONArray playlistArr = (JSONArray) library.get("playlist");
            for (int i = 0; i < playlistArr.size(); i++) {
                JSONObject playElement = (JSONObject) playlistArr.get(i);
                String playName = (String) playElement.get("name");
                if (playName != null)
                    if (name.equals(playName)) {
                        JSONArray songArr = (JSONArray) playElement.get("song");
                        for (int j = 0; j < songArr.size(); j++) {
                            JSONObject songElement = (JSONObject) songArr.get(j);
                            if (songElement != null) {
                                String id = (String) songElement.get("id");
                                System.out.println(id);
                                //System.out.println(library.toString());
                                if (songList.containsKey(id)) {
                                    Object[] row = {songList.get(id).getTitle(), songList.get(id).getArtist(), songList.get(id).getDuration()};
                                    dataModel.addRow(row);
                                }

                            }
                        }
                    }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }


        songTable.setFocusable(false);
        songTable.setRowSelectionAllowed(true);
        songTable.setAutoCreateRowSorter(true);
        songTable.setFillsViewportHeight(true);
        songTable.setModel(dataModel);
        songTable.setDefaultRenderer(Object.class, new CustomCellRender());
        songTable.setComponentPopupMenu(showPopupMenu());
    }


    void createPlaylist(String name) {
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(new FileReader(file));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = (JSONObject) obj;
        JSONObject library = (JSONObject) jsonObject.get("library");
        JSONArray playArr = (JSONArray) library.get("playlist");

        JSONObject newEntry = new JSONObject();
        newEntry.put("song", new JSONArray());
        newEntry.put("name", name);

        playArr.add(newEntry);

        try {
            System.out.println("Writing to JSON");
            FileWriter writer= new FileWriter(file);
            writer.write(jsonObject.toJSONString());
            writer.flush();
            writer.close();
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Wrote to JSON");
            System.out.println(jsonObject.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }


        //System.out.println(playArr.toString());


    }

    private JPopupMenu showPopupMenu() {
        JPopupMenu optionMenu = new JPopupMenu();
        JMenuItem itemPlay = new JMenuItem("Play");
        JMenuItem itemInfo = new JMenuItem("Get Info");
        JMenuItem itemDelete = new JMenuItem("Delete");

        optionMenu.add(itemPlay);
        optionMenu.add(itemInfo);
        optionMenu.add(itemDelete);
        return optionMenu;
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

    private void createIcon(JLabel label, String name, int width, int height) {
        ImageIcon icon = findImagePath("/images/" + name + ".gif");
        if (icon != null) {
            Image image = icon.getImage();
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaledImage);
            label.setIcon(icon);
        } else {
            System.err.println("GIF " + name + " was not found");
        }

    }

    private void createIconPNG(JButton label, String name, int width, int height) {
        //Uses above method and sets the icon to the local JLabel
        ImageIcon icon = findImagePath("/images/" + name + ".png");
        if (icon != null) {
            Image image = icon.getImage();
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaledImage);
            label.setIcon(icon);
        } else {
            System.err.println("PNG " + name + "  was  not found");
        }

    }


}
