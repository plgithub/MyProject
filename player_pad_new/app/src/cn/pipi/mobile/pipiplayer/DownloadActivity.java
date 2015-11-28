package cn.pipi.mobile.pipiplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.pipi.mobile.pipiplayer.adapter.DownloadAdapter;
import cn.pipi.mobile.pipiplayer.adapter.DownloadAdapter.DownloadEditInterface;
import cn.pipi.mobile.pipiplayer.adapter.DownloadDetailAdapter;
import cn.pipi.mobile.pipiplayer.adapter.StoreGridAdapter;
import cn.pipi.mobile.pipiplayer.adapter.StoreGridAdapter.StoreEditInterface;
import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.local.vlc.MediaLibrary;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.video.VideoGridFragment;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.ComparatorDownTask;
import cn.pipi.mobile.pipiplayer.util.FileUtils;
import cn.pipi.mobile.pipiplayer.util.SdcardUtil;
import cn.pipi.mobile.pipiplayer.view.CustomDialog;

/**
 * 下载界面
 *
 * @author qiny
 */
public class DownloadActivity extends SherlockFragmentActivity implements
        OnClickListener, OnCheckedChangeListener, Runnable,
        OnItemClickListener, DownloadEditInterface, StoreEditInterface {

    private final int COMPUTE_SDCARDINFO = 0;

    private final int GET_DOWNLOAD_LIST = 1;

    private final int UPDATE_DOWNLOADADAPTER = 2;

    private final int GET_FAVOURITE_LIST = 3;

    private final int DEL_DOWNLOAD_EDIT = 4;

    private final int DEL_STORE_EDIT = 5;

    private final String mPageName = "pipiplayerPad_downloadActivity";

    public boolean isUpdate = true;

    private RadioGroup downloadRadioGroup;

    // 下载使用大小
    private TextView videoCacheTextView;
    // 剩余可用大小
    private TextView availableTextView;

    private TextView allPauseTextView;

    private TextView editTextView;

    private List<DownTask> mTaskList;

    private DownCenter mDownCenter;

    private DBHelperDao dbHelperDao;

    private GridView downGridView;

    private DownloadAdapter downloadAdapter;

    // 收藏
    private GridView storeGridView;

    private StoreGridAdapter storeGridAdapter;

    private LinearLayout shadowslayout;

    public static int currentIndex = 0;

    private boolean isEdit = false; // 是否为编辑状态

    private int downloadNum = 0;

    private int storeNum = 0;

    private VideoGridFragment fragment;

    private TextView emptyInfo;

    // ........................................

    protected static final String ACTION_SHOW_PROGRESSBAR = "cn.pipi.mobile.pipiplayer.vlc.gui.ShowProgressBar";
    protected static final String ACTION_HIDE_PROGRESSBAR = "cn.pipi.mobile.pipiplayer.vlc.gui.HideProgressBar";
    protected static final String ACTION_SHOW_TEXTINFO = "cn.pipi.mobile.pipiplayer.vlc.gui.ShowTextInfo";
    public static final String ACTION_SHOW_PLAYER = "cn.pipi.mobile.pipiplayer.vlc.gui.ShowPlayer";
    private static final int ACTIVITY_SHOW_INFOLAYOUT = 6;
    private static final int ACTIVITY_RESULT_PREFERENCES = 7;
    private static final int ALL_PAUSE = 8;
    FrameLayout placeHolderLayout;
    private View mInfoLayout;
    private ProgressBar mInfoProgress;
    private TextView mInfoText;
    private boolean mScanNeeded = true;
    private static DownloadActivity instance;
    private List<Integer> delList;
    public static String PAUSE_ACTION = "pause_alldownloadtask";
    public static String SINGLE_PAUSE_ACTION = "pasue_singledownloadtask";
    private PauseRecever receiver;
    private RelativeLayout top_layout;

    public DownloadActivity() {

    }

    public static DownloadActivity getInstance() {
        if (instance == null) {
            instance = new DownloadActivity();
        }
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.download);
        widgetInit();
        getSdcardInfo();
        getStoreList();
        delList = new ArrayList<Integer>();
        // testGetLocalFile();
        //注册接收暂停下载任务的广播
        receiver = new PauseRecever();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PAUSE_ACTION);
        filter.addAction(SINGLE_PAUSE_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shadowslayout != null) {
            shadowslayout.setVisibility(View.GONE);
        }
        if (currentIndex == 0) {
            getDownloadList();
        } else if (currentIndex == 1) {
            if (mScanNeeded)
                MediaLibrary.getInstance(this).loadMediaItems(this);
        }
        DownloadDetailAdapter.getInstance().notifyDataSetChanged();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isUpdate = false;
        if (currentIndex == 1) {
            mScanNeeded = MediaLibrary.getInstance(this).isWorking();
            /* Stop scanning for files */
            MediaLibrary.getInstance(this).stop();
        }
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isUpdate = false;
        if (currentIndex == 1) {
            try {
                unregisterReceiver(messageReceiver);
            } catch (IllegalArgumentException e) {
            }
        }
        unregisterReceiver(receiver);
        //删除DownloadAdapter中的receiver
        downloadAdapter.unregister(this);

    }


    // @Override
    public void widgetInit() {
        // setPenentBack(false); // 不调用父类返回方法
        emptyInfo = (TextView) this.findViewById(R.id.empty_info);
        mTaskList = new ArrayList<DownTask>();
        mDownCenter = DownCenter.getExistingInstance();
        dbHelperDao = DBHelperDao.getDBHelperDaoInstace(this);
        ImageView backImgView = (ImageView) this
                .findViewById(R.id.download_backimgview);
        backImgView.setOnClickListener(this);
        downloadRadioGroup = (RadioGroup) this
                .findViewById(R.id.download_radiogroup);
        downloadRadioGroup.setOnCheckedChangeListener(this);
        Button setBtn = (Button) this.findViewById(R.id.download_setbtn);
        setBtn.setOnClickListener(this);
        videoCacheTextView = (TextView) this
                .findViewById(R.id.download_videocache);
        availableTextView = (TextView) this
                .findViewById(R.id.download_availablecache);
        // .................................
        allPauseTextView = (TextView) this.findViewById(R.id.download_allpause);
        allPauseTextView.setOnClickListener(this);
        // allPauseTextView.setVisibility(View.GONE);
        editTextView = (TextView) this.findViewById(R.id.download_edit);
        editTextView.setOnClickListener(this);
        downGridView = (GridView) this.findViewById(R.id.download_downgridview);
        downloadAdapter = new DownloadAdapter(this);
        downloadAdapter.register();
        downGridView.setAdapter(downloadAdapter);
        downGridView.setOnItemClickListener(this);
        // ...............................................
        placeHolderLayout = (FrameLayout) this
                .findViewById(R.id.fragment_placeholder);
        // ............................................
        storeGridView = (GridView) this
                .findViewById(R.id.download_storegridview);
        storeGridAdapter = new StoreGridAdapter(this);
        storeGridView.setAdapter(storeGridAdapter);
        storeGridView.setOnItemClickListener(this);
        shadowslayout = (LinearLayout) this
                .findViewById(R.id.download_shadowslayout);
    }

    private void updateDownloadAdapter() {
        isUpdate = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    public void getSdcardInfo() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (SdcardUtil.existSDcard()) {
                    // long sdCardTotalSize = SdcardUtil
                    // .computeSDSize1(DownloadActivity.this);
                    long pipiLocalFileSize = SdcardUtil.getFileSize(new File(
                            FileUtils.videoCachePath));
                    long avaiableSize = SdcardUtil
                            .getAvaliableSDSize(DownloadActivity.this);
                    Bundle data = new Bundle();
                    data.putLong("pipivideocachesize", pipiLocalFileSize);
                    data.putLong("avaiableSize", avaiableSize);
                    Message message = handler.obtainMessage();
                    message.setData(data);
                    message.what = COMPUTE_SDCARDINFO;
                    handler.sendMessage(message);
                }

            }
        }).start();
    }

    private void getDownloadList() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                mTaskList = mDownCenter.getDownTaskList();
                Message message = handler.obtainMessage();
                message.what = GET_DOWNLOAD_LIST; // 标示 更新界面ui 信息
                handler.sendMessage(message);

            }
        }).start();
    }

    private void getStoreList() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                List<MovieInfo> movielists = dbHelperDao.getFavouriteList();
                CommonUtil.sendMessage(GET_FAVOURITE_LIST, handler, movielists);
            }
        }).start();
    }

    private void testGetLocalFile() {
        MediaLibrary.getInstance(this).loadMediaItems(this);
    }

    private void scanLocalVideoFile() {
        fragment = new VideoGridFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, fragment).commit();
        // if(mScanNeeded)
        MediaLibrary.getInstance(this).loadMediaItems(this);
    }

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

    public static void sendTextInfo(Context context, String info, int progress,
                                    int max) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_RESULT_PREFERENCES) {
            // if (resultCode == PreferencesActivity.RESULT_RESCAN)
            // MediaLibrary.getInstance(this).loadMediaItems(this, true);
        }
    }

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equalsIgnoreCase(ACTION_SHOW_PROGRESSBAR)) {
                setSupportProgressBarIndeterminateVisibility(true);
                getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else if (action.equalsIgnoreCase(ACTION_HIDE_PROGRESSBAR)) {
                setSupportProgressBarIndeterminateVisibility(false);
                getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else if (action.equalsIgnoreCase(ACTION_SHOW_TEXTINFO)) {
                String info = intent.getStringExtra("info");
                int max = intent.getIntExtra("max", 0);
                int progress = intent.getIntExtra("progress", 100);
                mInfoText.setText(info);
                mInfoProgress.setMax(max);
                mInfoProgress.setProgress(progress);

                if (info == null) {
                    /* Cancel any upcoming visibility change */
                    handler.removeMessages(ACTIVITY_SHOW_INFOLAYOUT);
                    mInfoLayout.setVisibility(View.GONE);
                } else {
                    /*
                     * Slightly delay the appearance of the progress bar to
					 * avoid unnecessary flickering
					 */
                    if (!handler.hasMessages(ACTIVITY_SHOW_INFOLAYOUT)) {
                        Message m = new Message();
                        m.what = ACTIVITY_SHOW_INFOLAYOUT;
                        handler.sendMessageDelayed(m, 300);
                    }
                }
            }
        }
    };

    private void switchCurrentView() {
        switch (currentIndex) {
            case 0:
                downloadNum = 0;
                storeNum = 0;
                editTextView.setText("编辑");
                allPauseTextView.setVisibility(View.VISIBLE);
                updateDelBtnText(downloadNum);
                downGridView.setVisibility(View.VISIBLE);
                storeGridView.setVisibility(View.GONE);
                placeHolderLayout.setVisibility(View.GONE);
                if (downloadAdapter.getList() != null && downloadAdapter.getList().size() > 0) {
                    emptyInfo.setVisibility(View.GONE);
                } else {
                    emptyInfo.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                allPauseTextView.setVisibility(View.GONE);
                editTextView.setText("刷新");
                placeHolderLayout.setVisibility(View.VISIBLE);
                downGridView.setVisibility(View.GONE);
                storeGridView.setVisibility(View.GONE);
                scanLocalVideoFile();
                emptyInfo.setVisibility(View.GONE);
                break;
            case 2:
                storeNum = 0;
                downloadNum = 0;
                editTextView.setText("编辑");
                allPauseTextView.setText("全部暂停");
                allPauseTextView.setVisibility(View.VISIBLE);
                updateDelBtnText(storeNum);
                storeGridView.setVisibility(View.VISIBLE);
                downGridView.setVisibility(View.GONE);
                placeHolderLayout.setVisibility(View.GONE);
                if (storeGridAdapter.getList() != null && storeGridAdapter.getList().size() > 0) {
                    emptyInfo.setVisibility(View.GONE);
                } else {
                    emptyInfo.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    // private void changeEditState() {
    // if (!isEdit) {
    // editTextView.setText("取消");
    // allPauseTextView.setVisibility(View.VISIBLE);
    // isEdit = true;
    // } else {
    // editTextView.setText("编辑");
    // allPauseTextView.setVisibility(View.GONE);
    // isEdit = false;
    // }
    // switch (currentIndex) {
    // case 0:
    // downloadAdapter.notifiyByEdit(isEdit);
    // break;
    //
    // case 1:
    // MediaLibrary.getInstance(this).loadMediaItems(this);
    // break;
    // case 2:
    // storeGridAdapter.notifiyByEdit(isEdit);
    // break;
    // }
    // }

    private void editTextViewState() {
        switch (currentIndex) {
            case 0:// 下载
                if (!isEdit) {
                    editTextView.setText("取消");
                    allPauseTextView.setVisibility(View.VISIBLE);
                    isEdit = true;
                } else {
                    editTextView.setText("编辑");
                    allPauseTextView.setText("全部暂停");
                    isEdit = false;
                    downloadNum = 0;
                    downloadAdapter.resetEditState();
                }
                downloadAdapter.notifiyByEdit(isEdit);

                break;
            case 1:// 本地
                MediaLibrary.getInstance(this).loadMediaItems(this);
                break;
            case 2:
                if (!isEdit) {
                    editTextView.setText("取消");
                    isEdit = true;
                } else {
                    editTextView.setText("编辑");
                    isEdit = false;
                }
                storeGridAdapter.notifiyByEdit(isEdit);
                break;
        }
    }

    private void updateDelBtnText(int num) {
        if (num > 0) {
            allPauseTextView.setText("删除 ( " + num + " )");
        } else {
            if (currentIndex == 0) {
                allPauseTextView.setText("全部暂停");
            } else if (currentIndex == 2) {
                allPauseTextView.setText("");
            }
        }
    }

    private void changeRadioBtnTextColor(RadioGroup radioGroup, int checkedId) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.getId() != checkedId) {
                radioButton
                        .setTextColor(getResources().getColor(R.color.black));
            } else {
                radioButton
                        .setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    public void allPauseOnclickEvent() {
        switch (currentIndex) {
            case 0:
                if (downloadNum != 0) {
                    List<DownTask> list = downloadAdapter.getList();
                    isCurrent(list);
                    downloadAdapter.del();
                    downloadNum = 0;
                    updateDelBtnText(downloadNum);
                    getSdcardInfo();
                    editTextViewState();
                } else {
                    if (mDownCenter != null) {
                        mDownCenter.pauseDownTasks();
                    }
                }
                break;
            case 1:
                break;
            case 2:
                storeGridAdapter.delEditedItem();
                storeNum = 0;
                updateDelBtnText(storeNum);
                break;
        }
        if (storeGridAdapter != null && storeGridAdapter.getList().size() == 0) {
            emptyInfo.setVisibility(View.VISIBLE);
        }
    }

    private void isCurrent(List<DownTask> list) {
        if (list == null || list.size() == 0) {
            setResult(Activity.RESULT_CANCELED);
            System.out.println("列表为空");
            return;
        }
        for (DownTask downTask : list) {
            DownLoadInfo downLoadInfo = downTask.getDownLoadInfo();
            if (downLoadInfo != null && downLoadInfo.isEditState()) {
//                DownLoadInfo downLoadInfo = downTask.getDownLoadInfo();
                Intent intent = getIntent();
                String playingMovieId = intent.getStringExtra(BaseActivity.MOVIE_ID);
                int playingMoviePosition = intent.getIntExtra(BaseActivity.MOVIE_POSITION, -1);
                String deletingMovieId = downLoadInfo.getDownloadID();
                int deletingMoviePosition = downLoadInfo.getDownloadPosition();
                if (!TextUtils.isEmpty(playingMovieId) && playingMovieId.equals(deletingMovieId) && deletingMoviePosition == playingMoviePosition) {
                    intent.putExtra(BaseActivity.START_DOWNLOAD_ACTIVITY + "", true);
                    setResult(Activity.RESULT_OK, intent);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.download_backimgview:
                finish();
                break;
            case R.id.download_setbtn:// set
                if (shadowslayout != null) {
                    shadowslayout.setVisibility(View.VISIBLE);
                }
                MobclickAgent.onEvent(this, "Click_Page", "设置");
                Intent intent = new Intent();
                intent.setClass(this, SetActivity.class);
                startActivity(intent);
                break;
            case R.id.download_edit:
                // changeEditState();
                editTextViewState();
                break;
            case R.id.download_allpause:
                allPauseOnclickEvent();
                break;


        }


    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
//		changeRadioBtnTextColor(downloadRadioGroup, checkedId);
        switch (checkedId) {
            case R.id.download_radio_download:
                currentIndex = 0;
                break;
            case R.id.download_radio_local:
                currentIndex = 1;
                break;
            case R.id.download_radio_store:
                currentIndex = 2;
                break;
        }
        isEdit = true; // 取消编辑状态
        // changeEditState();
        editTextViewState();
        switchCurrentView();

    }

    @Override
    public void run() {
        if (!isUpdate) {
            Log.d(AppConfig.Tag, "界面暂停或不可见 取消更新!");
            return;
        }

        while (isUpdate) {
            // 更新适配器 一秒更新一次
            Log.d(AppConfig.Tag, "下载 界面更新适配器");
            CommonUtil.sendMessage(UPDATE_DOWNLOADADAPTER, handler, null);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public Handler getHandler() {
        return handler;
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message message) {

            switch (message.what) {
                case COMPUTE_SDCARDINFO:
                    Bundle bundle = message.getData();
                    if (bundle == null)
                        return;
                    long videoCacheSize = bundle.getLong("pipivideocachesize");
                    long availableSize = bundle.getLong("avaiableSize");
                    if (videoCacheSize != 0)
                        videoCacheTextView.setText("下载使用: "
                                + SdcardUtil.FormetFileSize(videoCacheSize));
                    availableTextView.setText("剩余: "
                            + SdcardUtil.FormetFileSize(availableSize));
                    break;

                case GET_DOWNLOAD_LIST:
//				System.out.println("获取下载列表");
                    if (mTaskList != null && mTaskList.size() != 0) {
                        ComparatorDownTask comparatorDownTask = new ComparatorDownTask();
                        Collections.sort(mTaskList, comparatorDownTask);
//					System.out.println("***按名称排序");
                        downloadAdapter.setList(mTaskList);

                        downloadAdapter.setSortedMovie(downloadAdapter.sortMovie(mTaskList));
                        downloadAdapter.notifyDataSetChanged();
                        downloadAdapter.initDeleteTaskBean();
                        updateDownloadAdapter();
                        emptyInfo.setVisibility(View.GONE);
                    }
                    break;
                case UPDATE_DOWNLOADADAPTER:
                    if (downloadAdapter != null) {
                        downloadAdapter.notifyDataSetChanged();
                        DownloadDetailAdapter adapter = CustomDialog.getInstance().adapter;
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                    }
                    break;
                case GET_FAVOURITE_LIST:
                    storeGridAdapter.setList((List<MovieInfo>) message.obj);
                    storeGridAdapter.notifyDataSetChanged();
                    break;
                case ACTIVITY_SHOW_INFOLAYOUT:
                    mInfoLayout.setVisibility(View.VISIBLE);
                    break;
                case ALL_PAUSE:
                    allPauseOnclickEvent();
                    break;
            }

        }

        ;
    };


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        if (arg0 == downGridView) {
            downloadAdapter.changeDownloadState(position);
            List<DownTask> list = downloadAdapter.getSortedMovie().get(position);
            CustomDialog.getInstance().setList(list);
            return;
        }

        if (arg0 == storeGridView) {
            if (storeGridAdapter == null || storeGridAdapter.getList() == null
                    || storeGridAdapter.getList().size() == 0) {
                return;
            }
            MovieInfo movieInfo = storeGridAdapter.getList().get(position);
            CommonUtil.toMovieDetialActivity(this, movieInfo.getMovieID(),
                    movieInfo.getMovieName());
        }

    }

    @Override
    public void downloadStateAdd() { // 下载编辑 添加
        // TODO Auto-generated method stub
//        delList.add(position);
        downloadNum++;
        updateDelBtnText(downloadNum);
    }

    @Override
    public void downloadStateRemovie() { // 下载编辑 删除
        // TODO Auto-generated method stub
//        delList.remove(position);
        downloadNum--;
        updateDelBtnText(downloadNum);
    }

    @Override
    public void storeStateAdd() {
        // TODO Auto-generated method stub
        storeNum++;
        updateDelBtnText(storeNum);
    }

    @Override
    public void storeStateRemovie() {// 收藏编辑 删除
        // TODO Auto-generated method stub
        storeNum--;
        updateDelBtnText(storeNum);
    }

    private void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        float scaleWidth = 0.1484375f;
        float scaleHeight = 0.27260637f;

        if (fragment instanceof VideoGridFragment) {
            fragment.setInitViewData((int) (screenWidth * scaleWidth), (int) (screenHeight * scaleHeight));
        }
    }

    public void initViewData() {
        initData();
    }

    //接收暂停广播
    class PauseRecever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PAUSE_ACTION)) {
                allPauseOnclickEvent();
            } else if (intent.getAction().equals(SINGLE_PAUSE_ACTION)) {
                String download_id = intent.getStringExtra("download_id");
                int position = intent.getIntExtra("position", 0);
                mDownCenter.pauseSingleDownTasks(download_id, position);
            }

        }
    }


}
