package io.bloc.android.blocly.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.model.RssFeed;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {

    // Enumeration for the items in the drawer

    public enum NavigationOption {
        NAVIGATION_OPTION_INBOX,
        NAVIGATION_OPTION_FAVORITES,
        NAVIGATION_OPTION_ARCHIVED
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.navigation_item, viewGroup, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        RssFeed rssFeed = null;

        if(position >= NavigationOption.values().length) {
            int feedPosition = position - NavigationOption.values().length;
            rssFeed = BloclyApplication.getSharedDataSource().getFeeds().get(feedPosition);
        }

        viewHolder.update(position, rssFeed);

    }

    @Override
    public int getItemCount() {
        return NavigationOption.values().length + BloclyApplication.getSharedDataSource().getFeeds().size();
    }

    // This is for just one view...just remember that

    class ViewHolder extends RecyclerView.ViewHolder {

        View topPadding;
        TextView title;
        View bottomPadding;
        View divider;

        public ViewHolder(final View itemView) {
            super(itemView);

            // Associate variables with layouts

            topPadding = itemView.findViewById(R.id.v_nav_item_top_padding);
            title = (TextView) itemView.findViewById(R.id.tv_nav_item_title);
            bottomPadding = itemView.findViewById(R.id.v_nav_item_bottom_padding);
            divider = itemView.findViewById(R.id.v_nav_item_divider);

            // Listener (for just one item)

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Nothing yet...", Toast.LENGTH_SHORT).show();
                }

            });
        }

        // Update method

        void update(int position, RssFeed rssFeed) {

            // To show top padding or not...

            boolean shouldShowTopPadding = position == NavigationOption.NAVIGATION_OPTION_INBOX.ordinal()
                || position == NavigationOption.values().length;

            topPadding.setVisibility(shouldShowTopPadding ? View.VISIBLE : View.GONE);

            // To show bottom padding or not depending how many items or at a certain position

            boolean shouldShowBottomPadding = position == NavigationOption.NAVIGATION_OPTION_ARCHIVED.ordinal()
                    || position == getItemCount()-1;

            bottomPadding.setVisibility(shouldShowBottomPadding ? View.VISIBLE : View.GONE);

            // Divider visibility

            divider.setVisibility(position == NavigationOption.NAVIGATION_OPTION_ARCHIVED.ordinal()
            ? View.VISIBLE : View.GONE);

            if (position < NavigationOption.values().length) {

                int[] titleTexts = {
                        R.string.navigation_option_inbox,
                        R.string.navigation_option_favorites,
                        R.string.navigation_option_archived
                };

                title.setText(titleTexts[position]);

            }
            else {
                title.setText(rssFeed.getTitle());
            }


        }

    }
}
