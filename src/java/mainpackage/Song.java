package mainpackage;

class Song {

    private String title;
    private String artist;
    private String album;
    private String duration;
    private String id;
    private Song song;

    Song(String id, String title, String artist, String album, String duration) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }

    public Song getSong(String id){
        if(id.equals(song.getId())){
            return new Song(song.getId(),song.getTitle(),song.getArtist(),song.getAlbum(),song.getDuration());
        }
        return null;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getArtist() {
        return artist;
    }

    void setArtist(String artist) {
        this.artist = artist;
    }

    String getAlbum() {
        return album;
    }

    void setAlbum(String album) {
        this.album = album;
    }

    String getDuration() {
        return duration;
    }

    void setDuration(String duration) {
        this.duration = duration;
    }

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }
}
