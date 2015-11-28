package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import cn.pipi.mobile.pipiplayer.bean.TypesBean;
import cn.pipi.mobile.pipiplayer.hd.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MoreMenuGridviewAdapter extends BaseAdapter{
	
	public Context context;
	
	private LayoutInflater mInflater;
	
	private final int convertViewBg=R.drawable.moremenu_gridview_itembg;
	
	private List<TypesBean> list;
	
	private int selectPosition=0;
	
	private ColorStateList blackColorStateList;

	private ColorStateList whiteColorStateList;
	
	public int getSelectPosition() {
		return selectPosition;
	}

	public void setSelectPosition(int selectPosition) {
		this.selectPosition = selectPosition;
	}

	public List<TypesBean> getList() {
		return list;
	}

	public void setList(List<TypesBean> list) {
		this.list = list;
	}

	public MoreMenuGridviewAdapter(Context context){
		this.context=context;
		mInflater=LayoutInflater.from(context);
		Resources resource = (Resources) context.getResources();
		blackColorStateList = (ColorStateList) resource
				.getColorStateList(R.color.black);
		whiteColorStateList = (ColorStateList) resource
				.getColorStateList(R.color.white);
	}
	
	public MoreMenuGridviewAdapter(Fragment fragment){
		mInflater=LayoutInflater.from(fragment.getActivity());
		Resources resource = (Resources) fragment.getActivity().getResources();
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

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView!=null){
			viewHolder=(ViewHolder) convertView.getTag();
		}else {
			convertView=mInflater.inflate(R.layout.moremenu_gridviewitem, null);
			viewHolder=new ViewHolder();
			viewHolder.nameTextView=(TextView) convertView.findViewById(R.id.moremenu_classifyname);
			convertView.setTag(viewHolder);		
		}
		if(getList()!=null){
			viewHolder.nameTextView.setText(getList().get(position).getTypesName());
			if(getSelectPosition()!=position){
				viewHolder.nameTextView.setTextColor(blackColorStateList);
				viewHolder.nameTextView.setBackgroundResource(R.color.full_transparent);
			}else{
				viewHolder.nameTextView.setTextColor(whiteColorStateList);
				viewHolder.nameTextView.setBackgroundResource(R.color.red);;
			}
		}
		return convertView;
	}
	
	class ViewHolder{
		
		TextView nameTextView;
	}

}
