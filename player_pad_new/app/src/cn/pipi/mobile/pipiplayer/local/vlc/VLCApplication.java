package cn.pipi.mobile.pipiplayer.local.vlc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import cn.pipi.mobile.pipiplayer.util.CatchHandler;
import cn.pipi.mobile.pipiplayer.util.FileUtils;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

public class VLCApplication extends Application {
    public final static String TAG = "PipiPlayer/VLCApplication";
    private static VLCApplication instance;

    public final static String SLEEP_INTENT = "cn.pipi.mobile.pipiplayer.local.vlc.SleepIntent";

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String p = pref.getString("set_locale", "");
        if (p != null && !p.equals("")) {
            Locale locale;
            // workaround due to region code
            if(p.equals("zh-TW")) {
                locale = Locale.TRADITIONAL_CHINESE;
            } else if(p.startsWith("zh")) {
                locale = Locale.CHINA;
            } else if(p.equals("pt-BR")) {
                locale = new Locale("pt", "BR");
            } else if(p.equals("bn-IN") || p.startsWith("bn")) {
                locale = new Locale("bn", "IN");
            } else {
                /**
                 * Avoid a crash of
                 * java.lang.AssertionError: couldn't initialize LocaleData for locale
                 * if the user enters nonsensical region codes.
                 */
                if(p.contains("-"))
                    p = p.substring(0, p.indexOf('-'));
                locale = new Locale(p);
            }
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
            CatchHandler.getInstance().init(getApplicationContext());//异常处理
        }
        instance = this;

        // Initialize the database soon enough to avoid any race condition and crash
        MediaDatabase.getInstance(this);
        initImageLoader(this);
    }
    
    public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2).threadPoolSize(3).memoryCache(new WeakMemoryCache())
				.discCacheSize(50 * 1024 * 1024).discCacheFileNameGenerator(new Md5FileNameGenerator())
				// .tasksProcessingOrder(QueueProcessingType.LIFO)
				// .writeDebugLogs() // Remove for release app
				.build();

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

    /**
     * Called when the overall system is running low on memory
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "System is running low on memory");

        BitmapCache.getInstance().clear();
    }

    /**
     * @return the main context of the Application
     */
    public static Context getAppContext()
    {
    	if(instance==null){
    		Log.i("VLCApplication", "instance ==null ");
    	}
        return instance;
    }

    /**
     * @return the main resources from the Application
     */
    public static Resources getAppResources()
    {
        if(instance == null) return null;
    	
        return instance.getResources();
    }
    //支持完美�?��程序
private List<Activity> mainActivity = new ArrayList<Activity>(); 
    
    public List<Activity> MainActivity() { 
    return mainActivity; 
    } 
    
    public void addActivity(Activity act) { 
    	if(!mainActivity.contains(act))
          mainActivity.add(act); 
    } 
    
    public void finishAll() { 
    for (Activity act : mainActivity) { 
    	if (!act.isFinishing()) { 
             act.finish(); 
          } 
    } 
    mainActivity = null; 
       System.exit(0);
    } 
    
    public boolean isServiceShowing=false;

	public boolean isServiceShowing() {
		return isServiceShowing;
	}

	public void setServiceShowing(boolean isServiceShowing) {
		this.isServiceShowing = isServiceShowing;
	}
   
}
