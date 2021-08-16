package com.rackluxury.rollsroyce.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.rackluxury.rollsroyce.R;

class FacebookShareDialogue {
    private AlertDialog dialogue;
    private final Activity activity;

    FacebookShareDialogue(Activity myActivity){
        activity = myActivity;
    }

    void startFacebookShareDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflate = activity.getLayoutInflater();
        builder.setView(inflate.inflate(R.layout.facebook_share_dialogue, null));
        builder.setCancelable(true);

        dialogue = builder.create();
        dialogue.show();
    }

    void dismissDialogue(){
        dialogue.dismiss();
    }
}
