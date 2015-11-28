package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import cn.pipi.mobile.pipiplayer.bean.SourceBean;
import cn.pipi.mobile.pipiplayer.hd.R;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 播放器来源 适配器
 * 
 * @author qiny
 * 
 */
public class PlayerSourceAdapter extends BaseAdapter {

	private Context context;

	private LayoutInflater mLayoutInflater;

	private List<SourceBean> list;
	
	private final int defaultConvertViewWidth=186;
	
	private final int defaultConvertViewHeight=186;
	
	private String currentSourceKey;
	
	public String getCurrentSourceKey() {
		return currentSourceKey;
	}

	public void setCurrentSourceKey(String currentSourceKey) {
		this.currentSourceKey = currentSourceKey;
	}

	private ColorStateList selectConvertColor;
	
	private Resources resources;


	public List<SourceBean> getList() {
		return list;
	}

	public void setList(List<SourceBean> list) {
		this.list = list;
	}

	public PlayerSourceAdapter(Context context) {
		// TODO Auto-generated constructor stub
		 resources=context.getResources();
		selectConvertColor=resources.getColorStateList(R.color.player_sourcelist_selectbg);
		this.mLayoutInflater = LayoutInflater.from(context);
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
		ViewHolder viewHolder=null;
		if(convertView!=null){
			viewHolder=(ViewHolder) convertView.getTag();
		}else{
			viewHolder=new ViewHolder();
			convertView=mLayoutInflater.inflate(R.layout.player_sourcelistview_item, null);
			viewHolder.sourceImageView=(ImageView) convertView.findViewById(R.id.player_sourcelistview_imgview);
			viewHolder.nameTextView=(TextView) convertView.findViewById(R.id.player_sourcelistview_txtview);
			convertView.setTag(viewHolder);
		}
		if(getList()!=null&&getList().size()!=0){
			SourceBean sourceBean=getList().get(position);
			if(!getCurrentSourceKey().equals(sourceBean.getKey())){
				convertView.setBackgroundColor(resources.getColor(R.color.player_sourcelist_selectbg));
			}else{
				convertView.setBackgroundColor(resources.getColor(R.color.translucent_background));
			}
			viewHolder.sourceImageView.setBackgroundResource(sourceBean.getValues());
			viewHolder.nameTextView.setText(sourceBean.getKey());
		}
		return convertView;
		
	}
	
	class ViewHolder{
		ImageView sourceImageView;
		TextView  nameTextView;
	}

}
