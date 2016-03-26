package com.yslc.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yslc.data.service.NewModelService;
import com.yslc.inf.GetDataCallback;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.ui.dialog.SetTextSizeDialog;
import com.yslc.ui.dialog.SetTextSizeDialog.OnTextSize;
import com.yslc.ui.dialog.SetTextSizeDialog.TextSizeEnum;
import com.yslc.util.CommonUtil;
import com.yslc.util.HttpUtil;
import com.yslc.util.ToastUtil;
import com.yslc.view.LoadView;
import com.yslc.view.LoadView.OnTryListener;

/**
 * 新闻WebView页面(咨讯详情，视频详情)
 *
 * @author HH
 */
public class WebActivity extends BaseActivity implements OnClickListener,
        OnTextSize, OnTryListener {
    private WebView webView;
    private FrameLayout video_fullView;//视频布局
    private View xCustomView;
    private CustomViewCallback xCustomViewCallback;
    private myWebChromeClient xwebchromeclient;
    private String URL;
    private LoadView loadView;
    private SetTextSizeDialog setTextSizeDialog;
    private TextSizeEnum currentTextSize; // 当前字体
    private Button send;
    private EditText contentInput;
    private MenuItem setTextMenu;//字体菜单
    private int type = 0;//判断新闻或视频

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化页面
        findView();
    }

    /**
     * 设置布局
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    /**
     * 初始化页面
     * <p>webView设置，构建url</p>
     * <p>加载圈圈、评论框、发送按钮初始化</p>
     * <p>开始加载网页</p>
     */
    private void findView() {
        type = getIntent().getIntExtra("type", 0);
        TextView titleTv = (TextView) findViewById(R.id.titleText);//标题
        webView = (WebView) findViewById(R.id.webView);//内容
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);//js
        webView.setWebViewClient(new myWebViewClient());//网页监听

        // Web类型（构建url)
        if (type == 0) {
            titleTv.setText(getString(R.string.newDetail));

            // 新闻页面
            if (getIntent().getStringExtra("nid").equals("-1")) {
                // 进入广告新闻详情页
                URL = HttpUtil.GET_NEW + "?nid=" + getIntent().getStringExtra("url");
                //findViewById(R.id.buttom).setVisibility(View.GONE);
            } else {
                // 进入列表新闻详情页
                URL = HttpUtil.GET_NEW + "?nid=" + getIntent().getStringExtra("nid");
            }
        } else if (type == 1) {
            // 视频页面
            titleTv.setText(getString(R.string.vedioDetail));
            video_fullView = (FrameLayout) findViewById(R.id.video_fullView);
            // settings.setPluginsEnabled(true);
            //settings.setPluginState(PluginState.ON);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            settings.setLoadWithOverviewMode(true);
            settings.setUseWideViewPort(true);
            xwebchromeclient = new myWebChromeClient();
            webView.setWebChromeClient(xwebchromeclient);

            URL = HttpUtil.GET_NEW + "?nid="
                    + getIntent().getStringExtra("nid");
        }
        //评论
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(this);
        contentInput = (EditText) findViewById(R.id.content);//评论输入框
        //加载圈圈
        loadView = (LoadView) findViewById(R.id.view);
        loadView.setOnClickListener(null);
        loadView.setOnTryListener(this);

        // 评论输入框字符监听
        inputTextChange();
        //默认字体中
        currentTextSize = TextSizeEnum.TYPE_MEDIUM;

        if (loadView.setStatus(LoadView.LOADING)) {
            webView.loadUrl(URL);//加载网页
        }

    }

    /**
     * 创建设置字体的菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.set_text_size, menu);
        setTextMenu = menu.findItem(R.id.action_settextsize);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 菜单选择事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settextsize) {
            setTextSize();//打开字体选择对话框
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 评论输入框字数监听
     */
    private void inputTextChange() {
        contentInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (contentInput.getText().toString().trim().length() == 0) {
                    // 显示去查看列表详情按钮
                    send.setText(WebActivity.this.getString(R.string.seeComments));
                } else {
                    // 显示发布样式的按钮
                    send.setText(WebActivity.this.getString(R.string.send));
                    send.setTextColor(ContextCompat.getColor(WebActivity.this, R.color.titleBg));
                }
            }
        });
    }

    /**
     * 加载过程监听（内部类）
     * <p>嵌入加载圈圈</p>
     */
    private class myWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if (loadView.getStatus() == LoadView.SUCCESS) {
                loadView.setStatus(LoadView.LOADING);
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            // 加载错误
            loadView.setStatus(LoadView.ERROR);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (loadView.getStatus() != LoadView.ERROR) {

                // 加载好了
                loadView.setStatus(LoadView.SUCCESS);

                setTextMenu.setEnabled(true);
            }
        }

    }

    /**
     * 内部类播放视频的时候调用
     */
    public class myWebChromeClient extends WebChromeClient {
        // 播放网络视频时全屏会被调用的方法

        /**
         * 显示视频(全屏）
         * @param view
         * @param callback
         */
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
            webView.setVisibility(View.INVISIBLE);
            // 如果一个视图已经存在，那么立刻终止并新建一个
            if (xCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            video_fullView.addView(view);
            xCustomView = view;
            xCustomViewCallback = callback;
            video_fullView.setVisibility(View.VISIBLE);
        }

        // 视频播放退出全屏会被调用的（回来webview)
        @Override
        public void onHideCustomView() {
            if (xCustomView == null)// 不是全屏播放状态
                return;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
            xCustomView.setVisibility(View.GONE);
            xCustomViewCallback.onCustomViewHidden();
            video_fullView.removeView(xCustomView);
            xCustomView = null;
            video_fullView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 判断是否是全屏
     *
     * @return 当前是否全屏
     */
    private boolean inCustomView() {
        return (xCustomView != null);
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    private void hideCustomView() {
        xwebchromeclient.onHideCustomView();
    }

    /**
     * 重写返回键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inCustomView()) {//视频
                hideCustomView();
                return true;
            } else {//网页退出
                webView.loadUrl("about:blank");
                onFinishActivity();
                return true;
            }
        }

        return false;
    }

    /**
     * 评论按钮点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                // 隐藏软键盘
                CommonUtil.hiddenSoftInput(this);

                if (send.getText().toString().equals("查看评论")) {
                    // 进入评论列表
                    Intent intent = new Intent(this, CommentActivity.class);
                    intent.putExtra("nid", getIntent().getStringExtra("nid"));
                    startActivity(intent);
                } else {
                    // 提交评论
                    commitComment();
                }
                break;
        }
    }

    /**
     * 进行字体设置
     * <p>字体选择对话框设置</p>
     */
    private void setTextSize() {
        if (null == setTextSizeDialog) {
            setTextSizeDialog = new SetTextSizeDialog(this, currentTextSize);//包含show
            setTextSizeDialog.setOnTextSize(this);//监听
        } else {
            setTextSizeDialog.show();
        }
    }

    /**
     * dialog监听事件回调
     * <p>改变字体大小</p>
     * @param ts 点击的字体大少
     */
    @Override
    public void setTextSize(TextSizeEnum ts) {
        if (ts == TextSizeEnum.TYPE_SMALL) {
            webView.getSettings().setTextSize(TextSize.SMALLER);
        } else if (ts == TextSizeEnum.TYPE_MEDIUM) {
            webView.getSettings().setTextSize(TextSize.NORMAL);
        } else {
            webView.getSettings().setTextSize(TextSize.LARGEST);
        }

        currentTextSize = ts;
    }

    /**
     * 停止播放视频
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (null != webView) {
            webView.onResume();
            webView.resumeTimers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (null != webView) {
            webView.onPause();
            webView.pauseTimers();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (type == 1) {
            ((FrameLayout) findViewById(R.id.parantView))
                    .removeView(webView);
            video_fullView.removeAllViews();
            webView.removeAllViews();
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
        }

        webView.destroy();
        webView = null;
    }

    /**
     * 提交评论
     */
    private void commitComment() {
        showWaitDialogs(R.string.doCommentInfo, true);
        new NewModelService(this).doNewComment(getIntent().getStringExtra("nid"),
                contentInput.getText().toString().trim(), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                hideWaitDialog();
                contentInput.setText("");//清空
                ToastUtil.showMessage(WebActivity.this, data.toString());//评论成功
            }

            @Override
            public <T> void failer(T data) {
                hideWaitDialog();
                ToastUtil.showMessage(WebActivity.this, data.toString());
            }
        });
    }

    /**
     * 重新加载
     */
    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            webView.loadUrl(URL);
        }
    }
}
