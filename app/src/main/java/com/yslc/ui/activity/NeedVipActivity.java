package com.yslc.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yslc.R;
import com.yslc.app.Constant;
import com.yslc.util.SharedPreferencesUtil;

public class NeedVipActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button botton;
    private LinearLayout linearLayout;
    private Button pay, history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_vip);
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setNavigationIcon(toolbar);//设置返回键

        linearLayout = (LinearLayout)findViewById(R.id.button_group);


        if(getIntent().getStringExtra("activity").equals("FastInfoActivity")){
            initFastInfo();
            saveActivity(SharedPreferencesUtil.FAST_INFO);
        }else if(getIntent().getStringExtra("activity").equals("InvestPaperActivity")){
            initInvestInfo();
            saveActivity(SharedPreferencesUtil.INVEST_PAPER);
        }

    }

    private void saveActivity(String fastInfo) {
        SharedPreferencesUtil share = new SharedPreferencesUtil(
                getApplicationContext(),SharedPreferencesUtil.NAME_PAY_ACTIVITY);
        share.setString(SharedPreferencesUtil.KEY_ACTIVITY, fastInfo);
    }

    /**
     * 投资快报跳过来的View初始化
     */
    private void initInvestInfo() {
        botton = (Button) findViewById(R.id.goto_pay);
        botton.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
        pay = (Button) findViewById(R.id.goto_pay2);
        history = (Button) findViewById(R.id.see_history);

        pay.setOnClickListener(topayActivity);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    /**
     * 闪电跳过来的View初始化
     */
    private void initFastInfo() {
        botton = (Button) findViewById(R.id.goto_pay);
        botton.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);

        botton.setOnClickListener(topayActivity);
    }

    /**
     * 跳转到支付页面事件
     */
    View.OnClickListener topayActivity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(NeedVipActivity.this, PayActivity.class));
            finish();
        }
    };

    /**
     * 设置toolbar Navigation
     * <p>设置了导航图标和回调接口（返回，结束本activity）</p>
     */
    protected void setNavigationIcon(Toolbar toolbar) {
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this,
                R.drawable.rollback));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }


}
