package com.dya.noor.module;

public class BookItem {

    int id;
    String name_kr;
    String name_ar;
    int count;
    int type;
    int sort;
    int sum_of_writer;


    public BookItem(int id, String name_kr, String name_ar,
                    int count, int type, int sort, int sum_of_writer) {
        this.id = id;
        this.name_kr = name_kr;
        this.name_ar = name_ar;
        this.count = count;
        this.type = type;
        this.sort = sort;
        this.sum_of_writer = sum_of_writer;

    }

    public int getId() {
        return id;
    }

    public String getName_kr() {
        return name_kr;
    }

    public String getName_ar() {
        return name_ar;
    }

    public int getCount() {
        return count;
    }

    public int getType() {
        return type;
    }

    public int getSort() {
        return sort;
    }

    public int getSum_of_writer() {
        return sum_of_writer;
    }
}

