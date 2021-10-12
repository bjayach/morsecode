package com.greencabbage.patch.util.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;

public class AsyncGetFileFromUrl extends AsyncTask<String, Integer, File> {
    @Override
    protected File doInBackground(String... params) {
        File myFile = null;
        InputStream input = null;
        try {
            URL url = new URL(params[0]);
            String fileName = params[1];

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();

            BufferedInputStream bis = new BufferedInputStream(input);
            myFile = new File(fileName);

            FileOutputStream fos = new FileOutputStream(myFile);
            byte buffer[] = new byte[1024];
            int dataSize;
            int loadedSize = 0;
            while ((dataSize = bis.read(buffer)) != -1) {
                loadedSize += dataSize;
                fos.write(buffer, 0, dataSize);
            }

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return myFile;
    }

    @Override
    protected void onPostExecute(File myFile) {
        super.onPostExecute(myFile);
    }
}