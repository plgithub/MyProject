package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import cn.pipi.mobile.pipiplayer.bean.TypesBean;
import cn.pipi.mobile.pipiplayer.hd.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FilmHorizonalAdapter extends BaseAdapter{
	
	private LayoutInflater mLayoutInflater;
	
	private List<TypesBean> list;
	
	private int selectIndex;
	
	private final int textViewBG=R.drawable.film_horizonlist_textviewbg;
	
	private ColorStateList blackColorStateList;

	private ColorStateList whiteColorStateList;
	
	public int getSelectIndex() {
		return selectIndex;
	}

	public void setSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;
	}

	public List<TypesBean> getList() {
		return list;
	}

	public void setList(List<TypesBean> list) {
		this.list = list;
	}

	public FilmHorizonalAdapter(Context context){
		mLayoutInflater=LayoutInflater.from(context);
		Resources resource = (Resources) context.getResources();
		blackColorStateList = (ColorStateList) resource
				.getColorStateList(R.color.black);
		whiteColorStateList = (ColorStateList) resource
				.getColorStateList(R.color.white);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list!=null&&list.size()!=0?list.size():0;
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
		if(convertView!=null){
			viewHolder=(ViewHolder) convertView.getTag();
		}else{
			viewHolder=new 	ViewHolder();
			convertView=mLayoutInflater.inflate(R.layout.film_horizonlistview_item, null);
			viewHolder.nameTextView=(TextView) convertView.findViewById(R.id.horizonlistview_textview);
			convertView.setTag(viewHolder);
		}
		if(getList().get(position)!=null){
			viewHolder.nameTextView.setText(getList().get(position).getTypesName());
			if(selectIndex!=position){
				viewHolder.nameTextView.setBackgroundDrawable(null);
				viewHolder.nameTextView.setTextColor(blackColorStateList);
			}else{
				viewHolder.nameTextView.setBackgroundResource(textViewBG);
				viewHolder.nameTextView.setTextColor(whiteColorStateList);
			}
		}
		return convertView;
	}
	
	class ViewHolder{
		
		TextView  nameTextView;
	}
	
	
	
	
	public void changeTextViewBg(int position){
		setSelectIndex(position);
		notifyDataSetChanged();
	}

}
