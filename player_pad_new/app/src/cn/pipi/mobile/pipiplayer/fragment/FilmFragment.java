package cn.pipi.mobile.pipiplayer.fragment;

import java.util.List;
import java.util.Map;

import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import cn.pipi.mobile.pipiplayer.AdDetailActivity;
import cn.pipi.mobile.pipiplayer.MainActivity;
import cn.pipi.mobile.pipiplayer.adapter.FilmHorizonalAdapter;
import cn.pipi.mobile.pipiplayer.adapter.HomePagerAdapter;
import cn.pipi.mobile.pipiplayer.adapter.MoreMenuListAdapter;
import cn.pipi.mobile.pipiplayer.async.ClassifyAsync;
import cn.pipi.mobile.pipiplayer.async.GetAdData;
import cn.pipi.mobile.pipiplayer.async.ItemClassifyAsync;
import cn.pipi.mobile.pipiplayer.bean.AdInfo;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.bean.TypesBean;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.config.MessageMark;
import cn.pipi.mobile.pipiplayer.fragment.HomeFragment.LoadMoreInterFace;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.MainClassifyRequestManager;
import cn.pipi.mobile.pipiplayer.util.ToastUtil;
import cn.pipi.mobile.pipiplayer.util.TypesManager;
import cn.pipi.mobile.pipiplayer.view.HorizontalListView;
import cn.pipi.mobile.pipiplayer.view.PullToRefreshView;
import cn.pipi.mobile.pipiplayer.view.PullToRefreshView.OnFooterRefreshListener;
import cn.pipi.mobile.pipiplayer.view.PullToRefreshView.OnHeaderRefreshListener;

/**
 * 电影页面
 * 
 * @author qiny
 * 
 */
