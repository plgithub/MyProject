package cn.pipi.mobile.pipiplayer.view;

import cn.pipi.mobile.pipiplayer.hd.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomePagerLayout extends LinearLayout {

	private final int defaultWeightSum = 5;

	private Context context;

	private LayoutInflater mLayoutInflater;

	private String groupTitels[];

	private LinearLayout[] childLayouts;

	public HomePagerLayout(Context context) {
		super(context);
		init(context);
	}

	public HomePagerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		mLayoutInflater = LayoutInflater.from(context);
		// this.setOrientation(LinearLayout.VERTICAL);
		this.setWeightSum(defaultWeightSum);
		childViewInit();
	}

	public void childViewInit() {
		this.removeAllViews();
		groupTitels = context.getResources().getStringArray(
				R.array.homepager_titelgroup);
		int size = groupTitels.length;
		childLayouts = new LinearLayout[size];
		for (int i = 0; i < size; i++) {
			childLayouts[i] = (LinearLayout) mLayoutInflater.inflate(
					R.layout.homepager_item, null);
			TextView titelTextView = (TextView) childLayouts[i].getChildAt(0);
			titelTextView.setText(groupTitels[i]);
		}
	}

}
