package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import cn.pipi.mobile.pipiplayer.hd.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchDropListViewAdapter extends BaseAdapter {

	private Context context;

	private List<String> list;

	private LayoutInflater mLayoutInflater;

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public SearchDropListViewAdapter(Context context) {
		this.context = context;
		this.mLayoutInflater = LayoutInflater.from(context);
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
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			convertView = mLayoutInflater.inflate(
					R.layout.searchdroplistview_item, null);
			viewHolder = new ViewHolder();
			viewHolder.movieNameView = (TextView) convertView
					.findViewById(R.id.search_droplistview_textview);
			convertView.setTag(viewHolder);
		}
		if (getList() != null) {
			viewHolder.movieNameView.setText(getList().get(position));
		}
		return convertView;
	}

	class ViewHolder {
		TextView movieNameView;
	}

}
