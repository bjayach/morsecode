package com.greencabbage.patch.util.async;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AsyncPostFeedback extends AsyncTask<String, Integer, String> {
    @Override
    protected String doInBackground(String... params) {


        String outputString = "";
        HttpURLConnection connection = null;

        try {
        URL url = new URL(params[0]);
        String json = params[1];
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");

        //set the sending type and receiving type to json
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        connection.setAllowUserInteraction(false);

        if (json != null) {
            //set the content length of the body
            connection.setRequestProperty("Content-length", json.getBytes().length + "");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            //send the json as body of the request
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(json.getBytes("UTF-8"));
            outputStream.close();
        }

        //Connect to the server
        connection.connect();

        int status = connection.getResponseCode();
        switch (status) {
            case 200:
            case 201:
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                bufferedReader.close();
                //return received string
                return sb.toString();
        }

    }  catch(
    Exception ex) {
        outputString = "EXCEPTION";
    } finally

    {
        if (connection != null) {
            try {
                connection.disconnect();
            } catch (Exception ex) {
            }
        }
    }
        return outputString;
}

    @Override
    protected void onPostExecute(String outputString) {
        super.onPostExecute(outputString);
    }
}