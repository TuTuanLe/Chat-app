package com.tutuanle.chatapp.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;

public class ConvertImageUrlToBitmap extends AsyncTask<String, Void, Bitmap> {

    private  Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public  ConvertImageUrlToBitmap(){

    }
    @Override
    protected Bitmap doInBackground(String... url) {
        String urlDisplay = url[0];
        bitmap = null;
        try {
            InputStream srt  = new java.net.URL(urlDisplay).openStream();
            bitmap = BitmapFactory.decodeStream(srt);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
    }
}
