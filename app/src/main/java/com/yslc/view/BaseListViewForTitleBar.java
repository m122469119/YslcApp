package com.yslc.view;

import com.yslc.R;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

/**
 * 基类ListView，实现加载更多
 * <p>相比BaseListView多了TitleBar部分，可以考虑重构</p>
 * @author HH
 */
public class BaseListViewForTitleBar extends ListView implements OnScrollListener {
    private int refreshLength = 15;
    private boolean isRefresh = true;
    private OnLoadMoreListener listener;
    private BaseListViewFooter footerView;
    private View titleBar;
    private boolean isFirst = false;
    private int defalutTop = 0;

    public BaseListViewForTitleBar(Context context) {
        super(context);
        footerView = new BaseListViewFooter(context);
        BaseListViewForTitleBar.this.addFooterView(footerView);
        setOnScrollListener(this);
    }

    public BaseListViewForTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        footerView = new BaseListViewFooter(context);
        BaseListViewForTitleBar.this.addFooterView(footerView);
        setOnScrollListener(this);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.listener = listener;
    }

    public void setTitleBar(View titleBar) {
        this.titleBar = titleBar;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (view.getChildCount() > 0) {

            if (!isFirst) {
                isFirst = true;
                defalutTop = view.getChildAt(0).getTop();
            }

            if (view.getChildAt(0).getTop() >= defalutTop) {
                titleBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));
            } else {
                titleBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.titleBg));
            }
        }

        if ((isRefresh && firstVisibleItem + visibleItemCount == totalItemCount)
                && (totalItemCount > 1)) {
            if (getAdapter().getCount() >= refreshLength && null != listener) {
                isRefresh = false;

                // 加载更多
                if (!footerView.isShown()) {
                    BaseListViewForTitleBar.this.addFooterView(footerView);
                    footerView.setVisibility(View.VISIBLE);
                }

                footerView.setState(BaseListViewFooter.STATE_LOADING);

                this.listener.onLoadMore();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /**
     * 加载更多
     */
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    /**
     * 加载更多完成
     */
    public void onFinishLoad() {
        isRefresh = true;
        footerView.setState(BaseListViewFooter.STATE_FINISH);
    }

    /**
     * 没有更多了
     */
    public void noMoreData() {
        isRefresh = false;
        footerView.setState(BaseListViewFooter.STATE_NODATA);
    }

}
