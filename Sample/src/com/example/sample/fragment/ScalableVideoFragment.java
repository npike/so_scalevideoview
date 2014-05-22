package com.example.sample.fragment;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.sample.R;
import com.example.sample.view.ScalableVideoView;

public class ScalableVideoFragment extends Fragment implements
		OnPreparedListener {
	private static final String TAG = "ScalableVideoFragment";
	private ScalableVideoView mVideoView;
	private ProgressBar mProgressBarVideo;
	private int mMaxVideoWidth;
	private int mMaxVideoHeight;

	public static ScalableVideoFragment getInstance() {
		return new ScalableVideoFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.frag_scaleable_video, container,
				false);

		mVideoView = (ScalableVideoView) v.findViewById(R.id.videoView);
		mProgressBarVideo = (ProgressBar) v.findViewById(R.id.progressBarVideo);

		mProgressBarVideo.setVisibility(View.VISIBLE);

		Uri uri = Uri
				.parse(getString(R.string.hls_test_url));
		mVideoView.setOnPreparedListener(this);
		mVideoView.setVideoURI(uri);
		mVideoView.start();

		return v;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mProgressBarVideo.setVisibility(View.GONE);
	}

	public void setVideoViewScale(float scaleX, float scaleY) {
		if (mMaxVideoWidth == 0) {
			mMaxVideoWidth = mVideoView.getWidth();
		}

		if (mMaxVideoHeight == 0) {
			mMaxVideoHeight = mVideoView.getHeight();
		}

		if (mMaxVideoWidth > 0) {
			
			mVideoView.changeVideoSize((int) (scaleX * mMaxVideoWidth),
					(int) (scaleY * mMaxVideoHeight));
		}
	}
}
