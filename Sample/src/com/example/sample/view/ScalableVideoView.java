package com.example.sample.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class ScalableVideoView extends VideoView {

	private int mVideoWidth;
	private int mVideoHeight;

	public ScalableVideoView(Context context) {
		super(context);
	}

	public ScalableVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScalableVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mVideoWidth = 0;
		mVideoHeight = 0;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		if (mVideoWidth > 0 && mVideoHeight > 0) {
			// If a custom dimension is specified, force it as the measured dimension
			setMeasuredDimension(mVideoWidth, mVideoHeight);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public void changeVideoSize(int width, int height) {
		mVideoWidth = width;
		mVideoHeight = height;

		getHolder().setFixedSize(width, height);

		requestLayout();
		invalidate();
	}

}