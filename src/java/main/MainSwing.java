package main;


import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javazoom.jl.decoder.JavaLayerException;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

import static java.awt.Component.CENTER_ALIGNMENT;
import static javax.swing.SwingConstants.CENTER;
import static javax.swing.SwingConstants.VERTICAL;

//TODO- When all necessary methods are created, add Description comments for javadoc

//TODO-Add remove playlist functionality, which also means displaying available playlists to remove at runtime

//FIXME-Display playlists correctly after action is performed i.e remove or add playlist at runtime

//TODO- Change the previous and next images to be thicker, right now very thin looking

//TODO-Have a trail following the scrollbar

/**
 * Main class for application, handles most of the functionality of the app including JSON Parsing, Swing Component creation, and MP3 Data conversion from ID3 Tags
 */

public class MainSwing {
    private static final File jsonFile = new File("src/resources/json/library.json");
    private static final File musicDir = new File("src/resources/music");

    private static final String play = "play_button";
    private static final String pause = "pause_button";
    private static final String previous = "previous_button";
    private static final String next = "next_button";
    private static final String shuffle = "shuffle_button";
    private static final String help = "help_button";


    private FileInputStream fileInputStream = new FileInputStream("src/resources/music/test.mp3");

    private JTable songTable = new JTable();
    private JFrame jFrame;
    private JPanel libraryPanel;
    private JButton playPauseButton;
    private JButton previousButton;
    private JButton nextButton;
    private JButton shuffleButton;
    private JButton helpButton;
    private JScrollPane scrollPane;
    private final JFileChooser fileChooser = new JFileChooser();
    private final FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("MP3 Files", "mp3");
    private JLabel currentTime = new JLabel();

    private ArrayList<String> playlistNames = new ArrayList<>();

    private MusicPlayer player = new MusicPlayer(fileInputStream);

    private boolean isPlaying = false;

    private CustomJSON json;

    private String newTitle;
    private String newArtist;
    private String newDuration;


