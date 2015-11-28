package cn.pipi.mobile.pipiplayer.adapter;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.hd.R;

public class HorizonListAdapter extends BaseAdapter {

	private LayoutInflater mLayoutInflater;

	private List<String> list;

	private List<MovieInfo> mainList;

	private int selectPosition = 0;

	private ColorStateList blackColorStateList;

	private ColorStateList whiteColorStateList;

	public int getSelectPosition() {
		return selectPosition;
	}

	public void setSelectPosition(int selectPosition) {
		this.selectPosition = selectPosition;
	}

	public List<MovieInfo> getMainList() {
		return mainList;
	}

	public void setMainList(List<MovieInfo> mainList) {
		this.mainList = mainList;
	}

	public HorizonListAdapter(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
		Resources resource = (Resources) context.getResources();
		blackColorStateList = (ColorStateList) resource.getColorStateList(R.color.black);
		whiteColorStateList = (ColorStateList) resource.getColorStateList(R.color.white);
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		return list != null && list.size() != 0 ? list.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.search_horizonlistview_item, null);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.horizonlistview_textview);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (getList().size() != 0) {
			if (getMainList().size() != 0) {
				viewHolder.textView.setText(getList().get(position) + "/"
						+ selectListSizeFromType(mainList, getList().get(position)));
			}
			if (getSelectPosition() != position) {
				viewHolder.textView.setTextColor(blackColorStateList);
				viewHolder.textView.setBackgroundDrawable(null);
			} else {
				viewHolder.textView.setTextColor(whiteColorStateList);
				viewHolder.textView.setBackgroundResource(R.drawable.search_radio_bg);
			}
		}

		return convertView;
	}

	class ViewHolder {
		TextView textView;
	}

	/**
	 * 根据type 获取该type下list size
	 * 
	 * @param mainList
	 * @param type
	 * @return
	 */
	public static int selectListSizeFromType(List<MovieInfo> mainList, String type) {
		// List<MovieInfo> list=new ArrayList<>();
		int tmp = 0;
		for (MovieInfo movieInfo : mainList) {
			if (movieInfo.getMovieType() == null) {
				tmp++;
				continue;
			}
			if (!movieInfo.getMovieType().equals(type))
				tmp++;
		}
		return tmp != mainList.size() ? mainList.size() - tmp : mainList.size();
	}

	public static List<MovieInfo> getCurrentTypeList(List<MovieInfo> mainList, String type) {
		List<MovieInfo> list = new ArrayList<MovieInfo>();
		for (MovieInfo movieInfo : mainList) {
			if (movieInfo.getMovieType() == null) {
				list.add(movieInfo);
				continue;
			}
			if (movieInfo.getMovieType().equals(type)) {
				list.add(movieInfo);
			}
		}
		return list;
	}

}
