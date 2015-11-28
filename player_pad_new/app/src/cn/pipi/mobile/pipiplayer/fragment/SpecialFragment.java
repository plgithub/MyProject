package cn.pipi.mobile.pipiplayer.fragment;

import java.util.List;

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
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import cn.pipi.mobile.pipiplayer.DetialActivity;
import cn.pipi.mobile.pipiplayer.MainActivity;
import cn.pipi.mobile.pipiplayer.adapter.SpecialAdapter;
import cn.pipi.mobile.pipiplayer.async.SpecialAsync;
import cn.pipi.mobile.pipiplayer.bean.SpecialMovieInfo;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.config.MessageMark;
import cn.pipi.mobile.pipiplayer.fragment.HomeFragment.LoadMoreInterFace;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.ToastUtil;
import cn.pipi.mobile.pipiplayer.view.MyGridView;

/**
 * 专题
 * 
 * @author qiny
 * 
 */
public class SpecialFragment extends BaseFragment implements OnItemClickListener, OnTouchListener {

	private View view;

	private MyGridView gridView;
	
	private LinearLayout loadingLayout;
	
	private SpecialAdapter specialAdapter;
	
	private MainActivity mainActivity;
	
	private float x = 0, y = 0;
	
	@Override
	public void widgetInit() {
		if (view == null)
			return;
		loadingLayout=(LinearLayout) view.findViewById(R.id.loadinglayout);
		gridView = (MyGridView) view.findViewById(R.id.special_gridview);
		specialAdapter=new SpecialAdapter(getActivity());
		gridView.setAdapter(specialAdapter);
		gridView.setOnItemClickListener(this);
		gridView.setOnTouchListener(this);
		Log.d(AppConfig.Tag, "----专题初始化完成----");
	}

	@Override
	public void onAttach(Activity activity) {
		mainActivity = (MainActivity) activity;
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.special, null);
		widgetInit();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getServerData();
	}
	
	private void getServerData(){
		SpecialAsync specialAsync=new SpecialAsync(getActivity(),0);
		specialAsync.setHandler(handler);
		specialAsync.setLoadingview(loadingLayout);
		specialAsync.execute();
	}

	 @SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MessageMark.OKAY:
				System.out.println("-----获取数据成功----");
				specialAdapter.setList((List<SpecialMovieInfo>)msg.obj);
				specialAdapter.notifyDataSetChanged();
				break;
			case MessageMark.NETTYPE_NONETWORK:
				ToastUtil.ToastShort(getActivity(), "无网络");
				break;
			}
		};
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(specialAdapter==null||specialAdapter.getList()==null||specialAdapter.getList().size()==0)return ;
		if(getActivity()!=null){
			MobclickAgent.onEvent(getActivity(), "Click_tuijian","专题");
		}
		Intent intent=new Intent();
		intent.setClass(getActivity(), DetialActivity.class);
		intent.putExtra("moviename", specialAdapter.getList().get(arg2).getMovieName());
		intent.putExtra("movieid", specialAdapter.getList().get(arg2).getMovieId());
		getActivity().startActivity(intent);
		
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (x==0 && y==0) {
			x = event.getX();
			y = event.getY();
		}
		if (event.getX()-x > 50 && Math.abs(event.getY()-y)<50) {
			mainActivity.showOrHideMenu();
		}
		if (event.getAction()==MotionEvent.ACTION_UP) {
			x = 0;
			y = 0;
		}
		return false;
	}
}
