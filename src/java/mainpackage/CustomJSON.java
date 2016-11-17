package mainpackage;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by brendan<brendan.goldberg@cainkade.com> on 11/15/16.
 */
public class CustomJSON {

    private static final File jsonFile = new File("src/resources/json/library.json");

    private String[] colNames = {"Song", "Artist", "Duration"};



    private boolean isPlaying = false;

    private JTable songTable;
    private HashMap<String, Song> songList;
    private List<Playlist> playlists = new ArrayList<>();
    private JScrollPane scrollPane;
    private ArrayList<String> playlistNames;
    private DefaultListModel<String> libraryModel = new DefaultListModel<>();
    private JList<String> list;
    private JPanel libraryPanel;
    private JScrollPane playScroll;

    private int MAX_ID = 0;

    private MusicPlayer player;

    public CustomJSON(JTable songTable, JScrollPane scrollPane, JPanel libraryPanel, ArrayList<String> playlistNames) {
        this.libraryPanel = libraryPanel;
        this.songTable = songTable;
        this.scrollPane = scrollPane;
        this.playlistNames = playlistNames;

    }

    public void setupTableMethods(TableModel dataModel) {
        TableModel tableModel = dataModel;
        songTable.setDragEnabled(true);
        songTable.setFocusable(true);
        songTable.setRowSelectionAllowed(true);
        songTable.setAutoCreateRowSorter(true);
        songTable.setFillsViewportHeight(true);
        songTable.setModel(tableModel);
        songTable.setDefaultRenderer(Object.class, new CustomCellRender());
        songTable.setComponentPopupMenu(showPopupMenu());
        songTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void initializeJson() {

        ArrayList<String> jsonIdList = new ArrayList<>();
        songList = new HashMap<>();

        TableModel dataModel = new TableModel(colNames, 0);

        JSONParser parser = new JSONParser();
        Song tempSong;
        try {
            Object obj = parser.parse(new FileReader(jsonFile));
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
                            Playlist tempPlaylist = new Playlist(playlistName, songList);
                            playlists.add(tempPlaylist);
                            Object[] rowObj = {tempSong.getTitle(), tempSong.getArtist(), tempSong.getDuration()};
                            dataModel.addRow(rowObj);
                            scrollPane.getViewport().revalidate();
                            MAX_ID++;
                        }

                    }
                }

            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        setupTableMethods(dataModel);
        //fillEmptyRows();
    }

    public void addSong(String name) {
        MAX_ID++;
        try {
            Mp3File mp3 = new Mp3File("src/resources/music/" + name);
            FileInputStream fileInputStream = new FileInputStream("src/resources/music/" + name);
            player = new MusicPlayer(fileInputStream);

            String fileName = FilenameUtils.removeExtension(name);
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader(jsonFile));

            JSONObject jsonObject = (JSONObject) obj;
            JSONObject library = (JSONObject) jsonObject.get("library");
            JSONArray playArr = (JSONArray) library.get("playlist");
            JSONObject playElement = (JSONObject) playArr.get(0);
            String playlistName = (String) playElement.get("name");
            if (playlistName.equalsIgnoreCase("default")) {
                JSONArray songArr = (JSONArray) playElement.get("song");
                JSONObject newEntry = new JSONObject();
                newEntry.put("title", fileName);
                newEntry.put("id", Integer.toString(MAX_ID));
                newEntry.put("duration", player.getDuration(name));

                if (mp3.hasId3v1Tag()) {
                    newEntry.put("artist", mp3.getId3v1Tag().getArtist());
                    songArr.add(newEntry);

                } else if (mp3.hasId3v2Tag()) {
                    newEntry.put("artist", mp3.getId3v2Tag().getArtist());
                    songArr.add(newEntry);
                } else {
                    System.err.println("File is not MP3 Format");
                }
            }


            System.out.println("Writing to JSON");
            FileWriter writer = new FileWriter(jsonFile);
            writer.write(jsonObject.toJSONString());
            writer.flush();
            writer.close();
            Thread.sleep(40);
        } catch (InvalidDataException | UnsupportedTagException | IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        initializeJson();
    }


    public void loadPlaylistToTable(String name) {

        TableModel dataModel = new TableModel(colNames, 0);

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(jsonFile));
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
                                if (songList.containsKey(id)) {
                                    Object[] row = {songList.get(id).getTitle(), songList.get(id).getArtist(), songList.get(id).getDuration()};
                                    dataModel.addRow(row);
                                    scrollPane.getViewport().revalidate();
                                }

                            }
                        }
                    }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        setupTableMethods(dataModel);
       // fillEmptyRows();
    }

    public void initializeAddedPlaylists() {
        playlistNames = new ArrayList<>();

        JSONParser parser = new JSONParser();
        System.out.println("Initialized loading playlist names");
        try {
            Object obj = parser.parse(new FileReader(jsonFile));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject library = (JSONObject) jsonObject.get("library");
            JSONArray playlistArr = (JSONArray) library.get("playlist");
            for (int i = 0; i < playlistArr.size(); i++) {
                JSONObject playElement = (JSONObject) playlistArr.get(i);
                String playName = (String) playElement.get("name");
                if (playName != null) {
                    playlistNames.add(playName);
                }

            }
            System.out.println("Playlists added: " + playlistNames.toString());

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }


    }

    public void addSelectedSongToPlaylist(Song song, String playlistName) {
        //TODO- Create TransferHandler, i.e DnD functionality
    }


    public void createPlaylist(String name) {
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(new FileReader(jsonFile));

            JSONObject jsonObject = (JSONObject) obj;
            JSONObject library = (JSONObject) jsonObject.get("library");
            JSONArray playArr = (JSONArray) library.get("playlist");

            JSONObject newEntry = new JSONObject();
            newEntry.put("song", new JSONArray());
            newEntry.put("name", name);
            playArr.add(newEntry);


            System.out.println("Writing to JSON");
            FileWriter writer = new FileWriter(jsonFile);
            writer.write(jsonObject.toJSONString());
            writer.flush();
            writer.close();

            Thread.sleep(40);

            System.out.println("Wrote to JSON");
            System.out.println(jsonObject.toString());

        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }

    }


    public void loadPlaylistsToPanel() {
        JSONParser parser = new JSONParser();
        System.out.println("Initialized loading playlist names");
        if (list == null) {
            list = new JList<>(libraryModel);
            playScroll = new JScrollPane(list);
            list.setFont(list.getFont().deriveFont(15f));
            list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            list.setLayoutOrientation(JList.VERTICAL);
            list.setVisibleRowCount(-1);
        } else {
            list.removeAll();
            libraryModel.removeAllElements();
        }
        try {
            Object obj = parser.parse(new FileReader(jsonFile));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject library = (JSONObject) jsonObject.get("library");
            JSONArray playlistArr = (JSONArray) library.get("playlist");
            for (int i = 0; i < playlistArr.size(); i++) {
                JSONObject playElement = (JSONObject) playlistArr.get(i);
                String playName = (String) playElement.get("name");
                JButton jButton;
                if (playName.equals("default")) {
                    jButton = new JButton("All Songs");
                } else {
                    jButton = new JButton(playName);
                }
                libraryModel.addElement(jButton.getText());
                list.addMouseListener(mouseListener);
                if ((int) jButton.getSize().getWidth() > (int) libraryPanel.getSize().getWidth()) {
                    list.setFont(list.getFont().deriveFont(10f));
                }
            }
            libraryPanel.add(playScroll);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    public JPopupMenu showPopupMenu() {
        JPopupMenu optionMenu = new JPopupMenu();
        JMenuItem itemPlay = new JMenuItem("Play");
        optionMenu.add(itemPlay);
        JMenuItem itemEdit = new JMenuItem("Edit");
        itemEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(songTable.getSelectedRow() + " " + songTable.getSelectedColumn());

                //TODO Add a new Frame that shows all the information and once the user presses enter it edits the info for that row
            }
        });
        optionMenu.add(itemEdit);
        JMenuItem itemAdd = new JMenuItem("Add To Playlist");
        optionMenu.add(itemAdd);
        JMenu subMenu = new JMenu();
        for (int i = 0; i < 4; i++) {
            JMenuItem item = new JMenuItem(String.valueOf(i));
            subMenu.add(item);
        }
        JMenuItem itemInfo = new JMenuItem("Get Info");
        optionMenu.add(itemInfo);
        JMenuItem itemDelete = new JMenuItem("Delete");
        optionMenu.add(itemDelete);

        return optionMenu;
    }

    public void fillEmptyRows() {
        int rows = songTable.getRowCount();
        int rowHeight = songTable.getRowHeight();
        int tableHeight = songTable.getTableHeader().getHeight() + (rows * rowHeight);
        while (tableHeight < scrollPane.getViewport().getHeight()) {
            ((DefaultTableModel) songTable.getModel()).addRow(new Object[]{null, null, null});
            tableHeight += rowHeight;
        }
    }

    public MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            JList theList = (JList) e.getSource();
            if (e.getClickCount() == 2) {
                int index = theList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    String name = (String) theList.getModel().getElementAt(index);
                    if (name.equals("All Songs")) {
                        loadPlaylistToTable("default");
                    } else {
                        loadPlaylistToTable(name);
                    }
                }
                //fillEmptyRows();
            }
        }
    };


    public ArrayList<String> getPlaylistNames() {
        return playlistNames;
    }


}
