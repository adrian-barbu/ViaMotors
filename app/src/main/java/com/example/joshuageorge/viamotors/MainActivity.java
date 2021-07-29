package com.example.joshuageorge.viamotors;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.joshuageorge.viamotors.utils.DownloadFileTask;
import com.example.joshuageorge.viamotors.utils.Utils;
import com.example.joshuageorge.viamotors.utils.WaitDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{
    public static GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    public static int closestDealership = -1;
    public static DealershipParser dp;
    public static Bitmap bitmap;

    private boolean locationAccess = false;
    private boolean locationAccessSet = false;
    private SharedPreferences sp;

    String mJsonServiceCenterUrl = "http://cherrydigital.com/via/smartsheet.json";

    private WaitDialog mWaitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        initializeActivity();
    }

    private void buildGoogleApiClient() {
        MainActivity.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        sp = getSharedPreferences(getString(R.string.prefs_file), 0);
    }

    private void initializeActivity() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        getWindow().setBackgroundDrawableResource(R.drawable.owners_manual_background_square);

        mWaitDialog = new WaitDialog(this);

        Button ownersManualButton = (Button)findViewById(R.id.ownersManualButton);
        ownersManualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setTitle("Vehicle Type");
                ad.setMessage("Are you driving a Truck or Van?");
                ad.setPositiveButton("Truck", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OwnersManualActivity.truckPicked = true;
                        OwnersManualActivity.quickstartPicked = false;
                        Intent i = new Intent(getApplicationContext(), OwnersManualActivity.class);
                        startActivity(i);
                    }
                });

                ad.setNegativeButton("Van", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OwnersManualActivity.truckPicked = false;
                        OwnersManualActivity.quickstartPicked = false;
                        Intent i = new Intent(getApplicationContext(), OwnersManualActivity.class);
                        startActivity(i);
                    }
                });

                ad.create().show();
            }
        });

        Button quickStartButton = (Button)findViewById(R.id.quickStartButton);
        quickStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setTitle("Vehicle Type");
                ad.setMessage("Are you driving a Truck or Van?");
                ad.setPositiveButton("Truck", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OwnersManualActivity.truckPicked = true;
                        OwnersManualActivity.quickstartPicked = true;
                        Intent i = new Intent(getApplicationContext(), OwnersManualActivity.class);
                        startActivity(i);
                    }
                });

                ad.setNegativeButton("Van", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OwnersManualActivity.truckPicked = false;
                        OwnersManualActivity.quickstartPicked = true;
                        Intent i = new Intent(getApplicationContext(), OwnersManualActivity.class);
                        startActivity(i);
                    }
                });

                ad.create().show();
            }
        });

        Button nearestServiceCenterButton = (Button)findViewById(R.id.nearestServiceButton);
        nearestServiceCenterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // First, check the network connection

                if (Utils.isNetworkOnline(getApplicationContext())) {
                    findNearestServiceCenter(true);
                }
                else {
                    AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                    ad.setMessage(R.string.warning_get_lastest_service_center_list);
                    ad.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            findNearestServiceCenter(false /* Use Older */);
                        }
                    });

                    ad.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // DO NOTHING
                        }
                    });

                    ad.create().show();
                }
            }
        });

        Button callViaMotorsButton = (Button)findViewById(R.id.callViaButton);
        callViaMotorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setMessage("Call " + getString(R.string.via_motors_number) + "?");
                ad.setPositiveButton(R.string.call_string, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = getString(R.string.via_motors_call_url);
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
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

        Button plugshareButton = (Button)findViewById(R.id.plugShareButton);
        plugshareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plugsharePackageString = getString(R.string.plugshare_package_name);
                Intent plugshareIntent = getPackageManager().getLaunchIntentForPackage(plugsharePackageString);
                if (plugshareIntent != null) {
                    startActivity(plugshareIntent);
                } else {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + plugsharePackageString)));
                    } catch (android.content.ActivityNotFoundException e){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + plugsharePackageString)));
                    }
                }
            }
        });

        // Added By Stelian
        Button videoOverviewButton = (Button)findViewById(R.id.videoOverviewButton);
        videoOverviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, IntroVideoActivity.class));
            }
        });
    }

    /**
     * Get Nearest Service Center
     * @param needDownload
     */
    private void findNearestServiceCenter(boolean needDownload) {
        mWaitDialog.show();

        if (needDownload) {
            DownloadFileTask dfTask = new DownloadFileTask(new DownloadFileTask.OnDownloadCompleteListener() {
                @Override
                public void onComplete(String filePath) {
                    // Parse Json File
                    MainActivity.dp = new DealershipParser(getApplicationContext(), filePath);
                    mWaitDialog.dismiss();
                    goNearestServiceCenter();
                }
            });

            dfTask.execute(new String[]{mJsonServiceCenterUrl});
        }
        else {
            MainActivity.dp = new DealershipParser(getApplicationContext(), null);
            mWaitDialog.dismiss();
            goNearestServiceCenter();
        }
    }

    private void goNearestServiceCenter() {
        locationAccess = sp.getBoolean(getString(R.string.location_access_key), false);
        locationAccessSet = sp.getBoolean(getString(R.string.location_settings_set_key), false);
        if (!locationAccessSet) {
            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
            ad.setMessage(getString(R.string.local_or_list));
            ad.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    locationAccess = true;
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(getString(R.string.location_access_key), locationAccess);
                    editor.putBoolean(getString(R.string.location_settings_set_key), true);
                    editor.commit();
                    MainActivity.mGoogleApiClient.connect();
                }
            });

            ad.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    locationAccess = false;
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(getString(R.string.location_access_key), locationAccess);
                    editor.putBoolean(getString(R.string.location_settings_set_key), true);
                    editor.commit();
                    Intent i = new Intent(getApplicationContext(), ServiceCenterListActivty.class);
                    startActivity(i);
                }
            });

            ad.create().show();
        } else if (locationAccess == false){
            Intent i = new Intent(getApplicationContext(), ServiceCenterListActivty.class);
            startActivity(i);
        }
        else {
            if (!MainActivity.mGoogleApiClient.isConnected()) {
                MainActivity.mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle connection) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        float[] distance = new float[1];
        float minDistance = 0;
        for (int i = 0; i < dp.dealerList.size(); ++i)
        {
            try {
                if (i == 0) {
                    MainActivity.closestDealership = 0;
                    Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), dp.dealerList.get(i).latitude, dp.dealerList.get(i).longitude, distance);
                    minDistance = distance[0];
                } else {
                    Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), dp.dealerList.get(i).latitude, dp.dealerList.get(i).longitude, distance);
                    if (distance[0] < minDistance) {
                        MainActivity.closestDealership = i;
                        minDistance = distance[0];
                    }
                }
            } catch (Exception e)
            {

            }
        }

        Intent i = new Intent(getApplicationContext(), NearestServiceCenterActivity.class);
        MainActivity.mGoogleApiClient.disconnect();
        startActivity(i);

//        MainActivity.bitmap = null;
//        ImageLoader iL = new ImageLoader((ProgressBar)findViewById(R.id.progress));
//        try {
//            iL.execute(MainActivity.dp.dealerList.get(MainActivity.closestDealership).logoURL).get(2000, TimeUnit.MILLISECONDS);
//        }
//        catch (Exception e) {
//            Intent i = new Intent(getApplicationContext(), NearestServiceCenterActivity.class);
//            MainActivity.mGoogleApiClient.disconnect();
//            startActivity(i);
//        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mLastLocation = null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mLastLocation = null;
        Intent i = new Intent(getApplicationContext(), ServiceCenterListActivty.class);
        startActivity(i);
    }

    private class ImageLoader extends AsyncTask<String, String, Bitmap> {

        ProgressBar pb;
        public ImageLoader(ProgressBar progressBar)
        {
            pb = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                MainActivity.bitmap = BitmapFactory.decodeStream((InputStream)new URL(params[0]).getContent());
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Intent i = new Intent(getApplicationContext(), NearestServiceCenterActivity.class);
            MainActivity.mGoogleApiClient.disconnect();
            pb.setVisibility(View.GONE);
            startActivity(i);
        }
    }
}
