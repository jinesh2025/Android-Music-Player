/*
 * This is the source code of Music Player Lite for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @CodeCrunch Team, 2016-2017.
 */
package co.codecrunch.musicplayerlite.fragments;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.codecrunch.musicplayerlite.R;
import co.codecrunch.musicplayerlite.tablayout.SlidingTabLayout;
import co.codecrunch.musicplayerlite.childfragment.ChildFragmentAlbum;
import co.codecrunch.musicplayerlite.childfragment.ChildFragmentArtists;
import co.codecrunch.musicplayerlite.childfragment.ChildFragmentGenres;
import co.codecrunch.musicplayerlite.childfragment.ChildFragmentMostPlay;

public class FragmentLibrary extends Fragment {

    private final String[] TITLES = {"ALBUMS", "ALL SONGS", "FAVOURITE", "MOST PLAYED"};
    private TypedValue typedValueToolbarHeight = new TypedValue();
    //private ChildFragmentGenres childFragmentGenres;
    private FragmentFevorite fragmentfevorite;
    private FragmentAllSongs fragmentallsongs;
    //private ChildFragmentArtists childFragmentArtists;
    private ChildFragmentAlbum childFragmentAlbum;
    private ChildFragmentMostPlay childFragmentMostplay;

    private MyPagerAdapter pagerAdapter;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    private int tabsPaddingTop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_library, null);
        setupView(v);
        return v;
    }


    private void setupView(View view) {
        pager = (ViewPager) view.findViewById(R.id.pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getFragmentManager());
        pager.setAdapter(pagerAdapter);
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(false);
        // Tab indicator color
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.md_white_1000);
            }
        });
        tabs.setViewPager(pager);
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
	                childFragmentAlbum = ChildFragmentAlbum.newInstance(position, getActivity());
	                return childFragmentAlbum;
                case 1:
	                fragmentallsongs = FragmentAllSongs.newInstance(position, getActivity());
	                return fragmentallsongs;
                case 2:
	                fragmentfevorite = FragmentFevorite.newInstance(position, getActivity());
	                return fragmentfevorite;
                case 3:
                    childFragmentMostplay = ChildFragmentMostPlay.newInstance(position, getActivity());
                    return childFragmentMostplay;
            }
            return null;
        }
    }

    public int convertToPx(int dp) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }
}
