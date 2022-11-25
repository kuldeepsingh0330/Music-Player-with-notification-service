package com.ransankul.musicplayer.Model;

public class Song {
    private String id;
    private String title;
    private String album;
    private String artist;
    private String path;
    private String songImage;
    private long duration;

    public String getSongImage() {
        return songImage;
    }

    public void setSongImage(String songImage) {
        this.songImage = songImage;
    }

    public Song(String id, String title, String album, String artist, String path, String songImage, long duration) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.path = path;
        this.songImage = songImage;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
