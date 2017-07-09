package com.addah.tourplus;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by choxmi on 5/19/17.
 */

public class GenericConnector extends AsyncTask<String, String, String> {
    public AsyncResponse delegate = null;

    String result;
    URL url;
    public GenericConnector(String urlString) throws MalformedURLException {
        this.result = "";
        this.url = new URL(urlString);
    }

    @Override
    protected String doInBackground(String... params) {
        BufferedReader bufferedReader = null;
        URLConnection dc = null;
        try {
            dc = url.openConnection();

            Log.e("url",url.toString());
            dc.setConnectTimeout(15000);
            dc.setReadTimeout(15000);

            bufferedReader = new BufferedReader(new InputStreamReader(dc.getInputStream()));
            result = bufferedReader.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        delegate.processFinish(s);
    }
}

