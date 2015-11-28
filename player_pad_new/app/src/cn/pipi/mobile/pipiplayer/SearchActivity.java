package cn.pipi.mobile.pipiplayer;

import java.util.ArrayList;
import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.pipi.mobile.pipiplayer.adapter.HorizonListAdapter;
import cn.pipi.mobile.pipiplayer.adapter.SearchDropListViewAdapter;
import cn.pipi.mobile.pipiplayer.adapter.SearchGridAdapter;
import cn.pipi.mobile.pipiplayer.async.AutoSearchAsync;
import cn.pipi.mobile.pipiplayer.async.GetAdData;
import cn.pipi.mobile.pipiplayer.async.HotSearchAsync;
import cn.pipi.mobile.pipiplayer.bean.AdInfo;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.config.MessageMark;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.SearchResultManager;
import cn.pipi.mobile.pipiplayer.util.ToastUtil;
import cn.pipi.mobile.pipiplayer.util.XMLPullParseUtil;
import cn.pipi.mobile.pipiplayer.view.FlowLayout;
import cn.pipi.mobile.pipiplayer.view.HorizontalListView;

/**
 * 影片搜索
 * 
 * @author qiny
 * 
 */
public class SearchActivity extends Activity implements
		FlowLayout.FlowTextViewOnclick, OnClickListener,
		OnEditorActionListener, OnItemClickListener, TextWatcher {

	private final  String mPageName = "pipiplayerPad_searchactivity";
	
	private final int GET_FILTER_GRIDVIEW_DATA = 13;
	
	private final int AUTO_SEARCH=16;

	private FlowLayout hotFlowLayout;

	private FlowLayout historyFlowLayout;

	private EditText searchEditView;

	private HorizontalListView horizontalListView;

	private HorizonListAdapter horizonListAdapter;

	private GridView gridView;

	private SearchGridAdapter searchGridAdapter;

	private LinearLayout hotHistoryLayout;

	private LinearLayout resultLayout;

	private Button searchBtn;

	private boolean isSearching = false;

	// 当前页面是否是搜索结果页面
	private boolean isFirstPager = true;

	private List<MovieInfo> list; // gridview 数据

	private InputMethodManager imm;

	private ListView searchDropListView;

	private SearchDropListViewAdapter searchDropListViewAdapter;
	
	private boolean isDroplistSearching=false;
	
	private int itemWidth;
	private int itemHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		widgetInit();
	}
	


	private void widgetInit() {
		list = new ArrayList<MovieInfo>();
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		Button backBtn = (Button) this.findViewById(R.id.searchbar_back);
		Button clearHistoryBtn = (Button) this
				.findViewById(R.id.search_clearhistorybtn);
		searchEditView = (EditText) this.findViewById(R.id.search_editview);
		searchEditView.addTextChangedListener(this);
		searchEditView.setOnEditorActionListener(this);
		searchBtn = (Button) this.findViewById(R.id.searchbar_searchbtn);
		searchBtn.setOnClickListener(this);
		searchDropListView = (ListView) this.findViewById(R.id.search_listview);
		searchDropListViewAdapter = new SearchDropListViewAdapter(this);
		searchDropListView.setAdapter(searchDropListViewAdapter);
		searchDropListView.setOnItemClickListener(this);
		hotFlowLayout = (FlowLayout) this
				.findViewById(R.id.search_hotflowlayout);
		hotFlowLayout.setType(FlowLayout.FlowLayout_SEARCH);
		historyFlowLayout = (FlowLayout) this
				.findViewById(R.id.search_historyflowlayout);
		historyFlowLayout.setType(FlowLayout.FlowLayout_HISTORY);
		horizontalListView = (HorizontalListView) this
				.findViewById(R.id.search_horizonlistview);
		horizonListAdapter = new HorizonListAdapter(this);
		horizontalListView.setAdapter(horizonListAdapter);
		horizontalListView.setOnItemClickListener(this);
		gridView = (GridView) this.findViewById(R.id.search_gridview);
		searchGridAdapter = new SearchGridAdapter(this);
		gridView.setAdapter(searchGridAdapter);
		gridView.setOnItemClickListener(this);
		hotHistoryLayout = (LinearLayout) this
				.findViewById(R.id.hot_history_layout);
		resultLayout = (LinearLayout) this
				.findViewById(R.id.search_resultlayout);
		backBtn.setOnClickListener(this);
		clearHistoryBtn.setOnClickListener(this);
		getSearchHistoryList();
	}

	private void getSearchHistoryList() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<String> list = DBHelperDao.getDBHelperDaoInstace(
						SearchActivity.this).getSearchHistoryList();
				CommonUtil.sendMessage(MessageMark.GET_SEARCHHISTORY, handler,
						list);
			}
		}).start();
	}

	private void clearSearchHistory() {
		if (historyFlowLayout.isEmpty())
			return;
		new Thread(new Runnable() {

			@Override
			public void run() {
				DBHelperDao.getDBHelperDaoInstace(SearchActivity.this)
						.clearSearchHistory();

			}
		}).start();
		historyFlowLayout.removeAllViews();
	}

	public void search(final String movieName) {
		if (movieName == null || movieName.trim().equals("")) {
			ToastUtil.ToastShort(this, "搜索条件不能为空");
			return;
		}
		MobclickAgent.onEvent(this, "SearchVideo",movieName);
		if (isSearching) {
			ToastUtil.ToastShort(this, "正在搜索中");
			return;
		}
		isSearching = true;
		DBHelperDao.getDBHelperDaoInstace(this).insertToSearchHistoryTable(
				movieName);
		getSearchHistoryList();
		new Thread(new Runnable() {
			@Override
			public void run() {
				SearchResultManager searchResultManager = XMLPullParseUtil
						.getSearchResult(movieName);
				CommonUtil.sendMessage(MessageMark.GET_SEARCH_RESULT, handler,
						searchResultManager);
			}
		}).start();
	}

	private void changePager(boolean isShow) {
		// 默认isFirstPager=true 为第一个页面
		this.isFirstPager = isShow;
		if (isFirstPager) {
			hotHistoryLayout.setVisibility(View.VISIBLE);
			resultLayout.setVisibility(View.GONE);
		} else {
			hotHistoryLayout.setVisibility(View.GONE);
			resultLayout.setVisibility(View.VISIBLE);
		}
	}

	private void updataGridview(final List<MovieInfo> list) {
		new Thread(new Runnable() {

			@Override
			public void run() {

				CommonUtil.sendMessage(MessageMark.UPDATA_SEARCH_GRIDVIEW,
						handler, list);
			}
		}).start();
	}

	public void filterMovieList(final String name) {
		if (searchGridAdapter == null
				|| searchGridAdapter.getList().size() == 0) {
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<MovieInfo> tmplist = new ArrayList<MovieInfo>();
				if (!name.equals("全部"))
					for (MovieInfo movieInfo : list) {
						if(movieInfo.getMovieType()==null)continue;
						if (movieInfo.getMovieType().equals(name)) {
							tmplist.add(movieInfo);
						}
					}
				if (!name.equals("全部")) {
					CommonUtil.sendMessage(GET_FILTER_GRIDVIEW_DATA, handler,
							tmplist);
				} else {
					CommonUtil.sendMessage(GET_FILTER_GRIDVIEW_DATA, handler,
							list);
				}

			}
		}).start();

	}

	private void hideSoftKeyboard() {
		View view = this.getWindow().peekDecorView();
		if (imm != null && view != null)
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (hotFlowLayout.isEmpty()) {
			HotSearchAsync hotSearchAsync = new HotSearchAsync(this);
			hotSearchAsync.setHandler(handler);
			hotSearchAsync.execute(AppConfig.HOT_SEARCH_URL);
		}
		MobclickAgent.onPageStart( mPageName );
		MobclickAgent.onResume(this);
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageStart( mPageName );
		MobclickAgent.onPause(this);
		
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(searchDropListView.getVisibility()==View.VISIBLE){
			searchDropListView.setVisibility(View.GONE);
			return true;
		}
		return super.onTouchEvent(event);
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageMark.NETTYPE_NONETWORK:
				ToastUtil.ToastShort(SearchActivity.this, "无网络!");
				break;
			case MessageMark.GET_HOTSEARCH:
				hotFlowLayout.addChildView((List<String>) msg.obj);
				break;
			case MessageMark.GET_SEARCHHISTORY:
				historyFlowLayout.addChildView((List<String>) msg.obj);
				break;
			case MessageMark.GET_SEARCH_RESULT:
				isSearching = false;
				SearchResultManager searchResultManager = (SearchResultManager) msg.obj;
				if (searchResultManager.getList() == null
						|| searchResultManager.getList().size() == 0) {
					ToastUtil.ToastShort(SearchActivity.this, "无搜索结果!");
					return;
				}
				updataGridview(searchResultManager.getList());
				if (searchResultManager.getTypeSet().size() > 1) {
					horizontalListView.setVisibility(View.VISIBLE);
					ArrayList<String> tmpList = new ArrayList<String>();
					tmpList.add("全部");
					tmpList.addAll(new ArrayList<String>(searchResultManager
							.getTypeSet()));
					horizonListAdapter.setList(tmpList);
					horizonListAdapter.setMainList(searchResultManager
							.getList());
					horizonListAdapter.notifyDataSetChanged();
				} else {
					horizontalListView.setVisibility(View.GONE);
				}
				changePager(false);
				Log.d(AppConfig.Tag, "searchResultManager.getList():"
						+ searchResultManager.getList().size());
				break;
			case MessageMark.UPDATA_SEARCH_GRIDVIEW:
				list = (List<MovieInfo>) msg.obj;
				searchGridAdapter.setList(list);
				searchGridAdapter.notifyDataSetChanged();
				new GetAdData(this).start();
				break;
			case GET_FILTER_GRIDVIEW_DATA: // 全部 电影 根据名字(type)过滤后结果
				List<MovieInfo> list = (List<MovieInfo>) msg.obj;
				searchGridAdapter.setList(list);
				searchGridAdapter.notifyDataSetChanged();
				break;
			case AUTO_SEARCH:
				isDroplistSearching=false;
				List<String> autolist=(List<String>) msg.obj;
				System.out.println("AUTO_SEARCH:"+autolist.size());
				if(autolist!=null&&autolist.size()!=0){
					searchDropListView.setVisibility(View.VISIBLE);
					searchDropListViewAdapter.setList(autolist);
					searchDropListViewAdapter.notifyDataSetChanged();
				}else{
					searchDropListView.setVisibility(View.GONE);
				}
				break;
			case MessageMark.ADD_AD:
				searchGridAdapter.addAd((List<AdInfo>)msg.obj);
				searchGridAdapter.notifyDataSetChanged();
				break;
			}
		};
	};

	/**
	 * flowlayout 接口 响应textview 点击事件
	 */
	@Override
	public void searchFilmFromName(int type, String name) {
		Log.d(AppConfig.Tag, type + "  " + name);
		DBHelperDao.getDBHelperDaoInstace(this)
				.insertToSearchHistoryTable(name);
		getSearchHistoryList();
		search(name);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.searchbar_back:
			if (!isFirstPager) {
				changePager(true);
				return;
			}
			finish();
			break;
		case R.id.search_clearhistorybtn: // 清空播放历史
			clearSearchHistory();
			break;
		case R.id.searchbar_searchbtn: // 搜索按钮
			searchDropListView.setVisibility(View.GONE);
			hideSoftKeyboard();
			search(searchEditView.getText().toString());
			
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		if (arg0 == horizontalListView) {
			horizonListAdapter.setSelectPosition(position);
			horizonListAdapter.notifyDataSetChanged();
			filterMovieList(horizonListAdapter.getList().get(position));
			return;
		}
		if (arg0 == searchDropListView) {
			searchDropListView.setVisibility(View.GONE);
			hideSoftKeyboard();
			search(searchDropListViewAdapter.getList().get(position));
			return ;
		}

		if (arg0 == gridView) {
			if (searchGridAdapter == null
					|| searchGridAdapter.getList().size() == 0
					|| searchGridAdapter.getList().get(position) == null)
				return;
			MovieInfo movieInfo = searchGridAdapter.getList().get(position);
			if (movieInfo.isAd()) {
				AdInfo info = (AdInfo) movieInfo;
				Intent intent = new Intent(this, AdDetailActivity.class);
				intent.putExtra("name", info.getAppName());
				intent.putExtra("url", info.getHomePageUrl());
				this.startActivity(intent);
			} else {
				CommonUtil.toMovieDetialActivity(this, movieInfo.getMovieID(), movieInfo.getMovieName());
			}
		}

	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_GO) {
			Log.d(AppConfig.Tag, "IME_ACTION_GO");
			return false;
		}
		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			Log.d(AppConfig.Tag, "IME_ACTION_SEARCH");
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (!isFirstPager) {
				changePager(true);
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void afterTextChanged(Editable charSequence) {
		String mContext = charSequence.toString().trim();
		if (!TextUtils.isEmpty(mContext)) {
			if(!isDroplistSearching){
				isDroplistSearching=true;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				AutoSearchAsync async=new AutoSearchAsync(this, AUTO_SEARCH);
				async.setHandler(handler);
				async.execute(mContext);
			}
		} else {
			searchDropListView.setVisibility(View.GONE);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}
}
