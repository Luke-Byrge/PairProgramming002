package com.example.pairprogramming02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.pairprogramming02.MusicCompletionReceiver;
import com.example.pairprogramming02.MusicService;
import com.example.pairprogramming02.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView music;
    Button play;

    MusicService musicService;
    MusicCompletionReceiver musicCompletionReceiver;
    Intent startMusicServiceIntent;
    boolean isBound = false;
    boolean isInitialized = false;

    public static final String INITIALIZE_STATUS = "intialization status";
    public static final String MUSIC_PLAYING = "music playing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        music= (TextView) findViewById(R.id.music);
        play= (Button) findViewById(R.id.play);
        play.setOnClickListener(this);

        if(savedInstanceState != null){
            isInitialized = savedInstanceState.getBoolean(INITIALIZE_STATUS);
            music.setText(savedInstanceState.getString(MUSIC_PLAYING));
        }

        startMusicServiceIntent= new Intent(this, MusicService.class);

        if(!isInitialized){
            startService(startMusicServiceIntent);
            isInitialized= true;
        }

        musicCompletionReceiver = new MusicCompletionReceiver(this);

    }

    @Override
    public void onClick(View view) {
        if (isBound) {
            switch (musicService.getPlayingStatus()){
                case 0:
                    musicService.startMusic();
                    play.setText("Pause");
                    break;
                case 1:
                    musicService.pauseMusic();
                    play.setText("Resume");
                    break;
                case 2:
                    musicService.resumeMusic();
                    play.setText("Pause");
                    break;
            }
        }
    }


    public void updateName(String musicName) {

        music.setText(musicName);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isInitialized && !isBound){
            bindService(startMusicServiceIntent, musicServiceConnection, Context.BIND_AUTO_CREATE);
        }

        registerReceiver(musicCompletionReceiver, new IntentFilter(MusicService.COMPLETE_INTENT));
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(isBound){
            unbindService(musicServiceConnection);
            isBound= false;
        }

        unregisterReceiver(musicCompletionReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(INITIALIZE_STATUS, isInitialized);
        outState.putString(MUSIC_PLAYING, music.getText().toString());
        super.onSaveInstanceState(outState);
    }


    private ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MyBinder binder = (MusicService.MyBinder) iBinder;
            musicService = binder.getService();
            isBound = true;

            switch (musicService.getPlayingStatus()) {
                case 0:
                    play.setText("Start");
                    break;
                case 1:
                    play.setText("Pause");
                    break;
                case 2:
                    play.setText("Resume");
                    break;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicService = null;
            isBound = false;
        }
    };
}