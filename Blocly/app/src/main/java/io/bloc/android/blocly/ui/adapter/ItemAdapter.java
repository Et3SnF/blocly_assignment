package io.bloc.android.blocly.ui.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.DataSource;
import io.bloc.android.blocly.api.model.RssFeed;
import io.bloc.android.blocly.api.model.RssItem;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemAdapterViewHolder> {

    // For logcat

    private static String TAG = ItemAdapter.class.getSimpleName();

    @Override
    public ItemAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int index) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rss_item, viewGroup, false);
        return new ItemAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ItemAdapterViewHolder itemAdapterViewHolder, int index) {
        DataSource sharedDataSource = BloclyApplication.getSharedDataSource();
        itemAdapterViewHolder.update(sharedDataSource.getFeeds().get(0), sharedDataSource.getItems().get(index));
    }

    @Override
    public int getItemCount() {
        return BloclyApplication.getSharedDataSource().getItems().size();
    }

    class ItemAdapterViewHolder extends RecyclerView.ViewHolder implements ImageLoadingListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        // --- Member variables for the view holder --- //

        // TextView variables

        TextView title;
        TextView feed;
        TextView content;

        // ImageView variables

        View headerWrapper;
        ImageView headerImage;

        // Checkbox Variables

        CheckBox archiveCheckbox;
        CheckBox favoriteCheckbox;

        // Variable to pull information from RssItem.java

        RssItem rssItem;

        public ItemAdapterViewHolder(View itemView) {
            super(itemView);

            // Variables for TextView

            title = (TextView) itemView.findViewById(R.id.tv_rss_item_title);
            feed = (TextView) itemView.findViewById(R.id.tv_rss_item_feed_title);
            content = (TextView) itemView.findViewById(R.id.tv_rss_item_content);

            // Variables for Image Library

            headerWrapper = itemView.findViewById(R.id.fl_rss_item_image_header);
            headerImage = (ImageView) headerWrapper.findViewById(R.id.iv_rss_item_image);

            // Variables for checkboxes

            archiveCheckbox = (CheckBox) itemView.findViewById(R.id.cb_rss_item_check_mark);
            favoriteCheckbox = (CheckBox) itemView.findViewById(R.id.cb_rss_item_favorite_star);

            // Activate clickListener for ViewHolder

            itemView.setOnClickListener(this);

            // Activate Listeners for checkboxes

            archiveCheckbox.setOnCheckedChangeListener(this);
            favoriteCheckbox.setOnCheckedChangeListener(this);

        }

        void update(RssFeed rssFeed, RssItem rssItem) {

            this.rssItem = rssItem;

            // Update TextView

            feed.setText(rssFeed.getTitle());
            title.setText(rssItem.getTitle());
            content.setText(rssItem.getDescription());

            // Update image view

            if (rssItem.getImageUrl() != null) {
                headerWrapper.setVisibility(View.VISIBLE);
                headerImage.setVisibility(View.INVISIBLE);
                ImageLoader.getInstance().loadImage(rssItem.getImageUrl(), this);
            }
            else {
                headerWrapper.setVisibility(View.GONE);
            }

        }

        // Implement ImageLoadingListener methods

        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            Log.e(TAG, "onLoadingFailed: " + failReason.toString() + " for URL: " + imageUri);

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            if (imageUri.equals(rssItem.getImageUrl())) {
                headerImage.setImageBitmap(loadedImage);
                headerImage.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

            // Attempt a retry
            ImageLoader.getInstance().loadImage(imageUri, this);

        }

        // --- OnClickListener Methods -- //

        @Override
        public void onClick(View view) {

            Toast.makeText(view.getContext(), rssItem.getTitle(), Toast.LENGTH_SHORT).show();

        }

        // -- OnCheckChangedListener Methods -- //

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            // Check state of which checkbox is changed

            if(archiveCheckbox.isChecked()) {
                Log.v(TAG, "Archive check changed to: " + isChecked);
            }
            else if(favoriteCheckbox.isChecked()){
                Log.v(TAG, "Star check changed to: " + isChecked);
            }

        }

    }

}