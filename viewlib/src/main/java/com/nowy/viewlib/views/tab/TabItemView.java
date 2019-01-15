package com.nowy.viewlib.views.tab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.nowy.baselib.utils.AppUtil;
import com.nowy.viewlib.R;


import java.io.File;
import java.lang.ref.WeakReference;


/**
 * Created by Nowy on 2017/7/13.
 */

public class TabItemView extends FrameLayout {
    // 代表选中状态的集合
    private static final int[] CHECK_STATE_SET = new int[] {
            android.R.attr.state_checked
    };
    private static final int DEFAULT_COLOR = Color.GRAY;
    private static final int IMG_HEIGHT_DEF = 24;//dp 图片控件默认高度

    public static final int IMG_MODE_MATCH = 2;//图片显示模式-填充整个父容器宽高
    public static final int IMG_MODE_WRAP  =  1;//图片显示模式-包裹内容

    //显示模式
    public enum Mode{
        COMPLEX(0),//图片+文字
        IMAGE(1),//单图片
        TEXT(2);//单文字

        // 定义私有变量
        public int value ;

        // 构造函数，枚举类型只能为私有
        private Mode( int value) {
            this .value = value;
        }

        @Override
        public String toString() {
            return String.valueOf (this.value);
        }

        public static Mode getMode(int value){
            switch (value){
                case 1:
                    return Mode.IMAGE;
                case 2:
                    return Mode.TEXT;
                default:
                   return Mode.COMPLEX;
            }
        }
    }

    private View mTabView;
    private ImageView mIvIcon;
    private TextView mTvTitle;
    private ImageView mIvRedPoint;

//    private int mCurTextColor;
    private boolean mChecked;
    private OnCheckedChangeListener mOnCheckChangedListener;
    private ColorStateList mTextColorList;
//    private Drawable mIcon;
    private StateListDrawable mStateListDrawable;


    private Mode mMode;//当前模式
    private int mIconHeight;//图片的宽高，暂时作为正方形显示
    private static final int UPDATE_IMG = 3;
    private MyHandler mHandler = new MyHandler(this);
//    {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case UPDATE_IMG :
//                    StateListDrawable stateListDrawable = (StateListDrawable) msg.obj;
//                    setIcon(stateListDrawable);
//                    drawableStateChanged();
//                    break;
//            }
//        }
//    };

    static class MyHandler extends Handler{
        WeakReference<TabItemView> mTabItemRef;

