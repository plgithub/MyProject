package cn.pipi.mobile.pipiplayer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.pipi.mobile.pipiplayer.adapter.MainAdapter;
import cn.pipi.mobile.pipiplayer.bean.MainItemInfo;
import cn.pipi.mobile.pipiplayer.hd.R;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer. See the <a href=
 * "https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"
 * > design guidelines</a> for a complete explanation of the behaviors
 * implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements
		OnItemClickListener {

	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the
	 * user manually expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	private UpdataTitelCallbacks mUpdataTitelCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	// private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private View mFragmentContainerView;

	private int mCurrentSelectedPosition = 0;

	// 主界面list数据
	public List<MainItemInfo> list;

	private MainAdapter mainAdapter;

	private View view;

	public NavigationDrawerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// System.out.println("fragment---->onCreate");
		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState
					.getInt(STATE_SELECTED_POSITION);
			System.out.println("oncreate mCurrentSelectedPosition--->"
					+ mCurrentSelectedPosition);
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// System.out.println("fragment---->onActivityCreated");
		mainlistInit();
		widgetInit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.main_menu, null);
		mDrawerListView = (ListView) view.findViewById(R.id.drawer_listview);
		return view;
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 * 
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

	}

	public void selectItem(int position) {
		mainAdapter.setSelectPosition(position);
		mainAdapter.notifyDataSetChanged();
		mCurrentSelectedPosition = position;
		if (mUpdataTitelCallbacks != null) {
			mUpdataTitelCallbacks.updataTitel(list.get(position).getName());
		}
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position,
					list.get(position).getName());
		}

	}

	private void mainlistInit() {
		if (getActivity() == null)
			return;
		list = new ArrayList<MainItemInfo>();
		int normolIconID[] = { R.drawable.mainlist_home,
				R.drawable.mainlist_film, R.drawable.mainlist_teleplay,
				R.drawable.mainlist_variety, R.drawable.mainlist_cartoon,
				R.drawable.mainlist_hotfilm, R.drawable.mainlist_special };
		int selectIconID[] = { R.drawable.mainlist_home_press,
				R.drawable.mainlist_film_press,
				R.drawable.mainlist_teleplay_press,
				R.drawable.mainlist_variety_press,
				R.drawable.mainlist_cartoon_press,
				R.drawable.mainlist_hotfilm_press,
				R.drawable.mainlist_special_press };
		String name[] = getActivity().getResources().getStringArray(
				R.array.main_list);
		int length = name.length;
		for (int i = 0; i < length; i++) {
			MainItemInfo tmp = new MainItemInfo();
			tmp.setIconID(normolIconID[i]);
			tmp.setSelectIconID(selectIconID[i]);
			tmp.setName(name[i]);
			list.add(tmp);
		}
	}

	public void widgetInit() {
		mainAdapter = new MainAdapter(getActivity());
		mainAdapter.setList(list);
		mDrawerListView.setAdapter(mainAdapter);
		mDrawerListView.setOnItemClickListener(this);
		selectItem(mCurrentSelectedPosition);
		if (mDrawerLayout != null) {
			// mDrawerLayout.openDrawer(mFragmentContainerView);
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
	}

	public void switchDrawLayout() {
		if (mDrawerLayout == null || mFragmentContainerView == null)
			return;
		if (mDrawerLayout.isDrawerOpen(mFragmentContainerView)) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		} else {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}
	}
	
	public void closeDrawer(){
		if (mDrawerLayout == null || mFragmentContainerView == null)
			return;
		if (mDrawerLayout.isDrawerOpen(mFragmentContainerView)) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
			mUpdataTitelCallbacks = (UpdataTitelCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		System.out.println("onSaveInstanceState");
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Callbacks interface that all activities using this fragment must
	 * implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position, String titel);
	}

	public static interface UpdataTitelCallbacks {
		void updataTitel(String titel);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		if (mCurrentSelectedPosition != position) {
			selectItem(position);
			return;
		}

	}
}
