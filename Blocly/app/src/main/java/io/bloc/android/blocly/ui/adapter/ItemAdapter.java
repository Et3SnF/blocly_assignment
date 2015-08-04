package io.bloc.android.blocly.ui.adapter;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.model.RssFeed;
import io.bloc.android.blocly.api.model.RssItem;
import io.bloc.android.blocly.ui.UIUtils;

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

    // For mapping colors

    private Map<Long, Integer> rssFeedToColor = new HashMap<Long, Integer>();

    // Related to items and feeds

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

        boolean onTablet;
        boolean contentExpanded;

        TextView title;
        TextView content;

        TextView feed;
        View headerWrapper;
        ImageView headerImage;

        CheckBox archiveCheckbox;
        CheckBox favoriteCheckbox;

        View expandedContentWrapper;
        TextView expandedContent;
        TextView visitSite;

        // For Tablet

        TextView callout;

        RssItem rssItem;

        // Constructor

        public ItemAdapterViewHolder(View itemView) {
            super(itemView);

            // Inflate the views from rss_item.xml (both kinds)

            title = (TextView) itemView.findViewById(R.id.tv_rss_item_title);
            content = (TextView) itemView.findViewById(R.id.tv_rss_item_content);

            // FOR PHONE VIEWS
            // The id in the if-statement is exclusive to the phone layout only!

            if(itemView.findViewById(R.id.tv_rss_item_feed_title) != null) {
                feed = (TextView) itemView.findViewById(R.id.tv_rss_item_feed_title);
                headerWrapper = itemView.findViewById(R.id.fl_rss_item_image_header);
                headerImage = (ImageView) headerWrapper.findViewById(R.id.iv_rss_item_image);
                archiveCheckbox = (CheckBox) itemView.findViewById(R.id.cb_rss_item_check_mark);
                favoriteCheckbox = (CheckBox) itemView.findViewById(R.id.cb_rss_item_favorite_star);
                expandedContentWrapper = itemView.findViewById(R.id.ll_rss_item_expanded_content_wrapper);
                expandedContent = (TextView) expandedContentWrapper.findViewById(R.id.tv_rss_item_content_full);
                visitSite = (TextView) expandedContentWrapper.findViewById(R.id.tv_rss_item_visit_site);
                visitSite.setOnClickListener(this);
                archiveCheckbox.setOnCheckedChangeListener(this);
                favoriteCheckbox.setOnCheckedChangeListener(this);
            }
            else {

                // For Tablet View
                onTablet = true;
                callout = (TextView) itemView.findViewById(R.id.tv_rss_item_callout);

                // Need this check to check API version otherwise a RunTimeException occurs

                if(Build.VERSION.SDK_INT >= 21) {

                    callout.setOutlineProvider(new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, Outline outline) {
                            outline.setOval(0,0, view.getWidth(), view.getHeight());
                        }
                    });

                    callout.setClipToOutline(true);
                }
            }

            // Leave this here...this is for the recyclerview

            itemView.setOnClickListener(this);
        }

        void update(RssFeed rssFeed, RssItem rssItem) {

            this.rssItem = rssItem;

            // Update TextView

            title.setText(rssItem.getTitle());

                // This is the content when previewed (restricted to 3 lines)

            content.setText(rssItem.getDescription());

            if(onTablet) {
//                callout.setText("" + rssFeed.getTitle().toUpperCase().charAt(0));
//                Integer color = rssFeedToColor.get(rssFeed.getRowId());

                callout.setText("" + rssItem.getTitle().toUpperCase().charAt(0));
                Integer color = rssFeedToColor.get(rssItem.getRowId());

                if(color == null) {
                    color = UIUtils.generateRandomColor(itemView.getResources().getColor(android.R.color.white));
                    rssFeedToColor.put(rssFeed.getRowId(), color);
                }

                callout.setBackgroundColor(color);
                return;
            }

            feed.setText(rssFeed.getTitle());

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