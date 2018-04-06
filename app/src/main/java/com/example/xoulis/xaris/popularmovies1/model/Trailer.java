package com.example.xoulis.xaris.popularmovies1.model;

import com.google.gson.annotations.SerializedName;

public class Trailer {
    @SerializedName("name")
    private String name;
    @SerializedName("key")
    private String youtubeKey;

    public Trailer(String name, String youtubeKey) {
        this.name = name;
        this.youtubeKey = youtubeKey;
    }

    public String getName() {
        return name;
    }

    public String getYoutubeKey() {
        return youtubeKey;
    }
}
