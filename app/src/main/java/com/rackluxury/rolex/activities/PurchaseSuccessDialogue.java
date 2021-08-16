package com.rackluxury.rolex.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.rackluxury.rolex.R;

class PurchaseSuccessDialogue {
    private AlertDialog dialogue;
    private final Activity activity;

    PurchaseSuccessDialogue(Activity myActivity){
        activity = myActivity;
    }

    void startPurchaseSuccessDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflate = activity.getLayoutInflater();
        builder.setView(inflate.inflate(R.layout.purchase_success_dialogue, null));
        builder.setCancelable(true);

        dialogue = builder.create();
        dialogue.show();
    }

    void dismissDialogue(){
        dialogue.dismiss();
    }
}
