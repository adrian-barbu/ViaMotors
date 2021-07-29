package com.example.joshuageorge.viamotors.utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Download Service Center Json File From Server
 */
public class DownloadFileTask extends AsyncTask<String, String, String> {

    /**
     * Interface when download completed
     */
    public interface OnDownloadCompleteListener {
        void onComplete(String filePath);
    }

    OnDownloadCompleteListener mOnDownloadCompleteListener;

    public DownloadFileTask(OnDownloadCompleteListener listener ) {
        mOnDownloadCompleteListener = listener;
    }

    /**
     * Downloading file in background thread
     * */
    @Override
    protected String doInBackground(String... f_url) {
        int count;
        try {
            String root = Environment.getExternalStorageDirectory().toString();

            URL url = new URL(f_url[0]);

            URLConnection conection = url.openConnection();
            conection.connect();

            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file

            String filePath = root + "/service_center.json" ;
            OutputStream output = new FileOutputStream(filePath);
            byte data[] = new byte[1024];

            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;

                // writing data to file
                output.write(data, 0, count);

            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

            return filePath;

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }

        return null;
    }

    /**
     * After completing background task
     * **/
    @Override
    protected void onPostExecute(String filePath) {
        if (mOnDownloadCompleteListener != null)
            mOnDownloadCompleteListener.onComplete(filePath);
    }
}
