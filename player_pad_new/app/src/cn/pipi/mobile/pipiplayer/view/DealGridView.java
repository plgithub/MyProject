package cn.pipi.mobile.pipiplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;


public class DealGridView extends GridView {

	private int expandSpec;

	private boolean haveScrollbar = true;
	
	public DealGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public DealGridView(Context context) {
		super(context);
	}

	public DealGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/**
	 * . * 设置是否有ScrollBar，当要在ScollView中显示时，应当设置为false。 默认为 true
	 * 
	 * @param haveScrollbars
	 */
	public void setHaveScrollbar(boolean haveScrollbar) {
		this.haveScrollbar = haveScrollbar;
	}

	public int getExpandSpec() {
		return expandSpec;
	}

	public void setExpandSpec(int expandSpec) {
		this.expandSpec = expandSpec;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {	
		if (haveScrollbar) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		} else {
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		}

	}

}
