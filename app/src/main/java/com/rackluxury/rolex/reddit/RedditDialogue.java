package com.rackluxury.rolex.reddit;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.rackluxury.rolex.R;

public class RedditDialogue {
    private AlertDialog dialogue;
    private final Activity activity;

    public RedditDialogue(Activity myActivity){
        activity = myActivity;
    }

    public void startRedditDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflate = activity.getLayoutInflater();
        builder.setView(inflate.inflate(R.layout.reddit_dialogue, null));
        builder.setCancelable(true);

        dialogue = builder.create();
        dialogue.show();
    }

    public void dismissDialogue(){
        dialogue.dismiss();
    }
}
