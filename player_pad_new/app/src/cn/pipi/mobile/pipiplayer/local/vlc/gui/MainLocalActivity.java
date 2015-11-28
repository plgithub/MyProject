package cn.pipi.mobile.pipiplayer.local.vlc.gui;

import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVlcException;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVlcUtil;
import cn.pipi.mobile.pipiplayer.local.vlc.MediaLibrary;
import cn.pipi.mobile.pipiplayer.local.vlc.Util;
import cn.pipi.mobile.pipiplayer.local.vlc.WeakHandler;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.video.VideoGridFragment;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.video.VideoGridFragment.VideoGridInterface;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.video.VideoListAdapter;
import cn.pipi.mobile.pipiplayer.local.vlc.interfaces.ISortable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.umeng.analytics.MobclickAgent;

public class MainLocalActivity extends SherlockFragmentActivity implements VideoGridInterface{
    public final static String TAG = "PipiPlayer/MainActivity";

    protected static final String ACTION_SHOW_PROGRESSBAR = "cn.pipi.mobile.pipiplayer.vlc.gui.ShowProgressBar";
    protected static final String ACTION_HIDE_PROGRESSBAR = "cn.pipi.mobile.pipiplayer.vlc.gui.HideProgressBar";
    protected static final String ACTION_SHOW_TEXTINFO = "cn.pipi.mobile.pipiplayer.vlc.gui.ShowTextInfo";
    public static final String ACTION_SHOW_PLAYER = "cn.pipi.mobile.pipiplayer.vlc.gui.ShowPlayer";
    private static final String PREF_FIRST_RUN = "first_run";

    private static final int ACTIVITY_RESULT_PREFERENCES = 1;
    private static final int ACTIVITY_SHOW_INFOLAYOUT = 2;


    private View mInfoLayout;
    private ProgressBar mInfoProgress;
    private TextView mInfoText;
    private String mCurrentFragment;

    private SharedPreferences mSettings;

    private int mVersionNumber = -1;
    private boolean mFirstRun = false;
    private boolean mScanNeeded = true;

