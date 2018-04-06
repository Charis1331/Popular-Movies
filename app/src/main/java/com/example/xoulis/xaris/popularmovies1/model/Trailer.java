package com.example.xoulis.xaris.popularmovies1.model;

import com.google.gson.annotations.SerializedName;

public class Trailer {
    @SerializedName("name")
    private String name;
    @SerializedName("site")
    private String site;

    public Trailer(String name, String site) {
        this.name = name;
        this.site = site;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