@SuppressLint("HandlerLeak")
public class FilmFragment extends BaseFragment implements OnHeaderRefreshListener, OnFooterRefreshListener,
		OnItemClickListener, OnClickListener, MoreMenuListAdapter.TestInterFace, OnCheckedChangeListener,
		OnTouchListener {

	private View view;

	private LinearLayout loadingLayout;

	private RelativeLayout moreLayout;

	// 点击更多 展示布局
	private LinearLayout moreMenuLayout;

	private HorizontalListView horizontalListView;

	private FilmHorizonalAdapter filmHorizonalAdapter;

	private GridView gridView;

	private PullToRefreshView mPullToRefreshView;

	// 首页与分类共享同一适配器
	private HomePagerAdapter filmAdapter;

	private MainClassifyRequestManager mainClassifyRequestManager;

	private Map<String, List<TypesBean>> map;

	private String requestUrl = "";

	private boolean isLoading = false;

	private int startPager = 1;

	// 是否分类
	private boolean isClassify = false;

	// 显示或隐藏more菜单
	private boolean isMoreMenuShow = false;

	private ListView moreMenuListView;

	private MoreMenuListAdapter menuListAdapter;

	private RadioGroup moreMenuRadioGroup;

	private final int ASYNC_LOAD_HORIZONALLISTVIEW = 89;

	private final int ASYNC_FOOT_REFRESH = 91;

	private MainActivity mainActivity;

	private float x = 0, y = 0;

	public FilmFragment() {

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mainActivity = (MainActivity) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement LoadMoreInterFace");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// return super.onCreateView(inflater, container, savedInstanceState);
		view = inflater.inflate(R.layout.film, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		widgetInit();
		horizonlistviewInit();
		requestUrlInit();
	}

	@Override
	public void widgetInit() {
		if (view == null)
			return;
		loadingLayout = (LinearLayout) view.findViewById(R.id.loadinglayout);
		moreLayout = (RelativeLayout) view.findViewById(R.id.film_more);
		moreMenuLayout = (LinearLayout) view.findViewById(R.id.film_moremenu);
		moreMenuLayout.setOnClickListener(this);
		moreLayout.setOnClickListener(this);
		moreMenuRadioGroup = (RadioGroup) view.findViewById(R.id.film_moremenurgroup);
		moreMenuRadioGroup.setOnCheckedChangeListener(this);
		moreMenuListView = (ListView) view.findViewById(R.id.film_moremenulist);
		horizontalListView = (HorizontalListView) view.findViewById(R.id.film_horizonlistview);
		horizontalListView.setOnItemClickListener(this);
		mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.film_pulltorefreshview);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		gridView = (GridView) view.findViewById(R.id.film_gridview);
		gridView.setOnItemClickListener(this);
		gridView.setOnTouchListener(this);
	}

	private void horizonlistviewInit() {
		isLoading = true;
		mainClassifyRequestManager = new MainClassifyRequestManager();
		mainClassifyRequestManager.setTagName(AppConfig.mainClassifyNames[0]);
		requestUrl = mainClassifyRequestManager.requestUrl(0);
		filmHorizonalAdapter = new FilmHorizonalAdapter(getActivity());
		if (CommonUtil.isMainClassifyMapContainsKey(TypesManager.mainTypes[0])) {
			map = AppConfig.classifyMap.get(TypesManager.mainTypes[0]);
			List<TypesBean> list = map.get(TypesManager.tags[3]);
			// TypesBean typesBean = new TypesBean();
			// typesBean.setTypesID("0000");
			// typesBean.setTypesName("全部");
			// list.add(0, typesBean);
			filmHorizonalAdapter.setList(list);
			horizontalListView.setAdapter(filmHorizonalAdapter);
			menuListAdapter = new MoreMenuListAdapter(this);
			menuListAdapter.setMainClassifyRequestManager(mainClassifyRequestManager);
			menuListAdapter.setMap(map);
			menuListAdapter.setHandler(handler);
			moreMenuListView.setAdapter(menuListAdapter);
		} else {
			Log.d(AppConfig.Tag, "" + TypesManager.mainTypes[0] + "分类获取为空!");
		}
	}

	public void requestUrlInit() {
		isLoading = true;
		requestUrl = mainClassifyRequestManager.requestUrl(0);
		ClassifyAsync classifyAsync = new ClassifyAsync(mainClassifyRequestManager);
		classifyAsync.setContext(getActivity());
		classifyAsync.setLoadingView(loadingLayout);
		classifyAsync.setHandler(handler);
		classifyAsync.execute(requestUrl);
	}

	private void showOrHideMoreMenu() {
		if (moreMenuLayout == null)
			return;
		if (!isMoreMenuShow) {
			moreMenuLayout.setVisibility(View.VISIBLE);
			isMoreMenuShow = true;
		} else {
			moreMenuLayout.setVisibility(View.GONE);
			isMoreMenuShow = false;
		}
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {
			// @Override
			public void run() {
				mPullToRefreshView.onHeaderRefreshComplete("");
			}
		}, 500);
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		if (!isClassify) {
			requestUrl = mainClassifyRequestManager.requestUrl(++startPager);
			ClassifyAsync classifyAsync = new ClassifyAsync(ASYNC_FOOT_REFRESH, mainClassifyRequestManager);
			classifyAsync.setContext(getActivity());
			classifyAsync.setLoadingView(loadingLayout);
			classifyAsync.setHandler(handler);
			classifyAsync.execute(requestUrl);
		}
		mPullToRefreshView.postDelayed(new Runnable() {
			public void run() {
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}, 500);
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message message) {
			isLoading = false;// 更改正在加载状态
			switch (message.what) {
			case MessageMark.OKAY:
				filmAdapter = new HomePagerAdapter(getActivity());
				filmAdapter.setList((List<MovieInfo>) message.obj);
				gridView.setAdapter(filmAdapter);
				new GetAdData(this).start();
				// filmAdapter.notifyDataSetChanged();
				break;
			case ASYNC_FOOT_REFRESH: // 下拉加载更多操作
				if (filmAdapter.getList() != null && filmAdapter.getList().size() != 0) {
					filmAdapter.getList().addAll((List<MovieInfo>) message.obj);
				} else {
					filmAdapter.setList((List<MovieInfo>) message.obj);
				}
				break;
			case ASYNC_LOAD_HORIZONALLISTVIEW:// 水平listview
				filmAdapter.setList((List<MovieInfo>) message.obj);
				filmAdapter.notifyDataSetChanged();
				break;
			case MessageMark.NETTYPE_NONETWORK:
				break;
			case MessageMark.NODATA:
				ToastUtil.ToastShort(getActivity(), "+ - +  无数据  + - +");
				break;
			case MessageMark.ADD_AD:
				filmAdapter.addAd((List<AdInfo>) message.obj);
				filmAdapter.notifyDataSetChanged();
				break;
			}
		};
	};

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.film_more:
			showOrHideMoreMenu();
			break;

		case R.id.film_moremenu:
			isMoreMenuShow = true;
			showOrHideMoreMenu();
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (arg0 == horizontalListView) {
			if (getActivity() != null) {
				MobclickAgent.onEvent(getActivity(), "Click_Movie_Page");
			}
			// 隐藏更多菜单
			isMoreMenuShow = true;
			showOrHideMoreMenu();
			// 重置条件
			mainClassifyRequestManager.resetRequestCondition();
			moreMenuRadioGroup.check(R.id.moremenu_hotest);
			menuListAdapter.clearSelectIndex(true);
			filmHorizonalAdapter.changeTextViewBg(position);
			if (position != 0) {
				isClassify = true;
				ItemClassifyAsync itemClassifyAsync = new ItemClassifyAsync();
				itemClassifyAsync.setContext(getActivity());
				itemClassifyAsync.setHandler(handler);
				itemClassifyAsync.setMessageMark(ASYNC_LOAD_HORIZONALLISTVIEW);
				itemClassifyAsync.setLoadingView(loadingLayout);
				itemClassifyAsync.execute(filmHorizonalAdapter.getList().get(position).getTypesID());
			} else {// 全部
				isClassify = false;
				ClassifyAsync temp = new ClassifyAsync(mainClassifyRequestManager);
				temp.setContext(getActivity());
				temp.setLoadingView(loadingLayout);
				temp.setHandler(handler);
				temp.execute(mainClassifyRequestManager.requestUrl(0));
			}
		}
		if (arg0 == gridView) {
			MovieInfo movieInfo = filmAdapter.getList().get(position);
			if (movieInfo.isAd()) {
				AdInfo info = (AdInfo) movieInfo;
				Intent intent = new Intent(mContext, AdDetailActivity.class);
				intent.putExtra("name", info.getAppName());
				intent.putExtra("url", info.getHomePageUrl());
				mContext.startActivity(intent);
			} else {
				CommonUtil.toMovieDetialActivity(getActivity(), movieInfo.getMovieID(), movieInfo.getMovieName());
			}
		}

	}

	@Override
	public void doSomething() {
		filmHorizonalAdapter.setSelectIndex(0);
		filmHorizonalAdapter.notifyDataSetChanged();
		isMoreMenuShow = true;
		// showOrHideMoreMenu();
		menuListAdapter.clearSelectIndex(false);
		isClassify = false;
		startPager = 1;
		String requestUrl = mainClassifyRequestManager.requestUrl(0);
		ClassifyAsync classifyAsync = new ClassifyAsync(mainClassifyRequestManager);
		classifyAsync.setContext(getActivity());
		classifyAsync.setLoadingView(loadingLayout);
		classifyAsync.setHandler(handler);
		classifyAsync.execute(requestUrl);

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		menuListAdapter.clearSelectIndex(false);
		changeRadioBtnTextColor(moreMenuRadioGroup, checkedId);
		switch (checkedId) {
		case R.id.moremenu_newest:
			mainClassifyRequestManager.setOrder("1");
			break;
		case R.id.moremenu_hotest:
			mainClassifyRequestManager.setOrder("2");
			break;
		case R.id.moremenu_grade:
			mainClassifyRequestManager.setOrder("3");
			break;
		}
		isClassify = true;
		String requestUrl = mainClassifyRequestManager.requestUrl(0);
		ClassifyAsync classifyAsync = new ClassifyAsync(mainClassifyRequestManager);
		classifyAsync.setContext(getActivity());
		classifyAsync.setLoadingView(loadingLayout);
		classifyAsync.setHandler(handler);
		classifyAsync.execute(requestUrl);
	}

	private void changeRadioBtnTextColor(RadioGroup radioGroup, int checkedId) {
		for (int i = 0; i < radioGroup.getChildCount(); i++) {
			RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
			if (radioButton.getId() != checkedId) {
				radioButton.setTextColor(getResources().getColor(R.color.black));
			} else {
				radioButton.setTextColor(getResources().getColor(R.color.white));
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (x == 0 && y == 0) {
			x = event.getX();
			y = event.getY();
		}
		if (event.getX() - x > 50 && Math.abs(event.getY() - y) < 50) {
			mainActivity.showOrHideMenu();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			x = 0;
			y = 0;
		}
		return false;
	}
}