    private Handler mHandler = new MainActivityHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!LibVlcUtil.hasCompatibleCPU(this)) {
            Log.e(TAG, LibVlcUtil.getErrorMsg());
            Intent i = new Intent(this, CompatErrorActivity.class);
            startActivity(i);
            finish();
            super.onCreate(savedInstanceState);
            return;
        }
        setTitleColor(Color.WHITE);
        /* Get the current version from package */
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            Log.e(TAG, "package info not found.");
        }
        if (pinfo != null)
            mVersionNumber = pinfo.versionCode;

        /* Get settings */
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        /* Check if it's the first run */
        mFirstRun = mSettings.getInt(PREF_FIRST_RUN, -1) != mVersionNumber;
        if (mFirstRun) {
            Editor editor = mSettings.edit();
            editor.putInt(PREF_FIRST_RUN, mVersionNumber);
            editor.commit();
        }
        
        try {
            // Start LibVLC
            Util.getLibVlcInstance();
        } catch (LibVlcException e) {
            e.printStackTrace();
            Intent i = new Intent(this, CompatErrorActivity.class);
            i.putExtra("runtimeError", true);
            i.putExtra("message", "LibVLC failed to initialize (LibVlcException)");
            startActivity(i);
            finish();
            super.onCreate(savedInstanceState);
            return;
        }

        super.onCreate(savedInstanceState);

        /*** Start initializing the UI ***/

        /* Enable the indeterminate progress feature */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        // Set up the sliding menu
        setContentView(R.layout.local_movie_main);
        prepareActionBar();
        VideoGridFragment fragment=new VideoGridFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_placeholder, fragment).commit();
    }

    private void prepareActionBar() {
    	ActionBar mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setTitle("本地");
    }


    @Override
    protected void onResume() {
    	super.onResume();
        if (mScanNeeded)
            MediaLibrary.getInstance(this).loadMediaItems(this);
    }


    /**
     * Stop audio player and save opened tab
     */
    @Override
    protected void onPause() {
        super.onPause();
        /* Check for an ongoing scan that needs to be resumed during onResume */
        mScanNeeded = MediaLibrary.getInstance(this).isWorking();
        /* Stop scanning for files */
        MediaLibrary.getInstance(this).stop();
        /* Save the tab status in pref */
        SharedPreferences.Editor editor = getSharedPreferences("MainActivity", MODE_PRIVATE).edit();
        editor.putString("fragment", mCurrentFragment);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(messageReceiver);
        } catch (IllegalArgumentException e) {}
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        /* Reload the latest preferences */
    }



    /** Create menu from XML
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Note: on Android 3.0+ with an action bar this method
         * is called while the view is created. This can happen
         * any time after onCreate.
         */
        MenuInflater inflater = getSupportMenuInflater();
       /// inflater.inflate(R.menu.media_library, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Handle onClick form menu buttons
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Intent to start a new Activity
        Intent intent;

        // Handle item selection
        switch (item.getItemId()) {
            // Restore last playlist
            case android.R.id.home:
                /* Toggle the sidebar */
             /*   if(mMenu.isMenuShowing())
                    mMenu.showContent();
                else
                    mMenu.showMenu();*/
            	finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_RESULT_PREFERENCES) {
            if (resultCode == PreferencesActivity.RESULT_RESCAN)
                MediaLibrary.getInstance(this).loadMediaItems(this, true);
        }
    }

    /**
     * onClick event from xml
     * @param view
     */
    public void searchClick(View view) {
        onSearchRequested();
    }

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equalsIgnoreCase(ACTION_SHOW_PROGRESSBAR)) {
                setSupportProgressBarIndeterminateVisibility(true);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else if (action.equalsIgnoreCase(ACTION_HIDE_PROGRESSBAR)) {
                setSupportProgressBarIndeterminateVisibility(false);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else if (action.equalsIgnoreCase(ACTION_SHOW_TEXTINFO)) {
                String info = intent.getStringExtra("info");
                int max = intent.getIntExtra("max", 0);
                int progress = intent.getIntExtra("progress", 100);
                mInfoText.setText(info);
                mInfoProgress.setMax(max);
                mInfoProgress.setProgress(progress);

                if (info == null) {
                    /* Cancel any upcoming visibility change */
                    mHandler.removeMessages(ACTIVITY_SHOW_INFOLAYOUT);
                    mInfoLayout.setVisibility(View.GONE);
                }
                else {
                    /* Slightly delay the appearance of the progress bar to avoid unnecessary flickering */
                    if (!mHandler.hasMessages(ACTIVITY_SHOW_INFOLAYOUT)) {
                        Message m = new Message();
                        m.what = ACTIVITY_SHOW_INFOLAYOUT;
                        mHandler.sendMessageDelayed(m, 300);
                    }
                }
            }
        }
    };

    private static class MainActivityHandler extends WeakHandler<MainLocalActivity> {
        public MainActivityHandler(MainLocalActivity owner) {
            super(owner);
        }

        @Override
        public void handleMessage(Message msg) {
            MainLocalActivity ma = getOwner();
            if(ma == null) return;

            switch (msg.what) {
                case ACTIVITY_SHOW_INFOLAYOUT:
                    ma.mInfoLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    public static void showProgressBar(Context context) {
        if (context == null)
            return;
        Intent intent = new Intent();
        intent.setAction(ACTION_SHOW_PROGRESSBAR);
        context.getApplicationContext().sendBroadcast(intent);
    }

    public static void hideProgressBar(Context context) {
        if (context == null)
            return;
        Intent intent = new Intent();
        intent.setAction(ACTION_HIDE_PROGRESSBAR);
        context.getApplicationContext().sendBroadcast(intent);
    }

    public static void sendTextInfo(Context context, String info, int progress, int max) {
        if (context == null)
            return;
        Intent intent = new Intent();
        intent.setAction(ACTION_SHOW_TEXTINFO);
        intent.putExtra("info", info);
        intent.putExtra("progress", progress);
        intent.putExtra("max", max);
        context.getApplicationContext().sendBroadcast(intent);
    }

    public static void clearTextInfo(Context context) {
        sendTextInfo(context, null, 0, 100);
    }
    
    private void setWidthAndHeight(View view,int whdth,int heigth){
    	LayoutParams  para = view.getLayoutParams();
    if(whdth>0) para.width=whdth;
        para.height = heigth;
        view.setLayoutParams(para);
    }

	@Override
	public void initViewData() {
		// TODO Auto-generated method stub
		
	}
}
