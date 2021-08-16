package com.rackluxury.rolex.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.rackluxury.rolex.R;

class LoginDialogue {
    private AlertDialog dialogue;
    private final Activity activity;

    LoginDialogue(Activity myActivity){
        activity = myActivity;
    }

    void startLoginDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflate = activity.getLayoutInflater();
        builder.setView(inflate.inflate(R.layout.login_dialogue, null));
        builder.setCancelable(true);

        dialogue = builder.create();
        dialogue.show();
    }

    void dismissDialogue(){
        dialogue.dismiss();
    }
}
