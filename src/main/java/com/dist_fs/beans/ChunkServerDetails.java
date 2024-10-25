package com.dist_fs.beans;

public class ChunkServerDetails {
    private boolean live;
    private String url;

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "" + live;
    }
}
