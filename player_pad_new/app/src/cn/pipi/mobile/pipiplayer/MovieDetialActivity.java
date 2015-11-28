package cn.pipi.mobile.pipiplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobads.AdSize;
import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;
import com.umeng.analytics.MobclickAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import cn.pipi.mobile.pipiplayer.adapter.MovieDetialGridAdapter;
import cn.pipi.mobile.pipiplayer.adapter.MovieDetialReviewAdapter;
import cn.pipi.mobile.pipiplayer.adapter.MovieDetialSourceAdapter;
import cn.pipi.mobile.pipiplayer.adapter.RecommendAdapter;
import cn.pipi.mobile.pipiplayer.async.GetMoviePingfenAsyncTask;
import cn.pipi.mobile.pipiplayer.async.PostMoviePingfenAsyncTask;
import cn.pipi.mobile.pipiplayer.bean.AdInfo;
import cn.pipi.mobile.pipiplayer.bean.Const;
import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.bean.HistoryBean;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.bean.MovieReviewBean;
import cn.pipi.mobile.pipiplayer.bean.SourceBean;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.config.MessageMark;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.fragment.HistoryFragment;
import cn.pipi.mobile.pipiplayer.fragment.PlayerFragment;
import cn.pipi.mobile.pipiplayer.fragment.PlayerFragment.FullScreenCallBacks;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.util.BitmapManager;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.JsonUtil;
import cn.pipi.mobile.pipiplayer.util.KeyBoardUtil;
import cn.pipi.mobile.pipiplayer.util.MovieReviewManager;
import cn.pipi.mobile.pipiplayer.util.PipiPlayerConstant;
import cn.pipi.mobile.pipiplayer.util.SPUtils;
import cn.pipi.mobile.pipiplayer.util.ToastUtil;
import cn.pipi.mobile.pipiplayer.util.XMLPullParseUtil;
import cn.pipi.mobile.pipiplayer.view.MyGridView;
import cn.pipi.mobile.pipiplayer.view.MyListView;
import cn.pipi.mobile.pipiplayer.view.PullToRefreshView;
import cn.pipi.mobile.pipiplayer.view.PullToRefreshView.OnFooterRefreshListener;
import cn.pipi.mobile.pipiplayer.view.PullToRefreshView.OnHeaderRefreshListener;

/**
 * 影片详情界面
 *
 * @author qiny
 */

