package com.yslc.ui.activity;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.data.service.NewModelService;
import com.yslc.inf.GetDataCallback;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.CommentBean;
import com.yslc.util.CommonUtil;
import com.yslc.util.ToastUtil;
import com.yslc.util.ViewUtil;
import com.yslc.view.BaseListView;
import com.yslc.view.LoadView;
import com.yslc.view.BaseListView.OnLoadMoreListener;
import com.yslc.view.LoadView.OnTryListener;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * 评论列表页面Activity
 *
 * @author HH
 */
public class CommentActivity extends BaseActivity implements OnClickListener,
        OnTryListener {
    private SwipeRefreshLayout refreshLayout;
    private BaseListView listView;
    private LoadView loadView;
    private Button send;
    private EditText contentInput;
    private ImageLoader imageLoader;
    private QuickAdapter<CommentBean> adapter;
    private ArrayList<CommentBean> listData = null;
    private NewModelService service;

    /**
     * 设置布局
     * <p>包含标题栏，下拉刷新，和评论栏</p>
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.listview_comment;
    }

    /**
     * 设置标题
     * @return
     */
    @Override
    protected String getToolbarTitle() {
        return getString(R.string.commentTitle);
    }

    /**
     * 初始化布局组件
     * <p>关联所有组件，监听事件的设置</p>
     * <p>开始下载数据</p>
     */
    @Override
    protected void initView() {
        //发送评论按钮
        send = (Button) findViewById(R.id.send);
        send.setText("发送");
        send.setTextColor(ContextCompat.getColor(this, R.color.gray));
        send.setOnClickListener(this);
        send.setEnabled(false);
        //评论输入框
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
                if (contentInput.getText().toString().trim().length() == 0) {
                    // 显示去查看列表详情按钮
                    send.setTextColor(ContextCompat.getColor(CommentActivity.this, R.color.gray));
                    send.setEnabled(false);
                } else {
                    // 显示发布样式的按钮
                    send.setTextColor(ContextCompat.getColor(CommentActivity.this, R.color.titleBg));
                    send.setEnabled(true);
                }
            }
        });
        imageLoader = ImageLoader.getInstance();//图片工具
        //下拉刷新
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshable_view);
        refreshLayout.setColorSchemeResources(R.color.refreshViewColor1,
                R.color.refreshViewColor2, R.color.refreshViewColor3);
        //加载更多列表
        listView = (BaseListView) findViewById(R.id.listview);
        listView.setFooterDividersEnabled(true);
        listView.setSelector(ActivityCompat.getDrawable(this, R.drawable.line_dotted));
        //加载圈圈
        loadView = (LoadView) findViewById(R.id.view);
        loadView.setOnTryListener(this);
        listViewOnEvent();//listview监听事件
        service = new NewModelService(this);//业务逻辑类
        listData = new ArrayList<>();//评论数据
        if (loadView.setStatus(LoadView.LOADING)) {
            getData(true);
        }
    }

    /**
     * 下拉刷新&加载更多
     */
    private void listViewOnEvent() {
        // 下拉刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(true);
            }
        });

        // 加载更多
        listView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getData(false);
            }
        });
    }

    /**
     * 获取评论数据
     * <p>成功后设置数据</p>
     * @param isFrist 是否加载第一页数据
     */
    private void getData(boolean isFrist) {
        service.getNewCommentList(isFrist, getIntent().getStringExtra("nid"), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                refreshLayout.setRefreshing(false);
                listView.onFinishLoad();
                loadView.setStatus(LoadView.SUCCESS);

                ArrayList<CommentBean> list = (ArrayList<CommentBean>) data;
                if (list.size() == 0 && service.getPageIndex() == 2) {//没有数据
                    loadView.setStatus(LoadView.EMPTY_DATA);
                    return;
                }
                //加载更多
                if (list.size() < service.getPageSize() && service.getPageIndex() > 2) {
                    // 加载更多时没有更多了...
                    listView.noMoreData();
                }

                if (service.getPageIndex() == 2) {
                    //下拉刷新
                    listData.clear();
                }

                listData.addAll(list);
                setData();
            }

            @Override
            public <T> void failer(T data) {
                refreshLayout.setRefreshing(false);
                loadView.setStatus(LoadView.ERROR);
            }
        });
    }

    /**
     * 设置数据
     * <p>配置适配器</p>
     */
    private void setData() {
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        } else {
            final DisplayImageOptions options = ViewUtil.getCircleOptions();
            adapter = new QuickAdapter<CommentBean>(this,
                    R.layout.item_comment_listview, listData) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                // 隐藏软键盘
                CommonUtil.hiddenSoftInput(this);
                commitComment();
                break;
        }
    }

    /**
     * 提交评论
     */
    private void commitComment() {
        service.doNewComment(getIntent().getStringExtra("nid"),
                contentInput.getText().toString().trim(), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                hideWaitDialog();
                ToastUtil.showMessage(CommentActivity.this, data.toString());
                contentInput.setText("");
                getData(true);//刷新评论列表
            }

            @Override
            public <T> void failer(T data) {
                hideWaitDialog();
                ToastUtil.showMessage(CommentActivity.this, data.toString());
            }
        });
    }

    /**
     * 重新加载
     */
    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            getData(true);
        }
    }
}
