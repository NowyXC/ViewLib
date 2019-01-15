package com.nowy.viewlib.views.convenientbanner.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nowy.viewlib.R;


/**
 * Created by Sai on 15/8/4.
 * 本地图片Holder例子
 */
public class NetWorkImageHolderView implements Holder<String> {

    private ImageView imageView;

    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.drawable.ic_def_4_3);

        return imageView;
    }


    @Override
    public void UpdateUI(Context context,View itemView, int position, String data) {
        Glide.with(context).load(data).into(imageView);
    }

}
