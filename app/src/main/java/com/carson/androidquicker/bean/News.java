package com.carson.androidquicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by carson on 2018/3/9.
 */

public class News implements Parcelable {
    private int type;
    private int id;
    @SerializedName("ga_prefix")
    private String prefix;
    private String title;
        private List<String> images;
//    private String images;
    private boolean isRead = false;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGa_prefix() {
        return prefix;
    }

    public void setGa_prefix(String ga_prefix) {
        this.prefix = ga_prefix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeInt(this.id);
        dest.writeString(this.prefix);
        dest.writeString(this.title);
//        dest.writeString(this.images);
        dest.writeList(this.images);
        dest.writeByte(isRead ? (byte) 1 : (byte) 0);
    }

    public News() {
    }

    protected News(Parcel in) {
        this.type = in.readInt();
        this.id = in.readInt();
        this.prefix = in.readString();
        this.title = in.readString();
//        this.images = in.rea;
        this.isRead = in.readByte() != 0;
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        public News[] newArray(int size) {
            return new News[size];
        }
    };
}
