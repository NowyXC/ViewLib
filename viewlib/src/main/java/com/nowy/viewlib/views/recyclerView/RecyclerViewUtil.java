package com.nowy.viewlib.views.recyclerView;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;

/**
 * Created by Nowy on 2018/4/28.
 */

public class RecyclerViewUtil {
    public static void clearAnim(RecyclerView recyclerView){
        if(recyclerView == null) return;
        if(recyclerView.getItemAnimator() instanceof SimpleItemAnimator){
            SimpleItemAnimator simpleItemAnimator = ((SimpleItemAnimator)recyclerView.getItemAnimator());
            simpleItemAnimator.setSupportsChangeAnimations(false);
        }
    }
}
