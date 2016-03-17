package com.yslc.ui.dialog;

import com.yslc.R;
import com.yslc.util.HttpUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * 网络请求等待框
 * 进行网络请求的时候弹出该框，提示用户等待
 *
 * @author HH
 */
public class LoadingDialog extends BaseDialog {
    private View v;
    private Context context;

    public LoadingDialog(Context context, String text) {
        super(context);
        this.context = context;
        v = View.inflate(context, R.layout.dialog_loading, null);
        findView(text);

        setContentView(v);
        setCancelable(false);

        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // 单击返回后取消该等待框，同时取消该次网络请求
                HttpUtil.closeHttp(LoadingDialog.this.context);
            }
        });
    }

    /**
     * 初始化
     *
     * @param text 提示信息
     */
    private void findView(String text) {
        if (null == text) {
            v.findViewById(R.id.info).setVisibility(View.GONE);
        } else {
            ((TextView) v.findViewById(R.id.info)).setText(text);
        }

        // 圆形进度条进行旋转
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.load_progress_anim);
        v.findViewById(R.id.progress).startAnimation(hyperspaceJumpAnimation);
    }

}
