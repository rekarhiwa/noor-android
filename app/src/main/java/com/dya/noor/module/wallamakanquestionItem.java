package com.dya.noor.module;

public class wallamakanquestionItem {

    String id ;
    String shortTitle;
    String title;
    String ask;
    String question;
    String catId;
    String count;
    String kwrte;

    public wallamakanquestionItem(String id, String shortTitle, String title, String ask, String question, String catId, String count, String kwrte) {
        this.id = id;
        this.shortTitle = shortTitle;
        this.title = title;
        this.ask = ask;
        this.question = question;
        this.catId = catId;
        this.count = count;
        this.kwrte = kwrte;
    }

    public String getId() {
        return id;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getAsk() {
        return ask;
    }

    public String getQuestion() {
        return question;
    }

    public String getCatId() {
        return catId;
    }

    public String getCount() {
        return count;
    }

    public String getKwrte() {
        return kwrte;
    }
}
