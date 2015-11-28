package cn.pipi.mobile.pipiplayer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.umeng.analytics.MobclickAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import cn.pipi.mobile.pipiplayer.bean.SourceBean;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.db.PIPISharedPreferences;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVlcException;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVlcUtil;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.FileUtils;
import cn.pipi.mobile.pipiplayer.util.JsonUtil;
import cn.pipi.mobile.pipiplayer.util.XMLPullParseUtil;

/**
 * 闪屏 缓冲界面
 *
 * @author qiny
 */
public class SplashActivity extends BaseActivity {
    private int screenWidth=0;
    private int screenHeight = 0;
    private ImageView pipilogo;
    private RelativeLayout adsRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.splash);
        widgetInit();

    }

    private void toMianActivity() {
        try {
            DownCenter.getInstance(this);
        } catch (LibVlcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(AppConfig.Tag, "----跳转到主界面----");
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void widgetInit() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        screenWidth=wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
        pipilogo=(ImageView)findViewById(R.id.pipilogo);

        umengConfigInit();
        PIPISharedPreferences.getInstance(this).defaultAppConfigSet();
        getScreenInfo();
        setPenentBack(false);
        movieSourcesIconInit();
//        TextView copyRight = (TextView) findViewById(R.id.splash_coptreight);
//        copyRight.setText("Copyright © 2006-2013 皮皮网 All Rights Reserved.");
        AppConfig.deviceUUID = CommonUtil.getDevicesUUID(this);
        AppConfig.currentVersionName = this.getResources().getString(
                R.string.pipiplayer_versionname);
        try {
            AppConfig.dev = URLEncoder.encode("android", "UTF-8");
            AppConfig.dver = URLEncoder.encode(AppConfig.currentVersionName,
                    "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        InitAsyncTask initAsyncTask = new InitAsyncTask(this);
        initAsyncTask.execute();
        adsRl = (RelativeLayout) findViewById(R.id.adsRl);
        RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) adsRl.getLayoutParams();
        params.width=(int) (screenWidth * 0.85f);
        params.height=(int)(screenHeight*0.8f);
        adsRl.setLayoutParams(params);
        String adPlaceId = "2073862";// 重要：请填上您的广告位ID
        new SplashAd(this, adsRl, listener, adPlaceId, true,
                SplashAd.SplashType.REAL_TIME);


    }

    SplashAdListener listener = new SplashAdListener() {
        @Override
        public void onAdPresent() {
            pipilogo.setImageResource(R.drawable.pad_logo2);
        }

        @Override
        public void onAdDismissed() {
            toMianActivity();
        }

        @Override
        public void onAdFailed(String s) {
            pipilogo.setImageResource(R.drawable.pad_logo1);
            toMianActivity();
        }

        @Override
        public void onAdClick() {

        }
    };

    public void createAppCacheFolder() {
        FileUtils.makeAppCacheFolder(this);
        Log.d(AppConfig.Tag, "--创建程序缓存目录---");
    }

    private void getClientAddress() {

        Log.d(AppConfig.Tag, "网络正常    ---------获取用户地址--------");
        AppConfig.clientAddress = CommonUtil.getClientAddress();
        System.out.println(AppConfig.clientAddress);
        Log.d(AppConfig.Tag, "clientaddress:" + AppConfig.clientAddress);
    }

    private void loadJniLibrary() {
        try {
            System.loadLibrary("TransmitLayer");
        } catch (UnsatisfiedLinkError ule) {
            Log.e(AppConfig.Tag, "Can't load TransmitLayer library: " + ule);
            // / FIXME Alert user
            System.exit(1);
        } catch (SecurityException se) {
            Log.e(AppConfig.Tag,
                    "Encountered a security issue when loading vlcjni library: "
                            + se);
            // / FIXME Alert user
            System.exit(1);
        }

        try {

            System.loadLibrary("FileSession");
        } catch (UnsatisfiedLinkError ule) {
            Log.e(AppConfig.Tag, "Can't load TransmitLayer library: " + ule);
            // / FIXME Alert user
            System.exit(1);
        } catch (SecurityException se) {
            Log.e(AppConfig.Tag,
                    "Encountered a security issue when loading vlcjni library: "
                            + se);
            // / FIXME Alert user
            System.exit(1);
        }

        LibVlcUtil.hasCompatibleCPU(this);
        Log.d(AppConfig.Tag, "--加载播发器相关so文件成功---");
    }

    private void getScreenInfo() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        Log.d(AppConfig.Tag, "Max memory is " + maxMemory + "KB");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        display.getMetrics(displayMetrics);
        AppConfig.currentScreenWidth = displayMetrics.widthPixels;
        AppConfig.currentScreenHeight = displayMetrics.heightPixels;
        AppConfig.currentScreenDensity = displayMetrics.density;
        boolean isTable = CommonUtil.isTablet(this);
        Log.d(AppConfig.Tag, "-------------当前屏幕信息-----------------");
        Log.d(AppConfig.Tag, "是否为平板:" + isTable);
        Log.d(AppConfig.Tag, "currentScreenWidth:"
                + AppConfig.currentScreenWidth);
        Log.d(AppConfig.Tag, "currentScreenHeight:"
                + AppConfig.currentScreenHeight);
        Log.d(AppConfig.Tag, "currentScreenDensity:"
                + AppConfig.currentScreenDensity);
        Log.d(AppConfig.Tag, "---------------------------------------");
    }

    /**
     * 影片所有来源 Init
     *
     * @author qiny
     */
    private void movieSourcesIconInit() {
        AppConfig.sourceMap = new HashMap<String, SourceBean>();
        int count = AppConfig.sourcekeys.length;
        for (int i = 0; i < count; i++) {
            SourceBean sourceBean = new SourceBean();
            sourceBean.setKey(AppConfig.sourcekeys[i]);
            sourceBean.setValues(AppConfig.sourceNormolBG[i]);
            AppConfig.sourceMap.put(AppConfig.sourcekeys[i], sourceBean);
        }
        Log.d(AppConfig.Tag, "--影片来源图标初始化成功---");
    }

    private void umengConfigInit() {
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);
    }

    class InitAsyncTask extends AsyncTask<String, Void, Void> {

        Context context;

        // 默认无网络
        private boolean isNetworkConnet = false;

        public InitAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(String... params) {
            createAppCacheFolder();
            loadJniLibrary();
            if (isNetworkConnet && AppConfig.isCheckUpdata) {
                // 检测升级
                XMLPullParseUtil.getServiceVersionName(context);
            }
            if (isNetworkConnet && AppConfig.isGetClientAddress) {
                // 获取客户端地址
                getClientAddress();
            }
            if (isNetworkConnet && AppConfig.isSplashGetClassify) {
                // 获取所有分类
                AppConfig.classifyMap = JsonUtil.getAllClassifyTypes();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//			toMianActivity();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            if (CommonUtil.isNetworkConnect(context)) {
                isNetworkConnet = true;
            }
            super.onPreExecute();
        }

    }

}