public class MovieDetialActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener,
        OnItemClickListener, OnHeaderRefreshListener, OnFooterRefreshListener, FullScreenCallBacks,
        OnTouchListener, View.OnFocusChangeListener {

    private String getMovieID = "";

    private final String mPageName = "pipiplayerPad_moviedetial";
    // 获取详情
    private final int GET_MOVIE_INFO = 1;
    // 获取推荐
    private final int GET_RECOMMEND_MOVIE = 2;
    // 获取下载list
    private final int GET_DOWNLOAD_LIST = 3;
    // 获取影评
    private final int GET_MOVIE_REVIEW = 4;
    // 获取验证码图片
    private final int GET_AUTOIMG = 5;
    // 提交用户评论
    private final int COMMIT_USER_REVIEW = 6;
    // 隐藏历史碎片
    private final int HIDE_HISTORY = 7;

    // 是否正在加载数据
    private boolean isLoading = false;

    private RelativeLayout topRelativeLayout;
    //    private RelativeLayout movieDetial;
    private RelativeLayout old_view;
    private RelativeLayout content_view;
    private RelativeLayout mark_view;
    // header init
    private ImageView backImgBtn;
    // moviename
    private TextView titelTextView;

    private ImageView searchTextView;

    private ImageView historyTextView;

    private ImageView downloadTextView;

    private TextView movieNameTextView;

    private TextView movieActorTextView;

    private TextView movieSynthesisTextView;

    private TextView movieDescribeTextView;

    private Button expandBtn;

    private boolean isExpand = false;

    private ImageView playImageView;

    // 当前来源
    private RelativeLayout currentSourceLayout;

    private ImageView currentSourceImgView;

    private TextView currentSourceTextView;

    private ImageView sourceShadowImageView;

    // 收藏按钮
    private Button storeButton;

    private MyGridView sourceGridView;

    private boolean isShowSourceGridview = false;

    private MovieDetialSourceAdapter movieDetialSourceAdapter;

    private RelativeLayout classifyLayout;

    // 选集 和 下载 单选按钮
    private RadioGroup selectRadioGroup;

    private Button descButton;

    private Drawable upShadowDrawable;

    private Drawable downShadowDrawable;

    private LinearLayout rightLayout;

    // 详情和评论
    private RadioGroup infoRadioGroup;

    // 推荐listview
    private MyListView recommendListView;
    // 推荐listview适配器
    private RecommendAdapter recommendAdapter;

    private MyGridView movieDetialGridview;

    private MovieDetialGridAdapter movieDetialGridAdapter;

    // 播放器布局
    private LinearLayout playerFrameLayout;

    private ScrollView infoScrollView;

    // 评论
    private LinearLayout pingfen;

    private boolean canReview = true;

    private LinearLayout reviewLayout;

    private EditText review;

    private ImageView authImageView;

    private EditText nicknameEditText;

    private EditText authEditText;

    private EditText reviewEditText;

    private Button sendReviewBtn;

    private Button refreshAuthImgBtn;

    // 评论listview

    private ListView reviewListView;

    private MovieDetialReviewAdapter movieDetialReviewAdapter;

    // 影片来源及下载地址集合
    Map<String, List<DownLoadInfo>> movieDownUrlmap;

    // 当前所属来源标签
    private String currentSourceKey = "";

    private ScrollView scrollView;

    private MovieInfo movieInfo;

    private boolean isShowHistoryFragment = false;

    private PullToRefreshView reViewPullToRefreshView;

    private TextView movieReviewtipsView;

    private MovieReviewManager movieReviewManager;

    private int currentReviewIndex = 1;

    private boolean isReFresh = false;

    /**
     * 正在播放的集数
     */
    private int currentPlayPosition = 0;

    private boolean fromOther;

    private View cover;
    private Context context;
    private PlayerFragment playerFragment = null;
    private LinearLayout ad;
    private InterstitialAd interAd;
    private RelativeLayout play_container;
    private ViewGroup.LayoutParams params;
    private int sourceId = 0;
    //判断是否是从下载页面进入
    private boolean fromDownload = false;
    private RatingBar ratingbar;
    private TextView person_num;
    private TextView score;
    private HashMap<String, java.util.Date> datemap = new HashMap<String, java.util.Date>();
    private Activity activity;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.moviedetial);
        ButterKnife.inject(this);
        widgetInit();
        new GetAdData(handler).start();
        initAds();
        fromWhichActivity();
        new GetMoviePingfenAsyncTask(handler, Integer.parseInt(getMovieID)).execute("");
        this.activity = this;
        this.mContext = this;
        KeyBoardUtil.addOnSoftKeyBoardVisibleListener(activity, mContext, new KeyBoardUtil.KeyBoardListener() {
            @Override
            public void onSoftInputChange(boolean isShow) {
                if (isShow) {
                    content_view.setVisibility(View.VISIBLE);
                    mark_view.setVisibility(View.GONE);
                } else {
                    content_view.setVisibility(View.GONE);
                    mark_view.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void fromWhichActivity() {
        Intent intent = getIntent();
        String from = intent.getStringExtra("from");
        if (!TextUtils.isEmpty(from) && from.equals("download")) {
            fromDownload = true;
            String index = intent.getStringExtra("currentPosition");
            int num = Integer.parseInt(index);
            String sourceTag = intent.getStringExtra("sourceTag");
            Toast.makeText(this, "集数:" + (num + 1) + ",状态:" + sourceTag, Toast.LENGTH_LONG).show();
            Toast.makeText(this, "收到信息", Toast.LENGTH_LONG).show();
            //修改影片来源(是标清还是超清)
            currentSourceTextView.setText(sourceTag);
            //显示为下载状态
            RadioButton button = (RadioButton) findViewById(R.id.moviedetial_selectbtn2);
            button.setChecked(true);
            changeRadioBtnTextColor(selectRadioGroup, R.id.moviedetial_selectbtn2);
            movieDetialGridAdapter.setDownload(true);
            movieDetialGridAdapter.notifyDataSetChangeByIsDownload(true);
            //选中显示的集数
            movieDetialGridAdapter.setCurrentPosition(num);
            movieDetialGridAdapter.isFromDownload(true);


        }
    }


    private void initAds() {
        play_container = (RelativeLayout) findViewById(R.id.play_container);
        params = play_container.getLayoutParams();
        String adPlaceId = "2073863";// 重要：请填上您的广告位ID
        interAd = new InterstitialAd(this, AdSize.InterstitialForVideoBeforePlay, adPlaceId);
        interAd.setListener(listener);
        interAd.loadAdForVideoApp(200, 200);


    }

    private InterstitialAdListener listener = new InterstitialAdListener() {
        @Override
        public void onAdReady() {

        }

        @Override
        public void onAdPresent() {

        }

        @Override
        public void onAdClick(InterstitialAd interstitialAd) {

        }

        @Override
        public void onAdDismissed() {
            playMovie();
        }

        @Override
        public void onAdFailed(String s) {
            playMovie();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(this);
        if (DBHelperDao.getDBHelperDaoInstace(this).isFirstToinsertFavouriteTable(getMovieID)) {
            storeButton.setBackgroundResource(R.drawable.moviedetial_store);
        } else {
            storeButton.setBackgroundResource(R.drawable.moviedetial_stored);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(this);
    }

    private void setAds(List<AdInfo> ads) {
        if (ads == null || ads.size() == 0)
            return;
        ImageView img = (ImageView) findViewById(R.id.movie_info_ad_img);
        TextView name = (TextView) findViewById(R.id.movie_info_ad_name);
        TextView description = (TextView) findViewById(R.id.movie_info_ad_description);
        TextView down = (TextView) findViewById(R.id.movie_info_ad_down);
        AdInfo info = ads.get(0);
        BitmapManager bmpManager = BitmapManager.getInstance();
        bmpManager.loadBitmap(info.getImageUrl(), img);
        name.setText(info.getAppName());
        description.setText(info.getAppDescription());
        final String n = info.getAppName();
        final String url = info.getDownloadUrl();
        final String homeUrl = info.getHomePageUrl();
        down.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                    showToast("抱歉,您没有安装SD卡,不能下载...");
                    return;
                }
                DownloadManager downloadManager = (DownloadManager) MovieDetialActivity.this
                        .getSystemService(Context.DOWNLOAD_SERVICE);
                Request request = new Request(Uri.parse(url));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                        | DownloadManager.Request.NETWORK_WIFI);
                request.setVisibleInDownloadsUi(false);
                request.setDestinationInExternalFilesDir(MovieDetialActivity.this, "apk", n + ".apk");
                request.setTitle(n);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                downloadManager.enqueue(request);
            }
        });
        ad.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetialActivity.this, AdDetailActivity.class);
                intent.putExtra("name", n);
                intent.putExtra("url", homeUrl);
                MovieDetialActivity.this.startActivity(intent);
            }
        });
        ad.setVisibility(View.VISIBLE);
    }

    @Override
    public void widgetInit() {
        context = VLCApplication.getAppContext();
        movieDownUrlmap = new HashMap<String, List<DownLoadInfo>>();
        setPenentBack(false);
//        cover = this.findViewById(R.id.detial_cover);
//        cover.setOnClickListener(this);
        old_view = (RelativeLayout) findViewById(R.id.old_view);
        content_view = (RelativeLayout) findViewById(R.id.content_view);
        mark_view = (RelativeLayout) findViewById(R.id.mark_view);
        topRelativeLayout = (RelativeLayout) this.findViewById(R.id.moviedetial_toplayout);
        backImgBtn = (ImageView) this.findViewById(R.id.moviedetial_backimgview);
        titelTextView = (TextView) this.findViewById(R.id.moviedetial_nametextview);
        searchTextView = (ImageView) this.findViewById(R.id.actionbar_search);
        historyTextView = (ImageView) this.findViewById(R.id.actionbar_history);
        downloadTextView = (ImageView) this.findViewById(R.id.actionbar_download);
        commonOnclick(backImgBtn);
        commonOnclick(titelTextView);
        commonOnclick(searchTextView);
        commonOnclick(historyTextView);
        commonOnclick(downloadTextView);
        playImageView = (ImageView) this.findViewById(R.id.moviedetial_play);
        commonOnclick(playImageView);
        // 描述
        movieNameTextView = (TextView) this.findViewById(R.id.moviedetial_moviename);
        movieActorTextView = (TextView) this.findViewById(R.id.moviedetial_movieactor);
        movieSynthesisTextView = (TextView) this.findViewById(R.id.moviedetial_synthesis);
        movieDescribeTextView = (TextView) this.findViewById(R.id.moviedetial_moviedescribe);
        expandBtn = (Button) this.findViewById(R.id.moviedetial_expandbtn);
        expandBtn.setOnClickListener(this);
        classifyLayout = (RelativeLayout) this.findViewById(R.id.moviedetial_sourcelayout);
        selectRadioGroup = (RadioGroup) this.findViewById(R.id.moviedetial_selectradiogroup);
        infoRadioGroup = (RadioGroup) this.findViewById(R.id.movietdetial_inforadiogroup);
        selectRadioGroup.setOnCheckedChangeListener(this);
        infoRadioGroup.setOnCheckedChangeListener(this);
        descButton = (Button) this.findViewById(R.id.moviedetial_descbtn);
        descButton.setText("倒序");
        descButton.setBackgroundDrawable(null);
        upShadowDrawable = getResources().getDrawable(R.drawable.moviedetial_arraw_up);
        upShadowDrawable.setBounds(0, 0, upShadowDrawable.getMinimumWidth(), upShadowDrawable.getIntrinsicHeight());
        downShadowDrawable = getResources().getDrawable(R.drawable.moviedetial_arraw_down);
        downShadowDrawable.setBounds(0, 0, downShadowDrawable.getMinimumWidth(),
                downShadowDrawable.getIntrinsicHeight());
        commonOnclick(descButton);
        recommendListView = (MyListView) this.findViewById(R.id.moviedetial_recommendlistview);
        recommendAdapter = new RecommendAdapter(this);
        recommendAdapter.setmHandler(handler);
        recommendListView.setAdapter(recommendAdapter);
        recommendListView.setOnItemClickListener(this);
        movieDetialGridview = (MyGridView) this.findViewById(R.id.moviedetial_gridview);
        movieDetialGridAdapter = MovieDetialGridAdapter.getInstance(this);
//        movieDetialGridAdapter = new MovieDetialGridAdapter(this);
        movieDetialGridAdapter.setPlayimageView(playImageView);
        movieDetialGridview.setAdapter(movieDetialGridAdapter);
        // movieDetialGridview.setOnItemClickListener(this);
        playerFrameLayout = (LinearLayout) this.findViewById(R.id.moviedetial_frame_player);
        ViewTreeObserver vto = playerFrameLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                playerFrameLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                System.out.println("playerFrameLayout.getWidth():" + playerFrameLayout.getWidth());
                System.out.println("playerFrameLayout.getHeight():" + playerFrameLayout.getHeight());
            }
        });
        storeButton = (Button) this.findViewById(R.id.moviedetial_store);
        commonOnclick(storeButton);
        infoScrollView = (ScrollView) this.findViewById(R.id.moviedetial_infoscrolview);
        rightLayout = (LinearLayout) this.findViewById(R.id.moviedetial_right);
        // 评论
        reviewLayout = (LinearLayout) this.findViewById(R.id.moviedetial_reviewlayout);
        authImageView = (ImageView) this.findViewById(R.id.moviedetial_autocodeimgview);
        refreshAuthImgBtn = (Button) this.findViewById(R.id.moviedetial_refreshcode);
        commonOnclick(refreshAuthImgBtn);
        nicknameEditText = (EditText) findViewById(R.id.nickname);
        String nickname;
        nickname = SPUtils.get(this, "nickname", "").toString();
        if (!TextUtils.isEmpty(nickname))
            nicknameEditText.setText(nickname);
        review = (EditText) findViewById(R.id.review);
        commonOnclick(review);
