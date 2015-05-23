package com.lumi_dos.lge;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.google.android.gms.analytics.GoogleAnalytics;


public class IntranetActivity extends ActionBarActivity {

    String NAME = "LGE";
    String EMAIL = "secretariat@lge.lu";
    int PROFILE = R.drawable.profile;

    private Toolbar toolbar;

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    DrawerLayout Drawer;

    ActionBarDrawerToggle mDrawerToggle;

    public int currentSlideNumber = 1;
    public String slide_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intranet);

        //Get a Tracker (should auto-report)
        ((LGE) getApplication()).getTracker(LGE.TrackerName.APP_TRACKER);

        toolbar =(Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        String TITLES[] = getResources().getStringArray(R.array.nav_drawer_titles);
        int ICONS[] = LGE.ICONS;

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MyAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE, this);

        mRecyclerView.setAdapter(mAdapter);

        final GestureDetector mGestureDetector = new GestureDetector(IntranetActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });


        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());

                if(child!=null && mGestureDetector.onTouchEvent(motionEvent)){
                    Drawer.closeDrawers();
                    //Toast.makeText(NewsActivity.this, "The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();

                    int itemClicked = recyclerView.getChildPosition(child);

                    Intent intent = LGE.startActivityOnNavDrawerCAll(itemClicked,getApplicationContext(), getString(R.string.feedback_address), getString(R.string.feedback_subject), getString(R.string.feedback_subject), getString(R.string.choose_email_client));

                    startActivity(intent);

                    return true;

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //code here will be executed when drawer gets opened
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //code here gets executed  when drawer gets closed
            }
        };

        Drawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final WebView slideView = (WebView) findViewById(R.id.slideView);
        slideView.setVisibility(View.INVISIBLE);
        slideView.clearCache(true);
        slideView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.INVISIBLE);
                slideView.setVisibility(View.VISIBLE);
            }
        });
        WebSettings webSettings = slideView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //Keep for later, maybe it may be necessary to enable caching
        /*webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);*/

        //ImageView slideView = (ImageView) findViewById(R.id.slideView);

        slide_url = constructURL(currentSlideNumber);

        /*Picasso instance = Picasso.with(this);
        instance.setIndicatorsEnabled(true);
        instance.load(slide_url).into(slideView);*/


        slideView.loadUrl(slide_url);
    }

    public void onStart() {
        super.onStart();
        //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    public void onStop() {
        super.onStop();
        //Stop the analytics tracking
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String constructURL(int slideNumber) {
        return getString(R.string.base_intranet_url) + slideNumber;
        //return "http://www.lge.lu/lgeapp/intranet/2006/1996/Slide" + slideNumber;
    }

    public void slideBack(View view) {
        if(currentSlideNumber > 1) {
            currentSlideNumber--;
            initializeLoadingSequence(view);
            slide_url = constructURL(currentSlideNumber);
            WebView slideView = (WebView) findViewById(R.id.slideView);
            slideView.loadUrl(slide_url);

            /*ImageView slideView = (ImageView) findViewById(R.id.slideView);

            slide_url = constructURL(currentSlideNumber);

            Picasso instance = Picasso.with(this);
            instance.setIndicatorsEnabled(true);
            instance.load(slide_url).into(slideView);*/
        }
    }

    public void slideForward(View view) {
        currentSlideNumber++;
        initializeLoadingSequence(view);
        slide_url = constructURL(currentSlideNumber);
        WebView slideView = (WebView) findViewById(R.id.slideView);
        slideView.loadUrl(slide_url);

        /*ImageView slideView = (ImageView) findViewById(R.id.slideView);

        slide_url = constructURL(currentSlideNumber);

        Picasso instance = Picasso.with(this);
        instance.setIndicatorsEnabled(true);
        instance.load(slide_url).into(slideView);*/
    }

    public void initializeLoadingSequence(View view) {
        WebView slideView = (WebView) findViewById(R.id.slideView);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        slideView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
}