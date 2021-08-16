package com.rackluxury.rollsroyce.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.rackluxury.rollsroyce.R;

public class NoInternetDialogue {
    private AlertDialog dialogue;
    private final Activity activity;

    public NoInternetDialogue(Activity myActivity){
        activity = myActivity;
    }

    public void startNoInternetDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflate = activity.getLayoutInflater();
        builder.setView(inflate.inflate(R.layout.no_internet_dialogue, null));
        builder.setCancelable(true);

        dialogue = builder.create();
        dialogue.show();
    }

    public void dismissDialogue(){
        dialogue.dismiss();
    }
}
