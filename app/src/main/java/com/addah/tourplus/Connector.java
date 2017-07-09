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

public class Connector extends AsyncTask<String, String, String> {
    public AsyncResponse delegate = null;

    String result;
    URL url;
    public Connector(String urlString) throws MalformedURLException {
        this.result = "";
        this.url = new URL(urlString);
    }

    @Override
    protected String doInBackground(String... params) {
        BufferedReader bufferedReader = null;
        URLConnection dc = null;
        try {
            dc = url.openConnection();

            dc.setConnectTimeout(15000);
            dc.setReadTimeout(15000);
//
//            bufferedReader = new BufferedReader(new InputStreamReader(dc.getInputStream()));
//            while (bufferedReader.readLine()!=null) {
//                result = result+bufferedReader.readLine();
//            }

            InputStream is = dc.getInputStream();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            Element element=doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("overview_polyline");
//            NodeList nList = doc.getElementsByTagName("duration");
//
//            Node node = nList.item(nList.getLength()-1);
//            if (node.getNodeType() == Node.ELEMENT_NODE) {
//                    Element element2 = (Element) node;
//                    result = element2.getTextContent();
//                }
//            Log.e("Res",result);

            for (int i=0; i<nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    result = element2.getTextContent();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        delegate.processFinish(s);
    }
}

