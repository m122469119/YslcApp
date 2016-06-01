package com.yslc.ui.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yslc.R;

/**
 * Created by Administrator on 2016/5/30.
 */
public class UpdateDialog extends DialogFragment {
    Button update,cancel;

    UpdateListener listener;

    public interface UpdateListener{
        void update();
    }

    public void setUpdateListener(UpdateListener listener) {
        this.listener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update,container);
        update = (Button)view.findViewById(R.id.update_btn);
        cancel = (Button)view.findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.update();
                }
            }
        });
        return view;
    }

}
