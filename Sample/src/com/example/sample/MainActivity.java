package com.example.sample;

import net.npike.draggableviewtools.DragVideoView;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.sample.fragment.SimpleVideoFragment;
import com.github.pedrovgs.DragFrameLayout;
import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggableView;

public class MainActivity extends FragmentActivity implements DraggableListener {

	private static final String TAG_FRAG_SIMPLEVIDEO = "frag_simplevideo";
	protected static final String TAG = "MainActivity";
	private DraggableView mDraggableView;
	private Button mButtonStartVideo;
	private SimpleVideoFragment mFragmentVideo;
	private DraggableView mDraggableViewVideoInline;
	private Button mButtonStartVideoInline;
	private DragVideoView mVideoViewInline;
	private DragFrameLayout mFrameLayoutVideoWrap;
	private android.widget.RelativeLayout.LayoutParams paramsNotFullscreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mButtonStartVideo = (Button) findViewById(R.id.buttonStartVideo);
		mButtonStartVideoInline = (Button) findViewById(R.id.buttonStartVideoInline);
		mDraggableView = (DraggableView) findViewById(R.id.draggable_view);
		mDraggableViewVideoInline = (DraggableView) findViewById(R.id.draggable_view_inline);
		mVideoViewInline = (DragVideoView) findViewById(R.id.videoview_placeholder_video_inline);

		mFrameLayoutVideoWrap = (DragFrameLayout) findViewById(R.id.fragment_placeholder_video);
		paramsNotFullscreen = (android.widget.RelativeLayout.LayoutParams) mFrameLayoutVideoWrap
				.getLayoutParams();

		mButtonStartVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDraggableView.setVisibility(View.VISIBLE);
				mDraggableView.maximize();

				mFragmentVideo = SimpleVideoFragment.getInstance();
				getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.fragment_placeholder_video,
								mFragmentVideo, TAG_FRAG_SIMPLEVIDEO).commit();
			}
		});

		mButtonStartVideoInline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDraggableViewVideoInline.setVisibility(View.VISIBLE);
				mDraggableViewVideoInline.maximize();

				// start the video
				Uri uri = Uri.parse(getString(R.string.hls_test_url));
				mVideoViewInline.setVideoURI(uri);
				mVideoViewInline.start();

			}
		});

		mDraggableView.setDraggableListener(this);

		mDraggableViewVideoInline.setDraggableListener(new DraggableListener() {

			@Override
			public void onMinimized() {

			}

			@Override
			public void onMaximized() {

			}

			@Override
			public void onDraggableViewScaleChanged(float scaleX, float scaleY) {
				mVideoViewInline.changeVideoSize(scaleX, scaleY);
			}

			@Override
			public void onClosedToRight() {
				Log.d(TAG, "onClosedToRight");
			}

			@Override
			public void onClosedToLeft() {
				Log.d(TAG, "onClosedToLeft");
			}
		});

		mDraggableView.setVisibility(View.GONE);
		mDraggableView.closeToRight();

		mDraggableViewVideoInline.setVisibility(View.GONE);
		mDraggableViewVideoInline.closeToRight();
	}

	@Override
	public void onClosedToLeft() {
		mFragmentVideo.onStopPlayback();
	}

	@Override
	public void onClosedToRight() {

	}

	@Override
	public void onMaximized() {
		// When maximized allow the app to change orientations based on sensor
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		
		// restore the layout params for the fragment container
		mFrameLayoutVideoWrap.setLayoutParams(paramsNotFullscreen);
		mFrameLayoutVideoWrap.getParent().requestLayout();
	}

	@Override
	public void onMinimized() {
		// When minimized lock the app into a portrait orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public void onDraggableViewScaleChanged(float scaleX, float scaleY) {
		// Update the fragment container to have a new size based on the
		// draggableView scale
		mFrameLayoutVideoWrap.onChangeSize(scaleX, scaleY);
	}

	@Override
	public void onBackPressed() {
		if (mDraggableView.isMaximized()) {
			mDraggableView.minimize();
		} else if (mDraggableView.isMinimized()) {
			mDraggableView.closeToLeft();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// We want the video player fragment to appear fullscreen when going
		// from maximized in portrait to maximized in landscape.
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// set the parent layout out the video player fragment to consume
			// the entire dimensions of the screen
			RelativeLayout.LayoutParams fullscreenParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			mFrameLayoutVideoWrap.setLayoutParams(fullscreenParams);
			mFrameLayoutVideoWrap.requestLayout();
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// Restore original layoutparams when returning to a portrait
			// orientation
			mFrameLayoutVideoWrap.setLayoutParams(paramsNotFullscreen);
			mFrameLayoutVideoWrap.getParent().requestLayout();
		}
	}

}
