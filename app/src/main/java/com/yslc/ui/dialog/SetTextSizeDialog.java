package com.yslc.ui.dialog;

import com.yslc.R;

import android.content.Context;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 设置WebView字体弹出框
 *
 * @author HH
 */
public class SetTextSizeDialog extends BaseDialog {
    private View view = null;
    private OnTextSize onTextSize;
    private TextSizeEnum currentSize;

    /**
     * WebView字体大小枚举（小、中、大）
     */
    public enum TextSizeEnum {
        TYPE_SMALL, TYPE_MEDIUM, TYPE_BIG,
    }

    public SetTextSizeDialog(Context context, TextSizeEnum ts) {
        super(context);

        currentSize = ts;
        view = View
                .inflate(context, R.layout.dialog_set_webview_textsize, null);

        // 初始化界面
        findView();

        setContentView(view);

        show();
    }

    /**
     * 初始化界面
     */
    private void findView() {
        RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioGroup);
        rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.smallTextSize:
                        setTextSize(TextSizeEnum.TYPE_SMALL);
                        break;

                    case R.id.mediumTextSize:
                        setTextSize(TextSizeEnum.TYPE_MEDIUM);
                        break;

                    case R.id.bigTextSize:
                        setTextSize(TextSizeEnum.TYPE_BIG);
                        break;
                }
            }
        });

        if (currentSize == TextSizeEnum.TYPE_SMALL) {
            rg.check(R.id.smallTextSize);
        } else if (currentSize == TextSizeEnum.TYPE_MEDIUM) {
            rg.check(R.id.mediumTextSize);
        } else {
            rg.check(R.id.bigTextSize);
        }

    }

    /**
     * 设置大小
     *
     * @param ts
     */
    private void setTextSize(TextSizeEnum ts) {
        if (ts != currentSize && null != onTextSize) {
            // 更改字体大小，进行回调
            currentSize = ts;
            onTextSize.setTextSize(currentSize);
        }

        this.dismiss();
    }

    public void setOnTextSize(OnTextSize onTextSize) {
        this.onTextSize = onTextSize;
    }

    public interface OnTextSize {
        // 设置字体大小回调
        void setTextSize(TextSizeEnum ts);
    }

}
