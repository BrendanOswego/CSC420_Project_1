package main;

import java.util.HashMap;
import java.util.List;

/**
 * Created by brendan<brendan.goldberg@cainkade.com> on 11/15/16.
 */
public class Playlist {

    HashMap<String,Song> songList;
    String playlistNames;


    public Playlist(String playlistNames, HashMap<String,Song> songList){
        this.playlistNames = playlistNames;
        this.songList = songList;
    }


}
