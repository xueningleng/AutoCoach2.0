package com.example.autocoach20.Activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;

public class AudioComponents extends View {

    MediaPlayer coin = new MediaPlayer();
    public void playCoinsSound(){
        coin.start();
    }
    MediaPlayer improved = new MediaPlayer();
    public void playImprovedSound(){
        improved.start();
    }

    public AudioComponents(Context context) {
        super(context);
    }



}
