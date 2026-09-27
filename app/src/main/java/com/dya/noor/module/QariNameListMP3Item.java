package com.dya.noor.module;

public class QariNameListMP3Item {

    private final int id;
    private final String name;
    private final String imageName;
    private final String folderName;
    private final String url;

    public QariNameListMP3Item(int id, String name, String imageName, String folderName, String url) {
        this.id = id;
        this.name = name;
        this.imageName = imageName;
        this.folderName = folderName;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageName() {
        return imageName;
    }

    public String getFolderName() {
        return folderName;
    }

    public String getUrl() {
        return url;
    }
}
