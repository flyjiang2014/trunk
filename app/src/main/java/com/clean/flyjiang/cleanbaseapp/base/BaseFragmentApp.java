package com.clean.flyjiang.cleanbaseapp.base;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clean.flyjiang.cleanbaseapp.R;
import com.clean.flyjiang.cleanbaseapp.widget.loading.LoadingLayout;


/**
 * 作者：flyjiang
 * 时间: 2015/7/1 14:09
 * 说明: 基于APP的Fragment,最低API为11
 */
@Deprecated
public abstract class BaseFragmentApp extends Fragment {
    protected View rootView;
    protected Context mContext;
    protected boolean isVisible; //是否可见
    protected boolean isPrepared;  //View是否已加载完毕
    protected boolean isFirst = true;//是否第一次加载数据,为false时，切换不在重新加载数据
    protected int visibleTimes =0; //被可见的次数
    protected int pageSize = 10;
    /**
     * 页面加载过程布局
     */
    protected LoadingLayout mLoadingLayout;
    /**
     * 是否使用loading框架
     */
    private boolean isUseLoading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();//使用整个应用的上下文对象
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = initView(inflater);
        }
        if(isUseLoading){
            mLoadingLayout = (LoadingLayout) inflater.inflate(R.layout.loading_layout, null);
            mLoadingLayout.addView(rootView,0); //自定义的界面加载到最底层
            mLoadingLayout.setOnReloadListener(new LoadingLayout.OnReloadListener() { //load点击重试功能
                @Override
                public void onReload(View v) {
                    reLoadData();
                }
            });
        }
        return isUseLoading?mLoadingLayout:rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        lazyLoad();
    }

    public void lazyLoad(){
        if (!isPrepared  || !isVisible  || !isFirst ) {
            return;
        }
        initData();
        isFirst = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            visibleTimes ++;
        }
        if (getUserVisibleHint()) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null && rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }
    /**
     * 子类实现初始化View操作
     */
    protected abstract View initView(LayoutInflater inflater);

    /**
     * 子类实现初始化View本地数据初始化
     */
    protected abstract void initViewData();

    /**
     * 子类实现初始化数据操作(子类自己调用)
     */
    public abstract void initData();

    /**
     * 设置是否使用loading框架
     */
    public void setUseLoading(boolean useLoading) {
        isUseLoading = useLoading;
    }
    /**
     * 获取可见次数
     */
    public int getVisibleTimes() {
        return visibleTimes;
    }

    public LoadingLayout getmLoadingLayout() {
        return mLoadingLayout;
    }

    /**
     * 重试处理，需重写的处理方法
     */
    public void reLoadData() {
    }
}
