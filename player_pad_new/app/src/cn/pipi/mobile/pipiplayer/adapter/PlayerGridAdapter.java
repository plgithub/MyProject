package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import cn.pipi.mobile.pipiplayer.bean.DownLoadInfo;
import cn.pipi.mobile.pipiplayer.hd.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 播放器gridview选集适配器
 * @author qiny
 *
 */
public class PlayerGridAdapter extends BaseAdapter{
	
	private Context context;
	
	private List<DownLoadInfo> list;
	
	private int selectPosition;
	
	public int getSelectPosition() {
		return selectPosition;
	}

	public void setSelectPosition(int selectPosition) {
		this.selectPosition = selectPosition;
	}

	private String selectAddress;
	
	private LayoutInflater mLayoutInflater;
	
	private final int convertBG[]={R.drawable.player_gridview_normolbg,R.drawable.player_gridview_selectbg};
	
	private ColorStateList redColorStateList;

	private ColorStateList blackColorStateList;

	public List<DownLoadInfo> getList() {
		return list;
	}

	public void setList(List<DownLoadInfo> list) {
		this.list = list;
	}

//	public int getSelectPosition() {
//		return selectPosition;
//	}
//
//	public void setSelectPosition(int selectPosition) {
//		this.selectPosition = selectPosition;
//	}

	public String getSelectAddress() {
		return selectAddress;
	}

	public void setSelectAddress(String selectAddress) {
		this.selectAddress = selectAddress;
	}

	public PlayerGridAdapter(Context context) {
		this.context=context;
		setSelectAddress("");
		setSelectPosition(-1);
		mLayoutInflater=LayoutInflater.from(context);
		Resources resource = (Resources) context.getResources();
		blackColorStateList = (ColorStateList) resource
				.getColorStateList(R.color.black);
		redColorStateList = (ColorStateList) resource
				.getColorStateList(R.color.red);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return getList()!=null&&getList().size()!=0?getList().size():0;
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
			convertView=mLayoutInflater.inflate(R.layout.playgridview_item, null);
			viewHolder=new ViewHolder();
			viewHolder.nameView=(TextView) convertView.findViewById(R.id.playergridview_nameview);
			convertView.setTag(viewHolder);
		}
		if(getList()!=null&&getList().size()!=0){
//			viewHolder.nameView.setText(""+(getList().size()-position));
			// 设置选中背景
//			if(!(getSelectAddress().equals(getList().get(getList().size()-position-1).getDownAddress()))){
//				viewHolder.nameView.setTextColor(blackColorStateList);
//				convertView.setBackgroundResource(convertBG[0]);
//			}else{
//				viewHolder.nameView.setTextColor(redColorStateList);
//				convertView.setBackgroundResource(convertBG[1]);
//			}
			viewHolder.nameView.setText(""+(position+1));
//			if(getSelectPosition()!=(getList().size()-position-1)){
//				viewHolder.nameView.setTextColor(blackColorStateList);
//				convertView.setBackgroundResource(convertBG[0]);
//			}else{
//				viewHolder.nameView.setTextColor(redColorStateList);
//				convertView.setBackgroundResource(convertBG[1]);
//			}
			if(getSelectPosition()!=(position)){
				viewHolder.nameView.setTextColor(blackColorStateList);
				convertView.setBackgroundResource(convertBG[0]);
			}else{
				viewHolder.nameView.setTextColor(redColorStateList);
				convertView.setBackgroundResource(convertBG[1]);
			}
		}
		return convertView;
	}
	
	class ViewHolder{
		
		TextView nameView;
		
	}

}
