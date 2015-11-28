package cn.pipi.mobile.pipiplayer;

import java.util.List;

import cn.pipi.mobile.pipiplayer.adapter.HomePagerAdapter;
import cn.pipi.mobile.pipiplayer.async.ItemClassifyAsync;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.config.MessageMark;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.ToastUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 澶х墖 鍜�涓撻 璇︽儏鐣岄潰
 * 
 * @author qiny
 * 
 */
public class DetialActivity extends BaseActivity implements OnClickListener, OnItemClickListener{

	private TextView nameView;

	private ImageView backImageView;

	private LinearLayout loadingLayout;

	private GridView gridView;

	private HomePagerAdapter homePagerAdapter;
	
	private int itemWidth;
	
	private int itemHeight;
	
	private int itemImageHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.detial);
		widgetInit();
	}

	@Override
	public void widgetInit() {
		Intent intent = getIntent();
		if (intent == null)
			return;
		String movieId = intent.getStringExtra("movieid");
		String movieName = intent.getStringExtra("moviename");
		if (TextUtils.isEmpty(movieId) || !TextUtils.isDigitsOnly(movieId)) {
			
		}
		backImageView = (ImageView) this.findViewById(R.id.detial_backimgview);
		backImageView.setOnClickListener(this);
		nameView = (TextView) this.findViewById(R.id.detial_nameview);
		nameView.setText(movieName);
		loadingLayout = (LinearLayout) this.findViewById(R.id.loadinglayout);
		gridView = (GridView) this.findViewById(R.id.detial_gridview);
		gridView.setOnItemClickListener(this);
		ItemClassifyAsync itemClassifyAsync = new ItemClassifyAsync();
		itemClassifyAsync.setContext(this);
		itemClassifyAsync.setHandler(handler);
		itemClassifyAsync.setLoadingView(loadingLayout);
		itemClassifyAsync.execute(movieId);
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageMark.OKAY:
				initData();
				homePagerAdapter = new HomePagerAdapter(DetialActivity.this);
				homePagerAdapter.setList((List<MovieInfo>) msg.obj);
				gridView.setAdapter(homePagerAdapter);
//				homePagerAdapter.notifyDataSetChanged();
				break;
			case MessageMark.NETTYPE_NONETWORK:
				ToastUtil.ToastShort(DetialActivity.this, "+ - 无网络 - +");
				break;
			case MessageMark.NODATA:
				ToastUtil.ToastShort(DetialActivity.this, "+ - 无数据 - +");
				break;
			}

		};
	};

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.detial_backimgview:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(homePagerAdapter==null||homePagerAdapter.getList()==null||homePagerAdapter.getList().size()==0)return ;
		CommonUtil.toMovieDetialActivity(this, homePagerAdapter.getList().get(arg2).getMovieID(), homePagerAdapter.getList().get(arg2).getMovieName());
	}
	
	private void initData(){
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		
		float scaleWidth = 0.16f;
		float scaleHeight = 0.39f;
		float scaleImageHeight = 0.25f;
		
		itemWidth = (int)(scaleWidth*screenWidth);
		itemHeight = (int)(itemWidth*2);
		itemImageHeight = (int)(scaleImageHeight*screenHeight);
	}
}
