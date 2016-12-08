package mainpackage;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Created by Akeem Davis on 12/5/2016.
 */
public class ArtistView {
    HashMap<String,LinkedList<Song>> sortedArtistHashMap;
    HashMap<String,LinkedList<Song>> sortedAlbumHashMap;
    TreeSet<String> setOfArtist;
    TreeSet<String> setOfAlbums;
    CustomJSON json;
    List<Song> listOfSongs;
    JFrame mainFrame;
    JPanel infoMidPanel;
    JPanel mainPanel;
    JPanel artistPanel;
    JPanel albumPanel;
    boolean panelSelected = false;
    private JTable songTable = new JTable();
    private String[] colNames = {"Song","Artist", "Album", "Duration"};
    TableModel tableModel = new TableModel(colNames, 0);
    JPanel currentlySelectedPanel;
    JScrollPane scrollPane;
    MainSwing ms;


    ArtistView(MainSwing mainSwing){
        ms = mainSwing;
    }
    public void clearAVSongTable(){
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
    }
     public void setUpViewForArtist(JFrame main,JPanel infoMidPanel){
         panelSelected = false;
         scrollPane = new JScrollPane(songTable);
         songTable.addMouseListener(ms.songSelectListener);
         setupTableMethods();
         mainFrame = main;
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         mainFrame.setSize((screenSize.width / 2) + 500, screenSize.height-50);
         this.infoMidPanel = infoMidPanel;
         mainPanel = new JPanel();
         mainPanel.setLayout(new MigLayout("fill"));
         mainPanel.add(infoMidPanel,"north");
         artistPanel = new JPanel();
         artistPanel.setPreferredSize(new Dimension(700,870));
         artistPanel.setLayout(new MigLayout("fill,wrap 3"));
         for(String artist: setOfArtist){
             if(artist.equals("UnknownArtist")){
                 if(sortedArtistHashMap.get(artist).size() >= 1) {
                     JPanel cell = new JPanel();
                     cell.setLayout(new MigLayout());
                     cell.setBorder(BorderFactory.createRaisedBevelBorder());
                     cell.addMouseListener(new CellListener());
                     PicturePanel picturePanel = new PicturePanel(sortedArtistHashMap.get(artist).getFirst().getAlbum());
                     picturePanel.setBorder(BorderFactory.createLineBorder(Color.black));
                     picturePanel.repaint();
                     JLabel artistName = new JLabel(artist);
                     cell.add(picturePanel, "grow,push,wrap");
                     cell.add(artistName, "grow,push");
                     artistPanel.add(cell, "push,grow,gapleft 10,gapright 10");
                 }
             }else {
                 JPanel cell = new JPanel();
                 cell.setLayout(new MigLayout());
                 cell.setBorder(BorderFactory.createRaisedBevelBorder());
                 cell.addMouseListener(new CellListener());
                 PicturePanel picturePanel = new PicturePanel(sortedArtistHashMap.get(artist).getFirst().getAlbum());
                 picturePanel.setBorder(BorderFactory.createLineBorder(Color.black));
                 picturePanel.repaint();
                 JLabel artistName = new JLabel(artist);
                 cell.add(picturePanel, "wrap");
                 cell.add(artistName, "grow,push");
                 artistPanel.add(cell, "push,growy");
             }
         }
         mainPanel.add(new JScrollPane(artistPanel),"push,split 2");
         mainPanel.add(scrollPane,"push,grow");
         mainFrame.setContentPane(mainPanel);
         mainFrame.revalidate();
    }
    public void setUpViewForAlbum(JFrame main,JPanel infoMidPanel){
        panelSelected = false;
        scrollPane = new JScrollPane(songTable);
        songTable.addMouseListener(ms.songSelectListener);
        setupTableMethods();
        mainFrame = main;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setSize((screenSize.width / 2) + 500, screenSize.height-50);
        this.infoMidPanel = infoMidPanel;
        mainPanel = new JPanel();
        mainPanel.setLayout(new MigLayout("fill"));
        mainPanel.add(infoMidPanel,"north");
        albumPanel = new JPanel();
        albumPanel.setPreferredSize(new Dimension(700,870));
        albumPanel.setLayout(new MigLayout("fill,wrap 3"));
        for(String album: setOfAlbums){
            if(album.equals("UnknownAlbum")){
                if(sortedAlbumHashMap.get(album).size() >= 1) {
                    JPanel cell = new JPanel();
                    cell.setLayout(new MigLayout());
                    cell.setBorder(BorderFactory.createRaisedBevelBorder());
                    cell.addMouseListener(new CellListener());
                    PicturePanel picturePanel = new PicturePanel(sortedAlbumHashMap.get(album).getFirst().getAlbum());
                    picturePanel.setBorder(BorderFactory.createLineBorder(Color.black));
                    picturePanel.repaint();
                    JLabel artistName = new JLabel(album);
                    cell.add(picturePanel, "grow,push,wrap");
                    cell.add(artistName, "grow,push");
                    albumPanel.add(cell, "push,grow,gapleft 10,gapright 10");
                }
            }else {
                JPanel cell = new JPanel();
                cell.setLayout(new MigLayout());
                cell.setBorder(BorderFactory.createRaisedBevelBorder());
                cell.addMouseListener(new CellListener());
                PicturePanel picturePanel = new PicturePanel(sortedAlbumHashMap.get(album).getFirst().getAlbum());
                picturePanel.setBorder(BorderFactory.createLineBorder(Color.black));
                picturePanel.repaint();
                JLabel artistName = new JLabel(album);
                cell.add(picturePanel, "wrap");
                cell.add(artistName, "grow,push");
                albumPanel.add(cell, "push,growy");
            }
        }
        mainPanel.add(new JScrollPane(albumPanel),"push,split 2");
        mainPanel.add(scrollPane,"push,grow");
        mainFrame.setContentPane(mainPanel);
        mainFrame.revalidate();
    }
    private class CellListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent event) {
                    /* source is the object that got clicked
                     *
                     * If the source is actually a JPanel,
                     * then will the object be parsed to JPanel
                     * since we need the setBackground() method
                     */
            while (tableModel.getRowCount() > 0) {
                tableModel.removeRow(0);
            }
            Object source = event.getSource();
            if (source instanceof JPanel) {
                JPanel panelPressed = (JPanel) source;
                if (!panelSelected) {
                    panelPressed.setBackground(Color.cyan);
                    panelSelected = true;
                    currentlySelectedPanel = panelPressed;
                    Component c = panelPressed.getComponent(1);
                    if (ms.currentState.equals(MainSwing.PlayerState.ARTIST)) {
                        if (c instanceof JLabel) {
                            String artist = ((JLabel) c).getText();
                            for (Song song : sortedArtistHashMap.get(artist)) {
                                Object[] row = {song.getTitle(), song.getArtist(), song.getAlbum(), song.getDuration()};
                                tableModel.addRow(row);
                            }
                            scrollPane.getViewport().revalidate();
                        }
                        } else if (ms.currentState.equals(MainSwing.PlayerState.ALBUM)) {
                            c = panelPressed.getComponent(1);
                            if (c instanceof JLabel) {
                                String artist = ((JLabel) c).getText();
                                for (Song song : sortedAlbumHashMap.get(artist)) {
                                    Object[] row = {song.getTitle(), song.getArtist(), song.getAlbum(), song.getDuration()};
                                    tableModel.addRow(row);
                                }
                                scrollPane.getViewport().revalidate();
                            }
                        }
                    } else {
                            if (currentlySelectedPanel != null) {
                                currentlySelectedPanel.setBackground(null);
                                currentlySelectedPanel = panelPressed;
                                panelPressed.setBackground(Color.cyan);
                                Component c = panelPressed.getComponent(1);
                                if (c instanceof JLabel) {
                                    if (ms.currentState.equals(MainSwing.PlayerState.ARTIST)) {
                                        String artist = ((JLabel) c).getText();
                                        for (Song song : sortedArtistHashMap.get(artist)) {
                                            Object[] row = {song.getTitle(), song.getArtist(), song.getAlbum(), song.getDuration()};
                                            tableModel.addRow(row);
                                        }
                                        scrollPane.getViewport().revalidate();
                                    } else if (ms.currentState.equals(MainSwing.PlayerState.ALBUM)) {
                                        String artist = ((JLabel) c).getText();
                                        for (Song song : sortedAlbumHashMap.get(artist)) {
                                            Object[] row = {song.getTitle(), song.getArtist(), song.getAlbum(), song.getDuration()};
                                            tableModel.addRow(row);
                                        }
                                        scrollPane.getViewport().revalidate();
                                    }
                                }
                            }
                        }
                    }

            }
        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}
        }


    public void updateListOfSongs(List<Song> meep,CustomJSON json){
        listOfSongs = meep;
        sortListByArtist();
        sortListByAlbum();
        if(this.json == null) {
            this.json = json;
        }
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
                json.editSongInformation((String) songTable.getValueAt(modelRow, 0), (String) songTable.getValueAt(modelRow, 1));
                //TODO Add a new Frame that shows all the information and once the user presses enter it edits the info for that row
            }
        });
        optionMenu.add(itemEdit);
        JMenu itemAdd = new JMenu("Add To Playlist");

        for (int i = 0; i < json.getPlaylistNames().size(); i++) {
            JMenuItem item = new JMenuItem(json.getPlaylistNames().get(i));
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    json.addSelectedSongToPlaylist(item.getText());
                }
            });
            itemAdd.add(item);
        }
        optionMenu.add(itemAdd);
        JMenuItem itemDelete = new JMenuItem("Delete");
        itemDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                json.destroySelectedSong();
            }
        });
        optionMenu.add(itemDelete);

        return optionMenu;
    }

    private void sortListByArtist(){
            sortedArtistHashMap = new HashMap<>();
            setOfArtist = new TreeSet<String>();
            String unknownArtist = "UnknownArtist";
             setOfArtist.add(unknownArtist);
            LinkedList<Song> temp = new LinkedList<>();
            sortedArtistHashMap.put(unknownArtist,temp);
            for(Song song: listOfSongs){
                if(song.getArtist() == null || song.getArtist().equals("")){
                    sortedArtistHashMap.get("UnknownArtist").add(song);
                }else if(!sortedArtistHashMap.containsKey(song.getArtist())){
                    setOfArtist.add(song.getArtist());
                    temp = new LinkedList<>();
                    temp.add(song);
                    sortedArtistHashMap.put(song.getArtist(),temp);
                } else{
                    sortedArtistHashMap.get(song.getArtist()).add(song);
                }
            }
        if(ms.currentState.equals(MainSwing.PlayerState.ARTIST)){
            if(artistPanel != null){
                artistPanel.removeAll();
                artistPanel.setPreferredSize(new Dimension(700,870));
                artistPanel.setLayout(new MigLayout("fill,wrap 3"));
                for(String artist: setOfArtist){
                    if(artist.equals("UnknownArtist")){
                        if(sortedArtistHashMap.get(artist).size() >= 1) {
                            JPanel cell = new JPanel();
                            cell.setLayout(new MigLayout());
                            cell.setBorder(BorderFactory.createRaisedBevelBorder());
                            cell.addMouseListener(new CellListener());
                            PicturePanel picturePanel = new PicturePanel(sortedArtistHashMap.get(artist).getFirst().getAlbum());
                            picturePanel.setBorder(BorderFactory.createLineBorder(Color.black));
                            picturePanel.repaint();
                            JLabel artistName = new JLabel(artist);
                            cell.add(picturePanel, "grow,push,wrap");
                            cell.add(artistName, "grow,push");
                            artistPanel.add(cell, "push,grow,gapleft 10,gapright 10");
                        }
                    }else {
                        JPanel cell = new JPanel();
                        cell.setLayout(new MigLayout());
                        cell.setBorder(BorderFactory.createRaisedBevelBorder());
                        cell.addMouseListener(new CellListener());
                        PicturePanel picturePanel = new PicturePanel(sortedArtistHashMap.get(artist).getFirst().getAlbum());
                        picturePanel.setBorder(BorderFactory.createLineBorder(Color.black));
                        picturePanel.repaint();
                        JLabel artistName = new JLabel(artist);
                        cell.add(picturePanel, "wrap");
                        cell.add(artistName, "grow,push");
                        artistPanel.add(cell, "push,growy");
                    }
                }
                mainPanel.revalidate();
            }
        }
    }
    private void sortListByAlbum(){
        sortedAlbumHashMap = new HashMap<>();
        setOfAlbums = new TreeSet<String>();
        String unknownArtist = "UnknownAlbum";
        setOfAlbums.add(unknownArtist);
        LinkedList<Song> temp = new LinkedList<>();
        sortedAlbumHashMap.put(unknownArtist,temp);
        for(Song song: listOfSongs){
            if(song.getAlbum() == null || song.getAlbum().equals("")){
                sortedAlbumHashMap.get("UnknownAlbum").add(song);
            }else if(!sortedAlbumHashMap.containsKey(song.getAlbum())){
                setOfAlbums.add(song.getAlbum());
                temp = new LinkedList<>();
                temp.add(song);
                sortedAlbumHashMap.put(song.getAlbum(),temp);
            } else{
                sortedAlbumHashMap.get(song.getAlbum()).add(song);
            }
        }
        if(ms.currentState.equals(MainSwing.PlayerState.ALBUM)){
            if(albumPanel != null){
                albumPanel.removeAll();
                albumPanel.setPreferredSize(new Dimension(700,870));
                albumPanel.setLayout(new MigLayout("fill,wrap 3"));
                for(String album: setOfAlbums){
                    if(album.equals("UnknownAlbum")){
                        if(sortedAlbumHashMap.get(album).size() >= 1) {
                            JPanel cell = new JPanel();
                            cell.setLayout(new MigLayout());
                            cell.setBorder(BorderFactory.createRaisedBevelBorder());
                            cell.addMouseListener(new CellListener());
                            PicturePanel picturePanel = new PicturePanel(sortedAlbumHashMap.get(album).getFirst().getAlbum());
                            picturePanel.setBorder(BorderFactory.createLineBorder(Color.black));
                            picturePanel.repaint();
                            JLabel albumName = new JLabel(album);
                            cell.add(picturePanel, "grow,push,wrap");
                            cell.add(albumName, "grow,push");
                            albumPanel.add(cell, "push,grow,gapleft 10,gapright 10");
                        }
                    }else {
                        JPanel cell = new JPanel();
                        cell.setLayout(new MigLayout());
                        cell.setBorder(BorderFactory.createRaisedBevelBorder());
                        cell.addMouseListener(new CellListener());
                        PicturePanel picturePanel = new PicturePanel(sortedAlbumHashMap.get(album).getFirst().getAlbum());
                        picturePanel.setBorder(BorderFactory.createLineBorder(Color.black));
                        picturePanel.repaint();
                        JLabel albumName = new JLabel(album);
                        cell.add(picturePanel, "wrap");
                        cell.add(albumName, "grow,push");
                        albumPanel.add(cell, "push,growy");
                    }
                }
                mainPanel.revalidate();
            }
        }
    }

    class PicturePanel extends JPanel{
        String albumName;
        PicturePanel(String an){
            albumName = an;
            setPreferredSize(new Dimension(200, 200));
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
                if (json != null) {
                    if (json.getAlbumImage(albumName) != null) {
                        g.drawImage(json.getAlbumImage(albumName), 0, 0,
                                (int) getPreferredSize().getWidth(),
                                (int) getPreferredSize().getHeight(), null);
                    }
                }

        }


    }
    public JTable getAVSongTable(){
        return songTable;
    }
}


