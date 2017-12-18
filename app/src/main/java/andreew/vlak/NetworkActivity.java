/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package andreew.vlak;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by opalo on 18.12.2017.
 */

public class NetworkActivity extends Activity {
    /*
    public void DownloadFromUrl(String DownloadUrl, String fileName) {
        try {
            File root = android.os.Environment.getExternalStorageDirectory();

            File dir = new File (root.getAbsolutePath() + "/xmls");
            if(dir.exists()==false) {
                dir.mkdirs();
            }

            URL url = new URL(DownloadUrl);
            File file = new File(dir, fileName);

            long startTime = System.currentTimeMillis();
            Log.d("DownloadManager", "download begining");
            Log.d("DownloadManager", "download url:" + url);
            Log.d("DownloadManager", "downloaded file name:" + fileName);

            URLConnection ucon = url.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayOutputStream baf = new ByteArrayOutputStream(5000);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.flush();
            fos.close();
            Log.d("DownloadManager", "download ready in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");

        } catch (IOException e) {
            Log.d("DownloadManager", "Error: " + e);
        }

    }
    */

    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    
    private static final String URL =
            "http://www.palovsky.cz/score.php";
    
    
    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;

    // The user's current network preference setting.
    public static String sPref = null;

    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver = new NetworkReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register BroadcastReceiver to track connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
    }

    // Refreshes the display if the network connection and the
    // pref settings allow it.
    @Override
    public void onStart() {
        super.onStart();

        // Gets the user's network preference settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.
        sPref = sharedPrefs.getString("listPref", "Wi-Fi");

        updateConnectedFlags();

         if (refreshDisplay) {
            loadPage();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

    private void loadPage() {
        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected)) || ((sPref.equals(WIFI)) && (wifiConnected))) {
            // AsyncTask subclass
            new DownloadXmlTask(this).execute(URL);
        } else {

        }
    }


    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadXmlTask extends AsyncTask<String, Void, List<PolozkaScore>> {

    	Context context;
    	
        public DownloadXmlTask(Context context) {
            this.context = context;
		}

		@Override
        protected List<PolozkaScore> doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0], context);
            } catch (IOException e) {
                return null; //getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                return null; //getResources().getString(R.string.xml_error);
            }
        }

        @Override
        protected void onPostExecute(List<PolozkaScore> result) {
            setContentView(R.layout.activity_main4);
            // Displays the HTML string in the UI via a WebView
            ListView lv = (ListView)findViewById(R.id.listView1);
            
            Log.d("Result","length = " + result.size());

            Adapter adapter = new Adapter(context, R.layout.activity_main4, result);

            lv.setAdapter(adapter);

        }
    }

    // Uploads XML from stackoverflow.com, parses it, and combines it with
    // HTML markup. Returns HTML string.
    private List<PolozkaScore> loadXmlFromNetwork(String urlString, Context context) throws XmlPullParserException, IOException {
        InputStream stream = null;
        List<PolozkaScore> entries = null;


        XmlParser cnbXmlParser = new XmlParser();
  
        try {
            stream = downloadUrl(urlString);

            saveInputStreamToFile(stream, "soubor.xml", context);
            //stream = readInputStreamFromFile("soubor.xml");

            entries = cnbXmlParser.parse(stream);

        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return entries;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        InputStream stream = null;
        stream = conn.getInputStream();

        return stream;
    }

    private void saveInputStreamToFile(InputStream inputStream, String fileName, Context context) {
        String filename = "myfile";
        String string = "Hello world!";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private InputStream readInputStreamFromFile(String fileName){
        String path = Environment.getExternalStorageDirectory()+"/"+fileName;
        File file = new File(path);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileInputStream;
    }

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


            if (WIFI.equals(sPref) && networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                refreshDisplay = true;

            } else if (ANY.equals(sPref) && networkInfo != null) {
                refreshDisplay = true;

            } else {
                refreshDisplay = false;
            }
        }
    }
}
