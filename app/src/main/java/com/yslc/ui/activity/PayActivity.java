package com.yslc.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yslc.R;
import com.yslc.bean.GoodBean;
import com.yslc.util.HttpUtil;
import com.yslc.util.ParseUtil;
import com.yslc.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PayActivity extends AppCompatActivity {
    private GoodBean product;
    private IWXAPI api;
    private static final String APPID = "wx0955e887ac142b61";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //微信api
        api = WXAPIFactory.createWXAPI(this, APPID);
//        api.registerApp("wx0955e887ac142b61");//TODO 需要吗

        setContentView(R.layout.activity_pay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //获取商品信息
        getGoodInfo();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发起支付
                payVip();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * 发起支付
     */
    private void payVip() {
        RequestParams params = new RequestParams();
        params.put("productId", product.getProductId());
        params.put("phone", "15989143564");//TODO 判断登录,获取手机号
//        params.put("openid", "wx0955e887ac142b61");
        HttpUtil.post("/yslc/WxPay/apppay.ashx", this, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String s) {
                        super.onSuccess(s);
                        startWeiXin(s);//开启微信客户端
                        ToastUtil.showMessage(PayActivity.this, "后台以收到");
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
                req.appId = object.getString("appId");
//                req.partnerId = object.getString("partnerId");
                req.partnerId = "1336494601";
                req.prepayId = object.getString("");
                req.nonceStr = object.getString("");
                req.timeStamp = object.getString("");
                req.packageValue = object.getString("");
                req.sign = object.getString("");
                req.extData = "app data";//optional
                api.sendReq(req);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
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
