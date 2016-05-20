package com.yslc.ui.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.CommentBean;
import com.yslc.bean.StarBean;
import com.yslc.util.CommonUtil;
import com.yslc.util.HttpUtil;
import com.yslc.util.ParseUtil;
import com.yslc.util.SharedPreferencesUtil;
import com.yslc.util.ToastUtil;
import com.yslc.util.ViewUtil;
import com.yslc.view.BaseListView;
import com.yslc.view.LoadView;
import com.yslc.view.BaseListView.OnLoadMoreListener;
import com.yslc.view.LoadView.OnTryListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 明星文章内容详情页，内容+评论
 *
 * @author XHH
 */
public class StarContentActivity extends BaseActivity implements
        OnLoadMoreListener, OnClickListener, OnTryListener {
    private BaseListView listView;
    private QuickAdapter<CommentBean> adapter;
    private ImageLoader imageLoader;
    private Button send;
    private EditText contentInput;
    private LoadView loadView;
    private TextView noComment;
    private ArrayList<CommentBean> dataList;
    private boolean noData = false;
    private int pageSize,pageIndex;

    /**
     * 设置布局
     * <p>包含标题，内容，评论</p>
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.listview_star_content;
    }

    /**
     * 设置标题
     * @return
     */
    @Override
    protected String getToolbarTitle() {
        return getText(R.string.contentDetail).toString();
    }

    /**
     * 初始化布局
     * <p>实例化数据类、图片加载工具类、业务逻辑类</p>
     * <p>发表评论，编辑评论，评论列表、加载圈圈初始化和设置</p>
     */
    @Override
    protected void initView() {
        pageIndex = 1;
        dataList = new ArrayList<>();//数据类
        imageLoader = ImageLoader.getInstance();

        //发送评论按钮
        send = (Button) findViewById(R.id.send);
        send.setText("发送");
        send.setTextColor(ContextCompat.getColor(this, R.color.gray));
        send.setOnClickListener(this);
        send.setEnabled(false);
        //编辑评论
        contentInput = (EditText) findViewById(R.id.content);
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
                if (contentInput.getText().toString().trim().length() == 0) {//不可发送空字符
                    // 不可评论状态
                    send.setTextColor(ContextCompat.getColor(StarContentActivity.this, R.color.gray));
                    send.setEnabled(false);
                } else {
                    // 可以评论状态
                    send.setTextColor(ContextCompat.getColor(StarContentActivity.this, R.color.titleBg));
                    send.setEnabled(true);
                }
            }
        });
        //评论列表
        listView = (BaseListView) findViewById(R.id.listview);
        listView.setHeaderDividersEnabled(true);
        listView.setFooterDividersEnabled(true);
        listView.setOnLoadMoreListener(this);
        //加载圈圈
        loadView = (LoadView) findViewById(R.id.view);
        loadView.setOnClickListener(null);//这句有用吗？
        loadView.setOnTryListener(this);

        // 获取文章详情
        if (loadView.setStatus(LoadView.LOADING)) {
            getComment(false);//获取评论
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                //提交评论
                commitComment();
                break;
        }

    }

    /**
     * 获取评论列表和文章
     * <p>成功后显示文章和显示评论</p>
     * @param isRefresh 是否刷新
     */
    private void getComment(boolean isRefresh) {
        RequestParams params = new RequestParams();
        params.put("Sn_Id", getIntent().getStringExtra("snId"));
        params.put("pageSize", "15");
        params.put("pageIndex", String.valueOf(pageIndex));
        HttpUtil.get(HttpUtil.GET_STAR_COMTENT_COMMENT, this, params,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(Throwable arg0, JSONObject arg1) {
                        super.onFailure(arg0, arg1);
                        listView.onFinishLoad();
                        loadView.setStatus(LoadView.ERROR);
                    }

                    @Override
                    public void onSuccess(JSONObject jo) {
                        super.onSuccess(jo);

                        if (jo.optString("Status").equals(HttpUtil.ERROR_CODE)) {
                            listView.onFinishLoad();
                            loadView.setStatus(LoadView.ERROR);
                            return;
                        }

                        ArrayList<CommentBean> list = ParseUtil.parseCommentBean(jo);
                        StarBean mode = ParseUtil.parseSingleStarBean2(jo);

                        loadView.setStatus(LoadView.SUCCESS);
                        listView.onFinishLoad();

                        if (pageIndex ==1 ) {
                            // 表示刷新
                            dataList.clear();
                        }
                        //没有评论
                        if (list.size() == 0 && pageIndex ==1 ) {
                            noData = true;
                        }

                        // 是否到了最后一页
                        pageSize  = list.size();

                        //加载评论和详情
                        dataList.addAll(list);
                        if (mode != null) {
                            setHeaderData(mode);//显示文章
                        }
                        setCommentData();//显示评论

                    }
                });

    }

    /**
     * 设置ListView Header数据
     * <p>设置文章内容</p>
     */
    private void setHeaderData(StarBean mode) {
        if (listView.getHeaderViewsCount() == 0) {
            View header = View.inflate(this, R.layout.header_star_content, null);
            imageLoader.displayImage(mode.getSif_Img(), (ImageView) header.findViewById(R.id.img));
            ((TextView) header.findViewById(R.id.name)).setText(mode.getSif_Title());
            ((TextView) header.findViewById(R.id.time)).setText(mode.getSn_Time());
            TextView content =((TextView) header.findViewById(R.id.content));//设置文章内容
//            content.setText(mode.getContent());
            String aaa = mode.getContent();
//            String bbb = aaa.replace("<br/>", "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            String bbb = aaa.replace("<br/>", "\n");
            content.setText(bbb);
//            content.setText(Html.fromHtml(bbb));

            noComment = (TextView) header.findViewById(R.id.noComment);
            if (noData) {
                noComment.setText("暂无评论,赶快评论吧！");
            } else {
                noComment.setText("评论列表");
            }

            listView.addHeaderView(header);
        }
    }

    /**
     * 设置评论列表内容
     * <p>没有适配器则先配置适配器</p>
     */
    private void setCommentData() {
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        } else {
            final DisplayImageOptions options = ViewUtil.getCircleOptions();
            adapter = new QuickAdapter<CommentBean>(this,
                    R.layout.item_comment_listview, dataList) {
                @Override
                protected void convert(BaseAdapterHelper helper, CommentBean item) {
                    helper.setText(R.id.nickName, item.getNcikName());
                    helper.setText(R.id.time, item.getTime());
                    helper.setText(R.id.content, item.getContent());
                    imageLoader.displayImage(item.getUiImg(),
                            (ImageView) helper.getView(R.id.img), options);
                }
            };
            listView.setAdapter(adapter);
        }
    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMore() {
        if(pageSize < 15){
            listView.noMoreData();
            return;
        }
        pageIndex++;
        getComment(false);
    }

    /**
     * 提交评论
     * <p>提交成功后刷新列表</p>
     */
    private void commitComment() {
        showWaitDialogs(R.string.doCommentInfo, true);//等待对话框，可取消
        if(!checkLogin(StarContentActivity.this)){
            return;
        }
        RequestParams params = new RequestParams();
        params.put("Ui_Id", SharedPreferencesUtil.getUserId(this));
        params.put("Sn_Id", getIntent().getStringExtra("snId"));
        params.put("Snc_Content", CommonUtil.inputFilter(contentInput));
        HttpUtil.post(HttpUtil.GET_STAR_COMMENT_COMMINT, this, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);
                        hideWaitDialog();
                        ToastUtil.showMessage(StarContentActivity.this, "发表评论失败");
//                        callback.failer("发表评论失败");
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        super.onSuccess(arg0);

                        try {
                            JSONObject jo = new JSONObject(arg0);
                            if (jo.optString("Status").equals(
                                    HttpUtil.ERROR_CODE)) {
                                hideWaitDialog();
                                ToastUtil.showMessage(StarContentActivity.this, jo.optString("msg"));
                                // 发表失败
//                                callback.failer(jo.optString("msg"));
                            } else {
                                // 发表成功
//                                callback.success(jo.optString("msg"));
                                hideWaitDialog();//隐藏对话框
                                ToastUtil.showMessage(StarContentActivity.this, jo.optString("msg"));//成功
                                contentInput.setText("");
                                if (null != noComment) {
                                    noComment.setText("评论列表");
                                }
                                // 刷新评论
                                getComment(true);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private boolean checkLogin(Context context) {
        // 判断是否登录
        if (!SharedPreferencesUtil.isLogin(context)) {
            ToastUtil.showMessage(context, "请先登录");
            context.startActivity(new Intent(context, LoginActivity.class));
            return false;
        }
        return true;
    }

    /**
     * 重新加载
     */
    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            getComment(true);
        }
    }

}
