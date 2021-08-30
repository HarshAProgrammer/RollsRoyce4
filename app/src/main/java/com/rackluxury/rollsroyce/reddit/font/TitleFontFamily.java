package com.rackluxury.rollsroyce.reddit.font;

import com.rackluxury.rollsroyce.R;

public enum TitleFontFamily {
    Default(R.style.TitleFontFamily, "Default"),
    BalsamiqSans(R.style.TitleFontFamily_BalsamiqSans, "BalsamiqSans"),
    BalsamiqSansBold(R.style.TitleFontFamily_BalsamiqSansBold, "BalsamiqSansBold"),
    NotoSans(R.style.TitleFontFamily_NotoSans, "NotoSans"),
    NotoSansBold(R.style.TitleFontFamily_NotoSansBold, "NotoSansBold"),
    RobotoCondensed(R.style.TitleFontFamily_RobotoCondensed, "RobotoCondensed"),
    RobotoCondensedBold(R.style.TitleFontFamily_RobotoCondensedBold, "RobotoCondensedBold"),
    HarmoniaSans(R.style.TitleFontFamily_HarmoniaSans, "HarmoniaSans"),
    HarmoniaSansBold(R.style.TitleFontFamily_HarmoniaSansBold, "HarmoniaSansBold"),
    Inter(R.style.TitleFontFamily_Inter, "Inter"),
    InterBold(R.style.TitleFontFamily_InterBold, "InterBold"),
    Manrope(R.style.TitleFontFamily_Manrope, "Manrope"),
    ManropeBold(R.style.TitleFontFamily_ManropeBold, "ManropeBold"),
    Sriracha(R.style.TitleFontFamily_Sriracha, "Sriracha");

    private final int resId;
    private final String title;

    TitleFontFamily(int resId, String title) {
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
