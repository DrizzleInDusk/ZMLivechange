package com.android.zmlive.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 *解决了和 ScrollView嵌套冲突的问题
 */
public class MyGridView extends GridView {
	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyGridView(Context context) {
		super(context);
	}

	public MyGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				xDistance = yDistance = 0f;
				xLast = ev.getX();
				yLast = ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				final float curX = ev.getX();
				final float curY = ev.getY();

				xDistance += Math.abs(curX - xLast);
				yDistance += Math.abs(curY - yLast);
				xLast = curX;
				yLast = curY;

				if (xDistance > yDistance) {
					return false;
				}
		}

		return super.onInterceptTouchEvent(ev);
	}
}
