
package com.yslc.view;

import com.yslc.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义ListView FooterView
 * 实现加载状态切换变化
 *
 * @author HH
 */
public class BaseListViewFooter extends RelativeLayout {
    public final static int STATE_LOADING = 0;
    public final static int STATE_FINISH = 1;
    public final static int STATE_NODATA = 2;

    private View mContentView;
    private View mProgressBar;
    private TextView mHintView;

    public BaseListViewFooter(Context context) {
        super(context);
        initView(context);
    }

    public BaseListViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        RelativeLayout moreView = (RelativeLayout) View.inflate(context,
                R.layout.base_listview_footer, null);
        addView(moreView);
        moreView.setLayoutParams(new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        mContentView = moreView.findViewById(R.id.xlistview_footer_content);
        mContentView.setVisibility(View.GONE);
        mProgressBar = moreView.findViewById(R.id.xlistview_footer_progressbar);
        mHintView = (TextView) moreView
                .findViewById(R.id.xlistview_footer_hint_textview);
    }

    public void setState(int state) {
        /** ----------加载状态---------- **/
        if (state == STATE_LOADING) {
            loading();
        }

        /** ----------结束加载状态---------- **/
        else if (state == STATE_FINISH) {
            onFinish();
        }

        /** ----------没有更多数据了---------- **/
        else {
            noMore();
        }
    }

    /**
     * normal status
     */
    private void onFinish() {
        mContentView.setVisibility(View.GONE);
    }

    /**
     * loading status
     */
    private void loading() {
        mHintView.setVisibility(View.GONE);
        mContentView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * no more
     */
    private void noMore() {
        mContentView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mHintView.setVisibility(View.VISIBLE);
        mHintView.setText(R.string.xlistview_footer_hint_onmore);
    }

}
