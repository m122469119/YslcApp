package com.yslc.ui.activity;

import com.yslc.ui.base.BaseActivity;
import com.yslc.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 显示单条推送消息
 *
 * @author HH
 */
public class ShowNoticationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_notication;
    }

    @Override
    protected String getToolbarTitle() {
        return "通知消息";
    }

    @Override
    protected void setNavigationIcon(Toolbar toolbar) {
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this,
                R.drawable.rollback));
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowNoticationActivity.this,
                        IndexFragementActivity.class));
                onFinishActivity();
            }
        });
    }

    private void findView() {
        String info = getIntent().getStringExtra("info");
        ((TextView) findViewById(R.id.infoTv)).setText(info);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(ShowNoticationActivity.this,
                IndexFragementActivity.class));
        onFinishActivity();
    }

}
