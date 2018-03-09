package com.carson.androidquicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by carson on 2018/3/9.
 */
public class NewsList implements Parcelable {
    private long date;
    private List<News> stories;
//    private List<TopNews> top_stories;

//    public List<TopNews> getTop_stories() {
//        return top_stories;
//    }
//
//    public void setTop_stories(List<TopNews> top_stories) {
//        this.top_stories = top_stories;
//    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<News> getStories() {
        return stories;
    }

    public void setStories(List<News> stories) {
        this.stories = stories;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.date);
        dest.writeTypedList(stories);
//        dest.writeTypedList(top_stories);
    }

    public NewsList() {
    }

    protected NewsList(Parcel in) {
        this.date = in.readLong();
        this.stories = in.createTypedArrayList(News.CREATOR);
//        this.top_stories = in.createTypedArrayList(TopNews.CREATOR);
    }

    public static final Creator<NewsList> CREATOR = new Creator<NewsList>() {
        public NewsList createFromParcel(Parcel source) {
            return new NewsList(source);
        }

        public NewsList[] newArray(int size) {
            return new NewsList[size];
        }
    };
}

