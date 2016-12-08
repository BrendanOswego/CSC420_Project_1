package mainpackage;


import com.sun.javafx.tools.packager.Main;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import sun.jvm.hotspot.runtime.Bytes;

import javax.media.*;
import javax.media.pim.PlugInManager;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.regex.Pattern;

import javax.media.Format;
import javax.media.format.AudioFormat;

import static java.awt.Component.CENTER_ALIGNMENT;
import static javax.swing.SwingConstants.*;

//TODO- When all necessary methods are created, add Description comments for javadoc

//TODO-Add remove playlist functionality, which also means displaying available playlists to remove at runtime

//FIXME-Display playlists correctly after action is performed i.e remove or add playlist at runtime

//TODO- Change the previous and next images to be thicker, right now very thin looking

//TODO-Have a trail following the scrollbar

//FIXME- Check the creation of ID's, right now it's wrong and will affect playlists

/**
 * Main class for application, handles most of the functionality of the app including JSON Parsing, Swing Component creation, and MP3 Data conversion from ID3 Tags
 */

public class MainSwing {
    private static final File musicDir = new File("src/resources/music");
    private static final int PIC_W = 25;
    private static final int PIC_H = 25;
    private int BTN_W = 30;
    private int BTN_H = 25;

    private static final String play = "play_button";
    private static final String pause = "pause_button";
    private static final String previous = "previous_button";
    private static final String next = "next_button";
    private static final String shuffle = "shuffle_button";
    private static final String help = "help_button";
    private static final String shuffleOff = "shuffle_off_button";

    JLabel lblTitle = new JLabel();
    JLabel lblArtist = new JLabel();
    JLabel lblTotalTime = new JLabel();
    private JTable songTable = new JTable();
    private JTable backUpSongTable = songTable;
    private JFrame jFrame;
    JPanel infoMidPanel;
    JPanel infoMainPanel;
    private JFrame miniPlayer;
    private JButton playPauseButton;
    JPanel mainPanel;
    JPanel infoLeftPanel;
    JSlider musicSlider = new JSlider(HORIZONTAL);
    private JButton helpButton;
    private JButton shuffleButton;
    private JSlider volumeSlider;
    JLabel viewTitle = new JLabel("View: ");
    JComboBox switchView;
    private AlbumPanel albumPanel;
    private ArtistView AV;
    private JTextField searchField = new JTextField(8);
    private TableRowSorter<TableModel> rowSorter = null;
    private final JFileChooser fileChooser = new JFileChooser();
    private final FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("MP3 Files", "mp3");
    private JLabel currentTime = new JLabel();
    private UndoManager manager = new UndoManager();

    private int songSelecRow;

    public Song currentSong;

    public PlayerState currentState = PlayerState.MAIN;

    private ArrayList<String> playlistNames = new ArrayList<>();

    private LinkedList<Integer> songList = new LinkedList<>();
    private ListIterator<Integer> songIterator;

    private boolean isPlaying = false;
    private boolean isShuffle = false;

    private CustomJSON json;
    private ArrayList<Integer> songQueue;
    private int currentIndex = -1;


    private CustomBasicPlayer basicPlayer;

    private static MainSwing singleton = new MainSwing();