    public MainSwing() throws FileNotFoundException, JavaLayerException {
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new MainSwing().createDesign();
                } catch (FileNotFoundException | JavaLayerException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void createDesign() throws JavaLayerException {

        jFrame = new JFrame();
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        jFrame.setSize(screenSize.width / 2, screenSize.height);
        JPanel infoPanel = new JPanel();
        JPanel mainPanel = new JPanel();
        libraryPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel soundControlPanel = new JPanel();
        JPanel musicPanel = new JPanel(new FlowLayout());

        libraryPanel.setPreferredSize(new Dimension(120, jFrame.getHeight()));
        infoPanel.setPreferredSize(new Dimension(jFrame.getWidth(), 110));
        mainPanel.setPreferredSize(new Dimension(jFrame.getWidth(), jFrame.getHeight()));

        BoxLayout centerLayout = new BoxLayout(centerPanel, VERTICAL);
        BorderLayout mainLayout = new BorderLayout();
        FlowLayout playerLayout = new FlowLayout();
        BoxLayout westLayout = new BoxLayout(libraryPanel, BoxLayout.Y_AXIS);
        BoxLayout verticalLayout = new BoxLayout(infoPanel, BoxLayout.Y_AXIS);

        mainPanel.setLayout(mainLayout);
        soundControlPanel.setLayout(playerLayout);
        libraryPanel.setLayout(westLayout);
        centerPanel.setLayout(centerLayout);
        infoPanel.setLayout(verticalLayout);

        JLabel libraryHeader = new JLabel("Library");
        JLabel artistHeader = new JLabel("Artist");
        JLabel totalTime = new JLabel("Total Time");
        JLabel title = new JLabel("Title");
        JLabel artist = new JLabel("Artist");
        JSlider musicSlider = new JSlider(JSlider.HORIZONTAL);
        previousButton = new JButton();
        playPauseButton = new JButton();
        nextButton = new JButton();
        shuffleButton = new JButton();
        helpButton = new JButton();

        createIconPNG(previousButton, previous, 20, 20);
        createIconPNG(playPauseButton, play, 20, 20);
        createIconPNG(nextButton, next, 20, 20);
        createIconPNG(shuffleButton, shuffle, 20, 20);
        createIconPNG(helpButton, help, 20, 20);

        previousButton.addActionListener(previousListener);
        playPauseButton.addActionListener(playPauseListener);
        nextButton.addActionListener(nextListener);
        shuffleButton.addActionListener(shuffleListener);


        musicSlider.setValue(0);
        musicSlider.setPreferredSize(new Dimension(250, 20));

        title.setFont(title.getFont().deriveFont(15f));
        artist.setFont(artist.getFont().deriveFont(15f));
        libraryHeader.setFont(libraryHeader.getFont().deriveFont(15f));

        title.setAlignmentX(CENTER_ALIGNMENT);
        artist.setAlignmentX(CENTER_ALIGNMENT);
        libraryHeader.setAlignmentX(CENTER_ALIGNMENT);
        artistHeader.setAlignmentX(CENTER_ALIGNMENT);
        libraryHeader.setHorizontalAlignment(CENTER);

        playPauseButton.setOpaque(true);

        CustomMenuBar topMenu = new CustomMenuBar();

        int songTableWidth = (int) centerPanel.getSize().getWidth() - (int) libraryPanel.getSize().getWidth();
        int songTableHeight = (int) centerPanel.getSize().getHeight() - (int) infoPanel.getSize().getHeight();
        songTable.setPreferredSize(new Dimension(songTableWidth, songTableHeight));

        scrollPane = new JScrollPane(songTable);
        scrollPane.setPreferredSize(new Dimension((int) jFrame.getSize().getWidth(), (int) jFrame.getSize().getHeight()));

        //This has to be called before the songTable is added to the center panel
        //And anything that changes the songTable information as well i.e changing the name of a song

        json = new CustomJSON(songTable, scrollPane, libraryPanel, title, artist,totalTime, playlistNames);
        json.initializeJson();
        json.initializeAddedPlaylists();


        centerPanel.add(scrollPane);
        soundControlPanel.add(previousButton);
        soundControlPanel.add(playPauseButton);
        soundControlPanel.add(nextButton);
        soundControlPanel.add(shuffleButton);

        musicPanel.add(currentTime);
        musicPanel.add(musicSlider);
        musicPanel.add(totalTime);
        musicPanel.setAlignmentX(CENTER_ALIGNMENT);

        infoPanel.add(title);
        infoPanel.add(artist);
        infoPanel.add(soundControlPanel);
        infoPanel.add(musicPanel);


        libraryPanel.add(libraryHeader);
        json.loadPlaylistsToPanel();


        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(libraryPanel, BorderLayout.LINE_START);

        jFrame.setJMenuBar(topMenu.showMenuBar());

        jFrame.setContentPane(mainPanel);
        jFrame.setVisible(true);
        jFrame.setFocusable(true);

        json.fillEmptyRows();
        //player.play();

        System.out.println(player.getPlayerStatus());


    }



    private ActionListener previousListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO-Add functionality to play previous song...maybe have separate class for MP3 data?
        }
    };

    private ActionListener nextListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO-Add functionality to play next song...maybe have separate class for MP3 data?
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
            if (!isPlaying) {
                createIconPNG(playPauseButton, pause, 20, 20);
                isPlaying = true;
                player.play();
            } else {
                createIconPNG(playPauseButton, play, 20, 20);
                isPlaying = false;
                player.pause();
            }
            System.out.println(player.getPostion());
        }
    };


    private void showFileChooser() {
        fileChooser.setFileFilter(fileFilter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fileChooser.showOpenDialog(jFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File f = new File(fileChooser.getSelectedFile().getPath());
            try {
                FileUtils.copyFileToDirectory(f, musicDir);
                json.addSong(f.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //addSong(fileChooser.getSelectedFile().getName());
            System.out.println(fileChooser.getSelectedFile().getName());
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

    public int getPlaylistSize() {
        return playlistNames.size();
    }


    /**
     * Inner class that creates the JMenuBar for the main JFrame
     */
    private class CustomMenuBar extends JMenuBar {

        JMenuBar showMenuBar() {

            JMenu playlistAddSub = new JMenu("Add to Playlist");
            JMenu playlistOpenSub = new JMenu("Open Playlist");


            JMenuBar menuBar = new JMenuBar();
            menuBar.setPreferredSize(new Dimension((int) jFrame.getSize().getWidth(), 20));

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

            return menuBar;
        }
    }

}
