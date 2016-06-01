package com.yslc.ui.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.yslc.R;

/**
 * Created by Administrator on 2016/5/30.
 * 更新应用进度对话框
 */
public class ProgressDialog extends DialogFragment {
    Button cancel;
    ProgressBar progress;

    CancelListener listener;

    public interface CancelListener{
        void cancel();
    }

    public void setCancelListener(CancelListener listener) {
        this.listener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_progress,container);
        cancel = (Button)view.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != listener){
                    listener.cancel();
                }
                dismiss();
            }
        });
        progress = (ProgressBar)view.findViewById(R.id.update_progress);
        return view;
    }


    public void setProgress(int progress) {
        this.progress.setProgress(progress);
    }
}
