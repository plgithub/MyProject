package cn.pipi.mobile.pipiplayer.local.vlc.gui;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVLC;
import cn.pipi.mobile.pipiplayer.local.vlc.BitmapCache;
import cn.pipi.mobile.pipiplayer.local.vlc.MediaDatabase;
import cn.pipi.mobile.pipiplayer.local.vlc.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class PreferencesActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {


    public final static String VIDEO_RESUME_TIME = "VideoResumeTime";
    public final static String VIDEO_SUBTITLE_FILES = "VideoSubtitleFiles";
    public final static String VIDEO_CONTROL_GESTURE = "VideoControlGesture";
    public final static int RESULT_RESCAN = RESULT_FIRST_USER + 1;
    public final static int RESULT_RESTART = RESULT_FIRST_USER + 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        prepareActionBar();
        // Directories
        Preference directoriesPref = findPreference("directories");
        directoriesPref.setOnPreferenceClickListener(
                new OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(getApplicationContext(), BrowserActivity.class);
                        startActivity(intent);
                        setResult(RESULT_RESCAN);
                        return true;
                    }
                });

        // Screen orientation
        ListPreference screenOrientationPref = (ListPreference) findPreference("screen_orientation");
        screenOrientationPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(PreferencesActivity.this);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("screen_orientation_value", (String)newValue);
                editor.commit();
                return true;
            }
        });

        // Steal remote control
        Preference checkboxStealRC = findPreference("enable_steal_remote_control");
        checkboxStealRC.setOnPreferenceClickListener(
                new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        return true;
                    }
                });

        // Clear media library
        Preference clearMediaPref = findPreference("clear_media_db");
        clearMediaPref
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        MediaDatabase.getInstance(PreferencesActivity.this).emptyDatabase();
                        BitmapCache.getInstance().clear();
                        Toast.makeText(getBaseContext(), R.string.media_db_cleared, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

        /*** SharedPreferences Listener to apply changes ***/
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equalsIgnoreCase("hardware_acceleration")
                || key.equalsIgnoreCase("subtitle_text_encoding")
                || key.equalsIgnoreCase("aout")
                || key.equalsIgnoreCase("vout")
                || key.equalsIgnoreCase("chroma_format")
                || key.equalsIgnoreCase("deblocking")
                || key.equalsIgnoreCase("enable_frame_skip")
                || key.equalsIgnoreCase("enable_time_stretching_audio")
                || key.equalsIgnoreCase("enable_verbose_mode")
                || key.equalsIgnoreCase("network_caching")) {
            Util.updateLibVlcSettings(sharedPreferences);
            LibVLC.restart(this);
        }
    }

    private void prepareActionBar() {
		ActionBar mActionBar = getSupportActionBar();
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setTitle(R.string.preferences);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
       	          finish();
        }
        return super.onOptionsItemSelected(item);
	}
    
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
