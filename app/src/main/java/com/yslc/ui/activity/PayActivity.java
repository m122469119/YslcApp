package com.yslc.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yslc.R;
import com.yslc.bean.GoodBean;
import com.yslc.util.HttpUtil;
import com.yslc.util.Md5Util;
import com.yslc.util.ParseUtil;
import com.yslc.util.ToastUtil;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class PayActivity extends AppCompatActivity {
    private GoodBean product;
    private IWXAPI api;

    private static final String APPID = "wx0955e887ac142b61";
    private static final String PARTNER_KEY = "44934854899E40719F17DA83BE8FEE00";
    private static final String PARTNET_ID = "1336494601";

    private Button btn_pay;
    private RadioButton wechat_radio, alipay_radio;
    private RelativeLayout r_wechat, r_alipay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //微信api
        api = WXAPIFactory.createWXAPI(this, APPID, false);

        setContentView(R.layout.activity_pay);

        initView();
        //获取商品信息
        getGoodInfo();
    }

    /**
     * 初始化
     */
    private void initView() {
        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        //支付按钮
        btn_pay = (Button) findViewById(R.id.pay_now);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发起支付
                payVip();
            }
        });
    }

    /**
     * 发起支付
     */
    private void payVip() {
        RequestParams params = new RequestParams();
        params.put("productId", product.getProductId());
        params.put("phone", "13829645632");//TODO 判断登录,获取手机号
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
            //TODO retcode应该代表失败吧，需要询问后台
            if(null != object && !object.has("retcode")){
                PayReq req = new PayReq();
                req.appId = APPID;
                req.partnerId = PARTNET_ID;
                req.prepayId = object.getString("prepayId");
                req.nonceStr = object.getString("nonceStr");
                req.timeStamp = object.getString("timeStamp");
                req.packageValue = object.getString("package");
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

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


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
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable, JSONObject jsonObject) {
                        super.onFailure(throwable, jsonObject);
                    }
                });
    }


}
