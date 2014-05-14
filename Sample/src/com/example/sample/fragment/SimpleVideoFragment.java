package com.example.sample.fragment;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.example.sample.R;

public class SimpleVideoFragment extends Fragment implements OnPreparedListener {
	private VideoView mVideoView;
	private ProgressBar mProgressBarVideo;
	
	public static SimpleVideoFragment getInstance() {
		return new SimpleVideoFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.frag_simple_video, container, false);

		mVideoView = (VideoView) v.findViewById(R.id.videoView);
		mProgressBarVideo = (ProgressBar) v.findViewById(R.id.progressBarVideo);
		
		mProgressBarVideo.setVisibility(View.VISIBLE);

		Uri uri = Uri.parse(getString(R.string.hls_test_url));
		mVideoView.setOnPreparedListener(this);
		mVideoView.setVideoURI(uri);
		mVideoView.start();
		
		return v;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mProgressBarVideo.setVisibility(View.GONE);
	}
}
