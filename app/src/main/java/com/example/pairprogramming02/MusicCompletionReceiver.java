package com.example.pairprogramming02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MusicCompletionReceiver extends BroadcastReceiver {

    MainActivity mainActivity;

    public MusicCompletionReceiver() {
        //empty constructor
    }

    public MusicCompletionReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String musicName = intent.getStringExtra(MusicService.MUSICNAME);
        mainActivity.updateName(musicName);
    }
}