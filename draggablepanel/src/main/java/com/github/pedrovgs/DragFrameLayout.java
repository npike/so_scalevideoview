package com.github.pedrovgs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @author npike
 * 
 *         A simple extension of FrameLayout so that we can resize a Fragment
 *         being used in a DraggableView. Useful if your fragment contains a
 *         videoview that needs to be scaled appropriately to the size of its
 *         container.
 * 
 */
public class DragFrameLayout extends FrameLayout {

	private int mMaxViewWidth;
	private int mMaxViewHeight;

	public DragFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DragFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void onChangeSize(float scaleY, float scaleX) {
		if (mMaxViewWidth == 0) {
			mMaxViewWidth = getWidth();
		}

		if (mMaxViewHeight == 0) {
			mMaxViewHeight = getHeight();
		}

		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = (int) (mMaxViewHeight * scaleY);
		params.width = (int) (mMaxViewWidth * scaleX);

		setLayoutParams(params);
	}

}
