
package com.rackluxury.rolex.blog;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostListBlog {

    @SerializedName("nextPageToken")
    @Expose
    private String nextPageToken;
    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("items")
    @Expose
    private List<BlogItem> blogItems = null;
    @SerializedName("etag")
    @Expose
    private String etag;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<BlogItem> getBlogItems() {
        return blogItems;
    }

    public void setBlogItems(List<BlogItem> blogItems) {
        this.blogItems = blogItems;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
}
