package com.yslc.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yslc.R;
import com.yslc.app.Constant;
import com.yslc.bean.GoodBean;
import com.yslc.util.HttpUtil;
import com.yslc.util.Md5Util;
import com.yslc.util.ParseUtil;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;
import com.yslc.wxapi.WXPayEntryActivity;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class PayActivity extends AppCompatActivity {
    private GoodBean product;
    private IWXAPI api;

    private static String orderNo;

    private static String orderItem;

    private static String orderPrice;

    public static final String APPID = "wx0955e887ac142b61";

    private static final String PARTNER_KEY = "44934854899E40719F17DA83BE8FEE00";

    private static final String PARTNET_ID = "1336494601";

    private Button btn_pay;
    private RadioButton wechat_radio, alipay_radio;
    private TextView tvPayPrice, tvPayItem, tvTitle;
    private RelativeLayout r_wechat, r_alipay;

    /**
     * 获取订单号
     * @return
     */
    public static String getOrderNo() {
        return orderNo;
    }
    public static String getOrderPrice() {
        return orderPrice;
    }

    public static String getOrderItem() {
        return orderItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //微信api
        api = WXAPIFactory.createWXAPI(this, APPID, false);

        setContentView(R.layout.activity_pay);

        initView();

        isVip();
        //获取商品信息
//        getGoodInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK){
            isVip();
        }
    }

    private static final int LOGIN_REQUEST_CODE = 1;
    private void isVip() {
        //判断用户是否登录
        if (!SharedPreferencesUtil.isLogin(this)) {
            ToastUtil.showMessage(this, "请先登录");
            this.startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_REQUEST_CODE);
            return ;
        }
        //判读是否vip
        SharedPreferencesUtil share = new SharedPreferencesUtil(this, Constant.SPF_USER_INFO_NAME);
        RequestParams params = new RequestParams();
        params.put("phone", share.getString(Constant.SPF_USER_PHONE_KEY));
        params.put("belong", "YSLC");
        params.put("function", "YSLC0001");
        HttpUtil.get("/AppJson/pay/wx/verifyPower.ashx", this, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String s) {
                        super.onSuccess(s);
                        try {
                            JSONObject json = new JSONObject(s);
                            switch (json.getInt("status")) {
                                case 0://不是vip
                                    getGoodInfo();
//                                    btn_pay.setClickable(true);
                                    break;
                                case 1://是vip
                                    ToastUtil.showMessage(PayActivity.this,
                                            "你好,你已经是VIP，不需重复支付");
//                                    btn_pay.setClickable(false);
                                    btn_pay.setText("返回");
                                    break;
                                //检查是否有权限
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable, String s) {
                        super.onFailure(throwable, s);
                    }
                });
    }

    /**
     * 初始化
     */
    private void initView() {
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setNavigationIcon(toolbar);//设置返回键

        tvPayItem = (TextView) findViewById(R.id.pay_item);
        tvPayPrice = (TextView) findViewById(R.id.pay_price);
        tvTitle = (TextView) findViewById(R.id.title_pay);
        //选择微信支付/支付宝
        wechat_radio =(RadioButton)findViewById(R.id.rbt_wechat);
        alipay_radio = (RadioButton) findViewById(R.id.rbt_alipay);
        r_wechat = (RelativeLayout) findViewById(R.id.wechat_pay);
        r_wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.showMessage(PayActivity.this,"微信支付");
                wechat_radio.setChecked(true);
                alipay_radio.setChecked(false);
            }
        });
        r_alipay = (RelativeLayout) findViewById(R.id.alipay);
        r_alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.showMessage(PayActivity.this,"支付宝");
                alipay_radio.setChecked(true);
                wechat_radio.setChecked(false);
            }
        });
        //TODO 隐藏了支付宝支付功能
        r_alipay.setVisibility(View.GONE);

        //支付按钮
        btn_pay = (Button) findViewById(R.id.pay_now);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(PayActivity.this, WXPayEntryActivity.class));
                //发起支付
                if(btn_pay.getText().equals("返回")){
                    finish();
                }else if (wechat_radio.isChecked()) {
                    if(isHaveWeChat()){
                        payVip();
                    }
                } else if (alipay_radio.isChecked()) {
                    //TODO 支付宝支付
                }
            }
        });
        if(getIntent().getStringExtra("activity") != null &&
                getIntent().getStringExtra("activity").equals("payFailure")){//支付失败跳过来
            tvTitle.setText("支付失败，请重新支付");
        }else {
            tvTitle.setText(getResources().getString(R.string.pay_title));
        }
    }

    /**
     * 判断是否安装微信
     * @return
     */
    private boolean isHaveWeChat() {
        if( !api.isWXAppInstalled()){
            ToastUtil.showMessage(PayActivity.this, "您未安装微信，请先下载微信客户端");
            return false;
        }else if(api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT){
            return true;
        }else {
            ToastUtil.showMessage(PayActivity.this, "您的微信版本太低，不支持支付，请更新");
            return false;
        }
    }

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
//                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    /**
     * 发起支付
     */
    private void payVip() {
        SharedPreferencesUtil share = new SharedPreferencesUtil(this, Constant.SPF_USER_INFO_NAME);
        RequestParams params = new RequestParams();
        params.put("productId", product.getProductId());
        params.put("phone", share.getString(Constant.SPF_USER_PHONE_KEY));
        HttpUtil.originPost("http://pay.etz927.com/yslc/WxPay/apppay.ashx", this, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String s) {
                        super.onSuccess(s);
                        startWeiXin(s);//开启微信客户端
                    }

                    @Override
                    public void onFailure(Throwable throwable, String s) {
                        super.onFailure(throwable, s);
                    }
                });
    }

    /**
     * 开启微信客户端支付
     * @param s
     */
    private void startWeiXin(String s) {

        try{
            JSONObject object =new JSONObject(s);
            if(null != object ){
                PayReq req = new PayReq();
                req.appId = APPID;
                req.partnerId = PARTNET_ID;
                req.prepayId = object.getString("prepayId");
                req.nonceStr = object.getString("nonceStr");
                req.timeStamp = object.getString("timeStamp");
                req.packageValue = object.getString("package");
                orderNo = object.getString("orderNo");
//                req.packageValue = "Sign=WXPay";

                List<NameValuePair> signParams = new LinkedList<NameValuePair>();
                signParams.add(new BasicNameValuePair("appid", req.appId));
                signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
                signParams.add(new BasicNameValuePair("package", req.packageValue));
                signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
                signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
                signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
                req.sign = genAppSign(signParams);
//                req.extData = "app data";//optional
                api.sendReq(req);
                finish();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 使用MD5生成sign参数
     * @param params
     * @return
     */
    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(PARTNER_KEY);

        String appSign = Md5Util.getMD5(sb.toString().getBytes()).toUpperCase();
        return  appSign;
//        return URLEncodedUtils.format(params,"utf_8") + "&sign="+appSign;
    }

    /**
     * 获取商品信息
     */
    private void getGoodInfo() {
        RequestParams params = new RequestParams();
        params.put("ProductId", "YSLC0001");
        HttpUtil.originGet(HttpUtil.GET_PRODUCT, this, params,
                new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int i, JSONObject jsonObject) {
                        super.onSuccess(i, jsonObject);
                        if(jsonObject.optInt("status")== 0){//失败
                            ToastUtil.showMessage(PayActivity.this, jsonObject.optString("msg"));
                        }else if(jsonObject.optInt("status")== 1) { //成功
                            product = ParseUtil.parseGoodBean(jsonObject);
                            showData();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable, JSONObject jsonObject) {
                        super.onFailure(throwable, jsonObject);
                    }
                });
    }

    private void showData() {
        orderItem = product.getProductName();
        orderPrice = product.getPrice();
        tvPayPrice.setText(product.getPrice());
        tvPayItem.setText(product.getProductName());
    }


}
