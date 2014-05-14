package com.example.sample;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.VideoView;

import com.example.sample.fragment.ScalableVideoFragment;
import com.example.sample.fragment.SimpleVideoFragment;
import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggableView;

public class MainActivity extends FragmentActivity implements DraggableListener {

	private static final String TAG_FRAG_SIMPLEVIDEO = "frag_simplevideo";
	private DraggableView mDraggableView;
	private Button mButtonStartVideo;
	private Button mButtonStartVideoScale;
	private Fragment mFragmentVideo;
	private DraggableView mDraggableViewVideoInline;
	private Button mButtonStartVideoInline;
	private VideoView mVideoViewInline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mButtonStartVideo = (Button) findViewById(R.id.buttonStartVideo);
		mButtonStartVideoScale = (Button) findViewById(R.id.buttonStartVideoScale);
		mButtonStartVideoInline = (Button) findViewById(R.id.buttonStartVideoInline);
		mDraggableView = (DraggableView) findViewById(R.id.draggable_view);
		mDraggableViewVideoInline = (DraggableView) findViewById(R.id.draggable_view_inline);
		mVideoViewInline = (VideoView) findViewById(R.id.videoview_placeholder_video_inline);

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

		mButtonStartVideoScale.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDraggableView.setVisibility(View.VISIBLE);
				mDraggableView.maximize();

				mFragmentVideo = ScalableVideoFragment.getInstance();
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

		mDraggableView.setVisibility(View.GONE);
		mDraggableView.closeToRight();
		
		mDraggableViewVideoInline.setVisibility(View.GONE);
		mDraggableViewVideoInline.closeToRight();
	}

	@Override
	public void onClosedToLeft() {

	}

	@Override
	public void onClosedToRight() {

	}

	@Override
	public void onMaximized() {

	}

	@Override
	public void onMinimized() {

	}

	@Override
	public void onDraggableViewScaleChanged(float scaleX, float scaleY) {
		if (mFragmentVideo instanceof ScalableVideoFragment) {
			((ScalableVideoFragment) mFragmentVideo).setVideoViewScale(scaleX,
					scaleY);
		}
	}

	@Override
	public void onBackPressed() {
		if (mDraggableView.isMaximized()) {
			mDraggableView.minimize();
		} else if (mDraggableView.isMinimized()) {
			mDraggableView.closeToRight();
		} else {
			super.onBackPressed();
		}
	}

}