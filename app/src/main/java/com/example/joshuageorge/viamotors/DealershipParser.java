package com.example.joshuageorge.viamotors;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Joshua George on 8/15/2015.
 */
public class DealershipParser {

    Context context;
    public ArrayList<Dealer> dealerList = new ArrayList<Dealer>();

    DealershipParser(Context context, String filePath) {
        this.context = context;

        InputStream is;

        try {
            File file = new File(filePath);
            is = new FileInputStream(filePath);
        } catch (Exception e) {
            is = context.getResources().openRawResource(R.raw.smartsheet);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = "";
        String result = "";
        try {
            while ((line = br.readLine()) != null) {
                result += line;
            }

            JSONObject jo = new JSONObject(result);
            JSONArray dealerships = jo.getJSONArray(context.getResources().getString(R.string.json_name_rows));
            for (int i = 0; i < dealerships.length(); ++i) {
                Dealer d = new Dealer();

                JSONObject dealerObject = dealerships.getJSONObject(i);
                JSONArray dealerInfo = dealerObject.getJSONArray(context.getResources().getString(R.string.json_name_cells));

                for (int j = 0; j < dealerInfo.length(); ++j) {
                    JSONObject dealerInfoObject = dealerInfo.getJSONObject(j);
                    switch(j) {
                        case 0:
                            d.name = dealerInfoObject.getString(context.getResources().getString(R.string.json_name_value));
                            break;
                        case 1:
                            d.address = dealerInfoObject.getString(context.getResources().getString(R.string.json_name_value));
                            break;
                        case 2:
                            d.state = dealerInfoObject.getString(context.getResources().getString(R.string.json_name_value));
                            break;
                        case 3:
                            d.phone = dealerInfoObject.getString(context.getResources().getString(R.string.json_name_value));
                            break;
                        case 4:
                            d.contact = dealerInfoObject.getString(context.getResources().getString(R.string.json_name_value));
                            break;
                        case 6:
                            d.website = dealerInfoObject.getString(context.getResources().getString(R.string.json_name_value));
                            break;
                        case 7:
                            d.active = dealerInfoObject.getBoolean(context.getResources().getString(R.string.json_name_value));
                            break;
                        case 8:
                            d.latitude = dealerInfoObject.getDouble(context.getResources().getString(R.string.json_name_value));
                            break;
                        case 9:
                            d.longitude = dealerInfoObject.getDouble(context.getResources().getString(R.string.json_name_value));
                            break;
                        case 10:
                            d.logoURL = dealerInfoObject.getString(context.getResources().getString(R.string.json_name_value));
                        case 5:
                        default:
                            break;
                    }
                }

                dealerList.add(d);
            }
        } catch (Exception e) {

        }
    }
}
