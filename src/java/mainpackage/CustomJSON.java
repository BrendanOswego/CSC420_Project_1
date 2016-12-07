package mainpackage;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javazoom.jl.decoder.JavaLayerException;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by brendan<brendan.goldberg@cainkade.com> on 11/15/16.
 */
public class CustomJSON {

    private static final File jsonFile = new File("src/resources/json/library.json");

    private String[] colNames = {"Song", "Artist", "Album", "Duration"};


    private boolean isPlaying = false;

    private JTable songTable;
    private HashMap<String, Song> songList;
    private List<Playlist> playlists = new ArrayList<>();
    private JScrollPane scrollPane;
    private ArrayList<String> playlistNames = new ArrayList<>();
    private DefaultListModel<String> libraryModel = new DefaultListModel<>();
    private JList<String> list;
    private JPanel libraryPanel;
    private JScrollPane playScroll;
    private List<Integer> idList = new ArrayList<>();

    List<String> albumImages = new ArrayList<>();
    TableRowSorter<TableModel> rowSorter;
    private List<Song> listOfSongs;

    private MusicPlayer player;
    TableModel tableModel = new TableModel(colNames, 0);
    MainSwing mainSwing;
    private ArtistView AV;

    public CustomJSON(JTable songTable, JScrollPane scrollPane, JPanel libraryPanel, ArrayList<String> playlistNames, MainSwing mainSwing, TableRowSorter<TableModel> rowSorter) {
        this.libraryPanel = libraryPanel;
        this.songTable = songTable;
        this.scrollPane = scrollPane;
        this.playlistNames = playlistNames;
        this.mainSwing = mainSwing;
        this.rowSorter = rowSorter;

    }
    public void setupTableMethods() {
        songTable.setDragEnabled(true);
        songTable.setFocusable(true);
        songTable.setRowSelectionAllowed(true);
        songTable.setFillsViewportHeight(true);
        songTable.setAutoCreateRowSorter(true);
        songTable.setDefaultRenderer(Object.class, new CustomCellRender());
        songTable.setComponentPopupMenu(showPopupMenu());
        songTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        songTable.setModel(tableModel);

    }

    public void setupAV(ArtistView AV) {
        this.AV = AV;
    }

