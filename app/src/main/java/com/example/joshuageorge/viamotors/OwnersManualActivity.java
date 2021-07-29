package com.example.joshuageorge.viamotors;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.example.joshuageorge.viamotors.utils.Utils;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.String.format;


public class OwnersManualActivity extends Activity implements OnPageChangeListener {
    public static boolean truckPicked = true;
    public static boolean quickstartPicked = false;

    WebView wvPdfViewer;
    PDFView pdfView;
    int pageNumber = 1;
    String pdfName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owners_manual);

        initializeActivity();
    }

    private void initializeActivity() {
        wvPdfViewer = (WebView) findViewById(R.id.wvPdfViewer);
        pdfView = (PDFView) findViewById(R.id.pdfView);

        if (Utils.isNetworkOnline(getApplicationContext())) {
            wvPdfViewer.setVisibility(View.VISIBLE);
            pdfView.setVisibility(View.GONE);

            WebSettings settings = wvPdfViewer.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setAllowFileAccessFromFileURLs(true);
            settings.setAllowUniversalAccessFromFileURLs(true);
            settings.setBuiltInZoomControls(true);
            String s = "";
            if (!quickstartPicked) {
                s = "<iframe src='http://docs.google.com/viewer?embedded=true&url=http://www.cherrydigital.com/via/" + (truckPicked ? "TruckManual.pdf" : "VanManual.pdf") + "' width='100%' height='100%'style='border: none;'></iframe>";
            } else {
                s = "<iframe src='http://docs.google.com/viewer?embedded=true&url=http://www.cherrydigital.com/via/" + (truckPicked ? "Truck_Quickstart_Guide.pdf" : "Van_Quickstart_Guide.pdf") + "' width='100%' height='100%'style='border: none;'></iframe>";
            }

            wvPdfViewer.loadData(s, "text/html", "UTF-8");
        }
        else {

            pdfView.setVisibility(View.VISIBLE);
            wvPdfViewer.setVisibility(View.GONE);

            if (!quickstartPicked) {
                pdfName = truckPicked ? "TruckManual.pdf" : "VanManual.pdf";
            } else {
                pdfName = truckPicked ? "Truck_Quickstart_Guide.pdf" : "Van_Quickstart_Guide.pdf";
            }

            // Now display pdf
            display(pdfName, true);
        }
    }

    private void display(String assetFileName, boolean jumpToFirstPage) {
        if (jumpToFirstPage) pageNumber = 1;
        setTitle(pdfName = assetFileName);

        pdfView.fromAsset(assetFileName)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(format("%s %s / %s", pdfName, page, pageCount));
    }

    private boolean displaying(String fileName) {
        return fileName.equals(pdfName);
    }
}
