package com.yslc.ui.dialog;

import com.yslc.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

/**
 * 定义通用基类Dialog, 去除标题，去除背景
 *
 * @author HH
 */
public class BaseDialog extends Dialog {

    public BaseDialog(Context context) {
        super(context, R.style.Dialog);

        // 去除标题，去除背景
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

}
