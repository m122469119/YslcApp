package com.yslc.ui.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.StockCodeModelSerice;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.app.ActivityManager;
import com.yslc.app.Constant;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.StocyCodeBean;
import com.yslc.util.CommonUtil;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;

import java.util.ArrayList;

/**
 * 股票搜索界面
 *
 * @author HH
 */
public class SearchStocyActivity extends BaseActivity implements GetDataCallback {
    private ListView listView;
    private EditText keyIuput;
    private ImageButton delete;
    private View noData;
    private QuickAdapter<StocyCodeBean> adapter;
    private ArrayList<StocyCodeBean> filterCodeList;
    private StockCodeModelSerice service;
    private boolean isData;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_stocy;
    }

    @Override
    protected void initView() {
        filterCodeList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listview);
        listView.setSelector(ContextCompat.getDrawable(this, R.drawable.listview_selector));
        listView.setDivider(ContextCompat.getDrawable(this, R.drawable.line_dotted));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //隐藏软键盘
                CommonUtil.hiddenSoftInput(SearchStocyActivity.this);
                //清除缓存的股票行情数据
                new SharedPreferencesUtil(SearchStocyActivity.this, Constant.CACHE_STOCK_DATA_NAME).clearAll();
                //结束上一个股市行情Activity
                ActivityManager.getInstence().killActivity(StockMarketActivity.class);
                //进入一个新的股市行情页面
                Intent intent = new Intent(SearchStocyActivity.this, StockMarketActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("codeBean", filterCodeList.get(position));
                intent.putExtras(bundle);
                SearchStocyActivity.this.startActivity(intent);
                //结束本Activity
                onFinishActivity();
            }
        });

        service = new StockCodeModelSerice(this);
        service.setGetDataCallback(this);
        noData = findViewById(R.id.noData);
        isData = service.isData();
        noData.setVisibility(isData ? View.GONE : View.VISIBLE);
        noData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //导入股票代码数据
                initData();
            }
        });

        findViewById(R.id.rollback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinishActivity();
            }
        });

        delete = (ImageButton) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyIuput.setText("");
                delete.setVisibility(View.GONE);
            }
        });

        keyIuput = (EditText) findViewById(R.id.keyInput);
        keyIuput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isData) {
                    service.setFilter(keyIuput.getText().toString());
                }

                if (keyIuput.getText().toString().trim().length() < 1) {
                    delete.setVisibility(View.GONE);
                } else {
                    delete.setVisibility(View.VISIBLE);
                }
            }
        });

        service.setFilter("");
    }

    @Override
    public <T> void success(T data) {
        filterCodeList.clear();
        filterCodeList.addAll((ArrayList) data);
        Log.i("********", filterCodeList + "**********" + filterCodeList.size());
        setAdapter();
    }

    @Override
    public <T> void failer(T data) {

    }

    private void setAdapter() {
        if (null == adapter) {
            adapter = new QuickAdapter<StocyCodeBean>(this, R.layout.item_search_stocy_listview, filterCodeList) {
                @Override
                protected void convert(BaseAdapterHelper helper, StocyCodeBean item) {
                    helper.setText(R.id.stocyCode, item.getStock_Code());
                    helper.setText(R.id.stocyName, item.getStock_Name());
                    helper.setText(R.id.stocyLetter, item.getStock_Abbreviation());
                }
            };
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取数据
     */
    private void initData() {
        showWaitDialogs("正在导入，请稍等...", true);
        service.intoStockCodeList(new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                hideWaitDialog();
                ToastUtil.showMessage(SearchStocyActivity.this, "导入成功");
                service.setFilter("");
            }

            @Override
            public <T> void failer(T data) {
                hideWaitDialog();
                ToastUtil.showMessage(SearchStocyActivity.this, "导入失败");
            }
        });
    }

}
