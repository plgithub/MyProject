package cn.pipi.mobile.pipiplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.pipi.mobile.pipiplayer.bean.SourceBean;
import cn.pipi.mobile.pipiplayer.hd.R;

/**
 * 影片详情 来源gridview 适配器
 * @author qiny
 *
 */
public class MovieDetialSourceAdapter extends BaseAdapter{
	
	private Context context;
	
	private LayoutInflater mLayoutInflater;
	
	private List<SourceBean> list;
	private static MovieDetialSourceAdapter instance;

	public MovieDetialSourceAdapter(Context context) {
		this.context=context;
		mLayoutInflater=LayoutInflater.from(context);
	}
	public static MovieDetialSourceAdapter getInstance(Context context){
		if(instance==null){
			instance=new MovieDetialSourceAdapter(context);
		}
		return instance;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list!=null&&list.size()!=0?list.size():0;
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
		ViewHolder viewHolder=null;
		if(convertView!=null){
			viewHolder=(ViewHolder) convertView.getTag();
		}else{
			convertView=mLayoutInflater.inflate(R.layout.moviedetial_sourcegridview_item, null);
			viewHolder=new ViewHolder();
			viewHolder.iconImageView=(ImageView) convertView.findViewById(R.id.moviedetial_sourcegridview_imgview);
			viewHolder.nameView= (TextView) convertView.findViewById(R.id.moviedetial_sourcegridview_nameview);
			convertView.setTag(viewHolder);
		}
		if(getList()!=null&&getList().size()!=0){
			viewHolder.iconImageView.setBackgroundResource(getList().get(position).getValues());
			viewHolder.nameView.setText(getList().get(position).getKey());
		}
		return convertView;
	}
	
	public List<SourceBean> getList() {
		return list;
	}

	public void setList(List<SourceBean> list) {
		this.list = list;
	}

	class ViewHolder{
		ImageView iconImageView;
		
		TextView nameView;
	}

}
