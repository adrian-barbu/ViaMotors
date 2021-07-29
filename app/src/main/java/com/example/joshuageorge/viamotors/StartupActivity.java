package com.example.joshuageorge.viamotors;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_startup);

        final VideoView vv = (VideoView) findViewById(R.id.videoView);
        vv.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro));
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startActivity(new Intent(StartupActivity.this, MainActivity.class));
                finish();
            }
        });

        vv.start();

        final ImageView ivLogo = (ImageView) findViewById(R.id.ivLogo);
        ivLogo.setVisibility(View.INVISIBLE);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ivLogo.setVisibility(View.INVISIBLE);
//                vv.setVisibility(View.VISIBLE);
//                vv.start();
//            }
//        }, 3000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
    }
}
