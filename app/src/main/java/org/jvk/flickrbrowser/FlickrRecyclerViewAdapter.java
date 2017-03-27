package org.jvk.flickrbrowser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.jvk.flickrbrowser.domain.Photo;

import java.util.List;

/**
 * Created by johny on 26/03/2017.
 */

public class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrImageViewHolder> {
    private static final String LOG_TAG = FlickrRecyclerViewAdapter.class.getSimpleName();
    private List<Photo> photos;
    private Context context;

    public FlickrRecyclerViewAdapter(List<Photo> photos, Context context) {
        this.photos = photos;
        this.context = context;
    }

    @Override
    public FlickrImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, null);
        return new FlickrImageViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return (photos != null ? photos.size() : 0);
    }

    @Override
    public void onBindViewHolder(FlickrImageViewHolder holder, int position) {
        Photo photo = photos.get(position);
        Log.d(LOG_TAG, "Processing: " + photo.getTitle());
        Picasso.with(context)
                .load(photo.getImage())
                .error(R.drawable.error)
                .placeholder(R.drawable.placeholder).into(holder.thumbnail);
        holder.title.setText(photo.getTitle());

    }
}
