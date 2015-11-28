package cn.pipi.mobile.pipiplayer.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import cn.pipi.mobile.pipiplayer.MainActivity;
import cn.pipi.mobile.pipiplayer.adapter.AdPageAdapter;
import cn.pipi.mobile.pipiplayer.adapter.HomePagerListAdapter;
import cn.pipi.mobile.pipiplayer.adapter.HomePagerListAdapter.selectMoreInterFace;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.config.MessageMark;
import cn.pipi.mobile.pipiplayer.db.PIPISharedPreferences;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.JsonUtil;
import cn.pipi.mobile.pipiplayer.util.ToastUtil;
import cn.pipi.mobile.pipiplayer.view.MyListView;
import cn.pipi.mobile.pipiplayer.view.PullToRefreshView;
import cn.pipi.mobile.pipiplayer.view.PullToRefreshView.OnFooterRefreshListener;
import cn.pipi.mobile.pipiplayer.view.PullToRefreshView.OnHeaderRefreshListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class HomeFragment extends BaseFragment implements selectMoreInterFace, OnHeaderRefreshListener,
		OnFooterRefreshListener, OnTouchListener {

	private View view;
	private PullToRefreshView homePullToRefreshView;
	private MyListView listView;
	private HomePagerListAdapter homePagerListAdapter;
	private ViewPager mPager;
	private LinearLayout loadingLayout;
	private LoadMoreInterFace loadMoreInterFace;
	private boolean isLoading = false;
	private MainActivity mainActivity;
	private Context mContext;
	float x = 0;
	float y = 0;
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	private RelativeLayout mScroll;
    private int adHeight;

	public HomeFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
		try {
			loadMoreInterFace = (LoadMoreInterFace) activity;
			mainActivity = (MainActivity) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement LoadMoreInterFace");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.homepager, container, false);
		mScroll = (RelativeLayout) view.findViewById(R.id.main_ad_scroll);
		int width = (int) (AppConfig.currentScreenWidth * 0.9);
		mScroll.getLayoutParams().width = width;
        adHeight = (int) (width * 0.378);
		mScroll.getLayoutParams().height = adHeight;
		widgetInit();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getServerData();
		options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.img_default_big)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.img_default_big) // 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565).build();// 构建完成
		mImageLoader = ImageLoader.getInstance();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void widgetInit() {
		if (view == null)
			return;
		homePullToRefreshView = (PullToRefreshView) view.findViewById(R.id.home_pulltorefreshview);
		homePullToRefreshView.setOnHeaderRefreshListener(this);
		homePullToRefreshView.setOnFooterRefreshListener(this);
		loadingLayout = (LinearLayout) view.findViewById(R.id.loadinglayout);
		listView = (MyListView) view.findViewById(R.id.homepager_listview);
		listView.setOnTouchListener(this);
	}

	private void getServerData() {
		Log.d(AppConfig.Tag, "首页-----getServerData");
		isLoading = true;
		loadingLayout.setVisibility(View.VISIBLE);
		new GetHomeData().start();
	}

	private class GetHomeData extends Thread {
		@Override
		public void run() {
			super.run();
			if (CommonUtil.isNetworkConnect(mContext)) {
				Map<String, List<MovieInfo>> result = JsonUtil.getHomePagerData();
				if (result != null && result.keySet().size() != 0) {
					CommonUtil.sendMessage(MessageMark.OKAY, handler, result);
				} else {
					CommonUtil.sendMessage(MessageMark.NODATA, handler, null);
				}
			} else {
				CommonUtil.sendMessage(MessageMark.NETTYPE_NONETWORK, handler, JsonUtil.getHomePageDataByCache());
			}
		}
	}

	public void setScroll(final List<MovieInfo> list) {
		if (list == null || list.size() <= 0)
			return;
		Message msg = handler.obtainMessage();
		mPager = (ViewPager) mScroll.findViewById(R.id.main_view_pager);
		final RadioGroup mGroup = (RadioGroup) mScroll.findViewById(R.id.main_radio_group);
		List<View> mList = new ArrayList<View>();
		if (mGroup.getChildCount() > 0)
			mGroup.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			ImageView img = new ImageView(mContext);
			img.setImageResource(R.drawable.img_default_big);
			img.setScaleType(ScaleType.FIT_XY);
			mImageLoader.displayImage(list.get(i).getMovieImgPath(), img, options);
			final int t = i;
			img.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CommonUtil.toMovieDetialActivity(mContext, list.get(t).getMovieID(), list.get(t).getMovieName());
				}
			});
			mList.add(img);
			RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(15, 15);
			lp.setMargins(0, 0, 5, 0);
			RadioButton b = new RadioButton(mContext);
			b.setButtonDrawable(android.R.color.transparent);
			b.setBackgroundResource(R.drawable.ad_radio_bg);
			b.setLayoutParams(lp);
			mGroup.addView(b);
			if (i == 0) {
				((RadioButton) mGroup.getChildAt(0)).setChecked(true);
			}
		}
		mPager.setAdapter(new AdPageAdapter(mList));
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				RadioButton button = (RadioButton) mGroup.getChildAt(arg0 % list.size());
				button.setChecked(true);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
		});
		mScroll.setVisibility(View.VISIBLE);
		handler.removeMessages(MessageMark.SCROLL);
		handler.sendEmptyMessageDelayed(MessageMark.SCROLL, 6000);
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			isLoading = false;
			loadingLayout.setVisibility(View.GONE);
			switch (msg.what) {
			case MessageMark.OKAY:
				// System.out.println("-----初始化完成----");
				Map<String, List<MovieInfo>> tmp = (Map<String, List<MovieInfo>>) msg.obj;
				setScroll(tmp.get("scroll"));
				homePagerListAdapter = new HomePagerListAdapter(HomeFragment.this);
				homePagerListAdapter.setMap(tmp);
				listView.setAdapter(homePagerListAdapter);
				break;
			case MessageMark.NETTYPE_NONETWORK:
				ToastUtil.ToastShort(mContext, "无网络");
				Map<String, List<MovieInfo>> temp = (Map<String, List<MovieInfo>>) msg.obj;
				if (temp == null)
					return;
				setScroll(temp.get("scroll"));
				homePagerListAdapter = new HomePagerListAdapter(HomeFragment.this);
				homePagerListAdapter.setMap(temp);
				listView.setAdapter(homePagerListAdapter);
				break;
			case MessageMark.NODATA:
				ToastUtil.ToastShort(mContext, "无数据");
				break;
			case MessageMark.SCROLL:
				if (mPager == null)
					return;
				int i = mPager.getCurrentItem();
				mPager.setCurrentItem(i == Integer.MAX_VALUE ? 0 : i + 1);
                handler.sendEmptyMessageDelayed(MessageMark.SCROLL, 3000);
				break;
			}
		};
	};

	public interface LoadMoreInterFace {
		public void loadMore(int position);
	}

	@Override
	public void selectMore(int position) {

		if (loadMoreInterFace != null) {
			loadMoreInterFace.loadMore(position);
		}

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		if (isLoading) {
			homePullToRefreshView.postDelayed(new Runnable() {
				public void run() {
					homePullToRefreshView.onHeaderRefreshComplete();
				}
			}, 300);
			if (getActivity() != null)
				// Toast.ToastShort(getActivity(), "正在加载中...");
				return;
		}
		getServerData();
		homePullToRefreshView.postDelayed(new Runnable() {
			public void run() {
				homePullToRefreshView.onHeaderRefreshComplete();
			}
		}, 300);
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		homePullToRefreshView.postDelayed(new Runnable() {
			public void run() {
				homePullToRefreshView.onFooterRefreshComplete();
			}
		}, 300);
	}

	public HomePagerListAdapter getHomePagerListAdapter() {
		return this.homePagerListAdapter;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (x == 0 && y == 0) {
			x = event.getX();
			y = event.getY();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (event.getX() - x > 30 && Math.abs(event.getY() - y) < 30&& event.getY()>adHeight) {
				mainActivity.showOrHideMenu();
			}
			x = 0;
			y = 0;
		}
		return false;
	}
}
