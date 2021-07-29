package com.example.joshuageorge.viamotors;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class NearestServiceCenterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_service_center);

        initializeActivity();
    }

    private void initializeActivity() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        getWindow().setBackgroundDrawableResource(R.drawable.owners_manual_background_square);
        ImageView iv = (ImageView)findViewById(R.id.logoView);
//        if (MainActivity.bitmap != null) {
//            iv.setImageBitmap(MainActivity.bitmap);
//        }
        iv.setImageResource(R.drawable.logo);

        TextView serviceCenterName = (TextView)findViewById(R.id.serviceCenterNameText);
        serviceCenterName.setText(MainActivity.dp.dealerList.get(MainActivity.closestDealership).name);

        TextView addressText = (TextView)findViewById(R.id.addressText);
        addressText.setText(MainActivity.dp.dealerList.get(MainActivity.closestDealership).address);
        addressText.setGravity(Gravity.CENTER);

        addressText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "geo:?q=%s(%s)",
                        MainActivity.dp.dealerList.get(MainActivity.closestDealership).address,
                        MainActivity.dp.dealerList.get(MainActivity.closestDealership).name);
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(i);
            }
        });

        TextView phoneText = (TextView)findViewById(R.id.phoneText);
        phoneText.setText(MainActivity.dp.dealerList.get(MainActivity.closestDealership).phone);
        phoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(NearestServiceCenterActivity.this);
                ad.setMessage("Call " + MainActivity.dp.dealerList.get(MainActivity.closestDealership).phone + "?");
                ad.setPositiveButton(R.string.call_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = MainActivity.dp.dealerList.get(MainActivity.closestDealership).phone;
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + url));
                        startActivity(callIntent);
                    }
                });

                ad.setNegativeButton(R.string.cancel_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // DO NOTHING
                    }
                });

                ad.create().show();
            }
        });

        TextView individualText = (TextView)findViewById(R.id.individualText);
        individualText.setText(MainActivity.dp.dealerList.get(MainActivity.closestDealership).contact);

        TextView hyperlinkText = (TextView)findViewById(R.id.hyperlinkText);
        hyperlinkText.setText(MainActivity.dp.dealerList.get(MainActivity.closestDealership).website);
        hyperlinkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + MainActivity.dp.dealerList.get(MainActivity.closestDealership).website));
                startActivity(browserIntent);
            }
        });

        TextView listViewText = (TextView)findViewById(R.id.fullListText);
        listViewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ServiceCenterListActivty.class);
                startActivity(i);
            }
        });

        View layoutBack = (View) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NearestServiceCenterActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
