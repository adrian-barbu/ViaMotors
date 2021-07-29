package com.example.joshuageorge.viamotors;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Joshua George on 8/21/2015.
 */
public class ServiceCenterListAdapter extends ArrayAdapter<Dealer> {

    ArrayList<Dealer> dealerArrayList;
    LayoutInflater inflater;
    Context ctx;

    public ServiceCenterListAdapter(Context context, ArrayList<Dealer> list) {
        super(context, 0, list);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dealerArrayList = list;
        ctx = context;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_view_cell, parent, false);
        }

        Dealer d = getItem(position);

        TextView title = (TextView)view.findViewById(R.id.dealerNameCellText);
        title.setText(d.name);

        TextView address = (TextView)view.findViewById(R.id.addressCellText);
        address.setText(d.address);
        address.setGravity(Gravity.CENTER);
        address.setOnClickListener(new AddressOnClickListener(d, ctx));

        TextView phone = (TextView)view.findViewById(R.id.phoneCellText);
        phone.setText(d.phone);
        phone.setOnClickListener(new PhoneOnClickListener(d, view));

        return view;
    }

    private class AddressOnClickListener implements View.OnClickListener
    {
        Dealer d;
        Context c;

        public AddressOnClickListener(Dealer dealer, Context context) {
            d = dealer;
            c = context;
        }

        @Override
        public void onClick(View v) {
            String uri = String.format(Locale.ENGLISH, "geo:?q=%s(%s)",
                    d.address,
                    d.name);
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            c.startActivity(i);
        }
    }

    private class PhoneOnClickListener implements View.OnClickListener
    {
        Dealer d;
        final View v;

        public PhoneOnClickListener(Dealer dealer, View view) {
            d = dealer;
            v = view;
        }

        @Override
        public void onClick(View view) {
            AlertDialog.Builder ad = new AlertDialog.Builder(v.getContext());
            ad.setMessage("Call " + d.phone + "?");
            ad.setPositiveButton(R.string.call_string, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String url = d.phone;
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + url));
                    v.getContext().startActivity(callIntent);
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
    }
}