    public void initializeJson() {

        ArrayList<String> jsonIdList = new ArrayList<>();
        songList = new HashMap<>();
        listOfSongs = new ArrayList<>();

        tableModel.getDataVector().removeAllElements();

        String playlistName = null;
        JSONParser parser = new JSONParser();
        Song tempSong;
        try {
            Object obj = parser.parse(new FileReader(jsonFile));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject library = (JSONObject) jsonObject.get("library");
            JSONArray playlistArr = (JSONArray) library.get("playlist");

            for (int i = 0; i < playlistArr.size(); i++) {
                JSONObject playElement = (JSONObject) playlistArr.get(i);
                playlistName = (String) playElement.get("name");
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
                            String album = (String) songElement.get("album");
                            try {
                                Mp3File mp3 = new Mp3File("src/resources/music/" + title + ".mp3");
                                if (mp3.hasId3v2Tag()) {
                                    byte[] imageData = mp3.getId3v2Tag().getAlbumImage();
                                    if (imageData != null) {
                                        //System.out.println(mp3.getId3v2Tag().getAlbum());
                                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));
                                        File imageOutput = new File("src/resources/images/albums/" + mp3.getId3v2Tag().getAlbum() + ".png");
                                        String albumName = mp3.getId3v2Tag().getAlbum();
                                        if (!albumImages.contains(albumName)) {
                                            albumImages.add(albumName);
                                        }
                                        if (!imageOutput.exists()) {
                                            ImageIO.write(img, "jpg", imageOutput);
                                        }
                                    }
                                }
                            } catch (UnsupportedTagException | InvalidDataException e) {
                                e.printStackTrace();
                            }
                            tempSong = new Song(id, title, artist, album, duration);
                            listOfSongs.add(tempSong);
                            songList.put(id, tempSong);
                            Playlist tempPlaylist = new Playlist(playlistName, songList);
                            playlists.add(tempPlaylist);
                            if (!Objects.equals(tempSong.getAlbum(), null)) {
                                Object[] rowObj = {tempSong.getTitle(), tempSong.getArtist(), tempSong.getAlbum(), tempSong.getDuration()};
                                tableModel.addRow(rowObj);
                            } else {
                                Object[] rowObj = {tempSong.getTitle(), tempSong.getArtist(), "", tempSong.getDuration()};
                                tableModel.addRow(rowObj);
                            }

                            scrollPane.getViewport().revalidate();

                        }

                    }
                }

            }

            if (!playlistNames.contains(playlistName)) {
                playlistNames.add(playlistName);

            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        setupTableMethods();
        AV.updateListOfSongs(listOfSongs, this);
        //fillEmptyRows();
    }

    public void addSong(String name) {
        Random r = new Random();

        int id = r.nextInt(Integer.MAX_VALUE);
        while (idList.contains(id)) {
            id = r.nextInt();
        }
        idList.add(id);
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
                newEntry.put("id", Integer.toString(id));
                newEntry.put("duration", player.getDuration(name));
                if (mp3.hasId3v2Tag()) {
                    if (mp3.getId3v2Tag().getArtist() != null) {
                        newEntry.put("artist", mp3.getId3v2Tag().getArtist());
                    } else {
                        newEntry.put("artist", "");
                    }
                    byte[] imageData = mp3.getId3v2Tag().getAlbumImage();
                    if (imageData != null) {
                        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));
                        File imageOutput = new File("src/resources/images/albums/" + mp3.getId3v2Tag().getAlbum() + ".png");
                        System.out.println(imageOutput.getName());
                        ImageIO.write(img, "jpg", imageOutput);
                    }
                    if (mp3.getId3v2Tag().getAlbum() != null) {
                        newEntry.put("album", mp3.getId3v2Tag().getAlbum());
                    } else {
                        newEntry.put("album", "");
                    }

                    songArr.add(newEntry);
                } else if (mp3.hasId3v1Tag()) {
                    if (!mp3.getId3v1Tag().getArtist().isEmpty()) {
                        newEntry.put("artist", mp3.getId3v1Tag().getArtist());
                    } else {
                        newEntry.put("artist", "");
                    }
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

        JSONParser parser = new JSONParser();
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        if (!mainSwing.getSearchField().getText().isEmpty()) {
            mainSwing.getSearchField().setText("");
        }

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
                                    Object[] row = {songList.get(id).getTitle(), songList.get(id).getArtist(), songList.get(id).getAlbum(), songList.get(id).getDuration()};
                                    tableModel.addRow(row);
                                    scrollPane.getViewport().revalidate();
                                }

                            }
                        }
                    }
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        setupTableMethods();

    }

    public void initializeAddedPlaylists() {

        JSONParser parser = new JSONParser();
        System.out.println("Initialized loading playlist names");
        String playName = null;
        try {
            Object obj = parser.parse(new FileReader(jsonFile));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject library = (JSONObject) jsonObject.get("library");
            JSONArray playlistArr = (JSONArray) library.get("playlist");
            for (int i = 0; i < playlistArr.size(); i++) {
                JSONObject playElement = (JSONObject) playlistArr.get(i);
                playName = (String) playElement.get("name");


            }
            if (!playlistNames.contains(playName)) {
                playlistNames.add(playName);
            }
            System.out.println("Playlists added: " + playlistNames.toString());

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }


    }

    public void addSelectedSongToPlaylist(String userPlayName) {
        //TODO- Create TransferHandler, i.e DnD functionality
        JSONParser parser = new JSONParser();
        JSONObject newEntry = new JSONObject();
        try {
            Object obj = parser.parse(new FileReader(jsonFile));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject library = (JSONObject) jsonObject.get("library");
            JSONArray playlistArr = (JSONArray) library.get("playlist");
            for (int i = 0; i < playlistArr.size(); i++) {
                JSONObject playElement = (JSONObject) playlistArr.get(i);
                JSONArray songArr = (JSONArray) playElement.get("song");
                String playName = (String) playElement.get("name");
                for (int j = 0; j < songArr.size(); j++) {
                    JSONObject songElement = (JSONObject) songArr.get(j);
                    int viewRow = songTable.convertRowIndexToView(songTable.getSelectedRow());
                    int modelRow = songTable.convertRowIndexToModel(viewRow);
                    String title = (String) songTable.getValueAt(modelRow, 0);
                    String artist = (String) songTable.getValueAt(modelRow, 1);
                    String album = (String) songTable.getValueAt(modelRow, 2);
                    String duration = (String) songTable.getValueAt(modelRow, 3);
                    if (title.equalsIgnoreCase((String) songElement.get("title")) && artist.equalsIgnoreCase((String) songElement.get("artist"))) {
                        String id = (String) songElement.get("id");
                        newEntry.put("id", id);
                    }
                }

                if (playName.equalsIgnoreCase(userPlayName)) {
                    System.out.println(playlistArr.get(i));
                    songArr.add(newEntry);
                    System.out.println(songArr);
                    FileWriter writer = new FileWriter(jsonFile);
                    writer.write(jsonObject.toJSONString());
                    writer.flush();
                    writer.close();
                    Thread.sleep(40);
                }


            }


        } catch (ParseException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        initializeJson();
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
        setupTableMethods();

    }


    public void loadPlaylistsToPanel() {
        JSONParser parser = new JSONParser();
        System.out.println("Initialized loading playlist names");
        if (list == null) {
            list = new JList<>(libraryModel);
            list.setCellRenderer(new JListCellRenderer());
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
        setupTableMethods();

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
                int viewRow = songTable.convertRowIndexToView(songTable.getSelectedRow());
                int modelRow = songTable.convertRowIndexToModel(viewRow);
                editSongInformation((String) songTable.getValueAt(modelRow, 0), (String) songTable.getValueAt(modelRow, 1));
                //TODO Add a new Frame that shows all the information and once the user presses enter it edits the info for that row
            }
        });
        optionMenu.add(itemEdit);
        JMenu itemAdd = new JMenu("Add To Playlist");

        for (int i = 0; i < playlistNames.size(); i++) {
            JMenuItem item = new JMenuItem(playlistNames.get(i));
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addSelectedSongToPlaylist(item.getText());
                }
            });
            itemAdd.add(item);
        }
        optionMenu.add(itemAdd);
        JMenuItem itemDelete = new JMenuItem("Delete");
        itemDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                destroySelectedSong();
            }
        });
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

    public void destroySelectedSong() {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(jsonFile));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject library = (JSONObject) jsonObject.get("library");
            JSONArray playlistArr = (JSONArray) library.get("playlist");
            for (int i = 0; i < playlistArr.size(); i++) {
                JSONObject playElement = (JSONObject) playlistArr.get(i);
                System.out.println("Play Element: " + playElement);
                JSONArray songArr = (JSONArray) playElement.get("song");
                for (int j = 0; j < songArr.size(); j++) {
                    JSONObject songElement = (JSONObject) songArr.get(j);
                    System.out.println("Song Element: " + songElement.toString());
                    int viewRow = songTable.convertRowIndexToView(songTable.getSelectedRow());
                    int modelRow = songTable.convertRowIndexToModel(viewRow);
                    String title = (String) songTable.getValueAt(modelRow, 0);
                    String artist = (String) songTable.getValueAt(modelRow, 1);
                    String album = (String) songTable.getValueAt(modelRow, 2);
                    String duration = (String) songTable.getValueAt(modelRow, 3);
                    if (title.equalsIgnoreCase((String) songElement.get("title")) &&
                            artist.equalsIgnoreCase((String) songElement.get("artist"))) {

                        File f = new File("src/resources/music/" + title + ".mp3");
                        boolean removedMP3 = f.delete();
                        if (!removedMP3) {
                            throw new IllegalArgumentException("Delete: deletion failed");
                        }
                        songArr.remove(songElement);

                        FileWriter writer = null;
                        try {
                            writer = new FileWriter(jsonFile);
                            writer.write(jsonObject.toJSONString());
                            writer.flush();
                            writer.close();
                            Thread.sleep(40);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }


                    }

                }
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        initializeJson();
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

    public void editSongInformation(String currentTitle, String currentArtist) {
        JDialog dialog = new JDialog();
        dialog.setLayout(new MigLayout("ali 50% 50%"));
        int width = 300;
        int height = 200;
        int xPos = ((int) mainSwing.getFrame().getSize().getWidth() - width) / 2;
        int yPos = ((int) mainSwing.getFrame().getSize().getHeight() - height) / 2;
        dialog.setBounds(xPos, yPos, width, height);

        JLabel lblTitle = new JLabel("Title:");
        JLabel lblArtist = new JLabel("Artist:");
        JLabel lblAlbum = new JLabel("Album");
        JTextField tfTitle = new JTextField(15);
        JTextField tfArtist = new JTextField(15);
        JTextField tfAlbum = new JTextField(15);
        JButton submit = new JButton("Submit");
        JButton cancel = new JButton("Cancel");

        submit.setPreferredSize(new Dimension(80, 30));
        cancel.setPreferredSize(new Dimension(80, 30));
        dialog.add(lblTitle);
        dialog.add(tfTitle, "wrap");
        dialog.add(lblArtist);
        dialog.add(tfArtist, "wrap");
        dialog.add(lblAlbum);
        dialog.add(tfAlbum, "wrap");
        dialog.add(cancel);
        dialog.add(submit);
        dialog.pack();
        dialog.setVisible(true);
        dialog.setLocationRelativeTo(mainSwing.getFrame());
        JSONParser parser = new JSONParser();

        int viewRow = songTable.convertRowIndexToView(songTable.getSelectedRow());
        int modelRow = songTable.convertRowIndexToModel(viewRow);
        try {
            Object obj = parser.parse(new FileReader(jsonFile));
            JSONObject jsonObject = (JSONObject) obj;
            JSONObject library = (JSONObject) jsonObject.get("library");
            JSONArray playlistArr = (JSONArray) library.get("playlist");
            for (int i = 0; i < playlistArr.size(); i++) {
                JSONObject playElement = (JSONObject) playlistArr.get(i);
                System.out.println("Play Element: " + playElement);
                JSONArray songArr = (JSONArray) playElement.get("song");
                for (int j = 0; j < songArr.size(); j++) {
                    JSONObject songElement = (JSONObject) songArr.get(j);
                    System.out.println("Song Element: " + songElement.toString());
                    String title = (String) songTable.getValueAt(modelRow, 0);
                    String artist = (String) songTable.getValueAt(modelRow, 1);
                    String album = (String) songTable.getValueAt(modelRow, 2);
                    String duration = (String) songTable.getValueAt(modelRow, 3);

                    tfTitle.setText(title);
                    tfArtist.setText(artist);
                    tfAlbum.setText(album);

                }
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Object obj = parser.parse(new FileReader(jsonFile));
                    JSONObject jsonObject = (JSONObject) obj;
                    JSONObject library = (JSONObject) jsonObject.get("library");
                    JSONArray playlistArr = (JSONArray) library.get("playlist");
                    for (int i = 0; i < playlistArr.size(); i++) {
                        JSONObject playElement = (JSONObject) playlistArr.get(i);
                        System.out.println("Play Element: " + playElement);
                        JSONArray songArr = (JSONArray) playElement.get("song");
                        for (int j = 0; j < songArr.size(); j++) {
                            JSONObject songElement = (JSONObject) songArr.get(j);
                            System.out.println("Song Element: " + songElement.toString());
                            String title = (String) songElement.get("title");
                            String artist = (String) songElement.get("artist");
                            String album = (String) songElement.get("album");
                            String duration = (String) songTable.getValueAt(modelRow, 3);


                            if (title.equalsIgnoreCase(currentTitle) && artist.equalsIgnoreCase(currentArtist)) {
                                String newTitle = tfTitle.getText();
                                String newArtist = tfArtist.getText();
                                String newAlbum = tfAlbum.getText();
                                songElement.put("title", newTitle);
                                songElement.put("artist", newArtist);
                                songElement.put("album", newAlbum);
                                File currentMP3 = new File("src/resources/music/" + title + ".mp3");
                                File newMP3 = new File("src/resources/music/" + newTitle + ".mp3");
                                boolean renamedFile = currentMP3.renameTo(newMP3);
                                if (!renamedFile) {
                                    System.err.println("Error renaming music file");
                                }
                                FileWriter writer = null;
                                try {
                                    writer = new FileWriter(jsonFile);
                                    writer.write(jsonObject.toJSONString());
                                    writer.flush();
                                    writer.close();
                                    Thread.sleep(40);
                                } catch (IOException | InterruptedException exc) {
                                    exc.printStackTrace();
                                }

                            }
                            if (!album.equalsIgnoreCase(tfAlbum.getText())) {
                                songElement.put("album", album);
                                File currentAlbum = new File("src/resources/images/albums/" + album + ".png");
                                File newAlbum = new File("src/resources/images/albums/" + tfAlbum.getText() + ".png");
                                if (!newAlbum.exists()) {
                                    FileUtils.copyFile(currentAlbum, newAlbum);
                                }
                            }

                            initializeJson();
                            dialog.setVisible(false);

                        }
                    }
                } catch (ParseException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });

        initializeJson();


    }


    public ArrayList<String> getPlaylistNames() {
        return playlistNames;
    }


    public HashMap<String, Song> getSongList() {
        return songList;
    }

    public Image getAlbumImage(String albumName) {
        if (!albumName.isEmpty()) {
            System.out.println("Album Name: " + albumName + ".png");
            File imageOutput = null;
            if (albumName.equalsIgnoreCase("null")) {
                imageOutput = new File("src/resources/images/albums/" + "null" + ".png");
            } else {
                imageOutput = new File("src/resources/images/albums/" + albumName + ".png");
            }
            try {
                Image image = ImageIO.read(imageOutput);
                return image;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<Song> getListOfSongs() {
        return listOfSongs;
    }

    public DefaultTableModel getTableModel() {
        return this.tableModel;
    }



}
