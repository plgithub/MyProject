package cn.pipi.mobile.pipiplayer.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Presentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaRouter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.baidu.mobads.AdSize;
import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import cn.pipi.mobile.pipiplayer.DownCenter;
import cn.pipi.mobile.pipiplayer.HtmlPlayer;
import cn.pipi.mobile.pipiplayer.MovieDetialActivity;
import cn.pipi.mobile.pipiplayer.PlayerActivity;
import cn.pipi.mobile.pipiplayer.adapter.PlayerGridAdapter;
import cn.pipi.mobile.pipiplayer.adapter.PlayerSourceAdapter;
import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.bean.HistoryBean;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.bean.SourceBean;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.db.PIPISharedPreferences;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.local.libvlc.EventHandler;
import cn.pipi.mobile.pipiplayer.local.libvlc.IVideoPlayer;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVLC;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVlcException;
import cn.pipi.mobile.pipiplayer.local.libvlc.LibVlcUtil;
import cn.pipi.mobile.pipiplayer.local.libvlc.Media;
import cn.pipi.mobile.pipiplayer.local.vlc.AudioServiceController;
import cn.pipi.mobile.pipiplayer.local.vlc.MediaDatabase;
import cn.pipi.mobile.pipiplayer.local.vlc.Util;
import cn.pipi.mobile.pipiplayer.local.vlc.VLCApplication;
import cn.pipi.mobile.pipiplayer.local.vlc.WeakHandler;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.CommonDialogs;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.CommonDialogs.MenuType;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.MainLocalActivity;
import cn.pipi.mobile.pipiplayer.local.vlc.gui.PreferencesActivity;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.FileUtils;
import cn.pipi.mobile.pipiplayer.util.MD5Util;
import cn.pipi.mobile.pipiplayer.util.SdcardUtil;
import cn.pipi.mobile.pipiplayer.util.ToastUtil;
import cn.pipi.mobile.pipiplayer.util.UITimer;
import cn.pipi.mobile.pipiplayer.util.UITimer.OnUITimer;

/**
 * 详情页面 播放器碎片
 * 
 * @author qiny
 * 
 */
