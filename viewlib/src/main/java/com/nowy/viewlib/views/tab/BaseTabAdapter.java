package com.nowy.viewlib.views.tab;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Nowy on 2017/9/6.
 */

public abstract class BaseTabAdapter {
    private BottomTabLayout mTabLayout;
    public abstract View getView(int position, View convertView, ViewGroup parent);

    public void bingTabLayout(BottomTabLayout tabLayout){
        this.mTabLayout = tabLayout;
    }

    public void notifyDataSetChanged(){
        if(mTabLayout != null){
            mTabLayout.updateView();
        }
    }


    public int getItemCount(){
        if(mTabLayout != null) return mTabLayout.getChildCount();
        return 0;
    }
}
