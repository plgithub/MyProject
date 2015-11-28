package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;

import cn.pipi.mobile.pipiplayer.bean.MovieInfo;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.BitmapManager;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * 影片推荐视频器
 * 
 * @author qiny
 * 
 */
public class RecommendAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;

	private List<MovieInfo> list;
	
	private int imgWidth=68;
	
	private int imgHeight=50;
	
	private Bitmap defaultBitmap;
	
	private Handler mHandler;

	public List<MovieInfo> getList() {
		return list;
	}

	public void setList(List<MovieInfo> list) {
		this.list = list;
	}

	public RecommendAdapter(Context context) {
		layoutInflater = LayoutInflater.from(context);
		defaultBitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.img_default);
		imgValuesInit();
	}
	
	private void imgValuesInit(){
		imgWidth=CommonUtil.scaleWidgetWidth(imgWidth);
		imgHeight=CommonUtil.scaleWidgetHeight(imgHeight);
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
		ViewHolder viewHolder=null;
		if(convertView!=null){
			viewHolder=(ViewHolder) convertView.getTag();	
		}else{
			viewHolder=new ViewHolder();
			convertView=layoutInflater.inflate(R.layout.recommendlist_item, null);
			viewHolder.imgView=(ImageView) convertView.findViewById(R.id.recommendlist_imgview);
			viewHolder.nameTextView=(TextView) convertView.findViewById(R.id.recommendlist_nameview);
			viewHolder.descTextView=(TextView) convertView.findViewById(R.id.recommendlist_desc);
//			viewHolder.syntheticalView=(TextView) convertView.findViewById(R.id.recommendlist_synthesisview);
//			viewHolder.actorView=(TextView) convertView.findViewById(R.id.recommendlist_actorview);
			viewHolder.gradeView=(TextView) convertView.findViewById(R.id.recommendlist_gradeview);
//			setImgViewWidthAndHeight(viewHolder.imgView);
			convertView.setTag(viewHolder);
		}
		if(getList()!=null&&getList().size()!=0){
			MovieInfo movieInfo=getList().get(position);
			BitmapManager.getInstance().loadBitmap(movieInfo.getMovieImgPath(), viewHolder.imgView,null);
			viewHolder.nameTextView.setText(movieInfo.getMovieName());
			viewHolder.descTextView.setText(movieInfo.getDesc());
			viewHolder.gradeView.setText(movieInfo.getGrade());
		}
		return convertView;
	}
	
	private void setImgViewWidthAndHeight(ImageView imgview) {
		RelativeLayout.LayoutParams layoutParams = (LayoutParams) imgview.getLayoutParams();
		layoutParams.width = CommonUtil.scaleWidgetWidth(imgWidth);
		layoutParams.height = CommonUtil.scaleWidgetHeight(imgHeight);
		imgview.setLayoutParams(layoutParams);
	}

	class ViewHolder {

		ImageView imgView;

		TextView nameTextView;
		
		TextView descTextView;

		TextView syntheticalView;

		TextView actorView;

		TextView gradeView;

	}

	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

}