@SuppressLint("NewApi")
public class PlayerFragment extends BaseFragment implements IVideoPlayer, OnClickListener, OnItemClickListener,
		OnTouchListener {
	public final static String TAG = "VLC/VideoPlayerActivity";
	private View view;
	private SurfaceView mSurface;
	private SurfaceView mSubtitlesSurface;
	private SurfaceHolder mSurfaceHolder;
	private SurfaceHolder mSubtitlesSurfaceHolder;
	private FrameLayout mSurfaceFrame;
	private MediaRouter mMediaRouter;
	private MediaRouter.SimpleCallback mMediaRouterCallback;
	private SecondaryDisplay mPresentation;
	private int mPresentationDisplayId = -1;
	private LibVLC mLibVLC;
	private String mLocation;

	private static final int SURFACE_BEST_FIT = 0;
	private static final int SURFACE_FIT_HORIZONTAL = 1;
	private static final int SURFACE_FIT_VERTICAL = 2;
	private static final int SURFACE_FILL = 3;
	private static final int SURFACE_16_9 = 4;
	private static final int SURFACE_4_3 = 5;
	private static final int SURFACE_ORIGINAL = 6;
	private int mCurrentSize = SURFACE_BEST_FIT;

	//克隆设备屏幕，而不启动远程控制
    private boolean mEnableCloneMode;
    
	/** Overlay */
	private View mOverlayProgress;

	private RelativeLayout headerLayout;

	private Button backButton;

	private TextView movieNameView;

	private static final int OVERLAY_TIMEOUT = 4000;
	private static final int OVERLAY_INFINITE = 3600000;
	private static final int FADE_OUT = 1;
	private static final int SHOW_PROGRESS = 2;
	private static final int SURFACE_SIZE = 3;
	private static final int AUDIO_SERVICE_CONNECTION_SUCCESS = 5;
	private static final int AUDIO_SERVICE_CONNECTION_FAILED = 6;
	private static final int FADE_OUT_INFO = 4;
	private boolean mDragging;
	private boolean mShowing;
	private int mUiVisibility = -1;
	private SeekBar mSeekbar;
	private TextView mTime;
	private TextView mLength;
	private TextView mInfo;
	private ImageButton mPlayPause;

	private boolean mIsLocked = false;

	private ImageButton mLock;
	// private ImageButton mBackward;
	// private ImageButton mForward;
	private boolean mDisplayRemainingTime = false;
	private ImageButton mSize;
	// private ImageButton mMenu;
	private int mLastAudioTrack = -1;
	private int mLastSpuTrack = -2;

	/**
	 * For uninterrupted switching between audio and video mode
	 */
	private boolean mSwitchingView;
	private boolean mEndReached;
	private boolean mCanSeek;

	// Playlist
	private int savedIndexPosition = -1;

	// size of the video
	private int mVideoHeight;
	private int mVideoWidth;
	private int mVideoVisibleHeight;
	private int mVideoVisibleWidth;
	private int mSarNum;
	private int mSarDen;

	// Volume
	private AudioManager mAudioManager;
	private int mAudioMax;
	private OnAudioFocusChangeListener mAudioFocusListener;

	// Touch Events
	private static final int TOUCH_NONE = 0;
	private static final int TOUCH_VOLUME = 1;
	private static final int TOUCH_BRIGHTNESS = 2;
	private static final int TOUCH_SEEK = 3;
	private int mTouchAction;
	private int mSurfaceYDisplayRange;
	private float mTouchY, mTouchX, mVol;

	private ArrayList<String> mSubtitleSelectedFiles = new ArrayList<String>();

	// Whether fallback from HW acceleration to SW decoding was done.
	private boolean mDisabledHardwareAcceleration = false;
	private int mPreviousHardwareAccelerationMode;
	private DownCenter mDownCenterinstance = null;

	private boolean goNextMovie = false;

	// 是否自动播放下集
	private boolean isAutoPlayNext = false;
	// 从历史记录播放
	private boolean isPlayFromHistory = false;

	private LinearLayout bufferlayout;
	private TextView mSpeedView;
	private DownLoadInfo downInfo = new DownLoadInfo();
	private MovieInfo movieInfo = null;
	private SharedPreferences preferences;

	private Activity activity;

	private DBHelperDao dbHelperDao;

	private RelativeLayout playRelativeLayout;

	private FullScreenCallBacks fullScreenCallBacks;

	private RelativeLayout changeSourceLayout;

	private RelativeLayout sourceLayout;

	public boolean isFullScreen = false;

	private ImageView sourceIconImageView;

	private TextView sourceKeyTextView;

	private ListView sourceListView;// 影片来源

	private PlayerSourceAdapter playerSourceAdapter;

	private String sourceKey = "";// 当前来源标签

	private List<DownLoadInfo> currentList; // 当前剧集

	private Button selectBtn;// 选集按钮

	private RelativeLayout selectLayout;

	private GridView gridView;

	private PlayerGridAdapter playerGridAdapter;
	private InterstitialAd interAd;
	private RelativeLayout ad_container;
	@Override
	public void widgetInit() {
		if (view == null)
			return;
		view.setOnTouchListener(this);
		// view.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// if (event.getAction() == MotionEvent.ACTION_UP) {
		// if (!mShowing) {
		// showOverlay();
		// } else {
		// hideOverlay(true);
		// }
		// }
		// return true;
		// }
		// });
		// playRelativeLayout = (RelativeLayout) view
		// .findViewById(R.id.player_rellayout);
		// ViewTreeObserver vto = playRelativeLayout.getViewTreeObserver();
		// vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		// @Override
		// public void onGlobalLayout() {
		// playRelativeLayout.getViewTreeObserver()
		// .removeGlobalOnLayoutListener(this);
		// // System.out.println("playRelativeLayout.getWidth():"
		// // + playRelativeLayout.getWidth());
		// // System.out.println("playRelativeLayout.getHeight():"
		// // + playRelativeLayout.getHeight());
		// }
		// });
		headerLayout = (RelativeLayout) view.findViewById(R.id.player_header);
		backButton = (Button) view.findViewById(R.id.player_back);
		backButton.setOnClickListener(this);

		movieNameView = (TextView) view.findViewById(R.id.player_moviename);
		changeSourceLayout = (RelativeLayout) view.findViewById(R.id.player_changesourcelayout);
		changeSourceLayout.setOnClickListener(this);
		sourceIconImageView = (ImageView) view.findViewById(R.id.player_sourceicon);
		sourceKeyTextView = (TextView) view.findViewById(R.id.player_currentsourcekey);
		sourceLayout = (RelativeLayout) view.findViewById(R.id.player_sourcelayout);
		sourceLayout.setOnClickListener(this);
		selectBtn = (Button) view.findViewById(R.id.player_selectbtn);
		selectBtn.setOnClickListener(this);
		mOverlayProgress = view.findViewById(R.id.progress_overlay);
		mTime = (TextView) view.findViewById(R.id.player_overlay_time);
		mTime.setOnClickListener(mRemainingTimeListener);
		mLength = (TextView) view.findViewById(R.id.player_overlay_length);
		mLength.setOnClickListener(mRemainingTimeListener);
		mInfo = (TextView) view.findViewById(R.id.player_overlay_info);
		mPlayPause = (ImageButton) view.findViewById(R.id.player_overlay_play);
		mPlayPause.setOnClickListener(mPlayPauseListener);
		mSize = (ImageButton) view.findViewById(R.id.player_overlay_size);
		mSize.setOnClickListener(mSizeListener);
		sourceListView = (ListView) view.findViewById(R.id.player_sourcelist);
		sourceListView.setOnItemClickListener(this);
		selectLayout = (RelativeLayout) view.findViewById(R.id.player_selectlayout);
		gridView = (GridView) view.findViewById(R.id.player_gridview);
		gridView.setOnItemClickListener(this);
		mSurface = (SurfaceView) view.findViewById(R.id.player_surface);
		mSurfaceHolder = mSurface.getHolder();
		mSurfaceFrame = (FrameLayout) view.findViewById(R.id.player_surface_frame);
		String chroma = preferences.getString("chroma_format", "");
		Log.i(TAG, "chroma = " + chroma);
		if (LibVlcUtil.isGingerbreadOrLater() && chroma.equals("YV12")) {
			mSurfaceHolder.setFormat(ImageFormat.YV12);
		} else if (chroma.equals("RV16")) {
			mSurfaceHolder.setFormat(PixelFormat.RGB_565);
		} else {
			mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
		}
		
		mSubtitlesSurface = (SurfaceView) view.findViewById(R.id.subtitles_surface);
		mSubtitlesSurfaceHolder = mSubtitlesSurface.getHolder();
		mSubtitlesSurfaceHolder.setFormat(PixelFormat.RGBA_8888);
		mSubtitlesSurface.setZOrderMediaOverlay(true);
		
		if(mPresentation == null)
		{
			mSurfaceHolder.addCallback(mSurfaceCallback);
			mSubtitlesSurfaceHolder.addCallback(mSubtitlesSurfaceCallback);
		}
		/* Only show the subtitles surface when using "Full Acceleration" mode */
		if (mLibVLC.getHardwareAcceleration() == 2)
			mSubtitlesSurface.setVisibility(View.VISIBLE);

		Log.i(TAG, "getHardwareAcceleration = " + mLibVLC.getHardwareAcceleration());
		// Signal to LibVLC that the videoPlayerActivity was created, thus the
		// SurfaceView is now available for MediaCodec direct rendering.
		mSeekbar = (SeekBar) view.findViewById(R.id.player_overlay_seekbar);
		mSeekbar.setOnSeekBarChangeListener(mSeekListener);
		bufferlayout = (LinearLayout) view.findViewById(R.id.bufferlayout);
		mSpeedView = (TextView) view.findViewById(R.id.player_pre_speed);
		mLock = (ImageButton) view.findViewById(R.id.lock_overlay_button);
		mLock.setOnClickListener(this);
		initBrightnessTouch();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		try {
			fullScreenCallBacks = (FullScreenCallBacks) activity;
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("playerfragment----oncreate");
		
		try {
			mLibVLC = Util.getLibVlcInstance();
		} catch (LibVlcException e) {
			Log.d(TAG, "LibVLC initialisation failed");
			return;
		}
		
		if (LibVlcUtil.isJellyBeanMR1OrLater()) {
			Log.i(TAG, "isJellyBeanMR1OrLater = true");
			// Get the media router service (miracast)
			mMediaRouter = (MediaRouter) activity.getSystemService(Context.MEDIA_ROUTER_SERVICE);
			mMediaRouterCallback = new MediaRouter.SimpleCallback() {
			
				@Override
				public void onRoutePresentationDisplayChanged(MediaRouter router, MediaRouter.RouteInfo info) {
					Log.d(TAG, "onRoutePresentationDisplayChanged: info=" + info);
	                   Log.d(TAG, "onRoutePresentationDisplayChanged: info=" + info);
	                   final Display presentationDisplay = info.getPresentationDisplay();
	                   final int newDisplayId = presentationDisplay != null ? presentationDisplay.getDisplayId() : -1;
	                   if (newDisplayId != mPresentationDisplayId)
	                       removePresentation();
				}
			};
		}
		
		mEnableCloneMode = mLibVLC.getEnableCloneScreen();
		
		createPresentation();
		
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		if (LibVlcUtil.isICSOrLater())
			activity.getWindow().getDecorView().findViewById(android.R.id.content)
					.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
						@Override
						public void onSystemUiVisibilityChange(int visibility) {
							if (visibility == mUiVisibility)
								return;
							setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum,
									mSarDen);
							if (visibility == View.SYSTEM_UI_FLAG_VISIBLE && !mShowing && !activity.isFinishing()) {
								showOverlay();
							}
							mUiVisibility = visibility;
						}
					});

		mAudioManager = (AudioManager) activity.getSystemService(activity.AUDIO_SERVICE);
		mAudioMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		mSwitchingView = false;
		mEndReached = false;

		// Clear the resume time, since it is only used for resumes in external
		// videos.
		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(PreferencesActivity.VIDEO_RESUME_TIME, -1);
		// Also clear the subs list, because it is supposed to be per session
		// only (like desktop VLC). We don't want the customs subtitle file
		// to persist forever with this video.
		editor.putString(PreferencesActivity.VIDEO_SUBTITLE_FILES, null);
		editor.commit();

		mDownCenterinstance = DownCenter.getExistingInstance();
		mLibVLC.eventVideoPlayerActivityCreated(true);

		EventHandler em = EventHandler.getInstance();
		em.addHandler(eventHandler);

		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	private void initAds(View View){
		ad_container=(RelativeLayout)view.findViewById(R.id.ad_container);
		String adPlaceId="2073863";
		interAd=new InterstitialAd(getActivity(), AdSize.InterstitialForVideoPausePlay,adPlaceId);
		interAd.setListener(listener);
		interAd.loadAdForVideoApp(100,100);

	}
	InterstitialAdListener listener=new InterstitialAdListener() {
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
			interAd.loadAdForVideoApp(100,100);
		}

		@Override
		public void onAdFailed(String s) {
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		System.out.println("playerfragment----onCreateView");
		// view = null;
		if (view == null)
			view = inflater.inflate(R.layout.local_mini_player, null);
		initAds(view);
		widgetInit();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// System.out.println("playerfragment----onActivityCreated");
		setWidgetValues();
		dbHelperDao = DBHelperDao.getDBHelperDaoInstace(getActivity());
		getPlayPrefencesConfig();
		deletePlayCache();
	}

	@Override
	public void onResume() {
		super.onResume();
		mSwitchingView = false;
		AudioServiceController.getInstance().bindAudioService(activity,
				new AudioServiceController.AudioServiceConnectionListener() {
					@Override
					public void onConnectionSuccess() {
						mHandler.sendEmptyMessage(AUDIO_SERVICE_CONNECTION_SUCCESS);
					}

					@Override
					public void onConnectionFailed() {
						mHandler.sendEmptyMessage(AUDIO_SERVICE_CONNECTION_FAILED);
					}
				});

		if (mMediaRouter != null) {
			// Listen for changes to media routes.
			mMediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, mMediaRouterCallback);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// Debug.stopMethodTracing();
		if (mMediaRouter != null) {
			// Stop listening for changes to media routes.
			mediaRouterAddCallback(false);
		}
		if (mLocation != null && mLocation.indexOf("ppfilm://") > -1) {
			mDownCenterinstance.StopPlaytaskRead();
		}
		if (mSwitchingView) {
			Log.d(TAG, "mLocation = \"" + mLocation + "\"");
			AudioServiceController.getInstance().showWithoutParse(savedIndexPosition);
			AudioServiceController.getInstance().unbindAudioService(activity);
			return;
		}

		long time = mLibVLC.getTime();
		long length = mLibVLC.getLength();
		// remove saved position if in the last 5 seconds
		if (length - time < 5000)
			time = 0;
		else
			time -= 5000; // go back 5 seconds, to compensate loading time

		if (dbHelperDao != null && downInfo != null) {
			System.out.println("更新播放进度到数据库--id-->" + downInfo.getDownloadID());
			dbHelperDao.updatePlayHistoryPlayProgress(downInfo.getDownloadID(), time);
		}
		/*
		 * Pausing here generates errors because the vout is constantly trying
		 * to refresh itself every 80ms while the surface is not accessible
		 * anymore. To workaround that, we keep the last known position in the
		 * playlist in savedIndexPosition to be able to restore it during
		 * onResume().
		 */
		mLibVLC.stop();

		mSurface.setKeepScreenOn(false);

		SharedPreferences.Editor editor = preferences.edit();
		// Save position
		if (time >= 0 && mCanSeek) {
			if (MediaDatabase.getInstance(activity).mediaItemExists(mLocation)) {
				MediaDatabase.getInstance(activity).updateMedia(mLocation, MediaDatabase.mediaColumn.MEDIA_TIME, time);
			} else {
				// Video file not in media library, store time just for
				// onResume()
				editor.putLong(PreferencesActivity.VIDEO_RESUME_TIME, time);
			}
			// if(downInfo!=null){
			// downInfo.setDownProgress(time);
			// downInfo.setDownTotalSize(length);
			// DBHelperDao.getDBHelperDaoInstace(activity).insertMovieHistroy(downInfo);
			// }
		}
		// Save selected subtitles
		String subtitleList_serialized = null;
		if (mSubtitleSelectedFiles.size() > 0) {
			Log.d(TAG, "Saving selected subtitle files");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(mSubtitleSelectedFiles);
				subtitleList_serialized = bos.toString();
			} catch (IOException e) {
			}
		}
		editor.putString(PreferencesActivity.VIDEO_SUBTITLE_FILES, subtitleList_serialized);

		editor.commit();
		if (activity != null)
			AudioServiceController.getInstance().unbindAudioService(activity);
	}

	@Override
	public void onStop() {
		super.onStop();
		// Dismiss the presentation when the activity is not visible.
		if (mPresentation != null) {
			Log.i(TAG, "Dismissing presentation because the activity is no longer visible.");
			mPresentation.dismiss();
			mPresentation = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mLibVLC != null && !mSwitchingView) {
			if (mLocation != null && mLocation.indexOf("ppfilm://") > -1) {
				DownCenter.getExistingInstance().DestroyPlaytak();
			}
			mLibVLC.stop();
		}
		EventHandler em = EventHandler.getInstance();
		em.removeHandler(eventHandler);

		// MediaCodec opaque direct rendering should not be used anymore since
		// there is no surface to attach.
		mLibVLC.eventVideoPlayerActivityCreated(false);
		// HW acceleration was temporarily disabled because of an error, restore
		// the previous value.
		if (mDisabledHardwareAcceleration)
			mLibVLC.setHardwareAcceleration(mPreviousHardwareAccelerationMode);

		mAudioManager = null;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		view = null;

	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void mediaRouterAddCallback(boolean add) {
        if(!LibVlcUtil.isJellyBeanMR1OrLater() || mMediaRouter == null) return;

        if(add)
            mMediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, mMediaRouterCallback);
        else
            mMediaRouter.removeCallback(mMediaRouterCallback);
    }

	/**
	 * 删除上一次播放缓存文件
	 */
	private void deletePlayCache() {
		if (downInfo == null)
			return;
		// 当前播放地址
		String currentPlayUrl = downInfo.getDownAddress();
		// 上一次播放地址
		final String lastPlayUrl = PIPISharedPreferences.getInstance(getActivity()).getLeastPlayUrl();
		if (TextUtils.isEmpty(lastPlayUrl)) {
			// 初次播放时保存当前播放地址到SharedPreferences
			PIPISharedPreferences.getInstance(getActivity()).putLeastPlayUrl(currentPlayUrl);
			return;
		}
		boolean isDownload = dbHelperDao.isFirstInsertToDownloadTable(lastPlayUrl);
		boolean isSame = currentPlayUrl.equals(lastPlayUrl);
		if (isDownload && !isSame) { // 没有下载过 上一次和当前地址不相同 delete
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.d(AppConfig.Tag, "playerfragment 清除缓存");
					FileUtils.delPlayCacheFiles(lastPlayUrl);
				}
			}).start();
		}

		PIPISharedPreferences.getInstance(getActivity()).putLeastPlayUrl(currentPlayUrl);
	}

	private void getPlayPrefencesConfig() {
		isAutoPlayNext = PIPISharedPreferences.getInstance(getActivity()).getAutoPlayNext();
		isPlayFromHistory = PIPISharedPreferences.getInstance(getActivity()).getPlayFromHistory();
	}

	private void setWidgetValues() {
		if (activity == null || view == null)
			return;
		currentList = new ArrayList<DownLoadInfo>();
		downInfo = (DownLoadInfo) getArguments().getSerializable("downloadinfo");
		if (downInfo != null) {
			sourceKey = downInfo.getDownloadSourceTag();
			movieNameView.setText(downInfo.getDownloadName() + "." + (downInfo.getDownloadPosition() + 1));
		}
		if (!TextUtils.isEmpty(sourceKey)) {
			if (AppConfig.sourceMap != null && AppConfig.sourceMap.containsKey(sourceKey)) {
				sourceIconImageView.setBackgroundResource(AppConfig.sourceMap.get(sourceKey).getValues());
			}
			sourceKeyTextView.setText(sourceKey);
		}
		movieInfo = (MovieInfo) getArguments().getSerializable("movieinfo");
		if (movieInfo != null) {
			if (movieInfo.getMovieDownUrlmap() != null && movieInfo.getMovieDownUrlmap().keySet().size() != 0) {
				currentList = movieInfo.getMovieDownUrlmap().get(sourceKey);
			}
			playerSourceAdapter = new PlayerSourceAdapter(getActivity());
			playerSourceAdapter.setCurrentSourceKey(sourceKey);
			playerSourceAdapter.setList(movieInfo.getCurrentMovieDetialSources());
			sourceListView.setAdapter(playerSourceAdapter);
		}
		if (downInfo != null && currentList != null && currentList.size() != 0) {
			if (gridView.getChildCount() != 0)
				gridView.removeAllViews();
			playerGridAdapter = new PlayerGridAdapter(getActivity());
			// playerGridAdapter.setSelectAddress(downInfo.getDownAddress());
			playerGridAdapter.setSelectPosition(downInfo.getDownloadPosition());
			playerGridAdapter.setList(currentList);
			gridView.setAdapter(playerGridAdapter);
		}

		isFullScreen = getArguments().getBoolean("isFullScreen");
		if (!isFullScreen) {
			mSize.setBackgroundResource(R.drawable.player_fullscreenbtnbg);
			headerLayout.setVisibility(View.GONE);
			sourceLayout.setVisibility(View.GONE);
			selectLayout.setVisibility(View.INVISIBLE);
		} else {
			mSize.setBackgroundResource(R.drawable.player_fullscreenbtnbg_norrow);
		}
		if (activity instanceof PlayerActivity) {
			mSize.setVisibility(View.INVISIBLE);
			backButton.setVisibility(View.INVISIBLE);
			changeSourceLayout.setVisibility(View.INVISIBLE);
			selectBtn.setVisibility(View.INVISIBLE);
		}

	}

	private void startPlayback() {
		load();

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mLibVLC != null && mLibVLC.isPlaying()) {
					KeyguardManager km = (KeyguardManager) activity.getSystemService(activity.KEYGUARD_SERVICE);
					if (km.inKeyguardRestrictedInputMode())
						mLibVLC.pause();
				}
			}
		}, 500);

		// Add any selected subtitle file from the file picker
		if (mSubtitleSelectedFiles.size() > 0) {
			for (String file : mSubtitleSelectedFiles) {
				Log.i(TAG, "Adding user-selected subtitle " + file);
				mLibVLC.addSubtitleTrack(file);
			}
		}

		if (mMediaRouter != null) {
            // Listen for changes to media routes.
            mediaRouterAddCallback(true);
        }
	}

	private long exitTime = 0;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if ((System.currentTimeMillis() - exitTime) >= 300) {
				exitTime = System.currentTimeMillis();
			} else {
				if (!(activity instanceof MovieDetialActivity))
					return true;
				if (!isFullScreen) {
					showInfo("双击放大", 1000);
					isFullScreen = true;
					setFullScreenPlay(isFullScreen);
				} else {
					showInfo("双击缩小", 1000);
					isFullScreen = false;
					setFullScreenPlay(isFullScreen);
				}
				return true;
			}
		}
		// .............................
		if (mIsLocked) {
			// locked, only handle show/hide & ignore all actions
			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (!mShowing) {
					showOverlay();
				} else {
					hideOverlay(true);
				}
			}
			return true;
		}
		// .............................
		DisplayMetrics screen = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(screen);
		if (mSurfaceYDisplayRange == 0)
			mSurfaceYDisplayRange = Math.min(screen.widthPixels, screen.heightPixels);

		float y_changed = event.getRawY() - mTouchY;
		float x_changed = event.getRawX() - mTouchX;

		// coef is the gradient's move to determine a neutral zone
		float coef = Math.abs(y_changed / x_changed);
		float xgesturesize = ((x_changed / screen.xdpi) * 2.54f);

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			// Audio
			mTouchY = event.getRawY();
			mVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			mTouchAction = TOUCH_NONE;
			// Seek
			mTouchX = event.getRawX();
			break;

		case MotionEvent.ACTION_MOVE:
			if (coef > 2) {
				if (mTouchX > (view.getMeasuredWidth() / 2)) {
					doVolumeTouch(y_changed);
				} else {
					doBrightnessTouch(y_changed);
				}
				if (Util.hasNavBar())
					showOverlay();
			}
			// Seek (Right or Left move)
			doSeekTouch(coef, xgesturesize, false);
			break;

		case MotionEvent.ACTION_UP:
			// Audio or Brightness
			if (mTouchAction == TOUCH_NONE) {
				if (!mShowing) {
					showOverlay();
				} else {
					hideOverlay(true);
				}
			}
			// Seek
			doSeekTouch(coef, xgesturesize, true);
			break;
		}
		// return mTouchAction != TOUCH_NONE;
		return true;
	}

	private void initBrightnessTouch() {
		float brightnesstemp = preferences.getFloat("screenBrightness", 0);
		// Initialize the layoutParams screen brightness
		if (brightnesstemp == 0) {// 第一次调整亮度，用系统亮度
			try {
				brightnesstemp = android.provider.Settings.System.getInt(VLCApplication.getAppContext()
						.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS) / 255.0f;
			} catch (SettingNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (isAdded()) {
			WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
			lp.screenBrightness = brightnesstemp;
			activity.getWindow().setAttributes(lp);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		setSurfaceSize(mVideoWidth, mVideoHeight, mVideoVisibleWidth, mVideoVisibleHeight, mSarNum, mSarDen);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void setSurfaceSize(int width, int height, int visible_width, int visible_height, int sar_num, int sar_den) {
		if (width * height == 0)
			return;

		// store video size
		mVideoHeight = height;
		mVideoWidth = width;
		mVideoVisibleHeight = visible_height;
		mVideoVisibleWidth = visible_width;
		mSarNum = sar_num;
		mSarDen = sar_den;
		// System.out.println("*****setSurfaceSize*****");
		// System.out.println("mVideoWidth:"+mVideoWidth);
		// System.out.println("mVideoHeight:"+mVideoHeight);
		// System.out.println("mVideoVisibleWidth:"+mVideoVisibleWidth);
		// System.out.println("mVideoVisibleHeight:"+mVideoVisibleHeight);
		// System.out.println("mSarNum:"+mSarNum);
		// System.out.println("mSarDen:"+mSarDen);
		// System.out.println("*****setSurfaceSize*****");
		Message msg = mHandler.obtainMessage(SURFACE_SIZE);
		mHandler.sendMessage(msg);
	}

	/**
	 * Lock screen rotation
	 */
	private void lockScreen() {
		showInfo(R.string.locked, 1000);
		mLock.setBackgroundResource(R.drawable.ic_lock_glow);
		mTime.setEnabled(false);
		mSeekbar.setEnabled(false);
		mLength.setEnabled(false);
		hideOverlay(true);
	}

	/**
	 * Remove screen lock
	 */
	private void unlockScreen() {
		showInfo(R.string.unlocked, 1000);
		mLock.setBackgroundResource(R.drawable.ic_lock);
		mTime.setEnabled(true);
		mSeekbar.setEnabled(true);
		mLength.setEnabled(true);
		// mMenu.setEnabled(true);
		mShowing = false;
		showOverlay();
	}

	/**
	 * Show text in the info view for "duration" milliseconds
	 * 
	 * @param text
	 * @param duration
	 */
	private void showInfo(String text, int duration) {
		mInfo.setVisibility(View.VISIBLE);
		mInfo.setText(text);
		mHandler.removeMessages(FADE_OUT_INFO);
		mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
	}

	private void showInfo(int textid, int duration) {
		mInfo.setVisibility(View.VISIBLE);
		mInfo.setText(textid);
		mHandler.removeMessages(FADE_OUT_INFO);
		mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, duration);
	}

	/**
	 * Show text in the info view
	 * 
	 * @param text
	 */
	private void showInfo(String text) {
		mInfo.setVisibility(View.VISIBLE);
		mInfo.setText(text);
		mHandler.removeMessages(FADE_OUT_INFO);
	}

	/**
	 * hide the info view with "delay" milliseconds delay
	 * 
	 * @param delay
	 */
	private void hideInfo(int delay) {
		mHandler.sendEmptyMessageDelayed(FADE_OUT_INFO, delay);
	}

	/**
	 * hide the info view
	 */
	private void hideInfo() {
		hideInfo(0);
	}

	private void fadeOutInfo() {
		if (mInfo.getVisibility() == View.VISIBLE)
			mInfo.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_out));
		mInfo.setVisibility(View.INVISIBLE);
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	private void changeAudioFocus(boolean gain) {
		if (!LibVlcUtil.isFroyoOrLater()) // NOP if not supported
			return;

		if (mAudioFocusListener == null) {
			mAudioFocusListener = new OnAudioFocusChangeListener() {
				@Override
				public void onAudioFocusChange(int focusChange) {
					/*
					 * Pause playback during alerts and notifications
					 */
					switch (focusChange) {
					case AudioManager.AUDIOFOCUS_LOSS:
					case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
					case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
						if (mLibVLC.isPlaying())
							mLibVLC.pause();
						break;
					case AudioManager.AUDIOFOCUS_GAIN:
					case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
					case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
						if (!mLibVLC.isPlaying())
							mLibVLC.play();
						break;
					}
				}
			};
		}

		AudioManager am = (AudioManager) activity.getSystemService(activity.AUDIO_SERVICE);
		if (gain)
			am.requestAudioFocus(mAudioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		else
			am.abandonAudioFocus(mAudioFocusListener);
	}

	/**
	 * Handle libvlc asynchronous events
	 */
	private final Handler eventHandler = new VideoPlayerEventHandler(this);

	private static class VideoPlayerEventHandler extends WeakHandler<PlayerFragment> {
		public VideoPlayerEventHandler(PlayerFragment owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			PlayerFragment activity = getOwner();
			if (activity == null)
				return;
			// Do not handle events if we are leaving the VideoPlayerActivity
			if (activity.mSwitchingView)
				return;

			switch (msg.getData().getInt("event")) {
			case EventHandler.MediaParsedChanged:
				Log.i(TAG, "MediaParsedChanged");
				if (activity.mLibVLC.getVideoTracksCount() < 1) {
					Log.i(TAG, "No video track, open in audio mode");
					activity.switchToAudioMode();
				}
				break;
			case EventHandler.MediaPlayerBuffering:
				Bundle b = msg.getData();
				float progress = b.getFloat("data");
				Log.d(TAG, "--progress--" + progress);
				if (activity != null)
					activity.getBuffer(progress);
				break;
			case EventHandler.MediaPlayerPlaying:
				Log.i(TAG, "MediaPlayerPlaying");
				activity.showOverlay();
				/**
				 * FIXME: update the track list when it changes during the
				 * playback. (#7540)
				 */
				activity.setESTracks();
				activity.changeAudioFocus(true);
				break;
			case EventHandler.MediaPlayerPaused:
				Log.i(TAG, "MediaPlayerPaused");
				break;
			case EventHandler.MediaPlayerStopped:
				Log.i(TAG, "MediaPlayerStopped");
				activity.changeAudioFocus(false);
				break;
			case EventHandler.MediaPlayerEndReached:
				Log.i(TAG, "MediaPlayerEndReached");
				activity.changeAudioFocus(false);
				activity.endReached();
				break;
			case EventHandler.MediaPlayerVout:
				activity.handleVout(msg);
				break;
			case EventHandler.MediaPlayerPositionChanged:
				if (!activity.mCanSeek)
					activity.mCanSeek = true;
				// don't spam the logs
				break;
			case EventHandler.MediaPlayerEncounteredError:
				Log.i(TAG, "MediaPlayerEncounteredError");
				// activity.encounteredError();
				break;
			case EventHandler.HardwareAccelerationError:
				Log.i(TAG, "HardwareAccelerationError");
				activity.handleHardwareAccelerationError();
				break;
			default:
				Log.e(TAG, String.format("Event not handled (0x%x)", msg.getData().getInt("event")));
				break;
			}
			activity.updateOverlayPausePlay();
		}
	};

	/**
	 * Handle resize of the surface and the overlay
	 */
	private final Handler mHandler = new VideoPlayerHandler(this);

	private static class VideoPlayerHandler extends WeakHandler<PlayerFragment> {
		public VideoPlayerHandler(PlayerFragment owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			PlayerFragment activity = getOwner();
			if (activity == null) // WeakReference could be GC'ed early
				return;

			switch (msg.what) {
			case FADE_OUT:
				activity.hideOverlay(false);
				break;
			case SHOW_PROGRESS:
				int pos = activity.setOverlayProgress();
				if (activity.canShowProgress()) {
					msg = obtainMessage(SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
				}
				break;
			case SURFACE_SIZE:
				activity.changeSurfaceSize();
				break;
			case FADE_OUT_INFO:
				activity.fadeOutInfo();
				break;
			case AUDIO_SERVICE_CONNECTION_SUCCESS:
				activity.startPlayback();
				break;
			case AUDIO_SERVICE_CONNECTION_FAILED:
				// activity.finish();
				break;
			}
		}
	};

	private boolean canShowProgress() {
		return !mDragging && mShowing && mLibVLC.isPlaying();
	}

	private void endReached() {
		if (mLibVLC.getMediaList().expandMedia(savedIndexPosition) == 0) {
			Log.d(TAG, "Found a video playlist, expanding it");
			eventHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					load();
				}
			}, 1000);
		} else {
			/* Exit player when reaching the end */
			mEndReached = true;
			if (downInfo != null && isAutoPlayNext) {
				int i = downInfo.getDownloadPosition();
				if (currentList == null || currentList.size() == 0)
					return;
				System.out.println("---播放下集---");
				if (i + 1 <= currentList.size() - 1) {
					downInfo.setDownloadPosition(i + 1);
					downInfo.setDownAddress(currentList.get(i + 1).getDownAddress());
					playMovie(downInfo);
				} else if (isFullScreen) {
					isFullScreen = false;
					setFullScreenPlay(isFullScreen);
				}
			} else {
				if (isFullScreen) {
					isFullScreen = false;
					setFullScreenPlay(isFullScreen);
				}
			}
		}
	}

	/**
	 * 播放错误
	 */
	private void encounteredError() {
		/* Encountered Error, exit player with a message */
		AlertDialog dialog = new AlertDialog.Builder(activity)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						activity.finish();
					}
				}).setTitle(R.string.encountered_error_title).setMessage(R.string.encountered_error_message).create();
		dialog.show();
	}

	public void eventHardwareAccelerationError() {
		EventHandler em = EventHandler.getInstance();
		em.callback(EventHandler.HardwareAccelerationError, new Bundle());
	}

	private void handleHardwareAccelerationError() {
		mLibVLC.stop();
		AlertDialog dialog = new AlertDialog.Builder(activity)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						mDisabledHardwareAcceleration = true;
						mPreviousHardwareAccelerationMode = mLibVLC.getHardwareAcceleration();
						mLibVLC.setHardwareAcceleration(LibVLC.HW_ACCELERATION_DISABLED);
						mSubtitlesSurface.setVisibility(View.INVISIBLE);
						load();
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						activity.finish();
					}
				}).setTitle(R.string.hardware_acceleration_error_title)
				.setMessage(R.string.hardware_acceleration_error_message).create();
		dialog.show();
	}

	private void handleVout(Message msg) {
		if (msg.getData().getInt("data") == 0 && !mEndReached) {
			/* Video track lost, open in audio mode */
			Log.i(TAG, "Video track lost, switching to audio");
			mSwitchingView = true;
			activity.finish();
		}
	}

	private void switchToAudioMode() {
		mSwitchingView = true;
		// Show the MainActivity if it is not in background.
		if (activity.getIntent().getAction() != null && activity.getIntent().getAction().equals(Intent.ACTION_VIEW)) {
			Intent i = new Intent(activity, MainLocalActivity.class);
			startActivity(i);
		}
		activity.finish();
	}

	@SuppressLint("NewApi")
	private void changeSurfaceSize() {
		int sw;
		int sh;

		// get screen size
		if (mPresentation == null) {
			sw = activity.getWindow().getDecorView().getWidth();
			sh = activity.getWindow().getDecorView().getHeight();
		} else {
			sw = mPresentation.getWindow().getDecorView().getWidth();
			sh = mPresentation.getWindow().getDecorView().getHeight();
		}

		// System.out.println("*******************");
		// System.out.println("sw:" + sw);
		// System.out.println("sh:" + sh);
		// System.out.println("*******************");
		double dw, dh;
		if (isFullScreen) {
			dw = sw;
			dh = sh;
		} else {
			dw = getArguments().getInt("width");
			dh = getArguments().getInt("height");
			if (dw * dh == 0) {
				dw = CommonUtil.scaleWidgetWidth(896);
				dh = CommonUtil.scaleWidgetHeight(465);
			}
		}
		// double dw = 896, dh = 465;
		boolean isPortrait;

		if (mPresentation == null) {
			// getWindow().getDecorView() doesn't always take orientation into
			// account, we have to correct the values
			isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
		} else {
			isPortrait = false;
		}

		if (sw > sh && isPortrait || sw < sh && !isPortrait) {
			dw = sh;
			dh = sw;
		}

		// sanity check
		if (dw * dh == 0 || mVideoWidth * mVideoHeight == 0) {
			Log.e(TAG, "Invalid surface size");
			return;
		}

		// compute the aspect ratio
		double ar, vw;
		double density = (double) mSarNum / (double) mSarDen;
		if (density == 1.0) {
			/* No indication about the density, assuming 1:1 */
			vw = mVideoVisibleWidth;
			ar = (double) mVideoVisibleWidth / (double) mVideoVisibleHeight;
		} else {
			/* Use the specified aspect ratio */
			vw = mVideoVisibleWidth * density;
			ar = vw / mVideoVisibleHeight;
		}

		// compute the display aspect ratio
		double dar = dw / dh;

		switch (mCurrentSize) {
		case SURFACE_BEST_FIT:
			if (dar < ar)
				dh = dw / ar;
			else
				dw = dh * ar;
			break;
		case SURFACE_FIT_HORIZONTAL:
			dh = dw / ar;
			break;
		case SURFACE_FIT_VERTICAL:
			dw = dh * ar;
			break;
		case SURFACE_FILL:
			break;
		case SURFACE_16_9:
			ar = 16.0 / 9.0;
			if (dar < ar)
				dh = dw / ar;
			else
				dw = dh * ar;
			break;
		case SURFACE_4_3:
			ar = 4.0 / 3.0;
			if (dar < ar)
				dh = dw / ar;
			else
				dw = dh * ar;
			break;
		case SURFACE_ORIGINAL:
			dh = mVideoVisibleHeight;
			dw = vw;
			break;
		}

		SurfaceView surface;
		SurfaceView subtitlesSurface;
		SurfaceHolder surfaceHolder;
		SurfaceHolder subtitlesSurfaceHolder;
		FrameLayout surfaceFrame;

		if (mPresentation == null) {
			surface = mSurface;
			subtitlesSurface = mSubtitlesSurface;
			surfaceHolder = mSurfaceHolder;
			subtitlesSurfaceHolder = mSubtitlesSurfaceHolder;
			surfaceFrame = mSurfaceFrame;
		} else {
			surface = mPresentation.mSurface;
			subtitlesSurface = mPresentation.mSubtitlesSurface;
			surfaceHolder = mPresentation.mSurfaceHolder;
			subtitlesSurfaceHolder = mPresentation.mSubtitlesSurfaceHolder;
			surfaceFrame = mPresentation.mSurfaceFrame;
		}

		// 910 468
		// System.out.println("*******************");
		// System.out.println("mVideoWidth:" + mVideoWidth);
		// System.out.println("mVideoHeight:" + mVideoHeight);
		// System.out.println("*******************");
		// force surface buffer size
		surfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
		subtitlesSurfaceHolder.setFixedSize((int) dw, (int) dh);

		// set display size
		LayoutParams lp = surface.getLayoutParams();
		// lp.width = (int) Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
		// lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
		lp.width = (int) Math.ceil(dw);
		lp.height = (int) Math.ceil(dh);
		surface.setLayoutParams(lp);
		subtitlesSurface.setLayoutParams(lp);

		// set frame size (crop if necessary)
		lp = surfaceFrame.getLayoutParams();
		lp.width = (int) Math.floor(dw);
		lp.height = (int) Math.floor(dh);
		surfaceFrame.setLayoutParams(lp);

		surface.invalidate();
		subtitlesSurface.invalidate();
	}

	/**
	 * show/hide the overlay
	 */

	private void doSeekTouch(float coef, float gesturesize, boolean seek) {
		// No seek action if coef > 0.5 and gesturesize < 1cm
		if (coef > 0.5 || Math.abs(gesturesize) < 1 || !mCanSeek)
			return;

		if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_SEEK)
			return;
		mTouchAction = TOUCH_SEEK;

		// Always show seekbar when searching
		if (!mShowing)
			showOverlay();

		long length = mLibVLC.getLength();
		long time = mLibVLC.getTime();

		// Size of the jump, 10 minutes max (600000), with a bi-cubic
		// progression, for a 8cm gesture
		int jump = (int) (Math.signum(gesturesize) * ((600000 * Math.pow((gesturesize / 8), 4)) + 3000));

		// Adjust the jump
		if ((jump > 0) && ((time + jump) > length))
			jump = (int) (length - time);
		if ((jump < 0) && ((time + jump) < 0))
			jump = (int) -time;

		// Jump !
		if (seek && length > 0)
			mLibVLC.setTime(time + jump);

		if (length > 0)
			// Show the jump's size
			showInfo(
					String.format("%s%s (%s)", jump >= 0 ? "+" : "", Util.millisToString(jump),
							Util.millisToString(time + jump)), 1000);
		else
			showInfo(R.string.unseekable_stream, 1000);
	}

	private void doBrightnessTouch(float y_changed) {
		if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_BRIGHTNESS)
			return;
		mTouchAction = TOUCH_BRIGHTNESS;

		float delta = -y_changed / mSurfaceYDisplayRange * 0.2f;
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.screenBrightness = Math.min(Math.max(lp.screenBrightness + delta, 0.01f), 1);
		preferences.edit().putFloat("screenBrightness", lp.screenBrightness).commit();// 保存亮度
		activity.getWindow().setAttributes(lp);
		showInfo(
				VLCApplication.getAppResources().getString(R.string.brightness) + '\u00A0'
						+ Math.round(lp.screenBrightness * 15), 1000);
	}

	private void doVolumeTouch(float y_changed) {
		if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_VOLUME)
			return;
		int delta = -(int) ((y_changed / mSurfaceYDisplayRange) * mAudioMax);
		int vol = (int) Math.min(Math.max(mVol + delta, 0), mAudioMax);
		if (delta != 0) {
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
			mTouchAction = TOUCH_VOLUME;
			showInfo(getString(R.string.volume) + '\u00A0' + Integer.toString(vol), 1000);
		}
	}

	/**
	 * handle changes of the seekbar (slicer)
	 */
	private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mDragging = true;
			showOverlay(OVERLAY_INFINITE);
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mDragging = false;
			showOverlay();
			hideInfo();
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (fromUser && mCanSeek) {
				mLibVLC.setTime(progress);
				setOverlayProgress();
				mTime.setText(Util.millisToString(progress));
				showInfo(Util.millisToString(progress));
			}

		}
	};

	private final OnClickListener mPlayPauseListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mLibVLC.isPlaying()) {
				ad_container.setVisibility(View.VISIBLE);
				interAd.showAdInParentForVideoApp(getActivity(), ad_container);
				pause();
			}
			else {
				ad_container.setVisibility(View.GONE);
				play();
			}
			showOverlay();
		}
	};

	public void seek(int delta) {
		// unseekable stream
		if (mLibVLC.getLength() <= 0 || !mCanSeek)
			return;

		long position = mLibVLC.getTime() + delta;
		if (position < 0)
			position = 0;
		mLibVLC.setTime(position);
		showOverlay();
	}

	/**
	     *
	     */
	private final OnClickListener mSizeListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// showOverlay();
			// VideoPlayerActivity.start(activity, downInfo);
			if (activity != null && activity instanceof MovieDetialActivity) {
				if (!isFullScreen) {
					isFullScreen = true;
				} else {
					isFullScreen = false;
				}
				setFullScreenPlay(isFullScreen);
			}

		}
	};

	public void setFullScreenPlay(boolean isFull) {
		if (!isFull) {
			headerLayout.setVisibility(View.GONE);
			sourceLayout.setVisibility(View.GONE);
			selectLayout.setVisibility(View.GONE);
			mSize.setBackgroundResource(R.drawable.player_fullscreenbtnbg);
			if (fullScreenCallBacks != null) {
				fullScreenCallBacks.fullScreenPlay(isFullScreen);
			}
			if (activity instanceof MovieDetialActivity) {
				((MovieDetialActivity) activity)
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			}
		} else {
			if (activity instanceof MovieDetialActivity) {
				((MovieDetialActivity) activity).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}
			headerLayout.setVisibility(View.VISIBLE);
			mSize.setBackgroundResource(R.drawable.player_fullscreenbtnbg_norrow);
			if (fullScreenCallBacks != null) {
				fullScreenCallBacks.fullScreenPlay(isFullScreen);
			}
		}
		changeSurfaceSize();
	}

	private final OnClickListener mRemainingTimeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mDisplayRemainingTime = !mDisplayRemainingTime;
			showOverlay();
		}
	};

	/**
	 * attach and disattach surface to the lib
	 */
	private final SurfaceHolder.Callback mSurfaceCallback = new Callback() {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			if (format == PixelFormat.RGBX_8888)
				Log.d(TAG, "Pixel format is RGBX_8888");
			else if (format == PixelFormat.RGB_565)
				Log.d(TAG, "Pixel format is RGB_565");
			else if (format == ImageFormat.YV12)
				Log.d(TAG, "Pixel format is YV12");
			else
				Log.d(TAG, "Pixel format is other/unknown");
			mLibVLC.attachSurface(holder.getSurface(), PlayerFragment.this);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			mLibVLC.detachSurface();
		}
	};

	private final SurfaceHolder.Callback mSubtitlesSurfaceCallback = new Callback() {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			mLibVLC.attachSubtitlesSurface(holder.getSurface());
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			mLibVLC.detachSubtitlesSurface();
		}
	};

	/**
	 * show overlay the the default timeout
	 */
	private void showOverlay() {
		if (mPresentation == null)
			showOverlay(OVERLAY_TIMEOUT);
		else
			showOverlay(OVERLAY_INFINITE); // Hack until we have fullscreen
											// controls
	}

	/**
	 * show overlay
	 */
	private void showOverlay(int timeout) {
		mHandler.sendEmptyMessage(SHOW_PROGRESS);
		if (!mShowing) {
			mShowing = true;
			// dimStatusBar(false);
			if (!mIsLocked)
				mOverlayProgress.setVisibility(View.VISIBLE);
			mLock.setVisibility(View.VISIBLE);
		}
		Message msg = mHandler.obtainMessage(FADE_OUT);
		if (timeout != 0) {
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendMessageDelayed(msg, timeout);
		}
		updateOverlayPausePlay();
	}

	/**
	 * hider overlay
	 */
	private void hideOverlay(boolean fromUser) {
		if (mPresentation != null)
			return; // Hack until we have fullscreen controls

		if (mShowing) {
			mHandler.removeMessages(SHOW_PROGRESS);
			mOverlayProgress.setVisibility(View.INVISIBLE);
			mShowing = false;
			// dimStatusBar(true);
			mLock.setVisibility(View.INVISIBLE);
			if (sourceLayout != null && sourceLayout.getVisibility() == View.VISIBLE) {
				sourceLayout.setVisibility(View.GONE);
			}
			if (selectLayout != null && selectLayout.getVisibility() == View.VISIBLE) {
				selectLayout.setVisibility(View.GONE);
			}

		}
	}

	/**
	 * Dim the status bar and/or navigation icons when needed on Android 3.x.
	 * Hide it on Android 4.0 and later
	 */
	/*
	 * @TargetApi(Build.VERSION_CODES.JELLY_BEAN) private void
	 * dimStatusBar(boolean dim) { if (!LibVlcUtil.isHoneycombOrLater() ||
	 * !Util.hasNavBar()) return; int layout = 0; if (!Util.hasCombBar() &&
	 * LibVlcUtil.isJellyBeanOrLater()) layout =
	 * View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
	 * View.SYSTEM_UI_FLAG_LAYOUT_STABLE; int visibility = (dim ?
	 * (Util.hasCombBar() ? View.SYSTEM_UI_FLAG_LOW_PROFILE :
	 * View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) : View.SYSTEM_UI_FLAG_VISIBLE) |
	 * layout; mSurface.setSystemUiVisibility(visibility);
	 * mSubtitlesSurface.setSystemUiVisibility(visibility); }
	 */

	private void updateOverlayPausePlay() {
		if (mLibVLC == null) {
			return;
		}

		mPlayPause.setBackgroundResource(mLibVLC.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
	}

	/**
	 * update the overlay
	 */
	private int setOverlayProgress() {
		if (mLibVLC == null) {
			return 0;
		}
		int time = (int) mLibVLC.getTime();
		int length = (int) mLibVLC.getLength();
		if (length == 0) {
			Media media = MediaDatabase.getInstance(activity).getMedia(mLocation);
			if (media != null)
				length = (int) media.getLength();
		}

		mSeekbar.setMax(length);
		mSeekbar.setProgress(time);
		if (time >= 0)
			mTime.setText(Util.millisToString(time));
		if (length >= 0)
			mLength.setText(mDisplayRemainingTime && length > 0 ? "- " + Util.millisToString(length - time) : Util
					.millisToString(length));

		return time;
	}

	private void setESTracks() {
		if (mLastAudioTrack >= 0) {
			mLibVLC.setAudioTrack(mLastAudioTrack);
			mLastAudioTrack = -1;
		}
		if (mLastSpuTrack >= -1) {
			mLibVLC.setSpuTrack(mLastSpuTrack);
			mLastSpuTrack = -2;
		}
	}

	/**
    *
    */
	private void play() {
		mLibVLC.play();
		mSurface.setKeepScreenOn(true);

	}

	/**
    *
    */
	private void pause() {
		mLibVLC.pause();
		mSurface.setKeepScreenOn(false);
	}

	/**
	 * External extras: - position (long) - position of the video to start with
	 * (in ms)
	 */
	private void load() {
		mLocation = null;
		String title = getResources().getString(R.string.title);
		boolean dontParse = false;
		boolean fromStart = false;
		String itemTitle = null;
		int itemPosition = -1; // Index in the media list as passed by
								// AudioServer (used only for vout transition
								// internally)
		long intentPosition = -1; // position passed in by intent (ms)

		if (downInfo != null) {
			if (MD5Util.getFromHttpfilm(downInfo.getDownAddress())) {
				mLocation = "file://"
						+ DBHelperDao.getDBHelperDaoInstace(activity).getLocalByUrl(downInfo.getDownAddress());
			} else {
				mLocation = downInfo.getDownAddress();
			}
			itemTitle = downInfo.getDownloadName();
			dontParse = false;
			fromStart = AppConfig.playfromhistroy;
			// System.out.println("是否从历史记录开始播放 "+fromStart);
			itemPosition = downInfo.getDownloadPosition();
			int currentPosition = DBHelperDao.getDBHelperDaoInstace(getActivity()).getPlayPositionFromPlayHistory(
					downInfo.getDownloadID());
			if (itemPosition != currentPosition) {
				DBHelperDao.getDBHelperDaoInstace(getActivity()).updatePlayHistoryPosition(downInfo.getDownloadID(),
						itemPosition);
				DBHelperDao.getDBHelperDaoInstace(getActivity()).updatePlayHistoryPlayProgress(
						downInfo.getDownloadID(), 0);

			}
			if (fromStart && getActivity() != null) {
				intentPosition = DBHelperDao.getDBHelperDaoInstace(getActivity()).getPlayProgressFromPlayHistory(
						downInfo.getDownloadID());
			}

		}
		Log.i(TAG, "mLocation=" + mLocation);
		mSurface.setKeepScreenOn(true);
		if (mLocation != null && mLocation.indexOf("ppfilm://") > -1) {
			mLocation = mDownCenterinstance.startPlayTask2(mLocation);
			Log.i(TAG, "startPlayTask2===" + mLocation);
			if (!mLocation.startsWith("file:"))
				speedTimer.start();// 标识还是播放本地文件
		}
		/* Start / resume playback */
		if (dontParse && itemPosition >= 0) {
			// Provided externally from AudioService
			Log.d(TAG, "Continuing playback from AudioService at index " + itemPosition);
			savedIndexPosition = itemPosition;
			if (!mLibVLC.isPlaying()) {
				// AudioService-transitioned playback for item after sleep and
				// resume
				mLibVLC.playIndex(savedIndexPosition);
				dontParse = false;
			}
		} else if (savedIndexPosition > -1) {
			AudioServiceController.getInstance().stop(); // Stop the previous
															// playback.
			mLibVLC.setMediaList();
			mLibVLC.playIndex(savedIndexPosition);
		} else if (mLocation != null && mLocation.length() > 0 && !dontParse) {
			AudioServiceController.getInstance().stop(); // Stop the previous
															// playback.
			mLibVLC.setMediaList();
			mLibVLC.getMediaList().add(new Media(mLibVLC, mLocation));
			savedIndexPosition = mLibVLC.getMediaList().size() - 1;
			mLibVLC.playIndex(savedIndexPosition);
		}
		mCanSeek = false;

		if (mLocation != null && mLocation.length() > 0 && !dontParse) {
			// restore last position
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
			Media media = MediaDatabase.getInstance(activity).getMedia(mLocation);
			if (media != null) {
				// in media library
				if (media.getTime() > 0 && fromStart) {
					// 从历史记录开始播放
					mLibVLC.setTime(media.getTime());
				}

				mLastAudioTrack = media.getAudioTrack();
				mLastSpuTrack = media.getSpuTrack();
			} else {
				// not in media library
				long rTime = preferences.getLong(PreferencesActivity.VIDEO_RESUME_TIME, -1);
				SharedPreferences.Editor editor = preferences.edit();
				editor.putLong(PreferencesActivity.VIDEO_RESUME_TIME, -1);
				editor.commit();
				if (rTime > 0)
					mLibVLC.setTime(rTime);

				if (fromStart && intentPosition > 0)
					mLibVLC.setTime(intentPosition);
			}

			String subtitleList_serialized = preferences.getString(PreferencesActivity.VIDEO_SUBTITLE_FILES, null);
			ArrayList<String> prefsList = new ArrayList<String>();
			if (subtitleList_serialized != null) {
				ByteArrayInputStream bis = new ByteArrayInputStream(subtitleList_serialized.getBytes());
				try {
					ObjectInputStream ois = new ObjectInputStream(bis);
					prefsList = (ArrayList<String>) ois.readObject();
				} catch (ClassNotFoundException e) {
				} catch (StreamCorruptedException e) {
				} catch (IOException e) {
				}
			}
			for (String x : prefsList) {
				if (!mSubtitleSelectedFiles.contains(x))
					mSubtitleSelectedFiles.add(x);
			}

			try {
				if (itemTitle != null) {
					title = itemTitle;
				} else {
					title = URLDecoder.decode(mLocation, "UTF-8");
				}
			} catch (UnsupportedEncodingException e) {
			} catch (IllegalArgumentException e) {
			}
			if (title.startsWith("file:")) {
				title = new File(title).getName();
				int dotIndex = title.lastIndexOf('.');
				if (dotIndex != -1)
					title = title.substring(0, dotIndex);
			}
		} else if (itemTitle != null) {
			title = itemTitle;
		}
	}

	@SuppressWarnings("deprecation")
	private int getScreenRotation() {
		WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO /*
																 * Android 2.2
																 * has
																 * getRotation
																 */) {
			try {
				Method m = display.getClass().getDeclaredMethod("getRotation");
				return (Integer) m.invoke(display);
			} catch (Exception e) {
				return Surface.ROTATION_0;
			}
		} else {
			return display.getOrientation();
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private int getScreenOrientation() {
		WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int rot = getScreenRotation();
		/*
		 * Since getRotation() returns the screen's "natural" orientation, which
		 * is not guaranteed to be SCREEN_ORIENTATION_PORTRAIT, we have to
		 * invert the SCREEN_ORIENTATION value if it is "naturally" landscape.
		 */
		@SuppressWarnings("deprecation")
		boolean defaultWide = display.getWidth() > display.getHeight();
		if (rot == Surface.ROTATION_90 || rot == Surface.ROTATION_270)
			defaultWide = !defaultWide;
		if (defaultWide) {
			switch (rot) {
			case Surface.ROTATION_0:
				return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			case Surface.ROTATION_90:
				return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			case Surface.ROTATION_180:
				// SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
				// Level 9+
				return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
						: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			case Surface.ROTATION_270:
				// SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
				// Level 9+
				return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
						: ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			default:
				return 0;
			}
		} else {
			switch (rot) {
			case Surface.ROTATION_0:
				return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			case Surface.ROTATION_90:
				return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			case Surface.ROTATION_180:
				// SCREEN_ORIENTATION_REVERSE_PORTRAIT only available since API
				// Level 9+
				return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
						: ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			case Surface.ROTATION_270:
				// SCREEN_ORIENTATION_REVERSE_LANDSCAPE only available since API
				// Level 9+
				return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ? ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
						: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			default:
				return 0;
			}
		}
	}

	public void showAdvancedOptions(View v) {
		CommonDialogs.advancedOptions(activity, v, MenuType.Video);
	}

	// 缓冲速度显示
	UITimer speedTimer = new UITimer(1000, new OnUITimer() {
		public void onTimer() {
			int speed = DownCenter.getExistingInstance().getPlayTaskSpeed();
			if (speed != 0) {
				String tmpSpeed = SdcardUtil.formatSize(VLCApplication.getAppContext(), speed);
				mSpeedView.setText(tmpSpeed + "/s");
			} else {
				mSpeedView.setText("");
			}

		}
	});

	private void getBuffer(float progress) {
		if (progress < 100) {
			if (bufferlayout.getVisibility() != View.VISIBLE)
				bufferlayout.setVisibility(View.VISIBLE);
			// getString(R.string.buffer_loading)
			StringBuffer speed = new StringBuffer("努力缓冲中...");
			speed.append(progress);
			speed.append("%");
		} else {
			if (bufferlayout.getVisibility() == View.VISIBLE)
				bufferlayout.setVisibility(View.GONE);
		}
	}

	public boolean isFullScreen() {
		return isFullScreen;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void createPresentation()
    {
    	if(mMediaRouter == null || mEnableCloneMode)
    		return;
    	
    	// Get the current route and its presentation display.
    	MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
    	
    	Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;
    	
    	if(presentationDisplay != null)
    	{
    		// Show a new presentation if possible.
    		Log.i(TAG, "Showing presentation on display: " + presentationDisplay);
            mPresentation = new SecondaryDisplay(activity, presentationDisplay);
            mPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mPresentation.show();
                mPresentationDisplayId = presentationDisplay.getDisplayId();
            } catch (WindowManager.InvalidDisplayException ex) {
                Log.w(TAG, "Couldn't show presentation!  Display was removed in "
                        + "the meantime.", ex);
                mPresentation = null;
            }
    	}
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void removePresentation()
    {
    	if(mMediaRouter == null)
    		return;
    	
    	Log.i(TAG, "Dismissing presentation because the current route no longer "
                + "has a presentation display.");
        mLibVLC.stop();
        activity.finish(); //TODO restore the video on the new display instead of closing
        mPresentation.dismiss();
        mPresentation = null;
        mPresentationDisplayId = -1;
    }


	/**
	 * Listens for when presentations are dismissed.
	 */
	private final DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {
		@Override
		public void onDismiss(DialogInterface dialog) {
			if (dialog == mPresentation) {
				Log.i(TAG, "Presentation was dismissed.");
				mPresentation = null;
			}
		}
	};

	private final static class SecondaryDisplay extends Presentation {
		public final static String TAG = "VLC/SecondaryDisplay";

		private Context mContext;
		private SurfaceView mSurface;
		private SurfaceView mSubtitlesSurface;
		private SurfaceHolder mSurfaceHolder;
		private SurfaceHolder mSubtitlesSurfaceHolder;
		private FrameLayout mSurfaceFrame;
		private LibVLC mLibVLC;

		public SecondaryDisplay(Context context, Display display) {
			super(context, display);
			if (context instanceof Activity) {
				setOwnerActivity((Activity) context);
			}
			mContext = context;

			try {
				mLibVLC = Util.getLibVlcInstance();
			} catch (LibVlcException e) {
				Log.d(TAG, "LibVLC initialisation failed");
				return;
			}
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.local_player_remote);

			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);

			mSurface = (SurfaceView) findViewById(R.id.remote_player_surface);
			mSurfaceHolder = mSurface.getHolder();
			mSurfaceFrame = (FrameLayout) findViewById(R.id.remote_player_surface_frame);
			String chroma = pref.getString("chroma_format", "");
			if (LibVlcUtil.isGingerbreadOrLater() && chroma.equals("YV12")) {
				mSurfaceHolder.setFormat(ImageFormat.YV12);
			} else if (chroma.equals("RV16")) {
				mSurfaceHolder.setFormat(PixelFormat.RGB_565);
			} else {
				mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
			}

			// Fragment_VideoPlayer fragment =
			// (Fragment_VideoPlayer)getOwnerActivity();
			PlayerFragment fragment = null;
			if (fragment == null) {
				Log.e(TAG, "Failed to get the VideoPlayerActivity instance, secondary display won't work");
				return;
			}

			mSurfaceHolder.addCallback(fragment.mSurfaceCallback);

			mSubtitlesSurface = (SurfaceView) findViewById(R.id.remote_subtitles_surface);
			mSubtitlesSurfaceHolder = mSubtitlesSurface.getHolder();
			mSubtitlesSurfaceHolder.setFormat(PixelFormat.RGBA_8888);
			mSubtitlesSurface.setZOrderMediaOverlay(true);
			mSubtitlesSurfaceHolder.addCallback(fragment.mSubtitlesSurfaceCallback);

			/*
			 * Only show the subtitles surface when using "Full Acceleration"
			 * mode
			 */
			if (mLibVLC != null && mLibVLC.getHardwareAcceleration() == 2)
				mSubtitlesSurface.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 点击全屏按钮操作
	 * 
	 * @author qiny
	 * 
	 */
	public interface FullScreenCallBacks {

		public void fullScreenPlay(boolean isFullScreen);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.player_back:
			isFullScreen = false;
			headerLayout.setVisibility(View.GONE);
			sourceLayout.setVisibility(View.GONE);
			selectLayout.setVisibility(View.GONE);
			if (fullScreenCallBacks != null) {
				fullScreenCallBacks.fullScreenPlay(isFullScreen);
			}
			changeSurfaceSize();
			break;
		case R.id.player_changesourcelayout:// 切换来源
			if (sourceLayout.getVisibility() != View.VISIBLE) {
				sourceLayout.setVisibility(View.VISIBLE);
				selectLayout.setVisibility(View.GONE);
			} else {
				sourceLayout.setVisibility(View.GONE);
			}
			break;
		case R.id.player_selectbtn: // 选集
			if (selectLayout.getVisibility() != View.VISIBLE) {
				selectLayout.setVisibility(View.VISIBLE);
				sourceLayout.setVisibility(View.GONE);
			} else {
				selectLayout.setVisibility(View.GONE);
			}
			break;
		case R.id.lock_overlay_button:// 锁屏
			if (!mIsLocked) {
				mIsLocked = true;
				lockScreen();
			} else {
				mIsLocked = false;
				unlockScreen();
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg0 == sourceListView) {
			// ........................切换来源
			SourceBean sourceBean = playerSourceAdapter.getList().get(arg2);
			if (sourceKey.equals(sourceBean.getKey())) {
				ToastUtil.ToastShort(getActivity(), "已是当前来源");
				return;
			}
			sourceKey = sourceBean.getKey();
			downInfo.setDownloadSourceTag(sourceKey);
			playerSourceAdapter.setCurrentSourceKey(sourceKey);
			playerSourceAdapter.notifyDataSetChanged();
			List<DownLoadInfo> willPlayList = movieInfo.getMovieDownUrlmap().get(sourceKey);
			if (currentList == null || willPlayList == null) {
				return;
			}
			sourceIconImageView.setBackgroundResource(sourceBean.getValues());
			sourceKeyTextView.setText(sourceKey);
			if (currentList.size() != willPlayList.size()) {
				// 不同来源剧集不一致情况....
				sourceLayout.setVisibility(View.GONE);
				selectLayout.setVisibility(View.VISIBLE);
				currentList = willPlayList;
				// playerGridAdapter.setSelectAddress("");
				playerGridAdapter.setSelectPosition(-1);
				playerGridAdapter.setList(willPlayList);
				playerGridAdapter.notifyDataSetChanged();
			} else {// 不同来源 集数一致 直接播放
				currentList = willPlayList;
				playerGridAdapter.setList(willPlayList);
				playerGridAdapter.notifyDataSetChanged();
				DownLoadInfo tmpDownLoadInfo = willPlayList.get(downInfo.getDownloadPosition());
				downInfo.setDownAddress(tmpDownLoadInfo.getDownAddress());
				downInfo.setDownloadSourceTag(sourceKey);
				playMovie(downInfo);
			}
			return;
		}
		// 选集
		if (arg0 == gridView) {
			if (playerGridAdapter == null || playerGridAdapter.getList() == null) {
				return;
			}
			// int position = playerGridAdapter.getList().size() - arg2;
			// if (position - 1 < 0)
			// return;
			int position = arg2;
			DownLoadInfo downLoadInfo = playerGridAdapter.getList().get(position);
			if (!(playerGridAdapter.getSelectAddress().equals(downLoadInfo.getDownAddress()))) {
				playerGridAdapter.setSelectAddress(downLoadInfo.getDownAddress());
				playerGridAdapter.notifyDataSetChanged();
				downInfo.setDownAddress(downLoadInfo.getDownAddress());
				downInfo.setDownloadSourceTag(sourceKey);
				downInfo.setDownloadPosition(position);
				playMovie(downInfo);
			}
		}

	}

	public void playMovie(DownLoadInfo downLoadInfo) {
		if (downLoadInfo == null || TextUtils.isEmpty(downLoadInfo.getDownAddress()))
			return;
		HistoryBean historyBean = new HistoryBean();
		historyBean.setMovieId(downLoadInfo.getDownloadID());
		historyBean.setMovieName(downLoadInfo.getDownloadName());
		historyBean.setSourceTag(sourceKey);
		historyBean.setWatchedDate(CommonUtil.getCurrentDate());
		historyBean.setMovieUrl(downLoadInfo.getDownAddress());
		downLoadInfo.setDownloadSourceTag(sourceKey);
		if (dbHelperDao.isFirstToPlayHistoryTable(movieInfo.getMovieID())) {
			dbHelperDao.insertToPlayHistoryTable(historyBean);
		} else {
			dbHelperDao.updatePlayHistoryName(movieInfo.getMovieID(), historyBean);
		}
		if (CommonUtil.isPolySource(downLoadInfo.getDownAddress())) {
			ToastUtil.ToastShort(getActivity(), "聚合资源");
			Intent intent = new Intent();
			intent.setClass(getActivity(), HtmlPlayer.class);
			intent.putExtra("playurl", downLoadInfo.getDownAddress());
			getActivity().startActivity(intent);
			return;
		}
		if (getActivity() == null)
			return;
		FragmentManager fragmentManager = ((FragmentActivity) getActivity()).getSupportFragmentManager();
		FragmentTransaction ftTransaction = fragmentManager.beginTransaction();
		PlayerFragment playerFragment = new PlayerFragment();
		Bundle bundle = new Bundle();
		if (movieInfo != null)
			bundle.putSerializable("movieinfo", movieInfo);
		bundle.putSerializable("downloadinfo", downLoadInfo);
		bundle.putBoolean("isFullScreen", isFullScreen);
		playerFragment.setArguments(bundle);
		ftTransaction.replace(R.id.moviedetial_frame_player, playerFragment);
		ftTransaction.commit();
	}

	public boolean ismIsLocked() {
		return mIsLocked;
	}
}
