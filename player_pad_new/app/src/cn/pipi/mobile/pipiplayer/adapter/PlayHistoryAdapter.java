package cn.pipi.mobile.pipiplayer.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.bean.HistoryBean;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.SystemUtility;

/**
 * 播放历史适配器
 * 
 * @author qiny
 * 
 */
public class PlayHistoryAdapter extends BaseAdapter {

	private Context context;

	private LayoutInflater mLayoutInflater;

	private List<HistoryBean> list;

	private boolean isEditState = false;// 是否是编辑状态

	private final String dates[] = { "更早", "前天", "昨天", "今天" };

	private String currentDate;
	
	private EditImgViewInterFace editImgViewInterFace;
	
	private DBHelperDao dbHelperDao;

	public boolean isEditState() {
		return isEditState;
	}

	public void setEditState(boolean isEditState) {
		this.isEditState = isEditState;
	}

	public List<HistoryBean> getList() {
		return list;
	}

	public void setList(List<HistoryBean> list) {
		this.list = list;
	}

	public PlayHistoryAdapter(Context context) {
		this.context = context;
		this.mLayoutInflater = LayoutInflater.from(context);
		setEditState(false);
		currentDate = CommonUtil.getCurrentDate();
	}
	
	public PlayHistoryAdapter(Fragment fragment){
		dbHelperDao=DBHelperDao.getDBHelperDaoInstace(fragment.getActivity());
		this.mLayoutInflater = LayoutInflater.from(fragment.getActivity());
		setEditState(false);
		currentDate = CommonUtil.getCurrentDate();
		editImgViewInterFace=(EditImgViewInterFace) fragment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return getList() != null && getList().size() != 0 ? getList().size()
				: 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			convertView = mLayoutInflater.inflate(
					R.layout.playhistory_listview_item, null);
			viewHolder = new ViewHolder();
			viewHolder.movieNameView = (TextView) convertView
					.findViewById(R.id.playhistory_movienameview);
			viewHolder.sourceTagImgView = (ImageView) convertView
					.findViewById(R.id.playhistory_moviesourcetag);
			viewHolder.watchDateTextView = (TextView) convertView
					.findViewById(R.id.playhistory_watchdate);
			viewHolder.watchProgressView = (TextView) convertView
					.findViewById(R.id.playhistory_watchprogress);
			viewHolder.editImgView = (ImageView) convertView
					.findViewById(R.id.playhistory_editimgview);
			convertView.setTag(viewHolder);
		}
		if (isEditState()) {
			viewHolder.editImgView.setVisibility(View.VISIBLE);
		} else {
			viewHolder.editImgView.setVisibility(View.GONE);
		}

		if (getList() != null && getList().size() != 0) {
			HistoryBean historyBean = getList().get(position);
			viewHolder.movieNameView.setText(historyBean.getMovieName()+" 第"
					+ (historyBean.getPlayPosition() + 1) + "集" );
			String date = getWatchDate(currentDate,
					historyBean.getWatchedDate());
			viewHolder.watchDateTextView.setText(date);
			long time = historyBean.getWatchedTime();
			if (time != 0)
				viewHolder.watchProgressView.setText("观看至: "
						+ SystemUtility.getTimeString(time));
			if (!TextUtils.isEmpty(historyBean.getSourceTag())
					&& AppConfig.sourceMap.containsKey(historyBean
							.getSourceTag())) {// 来源图标
				viewHolder.sourceTagImgView
						.setBackgroundResource(AppConfig.sourceMap.get(
								historyBean.getSourceTag()).getValues());
			}

			if (isEditState()) {
				if (historyBean.isEditDel()) {
					viewHolder.editImgView
							.setBackgroundResource(R.drawable.playhistory_select_press);
				} else {
					viewHolder.editImgView
							.setBackgroundResource(R.drawable.playhistory_select_normol);
				}
			}
			viewHolder.editImgView.setOnClickListener(new EditImgViewOnclick(historyBean, viewHolder));

		}

		return convertView;
	}

	class EditImgViewOnclick implements OnClickListener {
		
		private HistoryBean historyBean;
		
		private ViewHolder viewHolder;

		public EditImgViewOnclick(int position) {

		}

		public EditImgViewOnclick(HistoryBean historyBean, ViewHolder viewHolder) {
                this.historyBean=historyBean;
                this.viewHolder=viewHolder;
		}

		@Override
		public void onClick(View view) {
		
			if(!historyBean.isEditDel()){
				historyBean.setEditDel(true);
				viewHolder.editImgView
				.setBackgroundResource(R.drawable.playhistory_select_press);
				if(editImgViewInterFace!=null){
					editImgViewInterFace.add();
				}
			}else{
				historyBean.setEditDel(false);
				viewHolder.editImgView
				.setBackgroundResource(R.drawable.playhistory_select_normol);
				editImgViewInterFace.removie();
			}
			

		}

	}
	
	public void editDel(){
		if(getList()==null||getList().size()==0) return ;
		List<HistoryBean> tmpList=new ArrayList<HistoryBean>();
		for (HistoryBean historyBean : getList()) {
			if(historyBean.isEditDel()){
				dbHelperDao.delPlayHistoryFromUrl(historyBean.getMovieUrl());
				tmpList.add(historyBean);
			}
		}
		getList().removeAll(tmpList);
	}

	class ViewHolder {
		TextView movieNameView;
		ImageView sourceTagImgView;
		TextView watchDateTextView;
		TextView watchProgressView;
		ImageView editImgView;
	}
	
	public interface  EditImgViewInterFace{
		
		public void add();
		
		public void removie();
		
	}

	public String getWatchDate(String currentDate, String historyWatchDate) {
		String tmp = dates[2];
		if (TextUtils.isEmpty(currentDate)
				|| TextUtils.isEmpty(historyWatchDate))
			return tmp;
		if (currentDate.length() < 8 || historyWatchDate.length() < 8)
			return tmp;
		int current = Integer.parseInt(currentDate);
		int history = Integer.parseInt(historyWatchDate);
		if (current - history == 0) {
			tmp = dates[3];
		} else if (current - history == 1) {
			tmp = dates[2];
		} else if (current - history == 2) {
			tmp = dates[1];
		} else {
			tmp = dates[0];
		}
		return tmp;
	}

}
