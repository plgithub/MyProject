package cn.pipi.mobile.pipiplayer.adapter;

import java.util.ArrayList;
import java.util.List;

import cn.pipi.mobile.pipiplayer.adapter.DownloadAdapter.ViewHolder;
import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.dao.DBHelperDao;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.BitmapManager;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * 收藏gridview 适配器
 * 
 * @author qiny
 * 
 */
public class StoreGridAdapter extends BaseAdapter {

	private Context context;

	private LayoutInflater mLayoutInflater;

	private List<MovieInfo> list;

	private boolean isEditBtnShow = false;
	
	private StoreEditInterface storeEditInterface;
	
	private DBHelperDao dbHelperDao;

	public List<MovieInfo> getList() {
		return list;
	}

	public void setList(List<MovieInfo> list) {
		this.list = list;
	}

	public boolean isEditBtnShow() {
		return isEditBtnShow;
	}

	public void setEditBtnShow(boolean isEditBtnShow) {
		this.isEditBtnShow = isEditBtnShow;
	}

	public StoreGridAdapter(Context context) {
		this.context = context;
		dbHelperDao=DBHelperDao.getDBHelperDaoInstace(context);
		storeEditInterface=(StoreEditInterface) context;
		mLayoutInflater = LayoutInflater.from(context);
		setEditBtnShow(false);
		initData();
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			convertView = mLayoutInflater.inflate(R.layout.store_gridview_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.imgView = (ImageView) convertView
					.findViewById(R.id.store_img);
			viewHolder.nameView = (TextView) convertView
					.findViewById(R.id.store_name);
			viewHolder.gradeView = (TextView) convertView
					.findViewById(R.id.store_grade);
			viewHolder.editView = (ImageView) convertView
					.findViewById(R.id.store_edit);
			setImgViewWidthAndHeight(viewHolder.imgView);
			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(this.itemWidth, this.itemHeight);
			convertView.setLayoutParams(layoutParams);
			convertView.setTag(viewHolder);
		}
		if (isEditBtnShow()) {
			viewHolder.editView.setVisibility(View.VISIBLE);
		} else {
			viewHolder.editView.setVisibility(View.GONE);
		}
		if (getList() != null && getList().size() != 0) {
			BitmapManager.getInstance().loadBitmap(
					getList().get(position).getMovieImgPath(),
					viewHolder.imgView, null, 190, 175);
			if (isEditBtnShow()) {
				if (getList().get(position).isEditState()) {
					viewHolder.editView
							.setBackgroundResource(R.drawable.download_downlist_select_press);
				} else {
					viewHolder.editView
							.setBackgroundResource(R.drawable.download_downlist_select_normol);
				}
			}
			viewHolder.editView.setOnClickListener(new EditImgViewOnclick(position, viewHolder));
			viewHolder.nameView.setText(getList().get(position).getMovieName());
			viewHolder.gradeView.setText(getList().get(position).getGrade());
		}
		return convertView;
	}
	
	class EditImgViewOnclick implements OnClickListener{
		
		private  int position;
		
		ViewHolder viewHolder;
		
		public EditImgViewOnclick(int position,ViewHolder viewHolder) {
			this.position=position;
			this.viewHolder=viewHolder;
			
		}

		@Override
		public void onClick(View view) {
			 if(getList()==null||getList().size()==0) return ;
			 if(!getList().get(position).isEditState()){
				 getList().get(position).setEditState(true);
				 viewHolder.editView
					.setBackgroundResource(R.drawable.download_downlist_select_press);
				 if(storeEditInterface!=null){
					 storeEditInterface.storeStateAdd();
				 }
			 }else{
				 getList().get(position).setEditState(false); 
				 viewHolder.editView
					.setBackgroundResource(R.drawable.download_downlist_select_normol);
				 if(storeEditInterface!=null){
					 storeEditInterface.storeStateRemovie();
				 }
			 }
			
		}
		
	}

	private void setImgViewWidthAndHeight(ImageView imgview) {
		LinearLayout.LayoutParams layoutParams = (LayoutParams) imgview
				.getLayoutParams();
		layoutParams.width = CommonUtil.scaleWidgetWidth(190);
		layoutParams.height = CommonUtil.scaleWidgetHeight(175);
		imgview.setLayoutParams(layoutParams);
	}

	public void notifiyByEdit(boolean isEdit) {
		setEditBtnShow(isEdit);
		if(getList()!=null&&getList().size()!=0){
			for (MovieInfo movieInfo : getList()) {
				movieInfo.setEditState(false);
			}
		}
		notifyDataSetChanged();
	}
	
	public interface StoreEditInterface {
		public void storeStateAdd();

		public void storeStateRemovie();
	}
	
	public void delEditedItem(){
		final List<MovieInfo> tmplist=new ArrayList<MovieInfo>();
		if(getList()!=null&&getList().size()!=0){
			for (MovieInfo movieInfo : getList()) {
				if(movieInfo.isEditState()){
					tmplist.add(movieInfo);
				}
			}
			if(tmplist.size()!=0)
			getList().removeAll(tmplist);
			notifyDataSetChanged();
			if(tmplist.size()!=0)
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					for (MovieInfo movieInfo : tmplist) {
						if(dbHelperDao!=null)
						dbHelperDao.deleteFavouriteFromMovieid(movieInfo.getMovieID());
					}
					
				}
			}).start();
		}
	}

	class ViewHolder {
		ImageView imgView;
		TextView nameView;
		TextView gradeView; // 打分
		ImageView editView;

	}

	private int itemWidth, itemHeight;
	public void initData(){
		DisplayMetrics dm = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		
		float scaleWidth = 0.15f;
		float scaleImageHeight = 0.25f;
		
		itemWidth = (int)(scaleWidth*screenWidth);
		itemHeight = (int)(itemWidth*1.8);
	}
}
