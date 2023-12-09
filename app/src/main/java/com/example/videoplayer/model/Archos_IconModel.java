package com.example.videoplayer.model;

public class Archos_IconModel {
    private String iconTitle;
    private int imageView;

    public Archos_IconModel(int imageView, String iconTitle) {
        this.imageView = imageView;
        this.iconTitle = iconTitle;
    }

    public int getImageView() {
        return this.imageView;
    }

    public void setImageView(int imageView) {
        this.imageView = imageView;
    }

    public String getIconTitle() {
        return this.iconTitle;
    }

    public void setIconTitle(String iconTitle) {
        this.iconTitle = iconTitle;
    }
}
