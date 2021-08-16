package com.rackluxury.rolex.adapters;

import java.util.Comparator;

public class CategoriesData {
    private final String categoriesName;
    private final String categoriesDescription;
    private final String categoriesPrice;
    private final int categoriesImage;
    private String key_id;
    private String favStatus;

    public CategoriesData(String categoriesName, String categoriesDescription, String categoriesPrice, int categoriesImage, String key_id, String favStatus) {
        this.categoriesName = categoriesName;
        this.categoriesDescription = categoriesDescription;
        this.categoriesPrice = categoriesPrice;
        this.categoriesImage = categoriesImage;
        this.favStatus = favStatus;
        this.key_id = key_id;
    }

    public String getCategoriesName() {
        return categoriesName;
    }

    public String getCategoriesDescription() {
        return categoriesDescription;
    }

    public String getCategoriesPrice() {
        return categoriesPrice;
    }

    public int getCategoriesImage() {
        return categoriesImage;
    }

    public String getKey_id() {
        return key_id;
    }

    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }

    public String getFavStatus() {
        return favStatus;
    }

    public void setFavStatus(String favStatus) {
        this.favStatus = favStatus;
    }

    public static final Comparator<CategoriesData> ByNameAToZ = new Comparator<CategoriesData>() {
        @Override
        public int compare(CategoriesData two, CategoriesData one) {
            return - String.valueOf(one.categoriesName).compareTo(String.valueOf(two.categoriesName));
        }
    };
    public static final Comparator<CategoriesData> ByNameZToA = new Comparator<CategoriesData>() {
        @Override
        public int compare(CategoriesData one, CategoriesData two) {
            return - String.valueOf(one.categoriesName).compareTo(String.valueOf(two.categoriesName));
        }
    };
    public static final Comparator<CategoriesData> ByPriceLowToHigh = new Comparator<CategoriesData>() {
        @Override
        public int compare(CategoriesData two, CategoriesData one) {
            int onePrice=Integer.parseInt(one.categoriesPrice);
            int twoPrice=Integer.parseInt(two.categoriesPrice);
            return -Integer.compare(onePrice, twoPrice);
        }
    };
    public static final Comparator<CategoriesData> ByPriceHighToLow = new Comparator<CategoriesData>() {
        @Override
        public int compare(CategoriesData one, CategoriesData two) {
            int onePrice=Integer.parseInt(one.categoriesPrice);
            int twoPrice=Integer.parseInt(two.categoriesPrice);
            return -Integer.compare(onePrice, twoPrice);
        }
    };
}
