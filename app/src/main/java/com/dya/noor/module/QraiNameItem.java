package com.dya.noor.module;

public class QraiNameItem {


    public int id;
    public String name;
    public String url;
    public String databaseName;

    public QraiNameItem(int id, String name, String url, String databaseName) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.databaseName = databaseName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
