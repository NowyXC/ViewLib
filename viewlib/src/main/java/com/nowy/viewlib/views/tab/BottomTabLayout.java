package com.nowy.viewlib.views.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nowy.baselib.utils.ViewUtil;
import com.nowy.viewlib.R;


/**
 * Created by Nowy on 2017/7/13.
 */

public class BottomTabLayout extends LinearLayout {

    private int mCheckedId;//当前选中ID
    private OnGroupCheckedChangeListener mOnCheckedChangeListener;

    private boolean isUpdating = false;//是否在更新界面
    private BaseTabAdapter mTabAdapter;//tab适配器
    private TabItemView.Mode mMode;
    private int mImgMode;//图片控件显示模式
    public BottomTabLayout(Context context) {
        super(context);
    }

    public BottomTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public BottomTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }


    private void init(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BottomTabLayout);

        mMode  = TabItemView.Mode.getMode(a.getInt(R.styleable.BottomTabLayout_btl_tabItem_mode, TabItemView.Mode.COMPLEX.value));
        mImgMode  = a.getInt(R.styleable.BottomTabLayout_btl_tabItem_img_mode, TabItemView.IMG_MODE_WRAP);
        a.recycle();
    }



    //TODO 现在只支持初始化时候的修改，要做到后面动态刷新需要修改刷新操作：updateView()中添加mMode和mImgMode设置
    public void setTabMode(TabItemView.Mode mMode) {
        this.mMode = mMode;
    }

    public void setTabMode(TabItemView.Mode mMode, int imgMode) {
        this.mMode = mMode;
        this.mImgMode = imgMode;
    }

    public TabItemView.Mode getTabMode() {
        return mMode;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mCheckedId != -1) {
            setCheckedStateForView(mCheckedId, true);
            setCheckedId(mCheckedId);
        }
    }


    /**
     * 重写addView方法，为没有ID的控件添加随机ID,同时设置选中项
     * @param child
     * @param index
     * @param params
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof TabItemView) {
            final TabItemView itemView = (TabItemView) child;
            itemView.setTabMode(mMode,mImgMode);
//            itemView.setOnClickListener(mOnClickListener);
            setClickListener(itemView,getChildCount());
            int id = child.getId();
            if (id == View.NO_ID) {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                    child.setId(ViewUtil.generateViewId());
//                } else {
//                    child.setId(View.generateViewId());
//                }

                ViewUtil.setViewId(child);
            }
            if (itemView.isChecked()) {
                if (mCheckedId != -1) {
                    setCheckedStateForView(mCheckedId, false);
                }
                setCheckedId(itemView.getId());
            }
        }
        super.addView(child, index, params);

    }


    @IdRes
    public int getCheckedId() {
        return mCheckedId;
    }

    public void clearCheck() {
        check(-1);
    }



    private void setCheckedId(@IdRes int id) {
       setCheckedId(id,true);
    }

    /**
     * 设置选中ID,注意初始化的时候一样会调用
     * 方法中存在{@link OnGroupCheckedChangeListener#onCheckedChanged}监听器，在初始化的时候一样会收到回调
     * @param id
     * @param canMonitor 是否通知监听器
     */
    public void setCheckedId(@IdRes int id,boolean canMonitor) {
        mCheckedId = id;
        if (canMonitor && mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mCheckedId);
        }
    }



    private void initView(BaseTabAdapter adapter){
        if(adapter != null){
            this.mTabAdapter.bingTabLayout(this);
            removeAllViews();
            int size = adapter.getItemCount();
            LayoutParams lp = new LayoutParams(0,
                    ViewGroup.LayoutParams.MATCH_PARENT,1);

            for(int i = 0 ; i < size ; i++){
                View child = adapter.getView(i,null,this);
               if(child.getLayoutParams()!=null){
                   lp = (LayoutParams) child.getLayoutParams();
               }
                addView(child,lp);
                setClickListener(child,i);
//                child.setOnClickListener(mOnClickListener);
            }
            invalidate();
        }
    }


    /**
     * 更新当前的布局
     */
    public void updateView(){
        if(isUpdating) return;
        isUpdating = true;
        int count = mTabAdapter.getItemCount();
        int childViewCount = this.getChildCount();
        LayoutParams lp = new LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT,1);
        for(int  i = 0 ; i < count ; i++){
            if(count < childViewCount){//复用
                View convertView = this.getChildAt(i);
                mTabAdapter.getView(i,convertView,this);

            }else{//新加
                View child = mTabAdapter.getView(i,null,this);
                if(child.getLayoutParams()!=null){
                    lp = (LayoutParams) child.getLayoutParams();
                }
                addView(child,lp);
                setClickListener(child,i);
//                child.setOnClickListener(mOnClickListener);
            }
        }

        //多的需要移除
        for (int i = childViewCount - 1; i >= count; i--) {
            this.removeViewAt(i);
        }
        invalidate();//更新布局
        isUpdating = false;
    }


    /**
     * 设置适配器
     * 会清空之前绑定的数据
     * @param mTabAdapter tab适配器
     */
    public void setAdapter(BaseTabAdapter mTabAdapter) {
        this.mTabAdapter = mTabAdapter;
        initView(mTabAdapter);
    }

    public BaseTabAdapter getTabAdapter() {
        return mTabAdapter;
    }

    /**
     * 选中状态切换，复原原选中的默认状态，
     * 根据传入的resId设置新的选中控件的状态
     * @param id resId
     */
    public void check(@IdRes int id) {
        check(id,true);
    }


    /**
     * 选中状态切换，复原原选中的默认状态，
     * 根据传入的resId设置新的选中控件的状态
     * @param id
     * @param canMonitor 是否通知变化监听器
     */
    public void check(@IdRes int id,boolean canMonitor) {
        if (id != -1 && (id == mCheckedId)) {
            return;
        }
        if (mCheckedId != -1) {
            setCheckedStateForView(mCheckedId, false);
        }

        if (id != -1) {
            setCheckedStateForView(id, true);
        }
        setCheckedId(id,canMonitor);
    }


    /**
     * 适配器模式下的选中设置
     * 适配器模式下，TabItemView的ID是动态生成的，
     * 故无法获取，通过POS去查找TabItemView
     * （adapter没有人为插入其他控件，所以getChildAt(pos)可以获取正确的TabItemView）
     * @param pos
     * @param canMonitor
     */
    public void setCheckedByPos(int pos,boolean canMonitor){
        if(mTabAdapter == null || pos != -1 && pos >= mTabAdapter.getItemCount()){
            return;
        }
        View child  = this.getChildAt(pos);
        checkIgnorePos((TabItemView) child,null,canMonitor);
    }


    /**
     * 根据child的ID查找它在layout中的位置
     * @param childId
     * @return
     */
    public int getChildIndexById(@IdRes int childId){
        if(childId < 0) return  -1;
        return indexOfChild(findViewById(childId));
    }


    /**
     * 过滤选中操作
     * @param id
     * @param ignoreIds
     */
    public void checkIgnoreIds(@IdRes int id,int[] ignoreIds){
        checkIgnoreIds(id,ignoreIds,true);
    }


    /**
     * 过滤选中操作
     * @param id
     * @param ignoreIds 过滤名单（id）
     */
    public void checkIgnoreIds(@IdRes int id,int[] ignoreIds,boolean canMonitor) {
        if (id != -1 && (id == mCheckedId)) {
            return;
        }
        if(ignoreIds != null){
            for(int ignore : ignoreIds){
                if(ignore == mCheckedId){//过滤名单中包含则过滤选中操作
                    return;
                }
            }
        }

        if (mCheckedId != -1) {
            setCheckedStateForView(mCheckedId, false);
        }

        if (id != -1) {
            setCheckedStateForView(id, true);
        }

        setCheckedId(id,canMonitor);
    }


    /**
     * 过滤选中操作
     * @param child
     * @param ignorePos
     */
    public void checkIgnorePos(TabItemView child, int[] ignorePos){
        checkIgnorePos(child,ignorePos,true);
    }

    /**
     * 过滤选中操作
     * @param child 选中的View
     * @param ignorePos 过滤名单(条目位置Position)
     */
    public void checkIgnorePos(TabItemView child, int[] ignorePos, boolean canMonitor) {
        if (child == null || (child.getId() == mCheckedId)) {
            return;
        }
        if(ignorePos != null){
            int position = this.indexOfChild(child);
            for(int ignore : ignorePos){
                if(ignore == position){//过滤名单中包含则过滤选中操作
                    return;
                }
            }
        }


        if (mCheckedId != -1) {
            setCheckedStateForView(mCheckedId, false);
        }

        if (child.getId() != -1) {
            setCheckedStateForView(child.getId(), true);
        }
        setCheckedId(child.getId(),canMonitor);
    }

    /**
     * 根据id设置TabItemView的选中状态
     * @param viewId resId
     * @param checked 选中状态
     */
    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);
        if (checkedView != null && checkedView instanceof TabItemView) {
            ((TabItemView) checkedView).setChecked(checked);
        }
    }


    public interface OnGroupCheckedChangeListener{
        void onCheckedChanged(BottomTabLayout layout, @IdRes int checkedId);
    }


//    OnClickListener mOnClickListener = new OnClickListener(){
//        @Override
//        public void onClick(View v) {
//            check(v.getId());
//        }
//    };


    /**
     * 默认条目点击事件
     * 作用：修改子控件的点击状态，并修改当前选中条目的标识符
     * 有些需求：过滤一些选项不参与点击切换选中，却需要点击后续监听，可以在此处修改(重新设置点击事件)
     *
     */
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            check(view.getId());
        }
    };


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
        setAllListener();
    }

    /**
     * 设置所有条目的点击事件
     */
    private synchronized void setAllListener(){
        int count = this.getChildCount();
        if(mOnItemClickListener != null){
            for(int i = 0 ; i < count ; i++){
                View child = this.getChildAt(i);
//                child.setOnClickListener(mOnClickListener);
                setClickListener(child,i);
            }
        }
    }


    //设置ChildClickListener
    private void setClickListener(View view, final int position){
        view.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(v,position);
                }
            }
        });
    }





    public void setOnCheckedChangeListener(OnGroupCheckedChangeListener onCheckedChangeListener) {
        this.mOnCheckedChangeListener = onCheckedChangeListener;
    }


    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
}
