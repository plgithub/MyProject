package cn.pipi.mobile.pipiplayer.adapter;

import java.util.List;
import java.util.Map;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.pipi.mobile.pipiplayer.async.ClassifyAsync;
import cn.pipi.mobile.pipiplayer.bean.TypesBean;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.MainClassifyRequestManager;
import cn.pipi.mobile.pipiplayer.util.TypesManager;
import cn.pipi.mobile.pipiplayer.view.MyGridView;

public class MoreMenuListAdapter extends BaseAdapter {

	private Context context;

	private LayoutInflater mLayoutInflater;

	private MainClassifyRequestManager mainClassifyRequestManager;

	private Map<String, List<TypesBean>> map;

	private final String typeNames[] = { "类型", "地区", "年代" };

	private Handler handler;

	TestInterFace testInterFace;

	Fragment fragment;
	
	private boolean isClearSelectIndex=false;
	
	

	public boolean isClearSelectIndex() {
		return isClearSelectIndex;
	}

	public void setClearSelectIndex(boolean isClearSelectIndex) {
		this.isClearSelectIndex = isClearSelectIndex;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public MainClassifyRequestManager getMainClassifyRequestManager() {
		return mainClassifyRequestManager;
	}

	public void setMainClassifyRequestManager(
			MainClassifyRequestManager mainClassifyRequestManager) {
		this.mainClassifyRequestManager = mainClassifyRequestManager;
	}

	public Map<String, List<TypesBean>> getMap() {
		return map;
	}

	public void setMap(Map<String, List<TypesBean>> map) {
		this.map = map;
	}

	public MoreMenuListAdapter(Context context) {
		this.context = context;
		mLayoutInflater = LayoutInflater.from(context);
		testInterFace = (TestInterFace) context;
		setClearSelectIndex(false);
	}

	public MoreMenuListAdapter(Fragment fragment) {
		this.fragment = fragment;
		mLayoutInflater = LayoutInflater.from(fragment.getActivity());
		testInterFace = (TestInterFace) fragment;
	}

	@Override
	public int getCount() {
		return !map.isEmpty() && map.keySet().size() != 0 ? map.keySet().size() - 1
				: 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView != null) {
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			convertView = mLayoutInflater.inflate(R.layout.moremenu_listitem,
					null);
			viewHolder = new ViewHolder();
			viewHolder.typeTextView = (TextView) convertView
					.findViewById(R.id.moremenu_type);
			viewHolder.myGridView = (MyGridView) convertView
					.findViewById(R.id.moremenu_gridview);
			viewHolder.menuGridviewAdapter = new MoreMenuGridviewAdapter(
					fragment);
			convertView.setTag(viewHolder);
		}

		viewHolder.typeTextView.setText(typeNames[position]);

		if (!map.isEmpty() && map.containsKey(TypesManager.tags[position])) {
			List<TypesBean> list = map.get(TypesManager.tags[position]);
			if(isClearSelectIndex){
				viewHolder.menuGridviewAdapter.setSelectPosition(0);
			}
			viewHolder.menuGridviewAdapter.setList(list);
			viewHolder.myGridView.setAdapter(viewHolder.menuGridviewAdapter);
		}
		viewHolder.myGridView.setOnItemClickListener(new GridviewItemOnclick(
				position, viewHolder));

		return convertView;
	}

	class GridviewItemOnclick implements OnItemClickListener {
		ViewHolder viewHolder;

		int position;

		public GridviewItemOnclick(int position, ViewHolder viewHolder) {
			this.viewHolder = viewHolder;
			this.position = position;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (viewHolder == null || viewHolder.menuGridviewAdapter == null)
				return;
			viewHolder.menuGridviewAdapter.setSelectPosition(arg2);
			viewHolder.menuGridviewAdapter.notifyDataSetChanged();
			String name="";
			if(arg2!=0){// 过滤全部
				name = viewHolder.menuGridviewAdapter.getList().get(arg2)
						.getTypesName();
			}
			System.out.println("-------------" + name);
			if(context!=null){
					MobclickAgent.onEvent(context, "Click_Movie_Select",mainClassifyRequestManager.getTagName());
			}
			switch (position) {
			case 0:
				mainClassifyRequestManager.setType(name);
				break;
			case 1:
				mainClassifyRequestManager.setArea(name);
				break;
			case 2:
				mainClassifyRequestManager.setYear(name);
				break;
			}
			testInterFace.doSomething();
		}

	}

	class ViewHolder {
		TextView typeTextView;

		MyGridView myGridView;

		MoreMenuGridviewAdapter menuGridviewAdapter;
	}

	public interface TestInterFace {

		void doSomething();
	}
	
	/**
	 * 清除之前选择
	 * @param isClear
	 */
	public void clearSelectIndex(boolean isClear){
		setClearSelectIndex(isClear);
		notifyDataSetChanged();
	}

}
