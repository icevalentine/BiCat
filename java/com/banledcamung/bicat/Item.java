package com.banledcamung.bicat;

public class Item {
    public final int TYPE_APP_RESOURCE = 1;
    public final int TYPE_USER_ADDED = 2;
    String Name;
    String description;
    String totalTime;
    String remainTime;
    int image;

    int pos;
    int type;

    boolean isFarvorite;


    public Item(String name, String description, String totalTime, String remainTime, int image, int pos) {
        Name = name;
        this.description = description;
        this.totalTime = totalTime;
        this.remainTime = remainTime;
        this.image = image;
        this.pos = pos;
        type = TYPE_APP_RESOURCE;
    }

    public String getName() {
        return Name;
    }

    public int getPos() {return pos;}

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(String remainTime) {
        this.remainTime = remainTime;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}

