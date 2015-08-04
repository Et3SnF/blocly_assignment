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

    // static interface to delegate to another class

    public static interface ItemAdapterDelegate {

        public void didSelectExpandItem(ItemAdapter itemAdapter, View itemView);
        public void didSelectContractItem(ItemAdapter itemAdapter, View itemView);
        public void didSelectVisitSite(ItemAdapter itemAdapter, TextView visitSite);
        public void didSelectFavoritesCheck(ItemAdapter itemAdapter, CheckBox favorites);
        public void didSelectArchivedCheck(ItemAdapter itemAdapter, CheckBox archived);

    }

    // Make any upcoming delegate a weak reference

    WeakReference<ItemAdapterDelegate> itemAdapterDelegate;

    // Setter and Getters of to ItemAdapter delegate

    public void setItemAdapterDelegate(ItemAdapterDelegate itemAdapterDelegate) {
        this.itemAdapterDelegate = new WeakReference<ItemAdapterDelegate>(itemAdapterDelegate);
    }

    public ItemAdapterDelegate getItemAdapterDelegate() {

        if(itemAdapterDelegate == null) {
            return null;
        }

        return this.itemAdapterDelegate.get();

    }

    class ItemAdapterViewHolder extends RecyclerView.ViewHolder implements ImageLoadingListener {

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

        public ItemAdapterViewHolder(final View itemView) {
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

            //---------//

            // Activate clickListener for one ViewHolder

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (view == itemView) {
                        animateContent(!contentExpanded);
                    }

                    if(getItemAdapterDelegate() == null) {
                        return;
                    }

                    if(contentExpanded) {
                        getItemAdapterDelegate().didSelectExpandItem(ItemAdapter.this, itemView);
                    }
                    else if(!contentExpanded) {
                        getItemAdapterDelegate().didSelectContractItem(ItemAdapter.this, itemView);
                    }

                }

            });

            // Activate Listeners for archive checkbox

            archiveCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    Log.v(TAG, "Archive Checked changed to: " + isChecked);

                    if(getItemAdapterDelegate() == null) {
                        return;
                    }

                    getItemAdapterDelegate().didSelectArchivedCheck(ItemAdapter.this, archiveCheckbox);
                }

            });

            // Activate listener for favorite checkbox

            favoriteCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    Log.v(TAG, "Archive Checked changed to: " + isChecked);

                    if(getItemAdapterDelegate() == null) {
                        return;
                    }

                    getItemAdapterDelegate().didSelectFavoritesCheck(ItemAdapter.this, favoriteCheckbox);
                }

            });

            // Activate listener for Visit Site link

            visitSite.setOnClickListener(new View.OnClickListener() {

                // --- OnClickListener Methods -- //

                @Override
                public void onClick(View view) {

                    //Toast.makeText(view.getContext(), "Visit " + rssItem.getUrl(), Toast.LENGTH_SHORT).show();

                    if(getItemAdapterDelegate() == null) {
                        return;
                    }

                    getItemAdapterDelegate().didSelectVisitSite(ItemAdapter.this, visitSite);

                }

            });

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

        /**
         *
         * ImageLoadingListener Methods
         *
         */

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

                startingHeight = finalHeight;

                // Set the transparency and the visibility of the wrapper (View class)

                expandedContentWrapper.setAlpha(0f);
                expandedContentWrapper.setVisibility(View.VISIBLE);

                // Use the measure method to ask view to measure itself (the width of content)

                expandedContentWrapper.measure(View.MeasureSpec.makeMeasureSpec(content.getWidth(),
                        View.MeasureSpec.EXACTLY), ViewGroup.LayoutParams.WRAP_CONTENT);

                // This height is actually unlimited

                finalHeight = expandedContentWrapper.getMeasuredHeight();
            }
            else {
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