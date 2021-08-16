package com.rackluxury.rolex.adapters;

import java.util.Comparator;

public class ExpensiveData {
    private final String expensiveName;
    private final String expensiveDescription;
    private final String expensivePrice;
    private final int expensiveImage;
    private String key_id;
    private String favStatus;


    public ExpensiveData(String expensiveName, String expensiveDescription, String expensivePrice, int expensiveImage, String key_id, String favStatus) {
        this.expensiveName = expensiveName;
        this.expensiveDescription = expensiveDescription;
        this.expensivePrice = expensivePrice;
        this.expensiveImage = expensiveImage;
        this.favStatus = favStatus;
        this.key_id = key_id;

    }

    public String getExpensiveName() {
        return expensiveName;
    }

    public String getExpensiveDescription() {
        return expensiveDescription;
    }

    public String getExpensivePrice() {
        return expensivePrice;
    }

    public int getExpensiveImage() {
        return expensiveImage;
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


    public static final Comparator<ExpensiveData> ByNameAToZ = new Comparator<ExpensiveData>() {
        @Override
        public int compare(ExpensiveData two, ExpensiveData one) {
            return - String.valueOf(one.expensiveName).compareTo(String.valueOf(two.expensiveName));
        }
    };
    public static final Comparator<ExpensiveData> ByNameZToA = new Comparator<ExpensiveData>() {
        @Override
        public int compare(ExpensiveData one, ExpensiveData two) {
            return - String.valueOf(one.expensiveName).compareTo(String.valueOf(two.expensiveName));
        }
    };
    public static final Comparator<ExpensiveData> ByPriceLowToHigh = new Comparator<ExpensiveData>() {
        @Override
        public int compare(ExpensiveData two, ExpensiveData one) {
            int onePrice=Integer.parseInt(one.expensivePrice);
            int twoPrice=Integer.parseInt(two.expensivePrice);
            return -Integer.compare(onePrice, twoPrice);
        }
    };
    public static final Comparator<ExpensiveData> ByPriceHighToLow = new Comparator<ExpensiveData>() {
        @Override
        public int compare(ExpensiveData one, ExpensiveData two) {
            int onePrice=Integer.parseInt(one.expensivePrice);
            int twoPrice=Integer.parseInt(two.expensivePrice);
            return -Integer.compare(onePrice, twoPrice);
        }
    };

}
