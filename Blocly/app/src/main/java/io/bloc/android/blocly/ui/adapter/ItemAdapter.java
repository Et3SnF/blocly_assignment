package io.bloc.android.blocly.ui.adapter;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.lang.ref.WeakReference;

import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.model.RssFeed;
import io.bloc.android.blocly.api.model.RssItem;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemAdapterViewHolder> {

    // Delegating to DataSource

    // This interface below de-couples ItemAdapter from DataSource class

    public static interface DataSource {

        public RssItem getRssItem(ItemAdapter itemAdapter, int position);
        public RssFeed getRssFeed(ItemAdapter itemAdapter, int position);
        public int getItemCount(ItemAdapter itemAdapter);

    }

    public static interface Delegate {

        public void onItemClicked(ItemAdapter itemAdapter, RssItem rssItem);
        public void onVisitClicked(ItemAdapter itemAdapter, RssItem rssItem);

    }

    // For logcat

    private static String TAG = ItemAdapter.class.getSimpleName();

    // For persisting state

    private RssItem expandedItem = null;
    private WeakReference<Delegate> delegate;
    private WeakReference<DataSource> dataSource;

    // For scrolling considerations

    private int collapsedItemHeight;
    private int expandedItemHeight;

    @Override
    public ItemAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int index) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rss_item, viewGroup, false);
        return new ItemAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ItemAdapterViewHolder itemAdapterViewHolder, int index) {

        if(getDataSource() == null) {
            return;
        }

        RssItem rssItem = getDataSource().getRssItem(this, index);
        RssFeed rssFeed = getDataSource().getRssFeed(this, index);
        itemAdapterViewHolder.update(rssFeed, rssItem);

    }

    @Override
    public int getItemCount() {

        if(getDataSource() == null) {
            return 0;
        }

        return getDataSource().getItemCount(this);

    }

    // Setter and getters DataSource interface

    public void setDataSource(DataSource dataSource) {
        this.dataSource = new WeakReference<DataSource>(dataSource);
    }

    public DataSource getDataSource() {

        if (dataSource == null) {
            return null;
        }

        return dataSource.get();

    }

    // Setter and Getter of Delegate interface

    public void setDelegate(Delegate delegate) {

        this.delegate = new WeakReference<Delegate>(delegate);

    }

    public Delegate getDelegate() {

        if (delegate == null) {
            return null;
        }

        return delegate.get();

    }

    // Setter and Getter for expandedItem field


    public void setExpandedItem(RssItem expandedItem) {
        this.expandedItem = expandedItem;
    }

    public RssItem getExpandedItem() {
        return expandedItem;
    }

    // Setter and getters for expandedItemHeight

    public void setExpandedItemHeight(int expandedItemHeight) {
        this.expandedItemHeight = expandedItemHeight;
    }

    public int getExpandedItemHeight() {
        return expandedItemHeight;
    }

    // Setter and getter of collapsedItemHeight

    public void setCollapsedItemHeight(int collapsedItemHeight) {
        this.collapsedItemHeight = collapsedItemHeight;
    }

    public int getCollapsedItemHeight() {
        return collapsedItemHeight;
    }

// ViewHolder class

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

            animateContent(getExpandedItem() == rssItem);

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

                if (getDelegate() != null) {
                    getDelegate().onItemClicked(ItemAdapter.this, rssItem);
                }

            }
            else {

                // Delegate this to the controller

                if(getDelegate() != null) {
                    getDelegate().onVisitClicked(ItemAdapter.this, rssItem);
                }

            }

        }

        // -- OnCheckChangedListener Methods -- //

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.v(TAG, "Checked changed to: " + isChecked);
        }

        // --- Animate Content --- //

        private void animateContent(final boolean expand) {

            // If it's already in its desired state, just run this if-statement

            if ((expand && contentExpanded) || (!expand && !contentExpanded)) {
                return;
            }

            // This area beyond this point is when we need to animate stuff

            int startingHeight = expandedContentWrapper.getMeasuredHeight();
            int finalHeight = content.getMeasuredHeight();

            if (expand) {

                setCollapsedItemHeight(itemView.getHeight());

                startingHeight = finalHeight;

                // Set the transparency and the visibility of the wrapper (View class)

                expandedContentWrapper.setAlpha(0f);
                expandedContentWrapper.setVisibility(View.VISIBLE);

                // Use the measure method to ask view to measure itself (the width of content)

                expandedContentWrapper.measure(View.MeasureSpec.makeMeasureSpec(content.getWidth(),
                        View.MeasureSpec.EXACTLY), ViewGroup.LayoutParams.WRAP_CONTENT);

                // This height is actually unlimited

                finalHeight = expandedContentWrapper.getMeasuredHeight();
            } else {
                content.setVisibility(View.VISIBLE);
            }

            // Animation progress is a floating point - between 0.0 to 1.0 where 0.5 means halfway through
            // THis is also used to set the opacity to give it a transition

            //important method overall to get this animation going

            startAnimator(startingHeight, finalHeight, new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    float animatedFraction = animation.getAnimatedFraction();
                    float wrapperAlpha = expand ? animatedFraction : 1f - animatedFraction;
                    float contentAlpha = 1f - wrapperAlpha;

                    // Set the opacity of the wrapper and content (causes a cross-fade)

                    expandedContentWrapper.setAlpha(wrapperAlpha);
                    content.setAlpha(contentAlpha);

                    // We get teh layout parameters, and set the height directly in code.
                    // If it equals to 1.0, set the parameters to "wrap_content". If not,
                    // get the animated value

                    expandedContentWrapper.getLayoutParams().height = animatedFraction == 1f ?
                            ViewGroup.LayoutParams.WRAP_CONTENT : (Integer) animation.getAnimatedValue();

                    // This method asks itself to redraw itself on the screen

                    expandedContentWrapper.requestLayout();

                    if (animatedFraction == 1f) {
                        if (expand) {
                            content.setVisibility(View.GONE);
                            setExpandedItemHeight(itemView.getHeight());
                        } else {
                            expandedContentWrapper.setVisibility(View.GONE);
                        }
                    }

                }
            });

            contentExpanded = expand;

        }

        private void startAnimator(int start, int end, ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {

            ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
            valueAnimator.addUpdateListener(animatorUpdateListener);

            // Animator just counts from one to the end. That's it. Nothing more.

            valueAnimator.setDuration(itemView.getResources().getInteger(android.R.integer.config_mediumAnimTime));

            // AccelerateDecelerateInterpolator method is there to animate constantly up until the end
            // where it gradually slows down to give that better transition

            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

            // Start the animation!

            valueAnimator.start();

        }

    }

}