package com.nowy.viewlib.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.nowy.baselib.utils.T;
import com.nowy.baselib.utils.TextUtil;
import com.nowy.viewlib.manager.LoadManager;


/**
 * Created by Nowy on 2018/1/12.
 */

public class BaseLoadDialogAty extends AppCompatActivity {
    private LoadManager mLoadManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLoadManager();
    }


    private void initLoadManager() {
        mLoadManager = new LoadManager(this);
    }


    public void showTips(String msg){
        if(!TextUtil.isEmpty(msg))
            T.s(msg);
    }

    public void showLoadDialog() {
        if (mLoadManager != null)
            mLoadManager.showLoadDialog();
    }


    public void dismissDialog(){
        if (mLoadManager != null)
            mLoadManager.dismissDialog();
    }

    @Override
    protected void onDestroy() {
        if (mLoadManager != null){
            mLoadManager.onDestroy();
        }
        super.onDestroy();
    }
}