        public MyHandler(TabItemView tabItem) {
            mTabItemRef = new WeakReference<>(tabItem);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_IMG :
                    if(mTabItemRef.get() != null){
                        StateListDrawable stateListDrawable = (StateListDrawable) msg.obj;
                        mTabItemRef.get().setIcon(stateListDrawable);
                        mTabItemRef.get().drawableStateChanged();
                    }
                    break;
            }
        }
    }


    public static TabItemView obtain(ViewGroup parent, LinearLayout.LayoutParams lp){
        TabItemView tabItemView = new TabItemView(parent.getContext());
        tabItemView.setLayoutParams(lp);
        return tabItemView;
    }


    public TabItemView(Context context) {
        super(context);
        init(context);
        setupDataDef();
    }

    public TabItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setupData(context,attrs);
    }


    public TabItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        setupData(context,attrs);
    }



    private void init(Context context){
        mTabView = LayoutInflater.from(context).inflate(R.layout.tab_item,null);
//        mTabView = inflate(context, R.layout.tab_item,this);
        LayoutParams lp= new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mTabView,lp);
        mIvIcon =  mTabView.findViewById(R.id.tab_item_IvIcon);
        mTvTitle =  mTabView.findViewById(R.id.tab_item_TvTitle);

        mIvRedPoint = new ImageView(context);
        mIvRedPoint.setImageResource(R.drawable.bg_shape_o_red_small);
        LayoutParams layoutParams =
                new LayoutParams(AppUtil.dip2px(getContext(),4.0f), AppUtil.dip2px(getContext(),4.0f));
        layoutParams.topMargin = AppUtil.dip2px(getContext(),6.0f);
        layoutParams.rightMargin = AppUtil.dip2px(getContext(),14.0f);
        layoutParams.gravity =  Gravity.END;
        addView(mIvRedPoint,layoutParams);

    }


    private void setupData(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabItemView);

        Drawable d = a.getDrawable(R.styleable.TabItemView_tabItem_icon);
        setIcon(d);

        CharSequence title = a.getText(R.styleable.TabItemView_tabItem_title);
        if(title != null){
            setText(title);
        }
        mTextColorList = a.getColorStateList(R.styleable.TabItemView_tabItem_txtColor);
        setTextColor(mTextColorList != null ? mTextColorList : ColorStateList.valueOf(DEFAULT_COLOR));
        boolean isSelected = a.getBoolean(R.styleable.TabItemView_tabItem_checked,false);
        setChecked(isSelected);
        boolean showRedPoint = a.getBoolean(R.styleable.TabItemView_tabItem_showRedPoint,false);
        showRedPoint(showRedPoint);

        mMode  = Mode.getMode(a.getInt(R.styleable.TabItemView_tabItem_mode, Mode.COMPLEX.value));
        showViewByMode(mMode);
        a.recycle();
    }


    public void setTabMode(Mode mode, final int imgMode) {
        this.mMode = mode;
        if(mMode == null)
            this.mMode = Mode.COMPLEX;
        post(new Runnable() {
            @Override
            public void run() {
                showViewByMode(mMode);
                if(Mode.IMAGE == mMode
                        && imgMode == IMG_MODE_MATCH){
                    changeIconHeightMatch();
                }else{
                    changeIconHeightDef();
                }
            }
        });
    }

    public void setTabMode(Mode mode) {
        this.mMode = mode;
        if(mMode == null)
            this.mMode = Mode.COMPLEX;
        post(new Runnable() {
            @Override
            public void run() {
                showViewByMode(mMode);
            }
        });
    }





    private void showViewByMode(Mode mode){
        if(mode == Mode.IMAGE){//单图片展示
            mIvIcon.setVisibility(VISIBLE);
            mTvTitle.setVisibility(GONE);
        }else if(mode == Mode.TEXT){//单文本展示
            mIvIcon.setVisibility(GONE);
            mTvTitle.setVisibility(VISIBLE);
        }else{//默认，图+文
            mIvIcon.setVisibility(VISIBLE);
            mTvTitle.setVisibility(VISIBLE);
        }

    }


    private void changeIconHeightMatch(){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        mIvIcon.setLayoutParams(lp);
    }

    private void changeIconHeightDef(){
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mIvIcon.getLayoutParams();
        lp.height = AppUtil.dip2px(getContext(),IMG_HEIGHT_DEF);
        lp.weight = LinearLayout.LayoutParams.WRAP_CONTENT;
        mIvIcon.setLayoutParams(lp);
    }


    private void setupDataDef(){
        showRedPoint(false);
    }





    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        if (!mChecked) {
            // 如果未选中，直接返回父类的结果
            return super.onCreateDrawableState(extraSpace);
        } else {
            // 如果选中，将父类的结果和选中状态合并之后返回
            final int[] drawableState = super
                    .onCreateDrawableState(extraSpace + 1);
            return mergeDrawableStates(drawableState, CHECK_STATE_SET);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        //根据状态修改控件的UI显示
        if(mStateListDrawable != null && mStateListDrawable.isStateful()){
            mStateListDrawable.setState(getDrawableState());
            setIcon(mStateListDrawable.getCurrent()); //图标修改
        }

        if (mTextColorList != null && mTextColorList.isStateful()){
            int color = mTextColorList.getColorForState(getDrawableState(),
                    DEFAULT_COLOR);
            mTvTitle.setTextColor(color);//文本颜色就改
        }
    }


    public void setIcon(Drawable drawable){
        if(drawable instanceof BitmapDrawable){//区分bmp
            BitmapDrawable bmp = (BitmapDrawable) drawable;
            mIvIcon.setImageDrawable(bmp);
        }else if(drawable instanceof StateListDrawable){//多状态的drawable
            mStateListDrawable = (StateListDrawable) drawable;
            mStateListDrawable.mutate(); // make sure that we aren't sharing state anymore
            mIvIcon.setImageDrawable(mStateListDrawable);
        }
    }

    public void setIcon(@DrawableRes int imgRes){
        Drawable d = ContextCompat.getDrawable(getContext(),imgRes);
        setIcon(d);
    }


    /**
     * 通过路径设置ImageView的图标。
     * @param checkedImgPath 选中时的图片路径
     * @param defImgPath 默认时的图片路径
     * @param iconHeight 图片显示的宽高，暂时只支持正方形
     */
    public void setIconPath(final String checkedImgPath, final String defImgPath, int iconHeight){

        final StateListDrawable stateListDrawable = new StateListDrawable();
        final int checked = android.R.attr.state_checked;
        mIconHeight = iconHeight != 0 ? iconHeight : Target.SIZE_ORIGINAL;

        RequestBuilder<Drawable> request;
        if(checkedImgPath.startsWith("http")){//网络
            request = Glide.with(getContext()).load(checkedImgPath);
        }else{//本地
            request = Glide.with(getContext()).load(new File(checkedImgPath));
        }


        request.into(new SimpleTarget<Drawable>(mIconHeight,mIconHeight) {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                stateListDrawable.addState(new int[]{checked},resource);
//                drawableStateChanged();
                sendUpdateImgMsg(stateListDrawable);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
            }
        });
        if(defImgPath.startsWith("http")){//网络
            request = Glide.with(getContext()).load(defImgPath);
        }else{//本地
            request = Glide.with(getContext()).load(new File(defImgPath));
        }
        request.into(new SimpleTarget<Drawable>(mIconHeight,mIconHeight) {
            @Override
            public void onResourceReady(@NonNull Drawable resource,  @Nullable Transition<? super Drawable> transition) {
                stateListDrawable.addState(new int[]{-checked},resource);
                sendUpdateImgMsg(stateListDrawable);

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
            }
        });

    }



    private void sendUpdateImgMsg(StateListDrawable drawableList){
        Message msg = mHandler.obtainMessage();
        msg.what = UPDATE_IMG;
        msg.obj = drawableList;
        mHandler.sendMessage(msg);
    }



    public void setText(CharSequence txt){
        mTvTitle.setText(txt);
    }


    /**
     * 设置textView的字体颜色
     * @param checkedColor 选中颜色
     * @param defColor  默认颜色
     */
    public void setTextColor(int checkedColor,int defColor){
        int[] colors = new int[] { checkedColor, defColor};
        final int checked = android.R.attr.state_checked;
        int[][] states = new int[2][];
        states[0] = new int[]{checked};
        states[1] = new int[]{};
        ColorStateList colorStateList = new ColorStateList(states,colors);
        setTextColor(colorStateList);
    }


    public void setTextColor(ColorStateList colorStateList){
        this.mTextColorList = colorStateList != null ? colorStateList : ColorStateList.valueOf(DEFAULT_COLOR);
        mTvTitle.setTextColor(mTextColorList);
    }


    /**
     * 设置选中状态，状态不一样的情况下刷新图片状态和文本颜色（根据{{@link View#getDrawableState()}的值改变）
     * 同时对外暴露OnCheckChangedListener监听器，提供选中监听
     * @param isChecked 是否选中
     */
    public void setChecked(boolean isChecked){
        if (mChecked != isChecked) {
            mChecked = isChecked;
            refreshDrawableState();
//            updateTextColors();

            if(mOnCheckChangedListener != null){
                mOnCheckChangedListener.onCheckedChanged(this,mChecked);
            }
        }

    }


//    /**
//     * textView根据状态修改颜色
//     */
//    private void updateTextColors() {
//        boolean inval = false;
//        int color = mTextColorList.getColorForState(getDrawableState(), 0);
//        if (color != mCurTextColor) {
//            mCurTextColor = color;
//            inval = true;
//        }
//        if (inval) {
//            // Text needs to be redrawn with the new color
//            invalidate();
//        }
//    }


    /**
     * 是否选中
     * @return 是否选中
     */
    public boolean isChecked() {
        return mChecked;
    }

    /**
     * 是否显示小红点,暂时不支持显示数字
     * @param isShow 显示为true
     */
    public void showRedPoint(boolean isShow){
        mIvRedPoint.setVisibility(isShow ? VISIBLE : GONE);
    }


    /**
     * 隐藏小红点
     */
    public void hideShowPoint(){
        mIvRedPoint.setVisibility(GONE);
    }


    public void setOnCheckChangedListener(OnCheckedChangeListener onCheckChangedListener) {
        this.mOnCheckChangedListener = onCheckChangedListener;
    }



    public interface OnCheckedChangeListener {
        void onCheckedChanged(TabItemView buttonView, boolean isChecked);
    }
}
