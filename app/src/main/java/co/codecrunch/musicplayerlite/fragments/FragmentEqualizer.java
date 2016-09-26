/*
 * This is the source code of Music Player Lite for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @CodeCrunch Team, 2016-2017.
 */
package co.codecrunch.musicplayerlite.fragments;

import co.codecrunch.musicplayerlite.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentEqualizer extends Fragment {

	public FragmentEqualizer() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootview = inflater.inflate(R.layout.fragment_equalizer, null);
		setupInitialViews(rootview);
		return rootview;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void setupInitialViews(View inflatreView) {

	}
}