//        review.clearFocus();
        review.setOnFocusChangeListener(this);
        authEditText = (EditText) this.findViewById(R.id.moviedetial_autocodeeditview);
        reviewEditText = (EditText) this.findViewById(R.id.moviedetial_revieweditview);
        sendReviewBtn = (Button) this.findViewById(R.id.moviedetial_sendreviewbtn);
        commonOnclick(sendReviewBtn);
        reViewPullToRefreshView = (PullToRefreshView) this.findViewById(R.id.moviedetial_reviewrefreshview);
        reViewPullToRefreshView.setOnHeaderRefreshListener(this);
        reViewPullToRefreshView.setOnFooterRefreshListener(this);
        movieReviewtipsView = (TextView) this.findViewById(R.id.moviedetial_reviewtips);
        reviewListView = (ListView) this.findViewById(R.id.moviedetial_reviewlistview);
        movieDetialReviewAdapter = new MovieDetialReviewAdapter(this);
        reviewListView.setAdapter(movieDetialReviewAdapter);
        //添加评分功能
        pingfen = (LinearLayout) findViewById(R.id.pingfen);
        commonOnclick(pingfen);
        ratingbar = (RatingBar) findViewById(R.id.ratingbar);
        ratingbar.setOnRatingBarChangeListener(ratingbarlistener);
        commonOnclick(ratingbar);
        person_num = (TextView) findViewById(R.id.person_num);
        score = (TextView) findViewById(R.id.score);


        // 来源相关初始化
        currentSourceLayout = (RelativeLayout) this.findViewById(R.id.moviedetial_currentsourcelayout);
        LinearLayout sourceSelectlayout = (LinearLayout) this.findViewById(R.id.moviedetial_sourceselectlayout);
        sourceSelectlayout.setOnClickListener(this);
        currentSourceImgView = (ImageView) this.findViewById(R.id.moviedetial_currentsourceimgview);
        currentSourceTextView = (TextView) this.findViewById(R.id.moviedetial_currentsourcekey);
        sourceShadowImageView = (ImageView) this.findViewById(R.id.moviedetial_source_shadowimgview);
        sourceGridView = (MyGridView) this.findViewById(R.id.moviedetial_sourcegridview);
        ad = (LinearLayout) this.findViewById(R.id.movie_info_ad);
        movieDetialSourceAdapter = MovieDetialSourceAdapter.getInstance(this);
