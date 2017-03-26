package org.jvk.flickrbrowser.services;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jvk.flickrbrowser.domain.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johny on 26/03/2017.
 */

public class DataService extends FlickrService {
    private static final String LOG_TAG = DataService.class.getSimpleName();
    private List<Photo> photos;
    private Uri destinationUrl;

    public DataService(String searchCriteria, boolean matchAll) {
        super(null);
        try {
            createAndUpdateUri(searchCriteria, matchAll);
            this.photos = new ArrayList<>();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    public void execute() {
        super.setRawUrl(destinationUrl.toString());
        Log.v(LOG_TAG, "Built URI: " + destinationUrl);
        DownloadJsonData downloadJsonData = new DownloadJsonData();
        downloadJsonData.execute(destinationUrl.toString());

    }

    private void createAndUpdateUri(String searchCriteria, boolean matchAll) throws Exception {
        final String FLICKR_API_URL = "https://api.flickr.com/services/feeds/photos_public.gne";
        final String TAGS_PARAM = "tags";
        final String TAG_MODE = "tagmode";
        final String DATA_FORMAT = "format";
        final String NO_JSONP = "nojsoncallback";
        destinationUrl = Uri.parse(FLICKR_API_URL)
                .buildUpon()
                .appendQueryParameter(TAGS_PARAM, searchCriteria)
                .appendQueryParameter(TAG_MODE, matchAll ? "ALL" : "ANY")
                .appendQueryParameter(DATA_FORMAT, "json")
                .appendQueryParameter(NO_JSONP, "1")
                .build();
        if (destinationUrl == null) {
            throw new Exception("Failed to build Flickr URI");
        }

    }

    private void processResult() {
        if (getDownloadStatus() != ServiceStatus.OK) {
            Log.e(LOG_TAG, "Error downloading data from Flickr");
            return;
        }
        final String FLICKR_ITEMS = "items";
        final String FLICKR_TITLE = "title";
        final String FLICKR_MEDIA = "media";
        final String FLICKR_PHOTO_URL = "m";
        final String FLICKR_AUTHOR = "author";
        final String FLICKR_AUTHOR_ID = "author_id";
        final String FLICKR_LINK = "link";
        final String FLICKR_TAGS = "tags";
        try {
            JSONArray items = new JSONObject(getData()).getJSONArray(FLICKR_ITEMS);
            for (int i = 0; i < items.length(); i++) {
                JSONObject jsonPhoto = items.getJSONObject(i);
                this.photos.add(new Photo(
                        jsonPhoto.getString(FLICKR_TITLE),
                        jsonPhoto.getString(FLICKR_AUTHOR),
                        jsonPhoto.getString(FLICKR_AUTHOR_ID),
                        jsonPhoto.getString(FLICKR_LINK),
                        jsonPhoto.getString(FLICKR_TAGS),
                        jsonPhoto.getJSONObject(FLICKR_MEDIA)
                                 .getString(FLICKR_PHOTO_URL)
                ));
            }
            for (Photo photo : photos) {
                Log.v(LOG_TAG, photo.toString());
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            Log.e(LOG_TAG, "Error processing JSON");
        }
    }

    private class DownloadJsonData extends DownloadRawData {

        @Override
        protected void onPostExecute(String apiData) {
            super.onPostExecute(apiData);
            processResult();
        }

        @Override
        protected String doInBackground(String... params) {
            return super.doInBackground(params);
        }
    }
}
