package com.yslc.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yslc.R;
import com.yslc.bean.GoodBean;
import com.yslc.util.HttpUtil;
import com.yslc.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PayActivity extends AppCompatActivity {
    GoodBean product;
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //微信api
        api = WXAPIFactory.createWXAPI(this, "wx0955e887ac142b61");
        api.registerApp("wx0955e887ac142b61");//TODO 需要吗

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
        params.put("phone", "15989143564");
        params.put("openid", "wx0955e887ac142b61");
        HttpUtil.originGet("http://pay.etz927.com/yslc/WxPay/apppay.ashx", this, params,
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
                req.partnerId = object.getString("partnerId");
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
        params.put("ProductId", "YSLC0002");
        HttpUtil.originGet("http://pay.etz927.com/yslc/Get_Product.ashx", this, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String s) {
                        super.onSuccess(s);
                        product = parseGoodBean(s);
                    }


                    @Override
                    public void onFailure(Throwable throwable, String s) {
                        super.onFailure(throwable, s);
                    }
                });
    }

    /**
     * 解析数据
     * @param s
     * @return
     */
    private GoodBean parseGoodBean(String s) {
        GoodBean good = new GoodBean();
        try{
            JSONArray array = new JSONArray(s);
            JSONObject o = array.getJSONObject(0);
            //[{"ProductId": "YSLC0002","ProductName": "投资快报","Price": "360.0000"}]
            good.setPrice(o.getString("Price"));
            good.setProductId(o.getString("ProductId"));
            good.setProductName(o.getString("ProductName"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return good;
    }

}
