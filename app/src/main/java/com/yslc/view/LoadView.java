package com.yslc.view;

import com.yslc.R;
import com.yslc.util.CommonUtil;
import com.yslc.util.ToastUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

/**
 * 自定义加载过程View
 *
 * @author XHH
 */
public class LoadView extends FrameLayout implements OnClickListener {
    // 加载状态(加载中、加载失败，加载成功，加载成功没有数据，没有网络)
    public static final int NONE = 0x00;
    public static final int LOADING = 0x01;
    public static final int ERROR = 0x02;
    public static final int SUCCESS = 0x03;
    public static final int EMPTY_DATA = 0x04;
    private int currStatus = NONE;       // 当前状态

    private Context mContext;
    private ImageView img;
    private TextView tryAgin;

    public LoadView(Context context) {
        super(context);
        mContext = context;
    }

    public LoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public LoadView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    /**
     * 初始化组件
     */
    private void init() {
        img = (ImageView) this.findViewById(R.id.progress);
        tryAgin = (TextView) this.findViewById(R.id.tryAgin);
        tryAgin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tryAgin:
                onTryListener.onTry();
                break;
        }
    }

    /**
     * 重新加载回调
     */
    private OnTryListener onTryListener;

    public void setOnTryListener(OnTryListener onTryListener) {
        this.onTryListener = onTryListener;

        init();
    }

    public interface OnTryListener {
        /**
         * 再次尝试
         */
        void onTry();
    }

    public int getStatus() {
        return currStatus;
    }

    /**
     * 设置加载状态
     */
    public boolean setStatus(int status) {
        currStatus = status;

        /** ---------加载失败----------- **/
        if (status == ERROR) {
            ToastUtil.showMessage(mContext, "加载失败");
            img.clearAnimation();
            img.setVisibility(View.GONE);
            tryAgin.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.getdata_error, 0, 0);
            tryAgin.setText("加载失败，点击重新加载");
            tryAgin.setEnabled(true);
            tryAgin.setVisibility(View.VISIBLE);
        }

        /** ---------加载成功----------- **/
        else if (status == SUCCESS) {
            setVisibility(View.GONE);
        }

        /** ---------没有数据----------- **/
        else if (status == EMPTY_DATA) {
            setVisibility(View.VISIBLE);
            img.clearAnimation();
            img.setVisibility(View.GONE);
            tryAgin.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.no_data, 0, 0);
            tryAgin.setText("暂无数据");
            tryAgin.setVisibility(View.VISIBLE);
            tryAgin.setEnabled(false);
        }

        /** ---------正在加载----------- **/
        else if (status == LOADING) {
            // 判断是否连接网络
            if (!CommonUtil.isNetworkAvalible(mContext)) {
                ToastUtil.showMessage(mContext, "请检查你的网络设置");
                img.setVisibility(View.GONE);
                tryAgin.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.getdata_checknet, 0, 0);
                tryAgin.setVisibility(View.VISIBLE);
                tryAgin.setText("网络设置出错，点击重新加载");
                tryAgin.setEnabled(true);
                return false;
            }

            setVisibility(View.VISIBLE);
            img.setVisibility(View.VISIBLE);
            tryAgin.setVisibility(View.GONE);
            img.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.load_progress_anim));
        }

        return true;
    }

}
