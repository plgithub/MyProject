package cn.pipi.mobile.pipiplayer;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import cn.pipi.mobile.pipiplayer.NavigationDrawerFragment.NavigationDrawerCallbacks;
import cn.pipi.mobile.pipiplayer.NavigationDrawerFragment.UpdataTitelCallbacks;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.fragment.CartoonFragment;
import cn.pipi.mobile.pipiplayer.fragment.FilmFragment;
import cn.pipi.mobile.pipiplayer.fragment.HistoryFragment;
import cn.pipi.mobile.pipiplayer.fragment.HomeFragment;
import cn.pipi.mobile.pipiplayer.fragment.HomeFragment.LoadMoreInterFace;
import cn.pipi.mobile.pipiplayer.fragment.HotFilmFragment;
import cn.pipi.mobile.pipiplayer.fragment.SpecialFragment;
import cn.pipi.mobile.pipiplayer.fragment.TeleplayFragment;
import cn.pipi.mobile.pipiplayer.fragment.VarietyFragment;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.updata.UpdateManager;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.JsonUtil;

public class MainActivity extends BaseActivity implements OnClickListener, NavigationDrawerCallbacks,
        UpdataTitelCallbacks, LoadMoreInterFace {

    public ImageView drawlayoutImgSwitch;

    public LinearLayout mainActivityLayout;

    public TextView mTitelView;

    public ImageView searchTextView;

    public ImageView historyTextView;

    public ImageView downlaodTextView;

    private TextView shadowsTextView;

    private DrawerLayout mDrawerLayout;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    // 保存fragmemt
    private HashMap<String, Fragment> mFragments;

    private boolean isShowHistoryFragment = false;

    private FrameLayout historyFrameLayout;

    private final String mPageName = "pipiplayerPad_main";

    private final String[] analyMainStrings = {"首页", "电影", "电视剧", "综艺", "动漫", "大片", "专题"};

    private int itemWidth;

    private int itemHeight;

    private int itemImageHeight;

    private Fragment fragment;

    // 监听网络变化
    private NetWorkStateReceiver mBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // System.out.println("activity --->onCreate");
        widgetInit();
    }

    @Override
    public void widgetInit() {
        registerAReceiver();
        actionbarInit();
        drawFragmentInit();
        setPenentBack(true);
        // checkUpdate()
    }

    public void actionbarInit() {
        drawlayoutImgSwitch = (ImageView) this.findViewById(R.id.main_actionbar_icon);
        mTitelView = (TextView) this.findViewById(R.id.main_titel);
        searchTextView = (ImageView) this.findViewById(R.id.actionbar_search);
        historyTextView = (ImageView) this.findViewById(R.id.actionbar_history);
        downlaodTextView = (ImageView) this.findViewById(R.id.actionbar_download);
        searchTextView.setOnClickListener(this);
        historyTextView.setOnClickListener(this);
        downlaodTextView.setOnClickListener(this);
        drawlayoutImgSwitch.setOnClickListener(this);
        mTitelView.setOnClickListener(this);
        shadowsTextView = (TextView) this.findViewById(R.id.main_shadows);
        shadowsTextView.setOnClickListener(this);
    }

    public void drawFragmentInit() {
        mainActivityLayout = (LinearLayout) this.findViewById(R.id.main_activity);
        historyFrameLayout = (FrameLayout) this.findViewById(R.id.history_container);
        mDrawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(
                R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, mDrawerLayout);

    }

    private void checkUpdate() {
        UpdateManager.getUpdateManager(this).checkAppUpdate(this);
    }

    private void hideHistoryFramelayout(boolean isHide) {
        if (isHide) {
            isShowHistoryFragment = false;
            historyFrameLayout.setVisibility(View.GONE);
        } else {
            historyFrameLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 分类为空 有网络情况下重新加载
     */
    private void reGetAllClassify() {
        if (CommonUtil.isNetworkConnect(this))    // 无网络 return
            if (AppConfig.classifyMap == null || AppConfig.classifyMap.keySet().size() == 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppConfig.classifyMap = JsonUtil.getAllClassifyTypes();
                    }
                }).start();
            }
    }

    private void registerAReceiver() {
        mBroadcast = new NetWorkStateReceiver();
        // 为广播接收者设置IntentFilter
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mBroadcast, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // MobclickAgent.onPageStart( mPageName );
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        // MobclickAgent.onPageEnd( mPageName );
        MobclickAgent.onPause(this);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mBroadcast != null) {
            unregisterReceiver(mBroadcast);
        }
    }

