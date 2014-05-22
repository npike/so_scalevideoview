package net.npike.draggableviewtools;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.VideoView;

public class DragVideoView extends VideoView{

	private static final String TAG = "DragVideoView";
	private int mMaxVideoWidth;
	private int mMaxVideoHeight;

	public DragVideoView(Context context) {
		super(context);
	}

	public DragVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DragVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		
//		Log.d(TAG, "onMeasure "+getMeasuredHeight());
//	}

	public void changeVideoSize(float scaleY, float scaleX) {
		if (mMaxVideoWidth == 0) {
			mMaxVideoWidth = getWidth();
		}

		if (mMaxVideoHeight == 0) {
			mMaxVideoHeight = getHeight();
		}

		LayoutParams params = getLayoutParams();
		params.height = (int) (mMaxVideoHeight * scaleY);
		params.width = (int) (mMaxVideoWidth * scaleX);
		
		Log.d("DragVideoView", "newHeight: "+params.height + " newWidth: "+params.width);

		setLayoutParams(params);
	}

 
 

}