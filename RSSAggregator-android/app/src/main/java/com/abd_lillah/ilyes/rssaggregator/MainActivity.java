package com.abd_lillah.ilyes.rssaggregator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private Button mFetchFeedButton;
    private SwipeRefreshLayout mSwipeLayout;
    private TextView mFeedTitleTextView;
    private TextView mFeedLinkTextView;
    private TextView mFeedDescriptionTextView;
    private Context mContext;
    private Activity mActivity;
    private Intent mIntent;
    private String mLinks;
    private RelativeLayout mRelativeLayout;
    private Button mButton;
    private RSSDataBaseAdapter mRss;
    private PopupWindow mPopupWindow;
    private List<RssFeedModel> mFeedModelList;
    private String mFeedTitle;
    private String mFeedLink;
    private String mFeedDescription;
    private FloatingActionButton mAddPreferency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            mEditText = (EditText) findViewById(R.id.rssFeedEditText);
            mAddPreferency = (FloatingActionButton) findViewById(R.id.fab);
            mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
            mFeedTitleTextView = (TextView) findViewById(R.id.feedTitle);
            mFeedDescriptionTextView = (TextView) findViewById(R.id.feedDescription);
            mFeedLinkTextView = (TextView) findViewById(R.id.feedLink);
            mContext = getApplicationContext();
            mActivity = MainActivity.this;
            mIntent = this.getIntent();
            mRss = new RSSDataBaseAdapter(this);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            mAddPreferency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Insert Title and RSS link");
                final TextInputEditText title = new TextInputEditText(MainActivity.this);
                final TextInputEditText link = new TextInputEditText(MainActivity.this);
                title.setInputType(InputType.TYPE_CLASS_TEXT);
                link.setInputType(InputType.TYPE_CLASS_TEXT);
                title.setHint("RSS Title");
                link.setHint("RSS Link");
                LinearLayout ll=new LinearLayout(MainActivity.this);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setPadding(55, 0, 55, 0);
                ll.addView(title);
                ll.addView(link);
                alertDialog.setView(ll);

                alertDialog.setCancelable(false);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",

                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",

                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (title.getText().toString().equals("") || link.getText().toString().equals("")) {
                                    Snackbar.make(view
                                            , "Empty fields, please retry", Snackbar.LENGTH_LONG)
                                            .setAction("CLOSE", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                }
                                            })
                                            .show();
                                } else {

                                    String id;
                                    mRss = mRss.open();
                                    mRss.insertEntry(link.getText().toString(), title.getText().toString(), Integer.valueOf(mIntent.getStringExtra("user_id")));
                                    Snackbar.make(view
                                            , "Feed " +  title.getText().toString() + " added successfully", Snackbar.LENGTH_LONG)
                                            .setAction("CLOSE", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                }
                                            })
                                            .show();
                                    mRss.close();
                                    alertDialog.dismiss();
                                }
                            }
                        });
                alertDialog.show();
            }
        });

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    new FetchFeedTask().execute((Void) null);
                    mEditText.clearFocus();
                    return true;
                }
                return false;
            }
        });

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchFeedTask().execute((Void) null);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void getUserLinks() {
        List<String> links = mRss.getLinks(Integer.valueOf(mIntent.getStringExtra("user_id")));
        Log.v("Logs", String.valueOf(links));
        for (int i = 0; i < links.size(); i++) {
            mLinks = links.get(i);
            URL url = null;
            try {
                url = new URL(mLinks);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            InputStream inputStream = null;
            try {
                inputStream = url.openConnection().getInputStream();
                mFeedModelList = this.parseFeed(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
    }

    public List<RssFeedModel> parseFeed(InputStream inputStream) throws XmlPullParserException, IOException {
        String title = null;
        String link = null;
        String description = null;
        boolean isItem = false;
        List<RssFeedModel> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MainActivity", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                }

                if (title != null && link != null && description != null) {
                    if(isItem) {
                        RssFeedModel item = new RssFeedModel(title, link, description);
                        items.add(item);
                    }
                    else {
                        mFeedTitle = title;
                        mFeedLink = link;
                        mFeedDescription = description;
                    }

                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }
            return items;
        } finally {
            inputStream.close();
        }
    }

    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        private String urlLink;

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
            mFeedTitle = null;
            mFeedLink = null;
            mFeedDescription = null;
            mFeedTitleTextView.setText("Feed Title: " + mFeedTitle);
            mFeedDescriptionTextView.setText("Feed Description: " + mFeedDescription);
            mFeedLinkTextView.setText("Feed Link: " + mFeedLink);
            urlLink = mEditText.getText().toString() == "" ? mLinks : mEditText.getText().toString();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(urlLink))
                return false;

            try {
                if(!urlLink.startsWith("http://") && !urlLink.startsWith("https://"))
                    urlLink = "http://" + urlLink;

                URL url = new URL(urlLink);
                InputStream inputStream = url.openConnection().getInputStream();
                mFeedModelList = parseFeed(inputStream);
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            } catch (XmlPullParserException e) {
                Log.e(TAG, "Error", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {
                mFeedTitleTextView.setText("Feed Title: " + mFeedTitle);
                mFeedDescriptionTextView.setText("Feed Description: " + mFeedDescription);
                mFeedLinkTextView.setText("Feed Link: " + mFeedLink);
                // Fill RecyclerView
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(new RssFeedListAdapter(mFeedModelList));
            } else {
                Snackbar.make(findViewById(android.R.id.content)
                        , "Retry, cannot get the RSS Feed", Snackbar.LENGTH_LONG)
                        .setAction("CLOSE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.feeds) {
            Intent intentSuccess = new Intent(MainActivity.this ,ListFeed.class);
            intentSuccess.putExtra("links", String.valueOf(mRss.getLinks(Integer.valueOf(mIntent.getStringExtra("user_id")))));
            startActivity(intentSuccess);
        }
        if (id == R.id.action_settings) {
            Intent intentSuccess = new Intent(MainActivity.this , SignInActivity.class);
            startActivity(intentSuccess);
            mRss.close();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.feeds) {
            Intent intentSuccess = new Intent(MainActivity.this ,ListFeed.class);
            intentSuccess.putExtra("links", String.valueOf(mRss.getLinks(Integer.valueOf(mIntent.getStringExtra("user_id")))));
            startActivity(intentSuccess);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
