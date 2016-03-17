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
 *
 * @author HH
 */
public class BaseListView extends ListView implements OnScrollListener {
    private int refreshLength = 15;
    private boolean isRefresh = true;
    private OnLoadMoreListener listener;
    private BaseListViewFooter footerView;

    public BaseListView(Context context) {
        super(context);
        init(context);
    }

    public BaseListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        footerView = new BaseListViewFooter(context);
        addFooterView(footerView);
        setFooterDividersEnabled(false);
        setHeaderDividersEnabled(false);
        setSelector(ContextCompat.getDrawable(context,
                R.drawable.listview_selector));
        setOnScrollListener(this);
    }

    public void setRefreshLength(int refreshLength) {
        // 设置刷新条数
        this.refreshLength = refreshLength;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.listener = listener;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if ((isRefresh && firstVisibleItem + visibleItemCount == totalItemCount)
                && totalItemCount > 1) {
            if ((getAdapter().getCount() - getHeaderViewsCount() - getFooterViewsCount()) >= refreshLength
                    && null != listener) {
                isRefresh = false;

                // 加载更多
                if (!footerView.isShown()) {
                    footerView.setVisibility(View.VISIBLE);
                }

                footerView.setState(BaseListViewFooter.STATE_LOADING);
                listener.onLoadMore();
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
