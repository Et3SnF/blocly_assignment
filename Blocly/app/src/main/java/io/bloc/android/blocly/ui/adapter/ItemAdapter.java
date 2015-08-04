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

        // Boolean variable to either allow expanded view or not

        boolean contentExpanded;

        // Variables for the expanded content

        View expandedContentWrapper;
        TextView expandedContent;
        TextView visitSite;

        // Constructor

        public ItemAdapterViewHolder(View itemView) {
            super(itemView);

            // Declare TextView variables to layout

            title = (TextView) itemView.findViewById(R.id.tv_rss_item_title);
            feed = (TextView) itemView.findViewById(R.id.tv_rss_item_feed_title);
            content = (TextView) itemView.findViewById(R.id.tv_rss_item_content);

            // Declare Image Library variables to layout

            headerWrapper = itemView.findViewById(R.id.fl_rss_item_image_header);
            headerImage = (ImageView) headerWrapper.findViewById(R.id.iv_rss_item_image);

            // Declare expanded content variables to layout

            expandedContentWrapper = itemView.findViewById(R.id.ll_rss_item_expanded_content_wrapper);
            expandedContent = (TextView) expandedContentWrapper.findViewById(R.id.tv_rss_item_content_full);
            visitSite = (TextView) expandedContentWrapper.findViewById(R.id.tv_rss_item_visit_site);

            // Declare CheckBox variables to layout

            archiveCheckbox = (CheckBox) itemView.findViewById(R.id.cb_rss_item_check_mark);
            favoriteCheckbox = (CheckBox) itemView.findViewById(R.id.cb_rss_item_favorite_star);

            // Activate clickListener for whole ViewHolder

            itemView.setOnClickListener(this);

            // Activate Listeners for checkboxes

            archiveCheckbox.setOnCheckedChangeListener(this);
            favoriteCheckbox.setOnCheckedChangeListener(this);

            // Activate listener for Visit Site link

            visitSite.setOnClickListener(this);

        }

        void update(RssFeed rssFeed, RssItem rssItem) {

            this.rssItem = rssItem;

            // Update TextView

            feed.setText(rssFeed.getTitle());
            title.setText(rssItem.getTitle());

                // This is the content when previewed (restricted to 3 lines)

            content.setText(rssItem.getDescription());

                // This is when the content when expanded

            expandedContent.setText(rssItem.getDescription());

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

            if (view == itemView) {

                // itemView came from RecyclerView.java file!

                // if contentExpanded is true, make it visible, otherwise, just don't show full content
                contentExpanded = !contentExpanded;
                expandedContentWrapper.setVisibility(contentExpanded ? View.VISIBLE : View.GONE);

                // if contentExpanded is true, make it visible, otherwise, just don't show full content

                expandedContentWrapper.setVisibility(contentExpanded ? View.VISIBLE : View.GONE);

                content.setVisibility(contentExpanded ? View.GONE : View.VISIBLE);
            }
            else {
                Toast.makeText(view.getContext(), "Visit " + rssItem.getUrl(), Toast.LENGTH_SHORT).show();
            }

        }

        // -- OnCheckChangedListener Methods -- //

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.v(TAG, "Checked changed to: " + isChecked);
        }

    }

}