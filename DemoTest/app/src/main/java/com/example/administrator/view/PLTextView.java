package com.example.administrator.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/12/5.
 */
public class PLTextView extends TextView {
    private int viewWdith = 0;
    private LinearGradient mLinearGradient;
    private Paint mPaint;
    private Matrix matrix;
    private int mTranslate;

    public PLTextView(Context context) {
        super(context);
    }

    public PLTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PLTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (viewWdith == 0) {
            viewWdith = getMeasuredWidth();
            if (viewWdith > 0) {
                mPaint = getPaint();
                mLinearGradient = new LinearGradient(0, 0, viewWdith, 0,
                        new int[]{
                                Color.RED,
                                Color.BLUE,
                                Color.RED,
                        }, null,
                        Shader.TileMode.CLAMP);
            }
            mPaint.setShader(mLinearGradient);
            matrix = new Matrix();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPaint != null) {
            mTranslate += viewWdith / 5;
            if (mTranslate > 2 * viewWdith) {
                mTranslate = -viewWdith;
            }
            matrix.setTranslate(mTranslate, 0);
            mLinearGradient.setLocalMatrix(matrix);
            postInvalidateDelayed(100);
        }
    }
}
