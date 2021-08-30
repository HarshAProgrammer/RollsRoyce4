package com.rackluxury.rollsroyce.reddit.font;

import com.rackluxury.rollsroyce.R;

public enum TitleFontStyle {
    Small(R.style.TitleFontStyle_Small, "Small"),
    Normal(R.style.TitleFontStyle_Normal, "Normal"),
    Large(R.style.TitleFontStyle_Large, "Large"),
    XLarge(R.style.TitleFontStyle_XLarge, "XLarge");

    private final int resId;
    private final String title;

    TitleFontStyle(int resId, String title) {
        this.resId = resId;
        this.title = title;
    }

    public int getResId() {
        return resId;
    }

    public String getTitle() {
        return title;
    }
}