    public enum PlayerState {
        MAIN, ARTIST, ALBUM
    }


    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new MainSwing().createDesign();
                } catch (JavaLayerException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void createDesign() throws JavaLayerException {

        jFrame = new JFrame();
        SwingUtilities.updateComponentTreeUI(jFrame);

        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        jFrame.setSize(screenSize.width / 2, screenSize.height);
        infoMidPanel = new JPanel(new MigLayout("center center, wrap, gapy 0", "[grow,fill]"));
        mainPanel = new JPanel();
        JPanel libraryPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel soundControlPanel = new JPanel(new MigLayout("ali 50% 50%", "", "[grow,fill]"));
        JPanel musicPanel = new JPanel(new FlowLayout());

        infoMainPanel = new JPanel(new MigLayout("ali 50% 50%", "[][][]", ""));
        infoLeftPanel = new JPanel(new MigLayout("ali 50% 50%"));


        libraryPanel.setPreferredSize(new Dimension(120, jFrame.getHeight()));
        infoMidPanel.setPreferredSize(new Dimension(300, 100));
        infoMidPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        mainPanel.setPreferredSize(new Dimension(jFrame.getWidth(), jFrame.getHeight()));

        BoxLayout centerLayout = new BoxLayout(centerPanel, VERTICAL);
        BorderLayout mainLayout = new BorderLayout();
        BoxLayout westLayout = new BoxLayout(libraryPanel, BoxLayout.Y_AXIS);

        mainPanel.setLayout(mainLayout);
        libraryPanel.setLayout(westLayout);
        centerPanel.setLayout(centerLayout);

        JLabel libraryHeader = new JLabel("Library");
        JLabel artistHeader = new JLabel("Artist");
        JButton previousButton = new JButton();
        playPauseButton = new JButton();
        JButton nextButton = new JButton();
        shuffleButton = new JButton();
        helpButton = new JButton();

        previousButton.setPreferredSize(new Dimension(BTN_W, BTN_H));
        playPauseButton.setPreferredSize(new Dimension(BTN_W, BTN_H));
        nextButton.setPreferredSize(new Dimension(BTN_W, BTN_H));
        shuffleButton.setPreferredSize(new Dimension(BTN_W, BTN_H));
        int soundControlWidth = (int) previousButton.getPreferredSize().getWidth() * 4;
        soundControlPanel.setPreferredSize(new Dimension(soundControlWidth, 20));
        musicPanel.setPreferredSize(new Dimension((int) infoMidPanel.getPreferredSize().getWidth(), 20));
        soundControlPanel.setAlignmentX(CENTER_ALIGNMENT);
        createIconPNG(previousButton, previous, PIC_W, PIC_H);
        createIconPNG(playPauseButton, play, PIC_W, PIC_H);
        createIconPNG(nextButton, next, PIC_W, PIC_H);
        createIconPNG(shuffleButton, shuffle, PIC_W, PIC_H);
        createIconPNG(helpButton, help, PIC_W, PIC_H);

        previousButton.addActionListener(previousListener);
        playPauseButton.addActionListener(playPauseListener);
        nextButton.addActionListener(nextListener);
        shuffleButton.addActionListener(shuffleListener);


        musicSlider.setValue(0);
        musicSlider.setPreferredSize(new Dimension(soundControlWidth + 90, (int) soundControlPanel.getPreferredSize().getHeight()));
        musicSlider.setBorder(BorderFactory.createLineBorder(Color.black));

        lblTitle.setFont(lblTitle.getFont().deriveFont(15f));
        lblArtist.setFont(lblArtist.getFont().deriveFont(15f));
        libraryHeader.setFont(libraryHeader.getFont().deriveFont(15f));

        libraryHeader.setAlignmentX(CENTER_ALIGNMENT);
        artistHeader.setAlignmentX(CENTER_ALIGNMENT);
        libraryHeader.setHorizontalAlignment(CENTER);

        playPauseButton.setOpaque(true);

        CustomMenuBar topMenu = new CustomMenuBar();
        int songTableWidth = (int) centerPanel.getSize().getWidth() - (int) libraryPanel.getSize().getWidth();
        int songTableHeight = (int) centerPanel.getSize().getHeight() - (int) infoMidPanel.getSize().getHeight();
        songTable.setPreferredSize(new Dimension(songTableWidth, songTableHeight));
        songTable.addMouseListener(songSelectListener);
        JScrollPane scrollPane = new JScrollPane(songTable);
        scrollPane.setPreferredSize(new Dimension((int) jFrame.getSize().getWidth(), (int) jFrame.getSize().getHeight()));

        //This has to be called before the songTable is added to the center panel
        //And anything that changes the songTable information as well i.e changing the name of a song
        AV = new ArtistView(this);
        json = new CustomJSON(songTable, scrollPane, libraryPanel, playlistNames, this, rowSorter);
        json.setupAV(AV);
        json.initializeJson();
        json.initializeAddedPlaylists();

        JButton button = new JButton("Test");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                basicPlayer.getThread().seek(400000);
            }
        });
        musicSlider.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();
                double percent = p.x / ((double) musicSlider.getWidth());
                int range = musicSlider.getMaximum() - musicSlider.getMinimum();
                double newVal = range * percent;
                int result = (int)(musicSlider.getMinimum() + newVal);
                basicPlayer.getThread().seek(result);


            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });


        TableModel dm = (TableModel) songTable.getModel();
        rowSorter = new TableRowSorter<>(dm);
        songTable.setRowSorter(rowSorter);

        albumPanel = new AlbumPanel(this);
        albumPanel.setPreferredSize(new Dimension(150, 110));

        switchView = new JComboBox();
        switchView.addItem("Main View");
        switchView.addItem("Artist");
        switchView.addItem("Album");
        switchView.addItem("MiniPlayer");
        switchView.addActionListener(swapperListener);
        JLabel searchLabel = new JLabel("Search");

        searchField.getDocument().addDocumentListener(SearchListener);

        centerPanel.add(scrollPane);
        soundControlPanel.add(previousButton);
        soundControlPanel.add(playPauseButton);
        soundControlPanel.add(nextButton);
        soundControlPanel.add(shuffleButton);

        musicPanel.add(currentTime);
        musicPanel.add(musicSlider);
        musicPanel.add(lblTotalTime);
        musicPanel.setAlignmentX(CENTER_ALIGNMENT);
        infoLeftPanel.add(searchLabel);
        infoLeftPanel.add(searchField, "wrap");
        infoLeftPanel.add(viewTitle);
        infoLeftPanel.add(switchView);

        CC componentConstraints = new CC();
        componentConstraints.alignX("center").spanX();

        infoMidPanel.add(lblTitle, "wrap");
        infoMidPanel.add(lblArtist, "wrap");
        infoMidPanel.add(soundControlPanel, "wrap, span");
        infoMidPanel.add(musicPanel, "wrap, span");

        infoMainPanel.add(infoMidPanel, "cell 1 0");
        infoMainPanel.add(albumPanel, "cell 2 0");
        infoMainPanel.add(infoLeftPanel, "cell 0 0");


        libraryPanel.add(libraryHeader);
        json.loadPlaylistsToPanel();


        mainPanel.add(infoMainPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(libraryPanel, BorderLayout.LINE_START);

        jFrame.setJMenuBar(topMenu.showMenuBar());

        jFrame.setContentPane(mainPanel);
        jFrame.setVisible(true);
        jFrame.setFocusable(true);
        songQueue = new ArrayList<>();
        createMiniPlayer();
    }

    public void createMiniPlayer() {
        //TODO: set hotkeys
        miniPlayer = new JFrame();
        miniPlayer.setLayout(new MigLayout("ali 50% 50%"));
        miniPlayer.setMinimumSize(new Dimension(400, 200));

        MiniMenuBar miniMenu = new MiniMenuBar();
        miniPlayer.setFocusable(true);
        miniPlayer.setResizable(false);
        miniPlayer.setJMenuBar(miniMenu.showMenuBar());

        /*
        We need to decide what functionality we want to have when the user closes out of the miniplayer.
        Should it default back to the original player or close down altogether?
         */

    }


    private ActionListener swapperListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String view = (String) switchView.getSelectedItem();
            if (view.equalsIgnoreCase("Main View")) {
                currentState = PlayerState.MAIN;
                AV.clearAVSongTable();
                mainPanel.add(infoMainPanel, BorderLayout.NORTH);
                jFrame.setContentPane(mainPanel);
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                jFrame.setSize(screenSize.width / 2, screenSize.height);
                jFrame.revalidate();
            } else if (view.equalsIgnoreCase("Artist")) {
                currentState = PlayerState.ARTIST;
                AV.clearAVSongTable();
                if (AV != null) {
                    AV.setUpViewForArtist(jFrame, infoMainPanel);
                }
            } else if (view.equalsIgnoreCase("Album")) {
                currentState = PlayerState.ALBUM;
                AV.clearAVSongTable();
                if (AV != null) {
                    AV.setUpViewForAlbum(jFrame, infoMainPanel);
                }
            } else {
                jFrame.setVisible(false);
                miniPlayer.add(infoMidPanel);
                miniPlayer.add(albumPanel);
                miniPlayer.setVisible(true);
                miniPlayer.pack();
            }

        }
    };
    public DocumentListener SearchListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            if (!currentState.equals(PlayerState.MAIN)) {
                songTable = AV.getAVSongTable();
                TableModel dm = (TableModel) songTable.getModel();
                rowSorter = new TableRowSorter<>(dm);
                songTable.setRowSorter(rowSorter);
            } else {
                songTable = backUpSongTable;
                TableModel dm = (TableModel) songTable.getModel();
                rowSorter = new TableRowSorter<>(dm);
                songTable.setRowSorter(rowSorter);
            }
            String text = searchField.getText();
            System.out.println(text);
            if (text.trim().length() >= 0) {
                songTable.setRowSorter(rowSorter);
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                System.out.println("Inside");
            } else {
                rowSorter.setRowFilter(null);
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            if (!currentState.equals(PlayerState.MAIN)) {
                songTable = AV.getAVSongTable();
                TableModel dm = (TableModel) songTable.getModel();
                rowSorter = new TableRowSorter<>(dm);
                songTable.setRowSorter(rowSorter);
            } else {
                songTable = backUpSongTable;
                TableModel dm = (TableModel) songTable.getModel();
                rowSorter = new TableRowSorter<>(dm);
                songTable.setRowSorter(rowSorter);
            }
            String text = searchField.getText();
            System.out.println(text);
            if (text.trim().length() >= 0) {
                songTable.setRowSorter(rowSorter);
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            } else {
                rowSorter.setRowFilter(null);
            }
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    };


    private ActionListener previousListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isShuffle()) { //if shuffle is off

                if (songTable.getSelectedRow() > 0) {
                    int viewRow = songTable.convertRowIndexToView(songTable.getSelectedRow());
                    int modelRow = songTable.convertRowIndexToModel(viewRow);
                    String name = (String) songTable.getValueAt(modelRow - 1, 0);
                    String album = (String) songTable.getValueAt(modelRow - 1, 2);
                    String totalTime = (String) songTable.getValueAt(modelRow - 1, 3);
                    String artist = (String) songTable.getValueAt(modelRow - 1, 1);
                    setAlbumImage(album);
                    lblArtist.setText(artist);
                    lblTitle.setText(name);
                    lblTotalTime.setText(totalTime);
                    if (basicPlayer != null) {
                        basicPlayer.stop();
                    }
                    playSong(modelRow - 1);
                    songTable.setRowSelectionInterval(songTable.getSelectedRow() - 1, songTable.getSelectedRow() - 1);
                    currentIndex = songTable.getRowCount() - 1;

                } else {
                    System.out.println(songTable.getRowCount() - 1);
                    String name = (String) songTable.getValueAt(songTable.getRowCount() - 1, 0);
                    String album = (String) songTable.getValueAt(songTable.getRowCount() - 1, 2);
                    String totalTime = (String) songTable.getValueAt(songTable.getRowCount() - 1, 3);
                    String artist = (String) songTable.getValueAt(songTable.getRowCount() - 1, 1);
                    setAlbumImage(album);
                    lblArtist.setText(artist);
                    lblTitle.setText(name);
                    lblTotalTime.setText(totalTime);
                    if (basicPlayer != null) {
                        basicPlayer.stop();
                    }
                    playSong(songTable.getRowCount() - 1);
                    songTable.setRowSelectionInterval(songTable.getRowCount() - 1, songTable.getRowCount() - 1);
                    currentIndex = songTable.getRowCount() - 1;

                    currentIndex = songTable.getRowCount() - 1;
                    isPlaying = true;
                    createIconPNG(playPauseButton, pause, PIC_W, PIC_H);

                }
                isPlaying = true;

                createIconPNG(playPauseButton, pause, PIC_W, PIC_H);

            } else { //if shuffle is on
                //if No previous item in songQueue do nothing
                //else play previous
            }
        }
    };

    private ActionListener nextListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!currentState.equals(PlayerState.MAIN)) {
                songTable = AV.getAVSongTable();
            } else {
                songTable = backUpSongTable;
            }

            if (!isShuffle()) {  // If Not on shuffle mode


                if (songTable.getSelectedRow() < songTable.getRowCount() - 1) { //If Not on shuffle mode && If no row is selected
                    int viewRow = songTable.convertRowIndexToView(songTable.getSelectedRow());
                    int modelRow = songTable.convertRowIndexToModel(viewRow);
                    String name = (String) songTable.getValueAt(modelRow + 1, 0);
                    String totalTime = (String) songTable.getValueAt(modelRow + 1, 3);
                    String artist = (String) songTable.getValueAt(modelRow + 1, 1);
                    String album = (String) songTable.getValueAt(modelRow + 1, 2);
                    setAlbumImage(album);
                    lblArtist.setText(artist);
                    lblTitle.setText(name);
                    lblTotalTime.setText(totalTime);
                    if (basicPlayer != null) {
                        basicPlayer.stop();
                    }
                    playSong(modelRow + 1);
                    System.out.println("Playing Song +1");

                    songTable.setRowSelectionInterval(songTable.getSelectedRow() + 1, songTable.getSelectedRow() + 1);
                    currentIndex = songTable.getRowCount() - 1;

                    isPlaying = true;
                    createIconPNG(playPauseButton, pause, PIC_W, PIC_H);

                } else { //If Not on shuffle mode && If a row is selected
                    String name = (String) songTable.getValueAt(0, 0);
                    String totalTime = (String) songTable.getValueAt(0, 3);
                    String artist = (String) songTable.getValueAt(0, 1);
                    String album = (String) songTable.getValueAt(0, 2);
                    setAlbumImage(album);
                    lblArtist.setText(artist);
                    lblTitle.setText(name);
                    lblTotalTime.setText(totalTime);
                    if (basicPlayer != null) {
                        basicPlayer.stop();
                    }
                    playSong(0);
                    System.out.println("Playing Song 0");
                    songTable.setRowSelectionInterval(0, 0);

                    isPlaying = true;
                    createIconPNG(playPauseButton, pause, PIC_W, PIC_H);

                }

            } else { // If on shuffle mode

                if (songQueue.size() == 0) { // If on shuffle mode && If you just turned on shuffle mode

                    Random r = new Random();
                    int randomSpot = r.nextInt(songTable.getRowCount());
                    String name = (String) songTable.getValueAt(randomSpot, 0);
                    String album = (String) songTable.getValueAt(randomSpot, 2);
                    String totalTime = (String) songTable.getValueAt(randomSpot, 3);
                    String artist = (String) songTable.getValueAt(randomSpot, 1);
                    setAlbumImage(album);
                    lblArtist.setText(artist);
                    lblTitle.setText(name);
                    lblTotalTime.setText(totalTime);
                    if (basicPlayer != null) {
                        basicPlayer.stop();
                    }
                    playSong(randomSpot);
                    songTable.setRowSelectionInterval(randomSpot, randomSpot);

                    songQueue.add(randomSpot);
                    currentIndex = songQueue.size();

                    isPlaying = true;
                    createIconPNG(playPauseButton, pause, PIC_W, PIC_H);

                } else { //If shuffle mode was already one.
                    if (currentIndex == songQueue.size()) {//if there is nothing else in the queue

                    }
                    // play Random
                    // update currentSong from playQueue
                    // if songQueue has Next Play Next
                }

            }
        }
    };

    private ActionListener shuffleListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO-Add shuffle functionality
            if (isShuffle()) {
                isShuffle = false;
                createIconPNG(shuffleButton, shuffle, BTN_W, BTN_H);
                System.out.println("isShuffle " + isShuffle);
            } else {
                isShuffle = true;
                createIconPNG(shuffleButton, shuffleOff, BTN_W, BTN_H);

            }
