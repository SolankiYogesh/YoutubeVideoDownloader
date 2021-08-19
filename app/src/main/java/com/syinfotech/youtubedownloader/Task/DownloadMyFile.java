package com.syinfotech.youtubedownloader.Task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.syinfotech.youtubedownloader.Fragments.VideosFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadMyFile extends AsyncTask<String, Integer, String> {
    @SuppressLint("StaticFieldLeak")
    public final Context mcontext;
    private final String Ttile;
    private final String ext;
    private String temppath,temptitle,tempfilename;

    public  DownloadMyFile(Context mcontext,String title,String exte) {
        this.mcontext = mcontext;
        Ttile = title;
        ext =exte;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        VideosFragment.roundedProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        VideosFragment.roundedProgressBar.setVisibility(View.GONE);
        if (s!=null){
            Toast.makeText(mcontext,"Download Error"+s,Toast.LENGTH_SHORT).show();
        }else {
            new DownloadDatabse(mcontext).AddToDownloadlist(temptitle,tempfilename,temppath);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        VideosFragment.roundedProgressBar.setProgressPercentage(Double.valueOf(values[0]),false);
    }

    @Override
    protected String doInBackground(String... strings) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            @SuppressLint("SdCardPath") String path = Environment.getExternalStorageDirectory()+ "/SY DOWNLOADER";
            File directory = new File(path);
            if (!directory.exists()){
                directory.mkdir();
            }
            output = new FileOutputStream(path+"/"+ext);
            temppath = path+"/"+ext;
            temptitle = Ttile;
            tempfilename = ext;

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }
}
