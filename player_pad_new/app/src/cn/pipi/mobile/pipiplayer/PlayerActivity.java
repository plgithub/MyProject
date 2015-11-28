package cn.pipi.mobile.pipiplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RelativeLayout;

import com.baidu.mobads.AdSize;
import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;

import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.bean.HistoryBean;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.fragment.PlayerFragment;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;

public class PlayerActivity extends BaseActivity {

    private InterstitialAd interAd;
    private RelativeLayout play_container;
    private Intent newintent;
    private DownLoadInfo downLoadInfo;

    @Override
    public void widgetInit() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.videoplayer);
        recvIntent();
        initAds();


    }

    private void recvIntent() {
        downLoadInfo = (DownLoadInfo) getIntent().getSerializableExtra("downloadinfo");
    }


    private void initAds() {
        play_container = (RelativeLayout) findViewById(R.id.play_video);
        String adPlaceId = "2073863";// 重要：请填上您的广告位ID
        interAd = new InterstitialAd(PlayerActivity.this, AdSize.InterstitialForVideoBeforePlay, adPlaceId);
        interAd.setListener(listener);
        interAd.loadAdForVideoApp(200, 200);


    }

    private InterstitialAdListener listener = new InterstitialAdListener() {
        @Override
        public void onAdReady() {
            interAd.showAdInParentForVideoApp(PlayerActivity.this, play_container);
        }

        @Override
        public void onAdPresent() {
        }

        @Override
        public void onAdClick(InterstitialAd interstitialAd) {

        }

        @Override
        public void onAdDismissed() {
            playVideo();
        }


        @Override
        public void onAdFailed(String s) {
            playVideo();
        }
    };

    public void playVideo() {
        if (downLoadInfo == null) finish();
        FragmentManager fragmentManager = getSupportFragmentManager();
        PlayerFragment playerFragment = new PlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("downloadinfo", downLoadInfo);
        bundle.putBoolean("isFullScreen", true);
        playerFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_player, playerFragment);
        fragmentTransaction.commit();

        Context context = VLCApplication.getAppContext();
        String movieId = downLoadInfo.getDownloadID();
        int currentPlayPosition = DBHelperDao.getDBHelperDaoInstace(context).getPlayPositionFromPlayHistory(movieId);
        String currentSourceKey = downLoadInfo.getDownloadSourceTag();
        HistoryBean historyBean = new HistoryBean();
        historyBean.setPlayPosition(currentPlayPosition);
        historyBean.setMovieId(downLoadInfo.getDownloadID());
        historyBean.setMovieName(downLoadInfo.getDownloadName());
        historyBean.setSourceTag(currentSourceKey);
        historyBean.setWatchedDate(CommonUtil.getCurrentDate());
        historyBean.setMovieUrl(downLoadInfo.getDownAddress());
        if (DBHelperDao.getDBHelperDaoInstace(context).isFirstToPlayHistoryTable(downLoadInfo.getDownloadID())) {
            DBHelperDao.getDBHelperDaoInstace(context).insertToPlayHistoryTable(historyBean);
        } else {
            DBHelperDao.getDBHelperDaoInstace(context).updatePlayHistoryName(downLoadInfo.getDownloadID(), historyBean);
        }
    }

    public static void startPlayerActivity(Context context, DownLoadInfo downLoadInfo) {
        Intent intent = new Intent();
        intent.setClass(context, PlayerActivity.class);
        intent.putExtra("downloadinfo", downLoadInfo);
        context.startActivity(intent);
    }

}