//	@Override
//	protected void onResumeFragments() {
//		super.onResumeFragments();
//		Log.d(AppConfig.Tag, "---onResumeFragments---");
//	}

    /**
     * 更新标题
     */
    @Override
    public void updataTitel(String name) {
        if (mTitelView != null)
            mTitelView.setText(name);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, String titel) {
        reGetAllClassify();
        hideHistoryFramelayout(true);
        switchFragment(position, titel);
    }

    public void switchFragment(int position, String titel) {
        MobclickAgent.onEvent(this, "Click_Page", analyMainStrings[position]);
        fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new FilmFragment();
                break;
            case 2:
                fragment = new TeleplayFragment();
                break;
            case 3:
                fragment = new VarietyFragment();
                break;
            case 4:
                fragment = new CartoonFragment();
                break;
            case 5:
                fragment = new HotFilmFragment();
                break;
            case 6:
                fragment = new SpecialFragment();
                break;
            default:
                fragment = new HomeFragment();
                break;
        }

        Bundle bundle = new Bundle();
        if (titel != null)
            bundle.putString("titel", titel);
        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    /**
     * 显示或隐藏播放历史碎片
     */
    private void showOrHideHistoryFragment() {
        // HistoryFragment historyFragment=new HistoryFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HistoryFragment historyFragment = (HistoryFragment) fragmentManager.findFragmentById(R.id.history_container);
        if (historyFragment == null) {
            historyFragment = new HistoryFragment();
        }
        if (!isShowHistoryFragment) {
            shadowsTextView.setVisibility(View.VISIBLE);
            fragmentTransaction.replace(R.id.history_container, historyFragment);
            isShowHistoryFragment = true;
        } else {
            shadowsTextView.setVisibility(View.GONE);
            fragmentTransaction.remove(historyFragment);
            isShowHistoryFragment = false;
        }
        fragmentTransaction.commit();
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
        }

        ;
    };

    public void showOrHideMenu() {
        if (mNavigationDrawerFragment != null)
            mNavigationDrawerFragment.switchDrawLayout();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_titel:
            case R.id.main_actionbar_icon:
                showOrHideMenu();
                break;
            case R.id.actionbar_search:// 搜索
                MobclickAgent.onEvent(this, "Click_Page", "搜索");
                redirectTo(false, SearchActivity.class);
                break;
            case R.id.actionbar_history:// 历史
                MobclickAgent.onEvent(this, "Click_Page", "历史");
                hideHistoryFramelayout(false);
                showOrHideHistoryFragment();
                break;
            case R.id.actionbar_download:// 下载
                MobclickAgent.onEvent(this, "Click_Page", "下载");
                redirectTo(false, DownloadActivity.class);
                break;
            case R.id.main_shadows:
                // shadowsTextView.setVisibility(View.GONE);
                // hideHistoryFramelayout(true);
                // if (mNavigationDrawerFragment != null) {
                // mNavigationDrawerFragment.closeDrawer();
                // }
                hideHistoryFramelayout(false);
                showOrHideHistoryFragment();
                break;
        }

    }

    @Override
    public void loadMore(int position) {
        if (mNavigationDrawerFragment == null)
            return;
        if (position == 0) {
            mNavigationDrawerFragment.selectItem(1); // 电影
        } else if (position > 0 && position < 5) {
            mNavigationDrawerFragment.selectItem(2);// 电视剧
        } else if (position == 5) {
            mNavigationDrawerFragment.selectItem(4);// 动漫
        } else if (position == 6) {
            mNavigationDrawerFragment.selectItem(3);// 综艺
        }
    }
}
