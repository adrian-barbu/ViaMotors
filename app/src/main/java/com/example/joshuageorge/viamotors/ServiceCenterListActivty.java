package com.example.joshuageorge.viamotors;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ServiceCenterListActivty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_center_list_activty);

        initializeActivity();
    }

    private void initializeActivity() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        ListView lv = (ListView)findViewById(R.id.dealerListView);
        ArrayAdapter<Dealer> adapter = new ServiceCenterListAdapter(ServiceCenterListActivty.this, MainActivity.dp.dealerList);
        lv.setAdapter(adapter);

        View layoutBack = (View) findViewById(R.id.layoutBack);
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
