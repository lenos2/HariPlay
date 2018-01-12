package zw.co.hariplay.hariplay.Home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Random;

import zw.co.hariplay.hariplay.DirectConnect.ConnectionsActivity;
import zw.co.hariplay.hariplay.Login.LoginActivity;
import zw.co.hariplay.hariplay.R;
import zw.co.hariplay.hariplay.Share.ShareActivity;
import zw.co.hariplay.hariplay.Share.VideoFragment;
import zw.co.hariplay.hariplay.Utils.BottomNavigationViewHelper;
import zw.co.hariplay.hariplay.Utils.MainfeedListAdapter;
import zw.co.hariplay.hariplay.Utils.SectionsPagerAdapter;
import zw.co.hariplay.hariplay.Utils.UniversalImageLoader;
import zw.co.hariplay.hariplay.Utils.ViewCommentsFragment;
import zw.co.hariplay.hariplay.models.Photo;
import zw.co.hariplay.hariplay.models.Video;

public class HomeActivity extends ConnectionsActivity implements
        MainfeedListAdapter.OnLoadMoreItemsListener{

    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos");
        HomeFragment fragment = (HomeFragment)getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mViewPager.getCurrentItem());
        if(fragment != null){
            //fragment.displayMorePhotos();
            fragment.displayMoreVideos();
        }
    }

    private static final String TAG = "HomeActivity";
    private static final String USER_NAME = "username";
    private static final int ACTIVITY_NUM = 0;
    private static final int HOME_FRAGMENT = 1;
    private static final int P2P_FRAGMENT = 3;
    private VideoFragment vFrag;

    private Context mContext = HomeActivity.this;
    private P2pConnectionFragment mP2pConnectionFragment;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;


    /**
     * Check a single permission is it has been verified
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(HomeActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }


    /**
     * This service id lets us find other nearby devices that are interested in the same thing. Our
     * sample does exactly one thing, so we hardcode the ID.
     */
    private static final String SERVICE_ID =
            "zw.co.hariplay.hariplay.SERVICE_ID";
    private static String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting.");
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);


        setupFirebaseAuth();

//        initImageLoader();
//        setupBottomNavigationView();
//        setupViewPager();

        setupNearBy();


    }

    private void setupNearBy() {
        String uName;
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        mName = prefs.getString(USER_NAME, "");

        //mName = generateRandomName() + "_" + uName;

    }

    private static String generateRandomName() {
        String name = "";
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            name += random.nextInt(10);
        }

        return name;
    }

    public void onCommentThreadSelected(Photo photo, String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");

        ViewCommentsFragment fragment  = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }
    public void onCommentThreadSelected(Video video, String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");

        ViewCommentsFragment fragment  = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.video), video);
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }


    public void showLayout(){
        Log.d(TAG, "hideLayout: showing layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mFrameLayout.getVisibility() == View.VISIBLE){
            showLayout();
        }
    }


    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    /**
     * Responsible for adding the 3 tabs: Camera, Home, Messages
     */
    private void setupViewPager(){
        vFrag = new VideoFragment();
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(vFrag); //index 0
        adapter.addFragment(new HomeFragment()); //index 1
        //adapter.addFragment(new MessagesFragment()); //index 2
        adapter.addFragment(setUpP2pConnectionFragment()); //index 3
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_recorder);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_main_feed);
        //tabLayout.getTabAt(2).setIcon(R.drawable.ic_arrow);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_direct_connect);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    //vFrag.startRecHome();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private P2pConnectionFragment setUpP2pConnectionFragment(){
        mP2pConnectionFragment = new P2pConnectionFragment();
        return mP2pConnectionFragment;
    }
    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, this,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * checks to see if the @param 'user' is logged in
     * @param user
     */
     private void checkCurrentUser(FirebaseUser user){
         Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

         if (user != null) {
             // User is signed in
             initImageLoader();
             setupBottomNavigationView();
             setupViewPager();

             mViewPager.setCurrentItem(HOME_FRAGMENT);
             //mViewPager.setCurrentItem(P2P_FRAGMENT);
             Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
         } else {
             // User is signed out
             Intent intent = new Intent(mContext, LoginActivity.class);
             startActivity(intent);

             Log.d(TAG, "onAuthStateChanged:signed_out");
         }
     }
    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);


                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        //mViewPager.setCurrentItem(HOME_FRAGMENT);
        checkCurrentUser(mAuth.getCurrentUser());


    }

    @Override
    protected void onAdvertisingStarted() {
        super.onAdvertisingStarted();

    }

    @Override
    protected void onEndpointDiscovered(Endpoint endpoint) {
        super.onEndpointDiscovered(endpoint);
        Toast.makeText(this,"End point discovered is : " +endpoint.getName(),Toast.LENGTH_LONG).show();
        mP2pConnectionFragment.setDevicesFound(getDiscoveredEndpoints());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);

        //disconnectFromAllEndpoints();
        startDiscovering();
        startAdvertising();
    }

    @Override
    protected void onDiscoveryStarted() {
        super.onDiscoveryStarted();
    }


    @Override
    protected String getName() {
        Toast.makeText(this,mName,Toast.LENGTH_SHORT).show();
        return mName;
    }

    @Override
    protected String getServiceId() {
        return SERVICE_ID;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }



}
