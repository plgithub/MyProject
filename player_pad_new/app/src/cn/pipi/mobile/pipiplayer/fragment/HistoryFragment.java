package cn.pipi.mobile.pipiplayer.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import cn.pipi.mobile.pipiplayer.adapter.PlayHistoryAdapter;
import cn.pipi.mobile.pipiplayer.bean.HistoryBean;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.ToastUtil;

/**
 * 播放历史界面
 * 
 * @author qiny
 * 
 */
public class HistoryFragment extends BaseFragment implements OnClickListener,
		PlayHistoryAdapter.EditImgViewInterFace, OnItemClickListener {

	private final int GET_PLAYHISTORY_LIST = 1;

	private final int DEL_PLAYHISTORY_LIST = 2;

	private View view;

	private ListView listView;

	boolean isEditState = false;

	private Button editBtn;

	private Button delBtn;

	private DBHelperDao dbHelperDao;

	private PlayHistoryAdapter playHistoryAdapter;

	private int num = 0;

	private ColorStateList redColorStateList;

	private ColorStateList blackColorStateList;

	public HistoryFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void widgetInit() {
		if (view == null)
			return;
		dbHelperDao = DBHelperDao.getDBHelperDaoInstace(getActivity());
		Resources resource = (Resources) getActivity().getResources();
		blackColorStateList = (ColorStateList) resource
				.getColorStateList(R.color.black);
		redColorStateList = (ColorStateList) resource
				.getColorStateList(R.color.red);
		editBtn = (Button) view.findViewById(R.id.history_editbtn);
		delBtn = (Button) view.findViewById(R.id.history_delbtn);
		delBtn.setOnClickListener(this);
		delBtn.setVisibility(View.GONE);
		editBtn.setOnClickListener(this);
		listView = (ListView) view.findViewById(R.id.history_listview);
		playHistoryAdapter = new PlayHistoryAdapter(this);
		listView.setAdapter(playHistoryAdapter);
		listView.setOnItemClickListener(this);
		Log.d(AppConfig.Tag, "historyfragment 初始化完成");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.history, null);
		// return super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		widgetInit();
		getHistoryList();
	}

	private void getHistoryList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<HistoryBean> list = dbHelperDao.getPlayHistoryList();
				CommonUtil.sendMessage(GET_PLAYHISTORY_LIST, handler, list);

			}
		}).start();
	}

	private void setDelBtnText(int num) {
		if (num > 0) {
			delBtn.setText("删除( " + num + " )");
			delBtn.setTextColor(redColorStateList);
		} else {
			delBtn.setText("删除");
			delBtn.setTextColor(blackColorStateList);
		}
	}

	private void del() {
		if (num == 0) {
			ToastUtil.ToastShort(getActivity(), "没有选中删除条目!");
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (playHistoryAdapter != null) {
					playHistoryAdapter.editDel();
					CommonUtil.sendMessage(DEL_PLAYHISTORY_LIST, handler, null);
				}
			}
		}).start();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message message) {

			switch (message.what) {
			case GET_PLAYHISTORY_LIST:
				List<HistoryBean> list = (List<HistoryBean>) message.obj;
				if (list != null && list.size() != 0) {
					playHistoryAdapter.setList(list);
					playHistoryAdapter.notifyDataSetChanged();
				} else {
					ToastUtil.ToastShort(getActivity(), "无播放历史");
				}
				break;
			case DEL_PLAYHISTORY_LIST:
				if (playHistoryAdapter != null) {
					ToastUtil.ToastShort(getActivity(), "已删除");
					playHistoryAdapter.notifyDataSetChanged();
					num = 0;
					setDelBtnText(num);
				}
				break;
			}

		};
	};

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.history_delbtn:
			del();
			break;

		case R.id.history_editbtn:
			if(playHistoryAdapter==null||playHistoryAdapter.getList()==null||playHistoryAdapter.getList().size()==0){
			    if(getActivity()!=null){
			    	ToastUtil.ToastShort(getActivity(), "无播放历史");
			    }
				return ;
			}
			if (!isEditState) {
				editBtn.setText("取消");
				playHistoryAdapter.setEditState(true);
				playHistoryAdapter.notifyDataSetChanged();
				isEditState = true;
				delBtn.setVisibility(View.VISIBLE);
			} else {
				editBtn.setText("编辑");
				playHistoryAdapter.setEditState(false);
				playHistoryAdapter.notifyDataSetChanged();
				isEditState = false;
				delBtn.setVisibility(View.GONE);
			}
			break;
		}

	}

	@Override
	public void add() {
		num++;
		setDelBtnText(num);
	}

	@Override
	public void removie() {
		num--;
		setDelBtnText(num);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		if (playHistoryAdapter != null) {
			CommonUtil.toMovieDetialActivity(getActivity(), playHistoryAdapter
					.getList().get(position).getMovieId(), playHistoryAdapter
					.getList().get(position).getMovieName());
			if(isAdded())
			getActivity().finish();
		}

	}

}
