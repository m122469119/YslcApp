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
import com.yslc.bean.StockCodeBean;
import com.yslc.util.CommonUtil;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;

import java.util.ArrayList;

/**
 * 股票搜索界面
 *
 * @author HH
 */
public class SearchStockActivity extends BaseActivity implements GetDataCallback {
    private ListView listView;
    private EditText keyIuput;
    private ImageButton delete;
    private View noData;
    private QuickAdapter<StockCodeBean> adapter;
    private ArrayList<StockCodeBean> filterCodeList;
    private StockCodeModelSerice service;
    private boolean isData;//判断是否有数据

    /**
     * 设置布局
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_stocy;
    }

    /**
     * 初始化布局
     * <p>listView点击事件</p>
     */
    @Override
    protected void initView() {
        filterCodeList = new ArrayList<>();//数据类
        //listView
        listView = (ListView) findViewById(R.id.listview);
        //点击颜色
        listView.setSelector(ContextCompat.getDrawable(this, R.drawable.listview_selector));
        //分隔线
        listView.setDivider(ContextCompat.getDrawable(this, R.drawable.line_dotted));
        //点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //隐藏软键盘
                CommonUtil.hiddenSoftInput(SearchStockActivity.this);
                //清除缓存的股票行情数据
                new SharedPreferencesUtil(SearchStockActivity.this, Constant
                        .CACHE_STOCK_DATA_NAME).clearAll();
                //结束上一个股市行情Activity
                //TODO 很煞笔
                ActivityManager.getInstence().killActivity(StockMarketActivity.class);
                //进入一个新的股市行情页面
                Intent intent = new Intent(SearchStockActivity.this, StockMarketActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("codeBean", filterCodeList.get(position));
                intent.putExtras(bundle);
                SearchStockActivity.this.startActivity(intent);
                //结束本Activity
                onFinishActivity();
            }
        });

        service = new StockCodeModelSerice(this);//业务逻辑类
        service.setGetDataCallback(this);//回调
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
        //返回事件
        findViewById(R.id.rollback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinishActivity();
            }
        });

        //清框按钮点击事件
        delete = (ImageButton) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyIuput.setText("");
                delete.setVisibility(View.GONE);
            }
        });
        //搜索输入框
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
                //清框按钮
                if (keyIuput.getText().toString().trim().length() < 1) {
                    delete.setVisibility(View.GONE);
                } else {
                    delete.setVisibility(View.VISIBLE);
                }
            }
        });

        service.setFilter("");//加载所有数据
    }
    //--------GetDataCallback接口（成功返回过滤数据）------
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
    //--------GetDataCallback接口结束-------
    private void setAdapter() {
        if (null == adapter) {
            adapter = new QuickAdapter<StockCodeBean>(this, R.layout.item_search_stocy_listview,
                    filterCodeList) {
                @Override
                protected void convert(BaseAdapterHelper helper, StockCodeBean item) {
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
                ToastUtil.showMessage(SearchStockActivity.this, "导入成功");
                service.setFilter("");
            }

            @Override
            public <T> void failer(T data) {
                hideWaitDialog();
                ToastUtil.showMessage(SearchStockActivity.this, "导入失败");
            }
        });
    }

}
