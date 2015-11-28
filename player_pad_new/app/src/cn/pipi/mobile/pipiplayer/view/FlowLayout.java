package cn.pipi.mobile.pipiplayer.view;

import java.util.List;

import cn.pipi.mobile.pipiplayer.hd.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FlowLayout extends ViewGroup {
	private int mHorizontalSpacing;
	private int mVerticalSpacing;
	private Paint mPaint;

	// private Context context;
	
	private LayoutInflater mInflater;
	
	public FlowTextViewOnclick flowTextViewOnclick;
	
	public int type;  // 当前是热门搜索还是历史
	
	public static final int FlowLayout_SEARCH=1;

	public static final int FlowLayout_HISTORY=2;

	public FlowLayout(Context context) {
		super(context);
		this.flowTextViewOnclick=(FlowTextViewOnclick) context;
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.flowTextViewOnclick=(FlowTextViewOnclick) context;
		this.readStyleParameters(context, attrs);
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public interface FlowTextViewOnclick{
		
		void searchFilmFromName(int type,String name);
		
	}

	private void readStyleParameters(Context context, AttributeSet attrs) {
		// this.context=context;
		this.mInflater = LayoutInflater.from(context);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.FlowLayout);
		try {
			mHorizontalSpacing = a.getDimensionPixelSize(
					R.styleable.FlowLayout_horizontalSpacing, 14);
			mVerticalSpacing = a.getDimensionPixelSize(
					R.styleable.FlowLayout_verticalSpacing, 12);
		} finally {
			a.recycle();
		}
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(0xffff0000);
		mPaint.setStrokeWidth(2.0f);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec)
				- getPaddingRight() - getPaddingLeft();
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//		Log.d(AppConfig.Tag, "widthSize:"+widthSize);
		boolean growHeight = widthMode != MeasureSpec.UNSPECIFIED; // 默认为true

		int width = 0;
		int height = getPaddingTop();

		int currentWidth = getPaddingLeft(); // ��ǰ�еĿ��
		int currentHeight = 0; // ��ǰ�еĸ߶�

		boolean breakLine = false;
		boolean newLine = false;
		int spacing = 0;

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			measureChild(child, widthMeasureSpec, heightMeasureSpec);

			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			spacing = mHorizontalSpacing;
			if (lp.horizontalSpacing >= 0) {
				spacing = lp.horizontalSpacing;
			}

			if (growHeight
					&& (breakLine || currentWidth + child.getMeasuredWidth() > widthSize)) {
				newLine = true;

				height += currentHeight + mVerticalSpacing;
				width = Math.max(width, currentWidth - spacing);

				currentHeight = 0;
				currentWidth = getPaddingLeft();

			} else {
				newLine = false;
			}

			lp.x = currentWidth;
			lp.y = height;

			currentWidth += child.getMeasuredWidth() + spacing;
			currentHeight = Math.max(currentHeight, child.getMeasuredHeight());

			breakLine = lp.breakLine;
		}

		if (!newLine) {
			height += currentHeight;
			width = Math.max(width, currentWidth - spacing);
		}

		width += getPaddingRight();
		height += getPaddingBottom();

		setMeasuredDimension(resolveSize(width, widthMeasureSpec),
				resolveSize(height, heightMeasureSpec));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y
					+ child.getMeasuredHeight());
		}
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		boolean more = super.drawChild(canvas, child, drawingTime);
		LayoutParams lp = (LayoutParams) child.getLayoutParams();
		if (lp.horizontalSpacing > 0) {
			float x = child.getRight();
			float y = child.getTop() + child.getHeight() / 2.0f;
			canvas.drawLine(x, y - 4.0f, x, y + 4.0f, mPaint);
			canvas.drawLine(x, y, x + lp.horizontalSpacing, y, mPaint);
			canvas.drawLine(x + lp.horizontalSpacing, y - 4.0f, x
					+ lp.horizontalSpacing, y + 4.0f, mPaint);
		}
		if (lp.breakLine) {
			float x = child.getRight();
			float y = child.getTop() + child.getHeight() / 2.0f;
			canvas.drawLine(x, y, x, y + 6.0f, mPaint);
			canvas.drawLine(x, y + 6.0f, x + 6.0f, y + 6.0f, mPaint);
		}
		return more;
	}

	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof LayoutParams;
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new LayoutParams(getContext(), attrs);
	}

	@Override
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new LayoutParams(p.width, p.height);
	}

	public static class LayoutParams extends ViewGroup.LayoutParams {
		int x;
		int y;

		public int horizontalSpacing;
		public boolean breakLine;

		public LayoutParams(Context context, AttributeSet attrs) {
			super(context, attrs);
			TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.FlowLayout_LayoutParams);
			try {
				horizontalSpacing = a
						.getDimensionPixelSize(
								R.styleable.FlowLayout_LayoutParams_layout_horizontalSpacing,
								-1);
				breakLine = a.getBoolean(
						R.styleable.FlowLayout_LayoutParams_layout_breakLine,
						false);
			} finally {
				a.recycle();
			}
		}

		public LayoutParams(int w, int h) {
			super(w, h);
		}
	}

	public boolean isEmpty() {
		boolean isEmpty = true;
		if (getChildCount() != 0) {
			isEmpty = false;
		}
		return isEmpty;
	}

	public void addChildView(List<String> list) {
		if (this.getChildCount() != 0) {
			this.removeAllViews();
		}
		if (mInflater == null || list == null || list.size() == 0)
			return;
		for (String name : list) {
			LinearLayout view = (LinearLayout) mInflater.inflate(
					R.layout.search_textview, null);
			TextView childTextView=(TextView) view.findViewById(R.id.searchtextview_flowlayout);
			childTextView.setText(name);
			childTextView.setTag(name);
			childTextView.setOnClickListener(onClickListener);
			this.addView(view);
		}
	}
	
	public void removieAllView(){
		if (this.getChildCount() != 0) {
			this.removeAllViews();
		}
	}
	
	OnClickListener onClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			String name=(String) view.getTag();
			if(flowTextViewOnclick!=null){
				flowTextViewOnclick.searchFilmFromName(type,name);
			}
		}
	};
}
