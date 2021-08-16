package com.rackluxury.rolex.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.rackluxury.rolex.R;

class ExpensiveDialogue
{
    private AlertDialog dialogue;
    private final Activity activity;

    ExpensiveDialogue(Activity myActivity){
        activity = myActivity;
    }

    void startExpensiveDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.expensive_dialogue, null));
        builder.setCancelable(true);


        dialogue = builder.create();
        dialogue.show();
    }


    void dismissDialogue(){
        dialogue.dismiss();
    }
}
