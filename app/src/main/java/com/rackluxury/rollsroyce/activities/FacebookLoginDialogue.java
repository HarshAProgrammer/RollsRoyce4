package com.rackluxury.rollsroyce.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.rackluxury.rollsroyce.R;

class FacebookLoginDialogue {
    private AlertDialog dialogue;
    private final Activity activity;

    FacebookLoginDialogue(Activity myActivity){
        activity = myActivity;
    }

    void startFacebookLoginDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflate = activity.getLayoutInflater();
        builder.setView(inflate.inflate(R.layout.facebook_login_dialogue, null));
        builder.setCancelable(true);

        dialogue = builder.create();
        dialogue.show();
    }

    void dismissDialogue(){
        dialogue.dismiss();
    }
}
