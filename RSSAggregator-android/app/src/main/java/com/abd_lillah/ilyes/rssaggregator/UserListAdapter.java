package com.abd_lillah.ilyes.rssaggregator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ilyes on 28/01/2017.
 */
public class UserListAdapter
        extends RecyclerView.Adapter<UserListAdapter.FeedModelViewHolder> {

    private List<ListUserModel> mRssFeedModels;
    private Context mContext;
    public static class FeedModelViewHolder extends RecyclerView.ViewHolder {
        private View rssFeedView;

        public FeedModelViewHolder(View v) {
            super(v);
            rssFeedView = v;
        }
    }


    public UserListAdapter(List<ListUserModel> rssFeedModels) {
        mRssFeedModels = rssFeedModels;
    }

    @Override
    public FeedModelViewHolder onCreateViewHolder(ViewGroup parent, final int type) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_feed, parent, false);
        FeedModelViewHolder holder = new FeedModelViewHolder(v);
        return holder;
    }


    @Override
    public void onBindViewHolder(final FeedModelViewHolder holder, final int position) {
        final ListUserModel rssFeedModel = mRssFeedModels.get(position);

        ((TextView)holder.rssFeedView.findViewById(R.id.titleFeed)).setText(rssFeedModel.link);
        ((TextView)holder.rssFeedView.findViewById(R.id.linkFeed)).setText(rssFeedModel.title);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(rssFeedModel.link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext = view.getContext();
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRssFeedModels.size();
    }
}