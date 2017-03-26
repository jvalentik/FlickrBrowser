package org.jvk.flickrbrowser.services;

import android.net.Uri;
import android.util.Log;

import org.jvk.flickrbrowser.domain.Photo;

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
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void createAndUpdateUri(String searchCriteria, boolean matchAll) throws Exception {
        final String FLICKR_API_URL = "https://api.flickr.com/services/feeds/photos_public.gne";
        final String TAGS_PARAM = "tags";
        final String TAG_MODE = "tagmode";
        final String DATA_FORMAT = "json";
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

    public class DownloadJsonData extends DownloadRawData {

    }
}
