package com.rackluxury.rolex.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.rackluxury.rolex.R;

class ShareDialogue {
    private AlertDialog dialogue;
    private final Activity activity;

    ShareDialogue(Activity myActivity){
        activity = myActivity;
    }

    void startShareDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflate = activity.getLayoutInflater();
        builder.setView(inflate.inflate(R.layout.share_dialogue, null));
        builder.setCancelable(true);

        dialogue = builder.create();
        dialogue.show();
    }

    void dismissDialogue(){
        dialogue.dismiss();
    }
}