//        movieDetialSourceAdapter = new MovieDetialSourceAdapter(this);
        sourceGridView.setAdapter(movieDetialSourceAdapter);
        sourceGridView.setOnItemClickListener(this);
        // scrollview
        scrollView = (ScrollView) this.findViewById(R.id.moviedetial_scrollview);
        // 获取请求Url
        fromOther = getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_VIEW);
        if (fromOther) {
            movieInfo = new MovieInfo();
            movieInfo.setMovieID(getIntent().getData().getQueryParameter("id"));
            movieInfo.setMovieName(URLDecoder.decode(getIntent().getData().getQueryParameter("name")));
        } else if (getIntent() != null) {
            movieInfo = new MovieInfo();
            getMovieID = getIntent().getStringExtra("movieid");
            String movieName = getIntent().getStringExtra("moviedetialtitel");
            if (!TextUtils.isEmpty(movieName))
                movieInfo.setMovieName(movieName);
        }
        titelTextView.setText("" + movieInfo.getMovieName());
        movieDetialGridAdapter.setMovieID(getMovieID);
        currentPlayPosition = DBHelperDao.getDBHelperDaoInstace(this).getPlayPositionFromPlayHistory(getMovieID);
        movieDetialGridAdapter.setCurrentPlayPosition(currentPlayPosition);
        if (!CommonUtil.isNetworkConnect(this)) {
            isLoading = true;
            ToastUtil.ToastShort(this, "无网络!");
            return;
        }
        MobclickAgent.onEvent(this, "DetailVideo", getMovieID);
        getMovieDetialInfo(getMovieID);
        getRecommendListData(getMovieID);
        getAuthImg();
    }

    RatingBar.OnRatingBarChangeListener ratingbarlistener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
            if (!fromUser)
                return;
            AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetialActivity.this);
            builder.setCancelable(true)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("提交评分");

            String strMsg;
            java.util.Date dateSet = datemap.get(getMovieID);
            if (dateSet != null) {
                strMsg = "您已对该影片评分过了。";
                builder.setPositiveButton("关闭", null);
            } else {

                if (rating > 4.0)
                    strMsg = "力荐，不容错过的经典";
                else if (rating > 3.0)
                    strMsg = "推荐，很好的佳作";
                else if (rating > 2.0)
                    strMsg = "还行，不妨一看";
                else if (rating > 1.0)
                    strMsg = "较差，不好看";
                else
                    strMsg = "很差，简直在浪费时间";

                DecimalFormat decimalFormat = new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                final String strPoint = decimalFormat.format(rating * 2);//format 返回的是字符串
                final String strPointHalf = String.valueOf(Math.round(rating));//format 返回的是字符串

                strMsg += " [";
                strMsg += strPoint;
                strMsg += "分]";

                builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        datemap.put(getMovieID, new Date(System.currentTimeMillis()));
                        ratingbar.setIsIndicator(true);
                        canReview = false;
                        new PostMoviePingfenAsyncTask(handler).execute(getMovieID, strPointHalf);
                    }
                })
                        .setNegativeButton("放弃", null);
            }
            builder.setMessage(strMsg);
            AlertDialog alert = builder.create();
            alert.show();

        }
    };

    public void goneAd() {
        ad.setVisibility(View.GONE);
    }

    private String requestUrl(String movieID) {
        StringBuffer stringBuffer = new StringBuffer();
        if (TextUtils.isEmpty(movieID) || !TextUtils.isDigitsOnly(movieID)) {
            return "";
        }
        Log.d(AppConfig.Tag, "详情界面movieID:" + movieID);
        int subId = Integer.parseInt(movieID);
        stringBuffer.append(AppConfig.GET_MOVIEDETIALINFO_URL);
        stringBuffer.append("" + (subId / 1000) + "/");
        stringBuffer.append("" + movieID + "_info.js");
        Log.d(AppConfig.Tag, "详情界面请求地址:" + stringBuffer.toString());
        return stringBuffer.toString();
    }

    private void getMovieDetialInfo(final String movieID) {
        if (TextUtils.isEmpty(movieID) || !TextUtils.isDigitsOnly(movieID)) {
            return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                isLoading = true;
                MovieInfo movieInfo = JsonUtil.getMovieDetialInfo(requestUrl(movieID));
                CommonUtil.sendMessage(GET_MOVIE_INFO, handler, movieInfo);
            }
        }).start();
    }

    /**
     * 获取影片推荐数据
     */
    private void getRecommendListData(final String movieID) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                List<MovieInfo> list = XMLPullParseUtil.getRecommendationMovieList(movieID);
                CommonUtil.sendMessage(GET_RECOMMEND_MOVIE, handler, list);
            }
        }).start();
    }

    /**
     * 获取下载地址集合
     *
     * @param movieInfo
     */
    private void getMovieDownloadlist(final String movieID, final MovieInfo movieInfo) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                Map<String, List<DownLoadInfo>> list = JsonUtil.getMovieDownLoadList(movieID, movieInfo);
                CommonUtil.sendMessage(GET_DOWNLOAD_LIST, handler, list);
            }
        }).start();
    }

    /**
     * 获取皮皮影评
     */
    public void getMovieReview(final String movieID, final int currentIndex) {
        if (!CommonUtil.isNetworkConnect(this)) {
            isReFresh = false;
            ToastUtil.ToastShort(this, "无网络");
            return;
        }
        // 如果不包含皮皮资源 不获取影评
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                MovieReviewManager movieReviewManager = JsonUtil.getMovieReview(movieID, currentIndex);
                CommonUtil.sendMessage(GET_MOVIE_REVIEW, handler, movieReviewManager);
            }
        }).start();
    }

    /**
     * 获取验证码图片
     */
    private void getAuthImg() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Bitmap bitmap = XMLPullParseUtil.getAuthImgNetBitmap(AppConfig.VERIFY_URL);
                CommonUtil.sendMessage(GET_AUTOIMG, handler, bitmap);
            }
        }).start();
    }

    /**
     * 提交用户评论
     */
    private void commitUserReview() {
        if (!CommonUtil.isNetworkConnect(this)) {
            ToastUtil.ToastShort(this, "无网络");
            return;
        }
        final StringBuffer stringBuffer = new StringBuffer();
        if (reviewEditText.getText() == null || reviewEditText.getText().toString().trim().equals("")) {
            ToastUtil.ToastShort(this, "评论不能为空");
            return;
        }
        if (authEditText.getText() == null || authEditText.getText().toString().trim().equals("")) {
            ToastUtil.ToastShort(this, "验证码不能为空");
            return;
        }

        if (authEditText.getText().toString().trim().length() != 4
                || !TextUtils.isDigitsOnly(authEditText.getText().toString())) {
            ToastUtil.ToastShort(this, "验证码格式不正确");
            return;
        }
        String content = "";
        String userName = "";
        String authCode = authEditText.getText().toString();
        try {
            content = URLEncoder.encode(reviewEditText.getText().toString().trim(), "utf-8");
            userName = nicknameEditText.getText().toString();
            if (!TextUtils.isEmpty(userName)) {
                SPUtils.put(this, "nickname", userName);
                userName = URLEncoder.encode(userName, "utf-8");
            } else {
                userName = URLEncoder.encode("android手机端游客", "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        stringBuffer.append("http://user.pipi.cn/action/reviewCommit.jsp?");
        stringBuffer.append("comment_txt=" + content);
        stringBuffer.append("&movId=" + getMovieID);
        stringBuffer.append("&userName=" + userName);
        stringBuffer.append("&vCode=" + authCode);


        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                int code = XMLPullParseUtil.WriteMovieInfoSms(handler, stringBuffer.toString());
                CommonUtil.sendMessage(COMMIT_USER_REVIEW, handler, code);
            }
        }).start();

    }

    private void showMovieInfo(MovieInfo movieInfo) {
        if (movieInfo == null)
            return;
        this.movieInfo = movieInfo;
        movieDetialGridAdapter.setMovieName(movieInfo.getMovieName());
        movieDetialGridAdapter.setMovieImgUrl(movieInfo.getMovieImgPath());
        movieNameTextView.setText(movieInfo.getMovieName());
        if (!TextUtils.isEmpty(movieInfo.getActor()))
            movieActorTextView.setText("主演:" + movieInfo.getActor());
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(movieInfo.getYear());
        stringBuffer.append("/");
        stringBuffer.append(movieInfo.getArea());
        stringBuffer.append("/");
        stringBuffer.append(movieInfo.getGrade());
        movieSynthesisTextView.setText(stringBuffer.toString());
        movieDescribeTextView.setText(movieInfo.getDesc());
        if (!TextUtils.isEmpty(movieInfo.getDesc()) && movieInfo.getDesc().length() > 140) {
            expandBtn.setVisibility(View.VISIBLE);
        }
        // final String movieID = movieInfo.getMovieID();
        // 获取影片下载剧集
        getMovieDownloadlist(getMovieID, this.movieInfo);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    /**
     * 注册Onclick事件
     *
     * @param view
     */
    private void commonOnclick(View view) {
        // if(view!=null)
        view.setOnClickListener(this);
    }

    private void changeRadioBtnTextColor(RadioGroup radioGroup, int checkedId) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.getId() != checkedId) {
                radioButton.setTextColor(getResources().getColor(R.color.black));
            } else {
                radioButton.setTextColor(getResources().getColor(R.color.red));
            }
        }
    }

    /**
     * 显示或者隐藏评论
     */
    private void hideReView(boolean isShow) {
        if (isShow) {
            infoScrollView.setVisibility(View.VISIBLE);
            reviewLayout.setVisibility(View.GONE);
        } else {
            infoScrollView.setVisibility(View.GONE);
            reviewLayout.setVisibility(View.VISIBLE);
        }
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
            fragmentTransaction.replace(R.id.history_container, historyFragment);
            isShowHistoryFragment = true;
//            cover.setVisibility(View.VISIBLE);
        } else {
            fragmentTransaction.remove(historyFragment);
            isShowHistoryFragment = false;
//            cover.setVisibility(View.GONE);
        }
        fragmentTransaction.commit();
    }

    private void playMovie() {
        if (movieInfo == null || movieDownUrlmap == null || movieDownUrlmap.keySet() == null
                || movieDownUrlmap.keySet().size() == 0 || TextUtils.isEmpty(currentSourceKey)) {
            // System.out.println("没获取到数据");
            return;
        }
        if (movieDownUrlmap.get(currentSourceKey) == null || movieDownUrlmap.get(currentSourceKey).size() == 0) {
            // System.out.println("没获取到数据");
            return;
        }
        DownLoadInfo downLoadInfo = movieDownUrlmap.get(currentSourceKey).get(currentPlayPosition);
        downLoadInfo.setDownloadPosition(currentPlayPosition);
        HistoryBean historyBean = new HistoryBean();
        historyBean.setPlayPosition(currentPlayPosition);
        historyBean.setMovieId(movieInfo.getMovieID());
        historyBean.setMovieName(movieInfo.getMovieName());
        historyBean.setSourceTag(currentSourceKey);
        historyBean.setWatchedDate(CommonUtil.getCurrentDate());
        historyBean.setMovieUrl(downLoadInfo.getDownAddress());
        if (DBHelperDao.getDBHelperDaoInstace(this).isFirstToPlayHistoryTable(movieInfo.getMovieID())) {
            DBHelperDao.getDBHelperDaoInstace(this).insertToPlayHistoryTable(historyBean);
        } else {
            DBHelperDao.getDBHelperDaoInstace(this).updatePlayHistoryName(movieInfo.getMovieID(), historyBean);
        }
        downLoadInfo.setDownloadID(movieInfo.getMovieID());
        downLoadInfo.setDownloadName(movieInfo.getMovieName());
        downLoadInfo.setDownloadSourceTag(currentSourceKey);
        movieInfo.setCurrentSourceKey(currentSourceKey);
        if (CommonUtil.isPolySource(downLoadInfo.getDownAddress())) {
            MobclickAgent.onEvent(this, "Click_Movie_Play", movieInfo.getMovieID());
            Intent intent = new Intent();
            intent.setClass(this, HtmlPlayer.class);
            intent.putExtra("playurl", downLoadInfo.getDownAddress());
            this.startActivity(intent);
            return;
        }
        playImageView.setVisibility(View.GONE);
        MobclickAgent.onEvent(this, "Click_Movie_Play", movieInfo.getMovieID());
        FragmentManager fragmentManager = ((FragmentActivity) this).getSupportFragmentManager();
        FragmentTransaction ftTransaction = fragmentManager.beginTransaction();
        playerFragment = new PlayerFragment();
        Bundle bundle = new Bundle();
        if (movieInfo != null)
            bundle.putSerializable("movieinfo", movieInfo);
        bundle.putSerializable("downloadinfo", downLoadInfo);
        bundle.putBoolean("isFullScreen", false);
        bundle.putInt("width", playerFrameLayout.getWidth());
        bundle.putInt("height", playerFrameLayout.getHeight());
        playerFragment.setArguments(bundle);
        ftTransaction.replace(R.id.moviedetial_frame_player, playerFragment);
        ftTransaction.commit();
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            isLoading = false;
            switch (msg.what) {
                case GET_MOVIE_INFO:// 展示详情
                    showMovieInfo((MovieInfo) msg.obj);
                    break;

                case GET_RECOMMEND_MOVIE:
                    List<MovieInfo> list = (List<MovieInfo>) msg.obj;
                    recommendAdapter.setList(list);
                    recommendAdapter.notifyDataSetChanged();
                    break;
                case GET_DOWNLOAD_LIST:// 影片剧集
                    movieDownUrlmap = (Map<String, List<DownLoadInfo>>) msg.obj;
                    if (movieDownUrlmap == null || movieDownUrlmap.keySet() == null || movieDownUrlmap.keySet().size() == 0) {
                        return;
                    }
                    movieInfo.setMovieDownUrlmap(movieDownUrlmap);
                    // 点击影片推荐 再次刷新界面
                    currentSourceLayout.setVisibility(View.VISIBLE);
                    if (CommonUtil.isContianPipiSource(movieInfo.getCurrentMovieDetialSources())) {
                        currentReviewIndex = 1;
                        // 获取影评
                        getMovieReview(getMovieID, currentReviewIndex); // 包含皮皮资源获取评论
                    } else {
                        movieDetialReviewAdapter.setList(new ArrayList<MovieReviewBean>());
                        movieDetialReviewAdapter.notifyDataSetChanged();
                    }
                    if (movieInfo.getCurrentMovieDetialSources() != null
                            && movieInfo.getCurrentMovieDetialSources().size() != 0) {
                        currentSourceKey = movieInfo.getCurrentMovieDetialSources().get(0).getKey();
                        movieInfo.setCurrentSourceKey(currentSourceKey);
                        currentSourceImgView.setBackgroundResource(movieInfo.getCurrentMovieDetialSources().get(0)
                                .getValues());
                        if (!fromDownload)
                            currentSourceTextView.setText(currentSourceKey);
                    }
                    // 来源
                    movieDetialSourceAdapter.setList(movieInfo.getCurrentMovieDetialSources());
                    movieDetialSourceAdapter.notifyDataSetChanged();
                    // 剧集
                    movieDetialGridAdapter.setCurrentSourceKey(currentSourceKey);
                    List<DownLoadInfo> downLoadInfo = movieDownUrlmap.get(currentSourceKey);
                    if (downLoadInfo != null) {
                        int len = downLoadInfo.size();
                        for (int i = 0; i < len; i++) {
                            movieDownUrlmap.get(currentSourceKey).get(i).setSourceIcon(movieInfo.getCurrentMovieDetialSources().get(0)
                                    .getValues());
                        }
                        movieDetialGridAdapter.setList(downLoadInfo);
                        movieDetialGridAdapter.setMovieInfo(movieInfo);
                        movieDetialGridAdapter.notifyDataSetChanged();
                    }
                    break;
                case GET_MOVIE_REVIEW:
                    isReFresh = false;
                    movieReviewManager = (MovieReviewManager) msg.obj;
                    if (movieReviewManager.getList() != null && movieReviewManager.getList().size() != 0) {
                        movieReviewtipsView.setVisibility(View.GONE);
                        movieDetialReviewAdapter.setList(movieReviewManager.getList());
                        movieDetialReviewAdapter.notifyDataSetChanged();
                    } else {
                        movieReviewtipsView.setVisibility(View.VISIBLE);
                    }
                    break;
                case GET_AUTOIMG: // 获取验证码
                    if (msg.obj != null) {
                        authImageView.setImageBitmap((Bitmap) msg.obj);

                    } else {
                        authImageView.setImageResource(R.color.gray);
                    }
                    break;
                case COMMIT_USER_REVIEW: // 提交用户评论
                    Integer coder = (Integer) msg.obj;
                    if (coder != 1) {
                        ToastUtil.ToastShort(MovieDetialActivity.this, "评论失败!");
                    } else {
                        ToastUtil.ToastShort(MovieDetialActivity.this, "评论成功!");
                    }
                    break;
                case HIDE_HISTORY:
                    hiderHistoryFragment();
                    break;
                case MessageMark.ADD_AD:
                    setAds((List<AdInfo>) msg.obj);
                    break;
                case PipiPlayerConstant.EXEC_NORMOL:
                    if (msg.arg1 == 1) {
                        //ping fen
                        ArrayList<Float> arPingfen = (ArrayList<Float>) msg.obj;
                        if (arPingfen.size() >= 2) {
                            DecimalFormat decimalFormat = new DecimalFormat(".0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                            String strFenShut = decimalFormat.format(arPingfen.get(1) * 2);//format 返回的是字符串
                            float num = arPingfen.get(0);
                            int persons = (int) num;
                            if (persons != 0) {
                                person_num.setText(persons + "人");
                                person_num.invalidate();
                                score.setText(strFenShut);
                                score.invalidate();
                                ratingbar.setRating(arPingfen.get(1));
                                ratingbar.invalidate();
                            }
                        }
                    } else if (msg.arg1 == 2) {
                        //refresh pingfen
                        new GetMoviePingfenAsyncTask(handler, Integer.parseInt(getMovieID)).execute("");
                    }
                    break;

            }
        }

        ;
    };
    private static MovieDetialActivity instance;

    public static MovieDetialActivity getInstance() {
        if (instance == null) {
            instance = new MovieDetialActivity();
        }
        return instance;
    }

    public Handler getMovieHandler() {
        if (handler != null) {
            return handler;
        }
        return null;

    }

    public void hiderHistoryFragment() {
        if (isShowHistoryFragment) {
            showOrHideHistoryFragment();
        }
    }

    @Override
    public void onClick(View view) {
        if (isShowHistoryFragment) {
            showOrHideHistoryFragment();
        }
        switch (view.getId()) {
            case R.id.moviedetial_backimgview:
                finish();
                break;
            case R.id.moviedetial_play:
                interAd.showAdInParentForVideoApp(this, play_container);
//			playMovie();
                break;
            case R.id.moviedetial_store:
                if (movieInfo == null)
                    return;
                if (DBHelperDao.getDBHelperDaoInstace(this).isFirstToinsertFavouriteTable(getMovieID)) {
                    ToastUtil.ToastShort(this, "已收藏");
                    DBHelperDao.getDBHelperDaoInstace(this).insertDataToFavouriteTable(movieInfo);
                    storeButton.setBackgroundResource(R.drawable.moviedetial_stored);
                } else {
                    ToastUtil.ToastShort(this, "已取消收藏");
                    DBHelperDao.getDBHelperDaoInstace(this).deleteFavouriteFromMovieid(getMovieID);
                    storeButton.setBackgroundResource(R.drawable.moviedetial_store);
                }
                break;
            case R.id.moviedetial_descbtn: // 升序或降序
                if (movieInfo == null)
                    return;
                if (!movieDetialGridAdapter.isDesc()) {
                    descButton.setText("倒序");
                    descButton.setCompoundDrawables(downShadowDrawable, null, null, null);
                } else {
                    descButton.setText("正序");
                    descButton.setCompoundDrawables(upShadowDrawable, null, null, null);
                }
                movieDetialGridAdapter.notifyDataSetChangeByDesc();
                break;
            case R.id.moviedetial_sourceselectlayout: // 显示或隐藏来源
                if (!isShowSourceGridview) {
                    sourceShadowImageView.setBackgroundResource(R.drawable.moviedetial_sourceshadow_up);
                    sourceGridView.setVisibility(View.VISIBLE);
                    isShowSourceGridview = true;

                } else {
                    sourceShadowImageView.setBackgroundResource(R.drawable.moviedetial_sourceshadow_down);
                    sourceGridView.setVisibility(View.GONE);
                    isShowSourceGridview = false;
                }
                break;
            case R.id.actionbar_search:// 搜索
                MobclickAgent.onEvent(this, "mainsearch");
                redirectTo(false, SearchActivity.class);
                break;
            case R.id.actionbar_history:// 历史
                MobclickAgent.onEvent(this, "checkdownload");
                MobclickAgent.onEvent(this, "playhistory");
                showOrHideHistoryFragment();
                break;
            case R.id.actionbar_download:// 下载
                // redirectTo(false, DownloadActivity.class);
                redirectTo(DownloadActivity.class, movieInfo.getMovieID(), currentPlayPosition);
                break;
            case R.id.moviedetial_refreshcode:
            case R.id.oldview_moviedetial_refreshcode:
                if (isLoading)
                    return;
                getAuthImg();
                break;
            case R.id.moviedetial_sendreviewbtn:// 评论
            case R.id.oldview_moviedetial_sendreviewbtn:
                if (isLoading)
                    return;
                commitUserReview();
                break;
            case R.id.pingfen:
            case R.id.row1:
                if (!canReview) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetialActivity.this);
                    builder.setCancelable(true)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("提交评分");
                    builder.setMessage("您已对该影片评分过了。");
                    builder.setPositiveButton("关闭", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;
            case R.id.moviedetial_expandbtn:
                if (!isExpand) {
                    expandBtn.setText("收起");
                    movieDescribeTextView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            movieDescribeTextView.setMaxLines(50);
                        }
                    }, 50);
                    isExpand = true;
                } else {
                    expandBtn.setText("展开");
                    movieDescribeTextView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            movieDescribeTextView.setMaxLines(6);
                        }
                    }, 50);
                    isExpand = false;
                }
                break;
           /* case R.id.review:
                KeyBoardUtil.openKeybord(review, this);
                break;*/
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (isShowHistoryFragment) {
            showOrHideHistoryFragment();
        }
        if (group == selectRadioGroup) {
            changeRadioBtnTextColor(selectRadioGroup, checkedId);
            switch (checkedId) {
                case R.id.moviedetial_selectbtn1: // 选集
                    movieDetialGridAdapter.notifyDataSetChangeByIsDownload(false);
                    break;

                case R.id.moviedetial_selectbtn2: // 下载
                    movieDetialGridAdapter.notifyDataSetChangeByIsDownload(true);
                    break;
            }
        }
        if (group == infoRadioGroup) {
            changeRadioBtnTextColor(infoRadioGroup, checkedId);
            switch (checkedId) {
                case R.id.moviedetial_inforaidobtn: // 详情
                    hideReView(true);
                    break;

                case R.id.moviedetial_reviewradiobtn: // 评论
                    // if (movieInfo != null
                    // && !CommonUtil.isContianPipiSource(movieInfo
                    // .getCurrentMovieDetialSources())) {
                    // movieReviewtipsView.setVisibility(View.VISIBLE);
                    // }
                    movieReviewtipsView.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            if (movieInfo != null
                                    && !CommonUtil.isContianPipiSource(movieInfo.getCurrentMovieDetialSources())) {
                                movieReviewtipsView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    hideReView(false);
                    break;
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (isShowHistoryFragment) {
//            showOrHideHistoryFragment();
        }
        if (arg0 == sourceGridView) {
            //记录视频画面状态(超清还是标清)
            sourceId = arg2;
            if (movieDetialSourceAdapter == null || movieDetialSourceAdapter.getList() == null
                    || movieDetialSourceAdapter.getList().size() == 0) {
                return;
            }
            SourceBean sourceBean = movieDetialSourceAdapter.getList().get(arg2);
            if (!currentSourceKey.equals(sourceBean.getKey())) {
                currentSourceImgView.setBackgroundResource(sourceBean.getValues());
                currentSourceKey = sourceBean.getKey();
                currentSourceTextView.setText(currentSourceKey);
                movieDetialGridAdapter.setCurrentSourceKey(currentSourceKey);
                List<DownLoadInfo> downLoadInfo = movieDownUrlmap.get(currentSourceKey);
                int len = downLoadInfo.size();
                for (int i = 0; i < len; i++) {
                    movieDownUrlmap.get(currentSourceKey).get(i).setSourceIcon(sourceBean.getValues());
                }
                movieDetialGridAdapter.setList(downLoadInfo);
                movieDetialGridAdapter.notifyDataSetChanged();
                isShowSourceGridview = false;
                sourceShadowImageView.setBackgroundResource(R.drawable.moviedetial_sourceshadow_down);
                sourceGridView.setVisibility(View.GONE);
            }

//			System.out.println("当前来源标签：" + movieDetialSourceAdapter.getList().get(arg2).getKey());
        }
        if (arg0 == movieDetialGridview) {

        }

        if (arg0 == recommendListView) { // 推荐
            getMovieID = recommendAdapter.getList().get(arg2).getMovieID();
            movieDetialGridAdapter.setMovieID(getMovieID);
            // titelTextView.setText(recommendAdapter.getList().get(arg2)
            // .getMovieName());
            // removieFragment();
            // getMovieDetialInfo(getMovieID);
            // getRecommendListData(getMovieID);
            this.finish();
            CommonUtil.toMovieDetialActivity(this, getMovieID, recommendAdapter.getList().get(arg2).getMovieName());
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {

        if (isReFresh) {
            ToastUtil.ToastShort(this, "正在刷新中");
        } else {
            isReFresh = true;
            if (movieReviewManager != null) {
                currentReviewIndex = 1;
                getMovieReview(getMovieID, currentReviewIndex);

            }
        }

        reViewPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                reViewPullToRefreshView.onHeaderRefreshComplete();
            }
        }, 500);

    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {

        if (isReFresh) {
            ToastUtil.ToastShort(this, "正在刷新中");
        } else {
            isReFresh = true;
            if (movieReviewManager != null) {
                if (movieReviewManager.hasMoreDate(currentReviewIndex)) {
                    getMovieReview(getMovieID, currentReviewIndex++);
                } else {
                    ToastUtil.ToastShort(this, "已经是最后一页....");
                }

            }
        }

        reViewPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                reViewPullToRefreshView.onFooterRefreshComplete();
            }
        }, 500);

    }

    @Override
    public void fullScreenPlay(boolean isFullScreen) {
        if (isFullScreen) {// 全屏
            topRelativeLayout.setVisibility(View.GONE);
            rightLayout.setVisibility(View.GONE);
            classifyLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);
        } else {// 取消全屏
            topRelativeLayout.setVisibility(View.VISIBLE);
            rightLayout.setVisibility(View.VISIBLE);
            classifyLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (playerFragment != null) {
            if (playerFragment.ismIsLocked())
                return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (playerFragment == null || !playerFragment.isFullScreen()) {
                return super.onKeyDown(keyCode, event);
            }
            if (playerFragment.isFullScreen()) {
                playerFragment.isFullScreen = false;
                playerFragment.setFullScreenPlay(playerFragment.isFullScreen);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (fromOther) {
            startActivity(new Intent(this, SplashActivity.class));
        }
        DownCenter.getExistingInstance().startAllTask();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        hiderHistoryFragment();
        System.out.println("MovieDetialActivity-->onTouch()");
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            KeyBoardUtil.openKeybord(review, this);
        }
    }


    private class GetAdData extends Thread {
        private Handler mHandler;

        public GetAdData(Handler handler) {
            mHandler = handler;
        }

        public void run() {
            Message msg = mHandler.obtainMessage();
            msg.obj = JsonUtil.getAds(Const.DETAIL_AD_URL, Const.DETAIL_AD_KEY);
            if (msg.obj == null)
                return;
            msg.what = MessageMark.ADD_AD;
            mHandler.sendMessage(msg);
        }


    }

    private boolean isEditorShown = false;

    public void showEditor() {
        if (!isEditorShown) {
            WindowManager manager = getWindowManager();
            View view = content_view;   //自定义的编辑控件，可以是一个EditText
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.gravity = Gravity.BOTTOM;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.format = PixelFormat.RGBA_8888;
            params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

//            EditorManger.prepareEditor(manager,this,view);

            manager.addView(view, params);

        }
       /* if(editText != null){
            editText.setText(getCellValue(selectRowIndex,selectColumnIndex));
        }*/
        isEditorShown = true;

    }

}
