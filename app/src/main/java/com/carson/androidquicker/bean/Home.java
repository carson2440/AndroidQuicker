package com.carson.androidquicker.bean;

/**
 * Created by carson on 2018/3/20.
 */

public class Home {
    private String title;
    private String message;

    public Home(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
