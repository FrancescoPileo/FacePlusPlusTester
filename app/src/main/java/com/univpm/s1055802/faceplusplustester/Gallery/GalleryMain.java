/*
 *     FacePlusPlusTester - Android application to test the FacePlusPlus' APIs
 *     Copyright (C) 2016-2020  Francesco Antonio Pileo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.univpm.s1055802.faceplusplustester.Gallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.univpm.s1055802.faceplusplustester.Person.PersonActions;
import com.univpm.s1055802.faceplusplustester.R;
import com.univpm.s1055802.faceplusplustester.Utils.Callback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kekko on 25/07/16.
 */
public class GalleryMain extends AppCompatActivity {

    //todo caricamento mentre estrapola frame

    private int numPages = 2;
    private ViewPager mPager;
    private PagerAdapter mPageAdapter;
    private PagerSlidingTabStrip tabs;

    private GalleryFragment activeFragment;

    private Toolbar toolbar = null;
    private Toolbar deleteToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        boolean video = getIntent().getBooleanExtra("video", true);
        if (!video)
            numPages = 1;

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPageAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPageAdapter);

        tabs.setViewPager(mPager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setActiveFragment(position);
                getActiveFragment().enableCheckbox(false);
                showDeleteToolbar(false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        deleteToolbar = (Toolbar) findViewById(R.id.deleteToolbar);
        deleteToolbar.inflateMenu(R.menu.delete_toolbar);
        setDeleteToolbar();

        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete_back_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            setResult(RESULT_OK);
            finish();
        }
        if (id == R.id.action_delete) {
            //enableCheckbox(true);
            getActiveFragment().enableCheckbox(true);
            showDeleteToolbar(true);
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Imposta il Fragment attivo
     * @param position indice del fragment
     */
    private void setActiveFragment(int position){
        activeFragment = (GalleryFragment) ((ScreenSlidePagerAdapter) mPageAdapter).getItem(position);
    }

    /**
     * Ottiene il Fragment attivo
     * @return il Fragment attivo
     */
    private GalleryFragment getActiveFragment(){
        return activeFragment;
    }

    /**
     * gestisce la toolbar di eliminazione
     */
    private void setDeleteToolbar(){
        deleteToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_cancel:
                        getActiveFragment().enableCheckbox(false);
                        showDeleteToolbar(false);
                        break;
                    case R.id.action_all:
                        getActiveFragment().selectAll(true);
                        break;
                    case R.id.action_ok:
                        getActiveFragment().deleteChecked();
                        break;
                }
                return true;
            }
        });
    }

    /**
     * visualizza la toolbar di eliminazione
     */
    private void showDeleteToolbar(boolean flag){
        if (flag) {
            toolbar.setVisibility(View.GONE);
            deleteToolbar.setVisibility(View.VISIBLE);
        } else {
            deleteToolbar.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Page adapter che permette la visualizzazione dei due Fragment (Immagini e Video)
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private final String[] TITLES = {"Photo", "Video"};
        private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            Fragment imageFragment = new GalleryImagesFragment();
            fragmentArrayList.add(0, imageFragment);
            Fragment videoFragment = new GalleryVideosFragment();
            fragmentArrayList.add(1, videoFragment);
            activeFragment = (GalleryFragment) fragmentArrayList.get(0);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return numPages;
        }
    }
}
