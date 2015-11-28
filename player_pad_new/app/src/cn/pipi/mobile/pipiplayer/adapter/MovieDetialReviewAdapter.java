package cn.pipi.mobile.pipiplayer.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.bean.MovieReviewBean;
import cn.pipi.mobile.pipiplayer.hd.R;

/**
 * 影评适配器
 * @author qiny
 *
 */
public class MovieDetialReviewAdapter extends BaseAdapter {

	private Context context;

	private List<MovieReviewBean> list;

	private LayoutInflater mLayoutInflater;

	private SimpleDateFormat sdf = null;

	private SimpleDateFormat newSdf = null;

	public MovieDetialReviewAdapter(Context context) {
		// TODO Auto-generated constructor stub
		mLayoutInflater = LayoutInflater.from(context);
		sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
		newSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	public List<MovieReviewBean> getList() {
		return list;
	}

	public void setList(List<MovieReviewBean> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			convertView = mLayoutInflater.inflate(
					R.layout.moviedetial_reviewlist_item, null);
			viewHolder = new ViewHolder();
			viewHolder.userName = (TextView) convertView
					.findViewById(R.id.username);
			viewHolder.dataTextView = (TextView) convertView
					.findViewById(R.id.data);
			viewHolder.contentTextView = (TextView) convertView
					.findViewById(R.id.content);
			convertView.setTag(viewHolder);
		}
		MovieReviewBean movieReviewBean = list.get(position);
		if (movieReviewBean != null) {
			String str = "";
			try {
				Date date = sdf.parse(movieReviewBean.getRevDate());
				str = newSdf.format(date);
				viewHolder.dataTextView.setText(str);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			viewHolder.userName.setText(movieReviewBean.getUserNickName());

			viewHolder.contentTextView.setText(movieReviewBean.getContent());
		}
		return convertView;
	}

	class ViewHolder {
		TextView userName;
		TextView dataTextView;
		TextView contentTextView;
	}

}
