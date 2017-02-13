package org.jvk.flickrbrowser.services;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by johny on 13/02/2017.
 */

public class FlickrService {
    private static final String LOG_TAG = FlickrService.class.getSimpleName();
    private String rawUrl;
    private String data;
    private ServiceStatus downloadStatus;

    public FlickrService(String rawUrl) {
        this.rawUrl = rawUrl;
        this.downloadStatus = ServiceStatus.IDLE;
    }

    public void reset() {
        this.downloadStatus = ServiceStatus.IDLE;
        this.rawUrl = null;
        this.data = null;
    }

    public String getData() {
        return data;
    }

    public ServiceStatus getDownloadStatus() {
        return downloadStatus;
    }

    public void execute() {
        downloadStatus = ServiceStatus.PROCESSING;
        DownloadRawData downloadRawData = new DownloadRawData();
        downloadRawData.execute(rawUrl);
    }

    public class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String apiData) {
            data = apiData;
            Log.v(LOG_TAG, "Downloaded data: " + data);
            if (data == null) {
                if (rawUrl == null) {
                    downloadStatus = ServiceStatus.NOT_INITIALIZED;
                } else {
                    downloadStatus = ServiceStatus.FAILED_OR_EMPTY;
                }
            } else {
                downloadStatus = ServiceStatus.OK;
            }
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            if (params == null) {
                return null;
            }

            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream is = urlConnection.getInputStream();
                if (is == null) {
                    return null;
                }
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                return buffer.toString();
            }
            catch (IOException e) {
                Log.d(LOG_TAG, "Error ", e);
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }

            }
            return null;
        }
    }
}
