package com.abd_lillah.ilyes.rssaggregator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by ilyes on 29/01/2017.
 */
public class ListFeed extends AppCompatActivity {
    private List<ListUserModel> mListUser;
    private RecyclerView mRecyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_feed);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        this.showUserList();
    }

    public void showUserList() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new UserListAdapter(mListUser));
    }
}
