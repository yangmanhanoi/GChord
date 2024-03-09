package model;

import java.util.List;

public class VideoInfor {
    int id,tempo;
    String url, title, artist, artistUrl, songUrl,chordPosition,lyricPosition;
    List<String> urls=null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtistUrl() {
        return artistUrl;
    }

    public void setArtistUrl(String artistUrl) {
        this.artistUrl = artistUrl;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getChordPosition() {
        return chordPosition;
    }

    public void setChordPosition(String chordPosition) {
        this.chordPosition = chordPosition;
    }

    public String getLyricPosition() {
        return lyricPosition;
    }

    public void setLyricPosition(String lyricPosition) {
        this.lyricPosition = lyricPosition;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
