package mainpackage;


import javazoom.jl.decoder.JavaLayerException;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

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
    private static final File jsonFile = new File("src/resources/json/library.json");
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


    JLabel lblTitle = new JLabel();
    JLabel lblArtist = new JLabel();
    JLabel lblTotalTime = new JLabel();
    private JTable songTable = new JTable();
    private JFrame jFrame;
    JPanel infoMidPanel;
    JPanel infoMainPanel;
    private JFrame miniPlayer;
    private JButton playPauseButton;
    private JButton helpButton;
    private JSlider volumeSlider;
    private AlbumPanel albumPanel;

    private final JFileChooser fileChooser = new JFileChooser();
    private final FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("MP3 Files", "mp3");
    private JLabel currentTime = new JLabel();


    private ArrayList<String> playlistNames = new ArrayList<>();

    private LinkedList songList = new LinkedList();
    private ListIterator songIterator;

    private MusicPlayer player;

    private boolean isPlaying = false;
    private boolean isShuffle = false;

    private CustomJSON json;

    private String newTitle;
    private String newArtist;
    private String newDuration;


    public MainSwing() {
        try {
            createDesign();
        } catch (JavaLayerException e) {
            e.printStackTrace();
        }
        songTable.addMouseListener(songSelectListener);

    }


    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainSwing();
            }
        });

    }


    private void createDesign() throws JavaLayerException {

        jFrame = new JFrame();
        SwingUtilities.updateComponentTreeUI(jFrame);

        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        jFrame.setSize(screenSize.width / 2, screenSize.height);
        infoMidPanel = new JPanel(new MigLayout("", "[][][]", "[][][][]"));
        JPanel mainPanel = new JPanel();
        JPanel libraryPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel soundControlPanel = new JPanel();
        JPanel musicPanel = new JPanel(new FlowLayout());

        infoMainPanel = new JPanel(new MigLayout("ali 50% 50%", "[][][]",""));
        JPanel infoLeftPanel = new JPanel();
        JPanel infoRightPabnel = new JPanel();


        libraryPanel.setPreferredSize(new Dimension(120, jFrame.getHeight()));
        infoMidPanel.setPreferredSize(new Dimension(300, 100));
        mainPanel.setPreferredSize(new Dimension(jFrame.getWidth(), jFrame.getHeight()));

        BoxLayout centerLayout = new BoxLayout(centerPanel, VERTICAL);
        BorderLayout mainLayout = new BorderLayout();
        FlowLayout playerLayout = new FlowLayout();
        BoxLayout westLayout = new BoxLayout(libraryPanel, BoxLayout.Y_AXIS);
        BoxLayout verticalLayout = new BoxLayout(infoMidPanel, BoxLayout.Y_AXIS);

        mainPanel.setLayout(mainLayout);
        soundControlPanel.setLayout(playerLayout);
        libraryPanel.setLayout(westLayout);
        centerPanel.setLayout(centerLayout);
        infoMidPanel.setLayout(verticalLayout);

        JLabel libraryHeader = new JLabel("Library");
        JLabel artistHeader = new JLabel("Artist");
        JSlider musicSlider = new JSlider(JSlider.HORIZONTAL);
        JButton previousButton = new JButton();
        playPauseButton = new JButton();
        JButton nextButton = new JButton();
        JButton shuffleButton = new JButton();
        helpButton = new JButton();

        previousButton.setPreferredSize(new Dimension(BTN_W, BTN_H));
        playPauseButton.setPreferredSize(new Dimension(BTN_W, BTN_H));
        nextButton.setPreferredSize(new Dimension(BTN_W, BTN_H));
        shuffleButton.setPreferredSize(new Dimension(BTN_W, BTN_H));

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

        lblTitle.setFont(lblTitle.getFont().deriveFont(15f));
        lblArtist.setFont(lblArtist.getFont().deriveFont(15f));
        libraryHeader.setFont(libraryHeader.getFont().deriveFont(15f));

        lblTitle.setAlignmentX(CENTER_ALIGNMENT);
        lblArtist.setAlignmentX(CENTER_ALIGNMENT);
        libraryHeader.setAlignmentX(CENTER_ALIGNMENT);
        artistHeader.setAlignmentX(CENTER_ALIGNMENT);
        libraryHeader.setHorizontalAlignment(CENTER);

        playPauseButton.setOpaque(true);

        CustomMenuBar topMenu = new CustomMenuBar();

        int songTableWidth = (int) centerPanel.getSize().getWidth() - (int) libraryPanel.getSize().getWidth();
        int songTableHeight = (int) centerPanel.getSize().getHeight() - (int) infoMidPanel.getSize().getHeight();
        songTable.setPreferredSize(new Dimension(songTableWidth, songTableHeight));

        JScrollPane scrollPane = new JScrollPane(songTable);
        scrollPane.setPreferredSize(new Dimension((int) jFrame.getSize().getWidth(), (int) jFrame.getSize().getHeight()));

        //This has to be called before the songTable is added to the center panel
        //And anything that changes the songTable information as well i.e changing the name of a song

        json = new CustomJSON(songTable, scrollPane, libraryPanel, playlistNames);
        json.initializeJson();
        json.initializeAddedPlaylists();


        albumPanel = new AlbumPanel(this);
        albumPanel.setPreferredSize(new Dimension(150,110));
        albumPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));


        centerPanel.add(scrollPane);
        soundControlPanel.add(previousButton);
        soundControlPanel.add(playPauseButton);
        soundControlPanel.add(nextButton);
        soundControlPanel.add(shuffleButton);

        musicPanel.add(currentTime);
        musicPanel.add(musicSlider);
        musicPanel.add(lblTotalTime);
        musicPanel.setAlignmentX(CENTER_ALIGNMENT);

        infoMidPanel.add(lblTitle, "cell 1 0");
        infoMidPanel.add(lblArtist, "cell 1 1");
        infoMidPanel.add(soundControlPanel, "cell 1 2");
        infoMidPanel.add(musicPanel, "cell 1 3");
        //infoMidPanel.setBackground(Color.GREEN);

        //infoMidPanel.setBorder(BorderFactory.createBevelBorder(RAISED));
        infoMainPanel.add(infoMidPanel,"cell 1 0");
        infoMainPanel.add(albumPanel, "cell 2 0");

        albumPanel.setAlbumString("Ghosts N Stuff - Single");
        System.out.println(albumPanel.getAlbumString());
        albumPanel.repaint();
        libraryPanel.add(libraryHeader);
        json.loadPlaylistsToPanel();


        mainPanel.add(infoMainPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(libraryPanel, BorderLayout.LINE_START);

        //"com.sun.java.swing.plaf.mac.MacLookAndFeel"

        jFrame.setJMenuBar(topMenu.showMenuBar());

        jFrame.setContentPane(mainPanel);
        jFrame.setVisible(true);
        jFrame.setFocusable(true);

        //json.fillEmptyRows();
        createMiniPlayer();
    }

    public void createMiniPlayer() {
        //TODO: set hotkeys
        miniPlayer = new JFrame();
        MiniMenuBar miniMenu = new MiniMenuBar();
        miniPlayer.setFocusable(true);
        miniPlayer.setResizable(false);
        miniPlayer.setJMenuBar(miniMenu.showMenuBar());
        /*
        We need to decide what functionality we want to have when the user closes out of the miniplayer.
        Should it default back to the original player or close down altogether?
         */

    }


    private ActionListener previousListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            songIterator = songList.listIterator();

            if (!isShuffle()) {
                if (player != null && player.getPlayerStatus() == 1) {
                    player.stop();
                    player = null;
                }
                try {
                    if (songTable.getSelectedRow() > -1) {
                        String name = (String) songTable.getValueAt(songTable.getSelectedRow() - 1, 0);
                        String totalTime = (String) songTable.getValueAt(songTable.getSelectedRow() - 1, 2);
                        String artist = (String) songTable.getValueAt(songTable.getSelectedRow() - 1, 1);
                        lblArtist.setText(artist);
                        lblTitle.setText(name);
                        lblTotalTime.setText(totalTime);
                        FileInputStream inputStream = new FileInputStream("src/resources/music/" + name + ".mp3");
                        player = new MusicPlayer(inputStream);
                        songTable.setRowSelectionInterval(songTable.getSelectedRow() - 1, songTable.getSelectedRow() - 1);
                    }
                    player.play();
                    isPlaying = true;

                    createIconPNG(playPauseButton, pause, PIC_W, PIC_H);
                } catch (FileNotFoundException ev) {
                    ev.printStackTrace();
                }
            }
        }
    };

    private ActionListener nextListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            songList.add(songTable.getSelectedRow());
            songIterator = songList.listIterator();
            songIterator.next();
            int previous = (int) songIterator.previous();
            System.out.println(previous);
            if (!isShuffle()) {
                if (player != null && player.getPlayerStatus() == 1) {
                    player.stop();
                    player = null;
                }
                try {
                    if (songTable.getSelectedRow() < songTable.getRowCount() - 1) {
                        String name = (String) songTable.getValueAt(songTable.getSelectedRow() + 1, 0);
                        String totalTime = (String) songTable.getValueAt(songTable.getSelectedRow() + 1, 2);
                        String artist = (String) songTable.getValueAt(songTable.getSelectedRow() + 1, 1);
                        lblArtist.setText(artist);
                        lblTitle.setText(name);
                        lblTotalTime.setText(totalTime);
                        FileInputStream inputStream = new FileInputStream("src/resources/music/" + name + ".mp3");
                        player = new MusicPlayer(inputStream);
                        songTable.setRowSelectionInterval(songTable.getSelectedRow() + 1, songTable.getSelectedRow() + 1);
                        player.play();
                        isPlaying = true;
                        createIconPNG(playPauseButton, pause, PIC_W, PIC_H);
                    } else {
                        String name = (String) songTable.getValueAt(0, 0);
                        String totalTime = (String) songTable.getValueAt(0, 2);
                        String artist = (String) songTable.getValueAt(0, 1);
                        lblArtist.setText(artist);
                        lblTitle.setText(name);
                        lblTotalTime.setText(totalTime);
                        FileInputStream inputStream = new FileInputStream("src/resources/music/" + name + ".mp3");
                        player = new MusicPlayer(inputStream);
                        songTable.setRowSelectionInterval(0, 0);
                        player.play();
                        isPlaying = true;
                        createIconPNG(playPauseButton, pause, PIC_W, PIC_H);
                    }


                } catch (FileNotFoundException ev) {
                    ev.printStackTrace();
                }
            }

        }
    };
    private ActionListener shuffleListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO-Add shuffle functionality
        }
    };

    private ActionListener playPauseListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isPlaying() && !songTable.isRowSelected(songTable.getSelectedRow())) {
                createIconPNG(playPauseButton, pause, PIC_W, PIC_H);
                playSong(0);
                songTable.setRowSelectionInterval(0, 0);
                player.play();
                return;
            }
            if (!isPlaying()) {
                isPlaying = true;
                if (songTable.isRowSelected(songTable.getSelectedRow())) {
                    createIconPNG(playPauseButton, pause, PIC_W, PIC_H);
                    player.play();
                }
            } else {
                isPlaying = false;
                if (songTable.isRowSelected(songTable.getSelectedRow())) {
                    createIconPNG(playPauseButton, play, PIC_W, PIC_H);
                    player.pause();
                }
            }
            //System.out.println(player.getPostion());
        }
    };
    private MouseListener songSelectListener = new MouseAdapter() {
        public void mousePressed(MouseEvent me) {
            JTable table = (JTable) me.getSource();
            if (me.getClickCount() == 2) {

                songList.add(songTable.getSelectedRow());
                songIterator = songList.listIterator();
                songIterator.next();
                int previous = (int) songIterator.previous();
                System.out.println(previous);

                playSong(table.getSelectedRow());
            }
        }
    };

    public void playSong(int row) {
        if (player != null && player.getPlayerStatus() == 1) {
            player.stop();
            player = null;
        }

        try {
            String name = (String) songTable.getValueAt(row, 0);
            String totalTime = (String) songTable.getValueAt(row, 2);
            String artist = (String) songTable.getValueAt(row, 1);
            lblArtist.setText(artist);
            lblTitle.setText(name);
            lblTotalTime.setText(totalTime);
            FileInputStream inputStream = new FileInputStream("src/resources/music/" + name + ".mp3");
            player = new MusicPlayer(inputStream);
            player.play();
            isPlaying = true;
            createIconPNG(playPauseButton, pause, PIC_W, PIC_H);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void shufflePlay(int row) {
        if (player != null && player.getPlayerStatus() == 1) {
            player.stop();
            player = null;
        }


        Random r = new Random(songTable.getRowCount());
        LinkedList<Integer> shuffleList = new LinkedList<>();

        for (int i = 0; i < songTable.getRowCount(); i++) {
            shuffleList.add(r.nextInt());
        }


//        try {
//            String name = (String) songTable.getValueAt(row, 0);
//            String totalTime = (String) songTable.getValueAt(row, 2);
//            String artist = (String) songTable.getValueAt(row, 1);
//            lblArtist.setText(artist);
//            lblTitle.setText(name);
//            lblTotalTime.setText(totalTime);
//            FileInputStream inputStream = new FileInputStream("src/resources/music/" + name + ".mp3");
//            player = new MusicPlayer(inputStream);
//            player.play();
//            isPlaying = true;
//            createIconPNG(playPauseButton, pause, PIC_W, PIC_H);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }


//    public Image loadAlbumCover(String album){
//
//    }

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
            //addSong(fileChooser.getSelectedFile().getName());
            //System.out.println(fileChooser.getSelectedFile().getName());
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

    private void createIconPNG(JLabel label, String name, int width, int height) {
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


    public ArrayList<String> getPlaylistNames() {
        return playlistNames;
    }

    public void setPlaylistNames(ArrayList<String> playlistNames) {
        this.playlistNames = playlistNames;
    }

    private boolean isPlaying() {
        return this.isPlaying;
    }

    private boolean isShuffle() {
        return this.isShuffle;
    }

    public int getPlaylistSize() {
        return playlistNames.size();
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

            item = new JMenuItem("Undo");
            menu.add(item);

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

            playlistMenu.add(playlistAddSub);
            for (int i = 0; i < json.getPlaylistNames().size(); i++) {
                item = new JMenuItem(json.getPlaylistNames().get(i));
                if (item.getText().equals("default")) {
                    item.setText("Library");
                }
                playlistAddSub.add(item);
            }
            JMenu viewMenu = new JMenu("View");
            menuBar.add(viewMenu);
            item = new JMenuItem("miniPlayer View");
            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    jFrame.setVisible(false);
                    miniPlayer.add(infoMidPanel);
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
            return menuBar;
        }
    }

    public CustomJSON getJSON(){
        return this.json;
    }

}
