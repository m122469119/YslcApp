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

import com.alipay.sdk.app.PayTask;
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
import com.yslc.util.SignUtils;
import com.yslc.util.ToastUtil;


import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PayActivity extends AppCompatActivity {
    private GoodBean product;
    private IWXAPI api;

    private static String orderNo;

    private static String orderItem;

    private static String orderPrice;

    //------微信支付-----------
    public static final String APPID = "wx0955e887ac142b61";

    private static final String PARTNER_KEY = "44934854899E40719F17DA83BE8FEE00";

    private static final String PARTNET_ID = "1336494601";
    //------微信支付----------
    //------支付宝支付--------
    private static final String PARTNER = "";//商户 ID
    private static final String SELLER = "";//商户收款账号
    private static final String RSA_PRIVATE = "";//商户私钥
    private static final String RSA_PUBLIC = "";//支付宝公钥
    //------支付宝支付--------

    private Button btn_pay;
    private RadioButton wechat_radio, alipay_radio;
    private TextView tvPayPrice, tvPayItem, tvTitle;
    private RelativeLayout r_wechat, r_alipay;

    /**
     * 获取订单号
     *
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
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            isVip();
        }
    }

    private static final int LOGIN_REQUEST_CODE = 1;

    private void isVip() {
        //判断用户是否登录
        if (!SharedPreferencesUtil.isLogin(this)) {
            ToastUtil.showMessage(this, "请先登录");
            this.startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_REQUEST_CODE);
            return;
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
        wechat_radio = (RadioButton) findViewById(R.id.rbt_wechat);
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
                if (btn_pay.getText().equals("返回")) {
                    finish();
                } else if (wechat_radio.isChecked()) {
                    if (isHaveWeChat()) {
                        payVip();
                    }
                } else if (alipay_radio.isChecked()) {
                    //TODO 支付宝支付
                    alipay();
                }
            }
        });
        if (getIntent().getStringExtra("activity") != null &&
                getIntent().getStringExtra("activity").equals("payFailure")) {//支付失败跳过来
            tvTitle.setText("支付失败，请重新支付");
        } else {
            tvTitle.setText(getResources().getString(R.string.pay_title));
        }
    }

    /**
     * 支付宝支付
     */
    private void alipay() {
        //拼接订单信息
        String orderInfo = getOrderInfo(product.getProductId(), product.getProductName(),
                product.getPrice());

        String sign = sign(orderInfo);//对订单签名

        //对sign 做URL编码
        try {
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         * <p>商品信息加上签名生成订单信息</p>
         */
        final String payInfo = orderInfo + "&sign=\"" +
                sign + "\"&" + getSignType();

        //异步发起支付
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayActivity.this);
                //调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);//true代码使用loading

                //TODO 处理支付结果 参数result
//                /**
//                 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
//                 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
//                 * docType=1) 建议商户依赖异步通知
//                 */
//                String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//
//                String resultStatus = payResult.getResultStatus();
//                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
//                if (TextUtils.equals(resultStatus, "9000")) {
//                    Toast.makeText(PayDemoActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//                } else {
//                    // 判断resultStatus 为非"9000"则代表可能支付失败
//                    // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
//                    if (TextUtils.equals(resultStatus, "8000")) {
//                        Toast.makeText(PayDemoActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
//                        Toast.makeText(PayDemoActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
//
//                    }
//                }
            }
        };

        //必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * 支付宝创建点单信息
     *
     * @param productId
     * @param productName
     * @param price
     * @return
     */
    private String getOrderInfo(String productId, String productName, String price) {
        //TODO 拼接订单
//        StringBuilder sb = new StringBuilder();
//
//        for (int i = 0; i < params.size(); i++) {
//            sb.append(params.get(i).getName());
//            sb.append('=');
//            sb.append(params.get(i).getValue());
//            sb.append('&');
//        }
        //签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";
        //签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";
        //商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";
        //商品名称
        orderInfo += "&subject=" + "\"" + productId + "\"";
        //商品详情
        orderInfo += "&body=" + "\"" + productName + "\"";
        //商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";
        //TODO 待改服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";
        //服务接口名称， 固定值
        orderInfo += "&service=" + "\"" + "mobile.securitypay.pay" + "\"";
        //支付类型， 固定值
        orderInfo += "&payment_type=" + "\"" + 1 + "\"";
        //参数编码， 固定值
        orderInfo += "&_input_charset=" + "\"" + "utf-8" + "\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

//        //支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
//        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";
        return orderInfo;
    }

    /**
     * 支付宝支付 获取唯一订单号
     *
     * @return
     */
    private String getOutTradeNo() {
//        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
//        Date date = new Date();
//        String key = format.format(date);
//
//        Random r = new Random();
//        key = key + r.nextInt();
//        key = key.substring(0, 15);
//        return key;
        return null;
    }

    /**
     * 判断是否安装微信
     *
     * @return
     */
    private boolean isHaveWeChat() {
        if (!api.isWXAppInstalled()) {
            ToastUtil.showMessage(PayActivity.this, "您未安装微信，请先下载微信客户端");
            return false;
        } else if (api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
            return true;
        } else {
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
     * 发起微信支付
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
     *
     * @param s
     */
    private void startWeiXin(String s) {

        try {
            JSONObject object = new JSONObject(s);
            if (null != object) {
                PayReq req = new PayReq();
                req.appId = APPID;
                req.partnerId = PARTNET_ID;
                req.prepayId = object.getString("prepayId");
                req.nonceStr = object.getString("nonceStr");
                req.timeStamp = object.getString("timeStamp");
                req.packageValue = object.getString("package");
                orderNo = object.getString("orderNo");
//                req.packageValue = "Sign=WXPay";

                LinkedHashMap<String ,String> params = new LinkedHashMap<String,String>();
                params.put("appid", req.appId);
                params.put("noncestr", req.nonceStr);
                params.put("package", req.packageValue);
                params.put("partnerid", req.partnerId);
                params.put("prepayid", req.prepayId);
                params.put("timestamp", req.timeStamp);
                req.sign = genAppSign2(params);

//                req.extData = "app data";//optional
                api.sendReq(req);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String genAppSign2(LinkedHashMap<String, String> params) {
        StringBuilder sb = new StringBuilder();

        Iterator itrt =params.entrySet().iterator();
        while(itrt.hasNext()){
            Map.Entry entry = (Map.Entry) itrt.next();
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());
            sb.append('&');
        }

        sb.append("key=");
        sb.append(PARTNER_KEY);

        String appSign = Md5Util.getMD5(sb.toString().getBytes()).toUpperCase();
        return appSign;
    }

    /**
     * 使用MD5生成sign参数
     *
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
        return appSign;
//        return URLEncodedUtils.format(params,"utf_8") + "&sign="+appSign;
    }

    /**
     * 获取商品信息
     */
    private void getGoodInfo() {
        RequestParams params = new RequestParams();
        params.put("ProductId", "YSLC0001");
        HttpUtil.originGet(HttpUtil.GET_PRODUCT, this, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, JSONObject jsonObject) {
                        super.onSuccess(i, jsonObject);
                        if (jsonObject.optInt("status") == 0) {//失败
                            ToastUtil.showMessage(PayActivity.this, jsonObject.optString("msg"));
                        } else if (jsonObject.optInt("status") == 1) { //成功
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