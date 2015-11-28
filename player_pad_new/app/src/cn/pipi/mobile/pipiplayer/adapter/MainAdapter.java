package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.bean.MainItemInfo;
import cn.pipi.mobile.pipiplayer.hd.R;

/**
 * 主菜单适配器
 * 
 * @author qiny
 * 
 */
public class MainAdapter extends BaseAdapter {

	private LayoutInflater mLayoutInflater;

	private int selectPosition = 0;

	private List<MainItemInfo> list;

	public Resources resources;

	private ColorStateList redColorStateList;

	private ColorStateList blackColorStateList;

	private ColorStateList whiteColorStateList;

	public int getSelectPosition() {
		return selectPosition;
	}

	public void setSelectPosition(int selectPosition) {
		this.selectPosition = selectPosition;
	}

	public List<MainItemInfo> getList() {
		return list;
	}

	public void setList(List<MainItemInfo> list) {
		this.list = list;
	}

	public MainAdapter(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
		resources = context.getResources();
		blackColorStateList = (ColorStateList) resources.getColorStateList(R.color.black);
		redColorStateList = (ColorStateList) resources.getColorStateList(R.color.red);

		whiteColorStateList = (ColorStateList) resources.getColorStateList(R.color.white);
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			viewHolder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.main_list_item, null);
			viewHolder.iconImgView = (ImageView) convertView.findViewById(R.id.mainlist_imgview);
			viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.mainlist_nameview);
			convertView.setTag(viewHolder);
		}

		if (list != null) {
			viewHolder.nameTextView.setText(getList().get(position).getName());
			// viewHolder.iconImgView.setBackgroundResource(getList().get(position).getIconID());
			if (getSelectPosition() != position) {
				viewHolder.iconImgView.setBackgroundResource(getList().get(position).getIconID());
				viewHolder.nameTextView.setTextColor(blackColorStateList);
				convertView.setBackgroundResource(R.drawable.mainlist_normol);
			} else {
				viewHolder.iconImgView.setBackgroundResource(getList().get(position).getSelectIconID());
				viewHolder.nameTextView.setTextColor(whiteColorStateList);
				convertView.setBackgroundResource(R.drawable.mainlist_down);
			}
		}
		return convertView;
	}

	class ViewHolder {
		ImageView iconImgView;

		TextView nameTextView;
	}

}
