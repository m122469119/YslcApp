package com.yslc.wxapi;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yslc.R;
import com.yslc.ui.activity.FastInfoActivity;
import com.yslc.ui.activity.InvestPaperActivity;
import com.yslc.ui.activity.PayActivity;
import com.yslc.util.HttpUtil;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
	TextView textView, tvOrderNum, tvOrderPrice;
    private Button button;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_pay_entry);
        initView();
        
    	api = WXAPIFactory.createWXAPI(this, PayActivity.APPID);
        api.handleIntent(getIntent(), this);
    }

    private void initView() {
        textView = (TextView)findViewById(R.id.pay_result_info);
        textView.setText("显示微信支付结果");
        tvOrderNum = (TextView) findViewById(R.id.order_num);
        tvOrderPrice = (TextView) findViewById(R.id.order_price);
        button = (Button) findViewById(R.id.pay_result_button);
        button.setText("返回");
        button.setOnClickListener(event);
    }

    /**
     * 点击事件
     */
    View.OnClickListener event = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferencesUtil share = new SharedPreferencesUtil(
                    getApplicationContext(),SharedPreferencesUtil.NAME_PAY_ACTIVITY);
            String activity = share.getString(SharedPreferencesUtil.KEY_ACTIVITY);
            if(activity == SharedPreferencesUtil.FAST_INFO){
                startActivity(new Intent(WXPayEntryActivity.this, FastInfoActivity.class));
            }else if(activity == SharedPreferencesUtil.INVEST_PAPER){
                startActivity(new Intent(WXPayEntryActivity.this, InvestPaperActivity.class));
            }else if(activity == SharedPreferencesUtil.NONE){
//                finish();
            }
            finish();
            //查看订单
        }
    };

    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {//微信支付回调
            switch (resp.errCode){
                case 0:
//                    textView.setText("微信支付结果："+ "成功");
                    queryPayResult();//向后台查询更详细的结果
                    break;
                case -1:
//                    textView.setText("微信支付结果：" + "失败");
                    handleFailure();
                    break;
                case -2:
//                    textView.setText("微信支付结果：" + "用户取消");
                    handleFailure();
                    break;
            }
//            queryPayResult();
        }
	}

    /**
     * 支付失败
     */
    private void handleFailure() {
        ToastUtil.showMessage(this, "支付失败");
        Intent intent = new Intent(WXPayEntryActivity.this, PayActivity.class);
        intent.putExtra("activity", "payFailure");
        startActivity(intent);
        finish();
    }

    /**
     * 向后台查询支付结果
     */
    private void queryPayResult() {
        RequestParams params = new RequestParams();
        params.put("orderNo", PayActivity.getOrderNo());
        HttpUtil.originGet("http://pay.etz927.com/yslc/WxPay/PayResult.ashx",this, params,
                new AsyncHttpResponseHandler(){
                    @Override
                    public void onSuccess(String s) {
                        super.onSuccess(s);
                        //{"status":"0","msg":"Specified cast is not valid."}
                        try{
                            JSONObject jsonObject = new JSONObject(s);
                            switch (jsonObject.getInt("status")){
                                case 0://失败
//                                    textView.append("\n后台查询结果：失败");
                                    handleFailure();
//                                    ToastUtil.showMessage(WXPayEntryActivity.this,
//                                            "失败"+jsonObject.getString("msg"));
                                    break;
                                case 1://成功
                                    setSuccessData();
//                                    textView.append("\n后台查询结果：成功");
//                                    ToastUtil.showMessage(WXPayEntryActivity.this,
//                                            "成功"+jsonObject.getString("msg"));
                                    break;
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable, String s) {
                        super.onFailure(throwable, s);
                    }
                });
    }

    private void setSuccessData() {
        tvOrderNum.setText(PayActivity.getOrderItem());
        tvOrderPrice.setText("￥"+ PayActivity.getOrderPrice());
//        tvOrderPrice.setText(String.format(getResources().getString(R.string.price),20));
    }
}