//            songQueue.clear();
//            currentIndex = -1;

        }
    };

    private ActionListener playPauseListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (!currentState.equals(PlayerState.MAIN)) {
                songTable = AV.getAVSongTable();
            } else {
                songTable = backUpSongTable;
            }
            if (!isPlaying() && !songTable.isRowSelected(songTable.getSelectedRow())) {
                songTable.setRowSelectionInterval(0, 0);
                int viewRow = songTable.convertRowIndexToView(songTable.getSelectedRow());
                int modelRow = songTable.convertRowIndexToModel(viewRow);
                String album = (String) songTable.getValueAt(modelRow, 2);
                System.out.println(album);
                setAlbumImage(album);
                createIconPNG(playPauseButton, pause, PIC_W, PIC_H);
                songTable.setRowSelectionInterval(0, 0);
                currentIndex = songTable.getRowCount() - 1;
                playSong(0);
                return;
            }
            if (!isPlaying()) {
                isPlaying = true;
                if (songTable.isRowSelected(songTable.getSelectedRow())) {
                    createIconPNG(playPauseButton, pause, PIC_W, PIC_H);
                    basicPlayer.resume();
                }
            } else {
                isPlaying = false;
                if (songTable.isRowSelected(songTable.getSelectedRow())) {
                    createIconPNG(playPauseButton, play, PIC_W, PIC_H);
                    basicPlayer.pause();
                }
            }

        }
    };
    public MouseListener songSelectListener = new MouseAdapter() {

        public void mousePressed(MouseEvent me) {

            if (!currentState.equals(PlayerState.MAIN)) {
                songTable = AV.getAVSongTable();
            } else {
                songTable = backUpSongTable;
            }
            JTable table = (JTable) me.getSource();
            if (me.getClickCount() == 2) {
                isPlaying = true;
                System.out.println("JTable Row Count: " + table.getRowCount());
                int viewRow = table.convertRowIndexToView(table.getSelectedRow());
                int modelRow = table.convertRowIndexToModel(viewRow);
                String title = (String) table.getValueAt(modelRow, 0);
                String album = (String) table.getValueAt(modelRow, 2);
                String artist = (String) table.getValueAt(modelRow, 1);
                String duration = (String) table.getValueAt(modelRow, 3);

                setAlbumImage(album);
                songList.add(songTable.getSelectedRow());
                songIterator = songList.listIterator();
                songIterator.next();
                int previous = songIterator.previous();
                System.out.println(previous);
                if (basicPlayer != null) {
                    basicPlayer.stop();
                    basicPlayer = null;
                }
                playSong(modelRow);

            }
        }
    };


    public void setAlbumImage(String albumName) {
        if (!Objects.equals(albumName, null)) {
            albumPanel.setHasImage(true);
            albumPanel.setAlbumString(albumName);
            albumPanel.repaint();
        } else {
            albumPanel.setHasImage(false);
            albumPanel.repaint();
            albumPanel.revalidate();
        }
    }

    public void playSong(int row) {
        if (!currentState.equals(PlayerState.MAIN)) {
            songTable = AV.getAVSongTable();
        } else {
            songTable = backUpSongTable;
        }


        String name = (String) songTable.getValueAt(row, 0);
        String totalTime = (String) songTable.getValueAt(row, 3);
        String artist = (String) songTable.getValueAt(row, 1);
        String album = (String) songTable.getValueAt(row, 2);
        currentSong = new Song(row, name, artist, album, totalTime);

        songSelecRow = row;
        lblArtist.setText(artist);
        lblTitle.setText(name);
        lblTotalTime.setText(totalTime);
        setupTime();
        isPlaying = true;
        if (basicPlayer != null) {
            basicPlayer.stop();
            basicPlayer = null;
        }

        basicPlayer = new CustomBasicPlayer(this);
        basicPlayer.play(name);
        if (basicPlayer != null && basicPlayer.getThread().getEvent().getValue() == BasicPlayerEvent.EOM) {
            if (!isShuffle()) {
                playSong(row + 1);
            }
        }


        createIconPNG(playPauseButton, pause, PIC_W, PIC_H);

    }

    public void playNextSong() {
        if (!isShuffle()) {
            int viewRow = songTable.convertRowIndexToView(songSelecRow);
            int modelRow = songTable.convertRowIndexToModel(viewRow);
            songTable.setRowSelectionInterval(modelRow + 1, modelRow + 1);
            String album = (String) songTable.getValueAt(modelRow + 1, 2);
            setAlbumImage(album);
            playSong(modelRow + 1);

        } else {
            Random r = new Random();
            int randomSpot = r.nextInt(songTable.getRowCount());
            songSelecRow = randomSpot;
            songTable.setRowSelectionInterval(randomSpot, randomSpot);
            String album = (String) songTable.getValueAt(randomSpot, 2);
            setAlbumImage(album);
            playSong(randomSpot);

        }

    }


    public void setupTime() {
        String delim = ":";
        String[] time = lblTotalTime.getText().split(delim);
        int minutes = Integer.valueOf(time[0]);
        int mTos = minutes * 60;
        int seconds = Integer.valueOf(time[1]);
        int totalTimeInSeconds = mTos + seconds;
        System.out.println("Time in seconds: " + totalTimeInSeconds);

        System.out.println("Minutes: " + minutes + "\nSeconds: " + seconds);
    }

    private void showFileChooser() {
        fileChooser.setFileFilter(fileFilter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        int returnVal = fileChooser.showOpenDialog(jFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            for (int i = 0; i < fileChooser.getSelectedFiles().length; i++) {
                File f = new File(fileChooser.getSelectedFiles()[i].getPath());
                try {
                    FileUtils.copyFileToDirectory(f, musicDir);
                    json.addSong(f.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("File Chooser Cancelled by User");
        }
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


    private void createIconPNG(JButton label, String name, int width, int height) {
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

    private void createIconPNG(JLabel label, String name, int width, int height) {
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

    private boolean isShuffle() {
        return this.isShuffle;
    }

    private class MiniMenuBar extends JMenuBar {
        JMenuBar showMenuBar() {
            JMenuItem item;
            JMenuBar menuBar = new JMenuBar();
            // menuBar.setPreferredSize(new Dimension((int) jFrame.getSize().getWidth()/4, 35));

            JMenu viewMenu = new JMenu("View");
            menuBar.add(viewMenu);
            item = new JMenuItem("Default View");
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    miniPlayer.setVisible(false);
                    infoMainPanel.add(infoMidPanel);
                    infoMainPanel.add(albumPanel);
                    //miniPlayer.add(miniControls);
                    //JPanel miniControls = controls;
                    //miniPlayer.pack();
                    jFrame.setVisible(true);
                }
            });
            viewMenu.add(item);
            return menuBar;
        }
    }


    /**
     * Inner class that creates the JMenuBar for the mainpackage JFrame
     */
    private class CustomMenuBar extends JMenuBar {

        JMenuBar showMenuBar() {

            JMenu playlistAddSub = new JMenu("Add to Playlist");
            JMenu playlistOpenSub = new JMenu("Open Playlist");


            JMenuBar menuBar = new JMenuBar();
            menuBar.setPreferredSize(new Dimension((int) jFrame.getSize().getWidth(), 35));

            JMenu menu = new JMenu("File");
            menuBar.add(menu);
            JMenuItem item = new JMenuItem("Add Song");
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showFileChooser();
                }
            });
            menu.add(item);

            menu = new JMenu("Edit");
            menuBar.add(menu);

            JMenuItem undoItem = new JMenuItem("Undo");
            undoItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    manager.undo();
                }
            });
            menu.add(undoItem);

            item = new JMenuItem("Redo");
            menu.add(item);


            JMenu playlistMenu = new JMenu("Playlist");

            menuBar.add(playlistMenu);


            item = new JMenuItem("Create New Playlist");
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    JDialog dialog = new JDialog();
                    dialog.setLayout(new MigLayout("align 50% 50%"));
                    int width = 250;
                    int height = 150;
                    int xPos = ((int) getSize().getWidth() - width) / 2;
                    int yPos = ((int) getSize().getHeight() - height) / 2;
                    dialog.setBounds(xPos, yPos, width, height);
                    JLabel title = new JLabel("Enter new Playlist name");
                    JButton submit = new JButton("Submit");
                    JButton cancel = new JButton("Cancel");
                    JTextField field = new JTextField(10);

                    dialog.add(title, "span");
                    dialog.add(field, "span");
                    dialog.add(submit);
                    dialog.add(cancel);

                    dialog.pack();
                    dialog.setVisible(true);
                    dialog.setLocationRelativeTo(jFrame);


                    cancel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            dialog.dispose();
                        }
                    });
                    submit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            if (!field.getText().isEmpty() && !json.getPlaylistNames().contains(field.getText())) {
                                System.out.println("Performing Action");
                                json.createPlaylist(field.getText());
                                playlistNames.add(field.getText());
                                json.initializeAddedPlaylists();
                                json.loadPlaylistsToPanel();
                                playlistAddSub.add(field.getText());
                                playlistOpenSub.add(field.getText());
                                //TODO-Create method that adds the newly added playlist to the menu a runtime
                                dialog.setVisible(false);
                                menuBar.revalidate();
                            } else if (json.getPlaylistNames().contains(field.getText())) {
                                JOptionPane.showMessageDialog(dialog, "Name already taken, please choose a different one");
                            }
                        }
                    });

                }
            });

            playlistMenu.add(item);

            playlistMenu.addSeparator();
            playlistMenu.add(playlistOpenSub);
            JMenuItem playlistOpenItem;
            for (int i = 0; i < json.getPlaylistNames().size(); i++) {
                playlistOpenItem = new JMenuItem(json.getPlaylistNames().get(i));
                if (playlistOpenItem.getText().equals("default")) {
                    playlistOpenItem.setText("Library");
                }
                int finalI = i;
                playlistOpenItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (json.getPlaylistNames().get(finalI).equals("Library")) {
                            json.loadPlaylistToTable(json.getPlaylistNames().get(0));
                        } else {
                            json.loadPlaylistToTable(json.getPlaylistNames().get(finalI));
                        }
                    }
                });
                playlistOpenSub.add(playlistOpenItem);
            }

            JMenu viewMenu = new JMenu("View");
            menuBar.add(viewMenu);
            item = new JMenuItem("miniPlayer View");
            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    jFrame.setVisible(false);
                    miniPlayer.add(infoMidPanel);
                    miniPlayer.add(albumPanel);
                    miniPlayer.setVisible(true);
                    miniPlayer.pack();
                }
            });
            viewMenu.add(item);

            item = new JMenuItem("Default View");
            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    miniPlayer.setVisible(false);
                    jFrame.setVisible(true);

                    // menuBar.remove(menu);
                    // menuBar.remove(playlistMenu);
                }
            });
            item = new JMenuItem("Artist View");
            item.addActionListener(swapperListener);
            viewMenu.add(item);
            return menuBar;
        }
    }

    public CustomJSON getJSON() {
        return this.json;
    }

    public TableRowSorter<TableModel> getRowSorter() {
        return this.rowSorter;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JFrame getFrame() {
        return this.jFrame;
    }

    public JSlider getMusicSlider() {
        return this.musicSlider;
    }


    public boolean isPlaying() {
        return this.isPlaying;
    }

    public Song getCurrentSong() {
        return this.currentSong;
    }

    public int getSongSelecRow() {
        return this.songSelecRow;
    }


}
