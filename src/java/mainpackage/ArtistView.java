package mainpackage;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
    private JTable songTable;



     public void setUpViewForArtist(JFrame main,JPanel infoMidPanel,JButton switcher,JLabel viewTitle){
         mainFrame = main;
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         mainFrame.setSize((screenSize.width / 2) + 500, screenSize.height-50);
         this.infoMidPanel = infoMidPanel;
         infoMidPanel.add(viewTitle,"cell 0 0");
         infoMidPanel.add(switcher, "cell 0 0");
         mainPanel = new JPanel();
         mainPanel.setLayout(new MigLayout("fill"));
         mainPanel.add(infoMidPanel,"north");
         JPanel artistPanel = new JPanel();
         artistPanel.setPreferredSize(new Dimension(700,870));
         artistPanel.setLayout(new MigLayout("fill,wrap 3"));
         for(String artist: setOfArtist){
             if(artist.equals("UnknownArtist")){
                 if(sortedArtistHashMap.get(artist).size() >= 1) {
                     JPanel cell = new JPanel();
                     cell.setLayout(new MigLayout());
                     cell.setBorder(BorderFactory.createRaisedBevelBorder());
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
                 PicturePanel picturePanel = new PicturePanel(sortedArtistHashMap.get(artist).getFirst().getAlbum());
                 picturePanel.setBorder(BorderFactory.createLineBorder(Color.black));
                 picturePanel.repaint();
                 JLabel artistName = new JLabel(artist);
                 cell.add(picturePanel, "wrap");
                 cell.add(artistName, "grow,push");
                 artistPanel.add(cell, "push,growy");
             }
         }
         mainFrame.addComponentListener(new ComponentAdapter() {
             @Override
             public void componentResized(ComponentEvent e) {
                 super.componentResized(e);
                 System.out.println(mainFrame.getSize());
             }
         });
         mainPanel.add(new JScrollPane(artistPanel),"push");
         mainFrame.setContentPane(mainPanel);
         mainFrame.revalidate();
    }
    public void updateListOfSongs(List<Song> meep,CustomJSON json){
        listOfSongs = meep;
        sortListByArtist();
        if(this.json == null) {
            this.json = json;
        }
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
                    System.out.println("its lit");
                    if (json.getAlbumImage(albumName) != null) {
                        System.out.println("Drawing in AritistView");
                        g.drawImage(json.getAlbumImage(albumName), 0, 0,
                                (int) getPreferredSize().getWidth(),
                                (int) getPreferredSize().getHeight(), null);
                    }
                }

        }
    }

}
