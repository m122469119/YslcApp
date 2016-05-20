package com.yslc.ui.activity;

import android.content.Intent;
import android.widget.TextView;

import com.yslc.ui.base.BaseActivity;
import com.yslc.R;

/**
 * Created by Administrator on 2016/4/1.
 */
public class FastInfoDetailActivity extends BaseActivity {
    private TextView title,date,content;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fast_info_detial;
    }

    @Override
    protected String getToolbarTitle() {
        return "情报详情";
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        date = (TextView) findViewById(R.id.date);
        date.setText(intent.getStringExtra("date"));
        title = (TextView)findViewById(R.id.title);
        title.setText(intent.getStringExtra("title"));
        content = (TextView)findViewById(R.id.content);
        content.setText(intent.getStringExtra("content"));
//        parseContent(title,content,intent.getStringExtra("content"));
    }

    /**
     * 解析data并设置在title和content中
     * @param title
     * @param content
     * @param data
     */
//    private void parseContent(TextView title, TextView content, String data) {
//        String[] temp = data.split("】");
//        title.setText(temp[0].substring(1));
//        content.setText(temp[1]);
//    }
}
