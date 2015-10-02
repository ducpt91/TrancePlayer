package object;

import java.util.ArrayList;

/**
 * Created by duc.t.pham on 9/23/15.
 */
public class Song {
    private int id;
    private String url;
    private String title;
    private boolean isPlaying;
    private ArrayList<Track> listTracks;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
    }

    public ArrayList<Track> getListTracks() {
        return listTracks;
    }

    public void setListTracks(ArrayList<Track> listTracks) {
        this.listTracks = listTracks;
    }
}
