package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import cn.pipi.mobile.pipiplayer.bean.SpecialMovieInfo;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.BitmapManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 专题 适配器
 * 
 * @author qiny
 * 
 */
public class SpecialAdapter extends BaseAdapter {

	private Context context;

	private LayoutInflater mInflater;

	List<SpecialMovieInfo> list;

	public List<SpecialMovieInfo> getList() {
		return list;
	}

	public void setList(List<SpecialMovieInfo> list) {
		this.list = list;
	}

	public SpecialAdapter(Context context) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if (convertView != null) {
			viewHolder=(ViewHolder) convertView.getTag();
		} else {
			viewHolder=new ViewHolder();
			convertView=mInflater.inflate(R.layout.special_gridview_item, null);
			viewHolder.imgView=(ImageView) convertView.findViewById(R.id.special_imgview);
			viewHolder.nameView=(TextView) convertView.findViewById(R.id.special_specialnametextview);
			viewHolder.showNameView=(TextView) convertView.findViewById(R.id.special_specialshownametextview);
			convertView.setTag(viewHolder);
		}
		if(getList()!=null&&getList().size()!=0){
			SpecialMovieInfo specialMovieInfo=getList().get(position);
			viewHolder.nameView.setText(specialMovieInfo.getMovieName());
			viewHolder.showNameView.setText(specialMovieInfo.getMovieShowName());
			BitmapManager.getInstance().loadBitmap(specialMovieInfo.getMovieImgPath(), viewHolder.imgView,null,190,135);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView imgView;

		TextView nameView;
		
		TextView showNameView;

	}

}
