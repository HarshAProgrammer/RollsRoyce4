package com.rackluxury.rollsroyce.reddit.user;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserData {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private final String name;
    @ColumnInfo(name = "icon")
    private final String iconUrl;
    @ColumnInfo(name = "banner")
    private final String banner;
    @ColumnInfo(name = "link_karma")
    private final int linkKarma;
    @ColumnInfo(name = "comment_karma")
    private final int commentKarma;
    @ColumnInfo(name = "awarder_karma")
    private int awarderKarma;
    @ColumnInfo(name = "awardee_karma")
    private int awardeeKarma;
    @ColumnInfo(name = "total_karma")
    private final int totalKarma;
    @ColumnInfo(name = "created_utc")
    private final long cakeday;
    @ColumnInfo(name = "is_gold")
    private final boolean isGold;
    @ColumnInfo(name = "is_friend")
    private final boolean isFriend;
    @ColumnInfo(name = "can_be_followed")
    private final boolean canBeFollowed;
    @ColumnInfo(name = "over_18")
    private final boolean isNSFW;
    @ColumnInfo(name = "description")
    private final String description;
    @Ignore
    private boolean isSelected;

    public UserData(@NonNull String name, String iconUrl, String banner, int linkKarma, int commentKarma,
                    int awarderKarma, int awardeeKarma, int totalKarma, long cakeday, boolean isGold,
                    boolean isFriend, boolean canBeFollowed, boolean isNSFW, String description) {
        this.name = name;
        this.iconUrl = iconUrl;
        this.banner = banner;
        this.commentKarma = commentKarma;
        this.linkKarma = linkKarma;
        this.awarderKarma = awarderKarma;
        this.awarderKarma = awardeeKarma;
        this.totalKarma = totalKarma;
        this.cakeday = cakeday;
        this.isGold = isGold;
        this.isFriend = isFriend;
        this.canBeFollowed = canBeFollowed;
        this.isNSFW = isNSFW;
        this.description = description;
        this.isSelected = false;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getBanner() {
        return banner;
    }

    public int getLinkKarma() {
        return linkKarma;
    }

    public int getCommentKarma() {
        return commentKarma;
    }

    public int getAwarderKarma() {
        return awarderKarma;
    }

    public int getAwardeeKarma() {
        return awardeeKarma;
    }

    public int getTotalKarma() {
        return totalKarma;
    }

    public long getCakeday() {
        return cakeday;
    }

    public boolean isGold() {
        return isGold;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public boolean isCanBeFollowed() {
        return canBeFollowed;
    }

    public boolean isNSFW() {
        return isNSFW;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
