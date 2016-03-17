package com.yslc.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.StarModelService;
import com.yslc.ui.base.BaseActivity;
import com.yslc.R;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.CommentBean;
import com.yslc.bean.StarBean;
import com.yslc.util.CommonUtil;
import com.yslc.util.ToastUtil;
import com.yslc.util.ViewUtil;
import com.yslc.view.BaseListView;
import com.yslc.view.LoadView;
import com.yslc.view.BaseListView.OnLoadMoreListener;
import com.yslc.view.LoadView.OnTryListener;

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
    private StarModelService starModelService;

    @Override
    protected int getLayoutId() {
        return R.layout.listview_star_content;
    }

    @Override
    protected String getToolbarTitle() {
        return getText(R.string.contentDetail).toString();
    }

    @Override
    protected void initView() {
        dataList = new ArrayList<>();
        imageLoader = ImageLoader.getInstance();

        send = (Button) findViewById(R.id.send);
        send.setText("发送");
        send.setTextColor(ContextCompat.getColor(this, R.color.gray));
        send.setOnClickListener(this);
        send.setEnabled(false);
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

        listView = (BaseListView) findViewById(R.id.listview);
        listView.setHeaderDividersEnabled(true);
        listView.setFooterDividersEnabled(true);
        listView.setOnLoadMoreListener(this);

        loadView = (LoadView) findViewById(R.id.view);
        loadView.setOnClickListener(null);
        loadView.setOnTryListener(this);

        // 获取文章详情
        starModelService = new StarModelService(this);
        if (loadView.setStatus(LoadView.LOADING)) {
            getComment(false);
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
     * 获取评论列表
     */
    private void getComment(boolean isRefresh) {
        starModelService.getStarArticeDetail(isRefresh, getIntent().getStringExtra("snId"), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                loadView.setStatus(LoadView.SUCCESS);
                listView.onFinishLoad();

                if (isRefresh) {
                    // 表示刷新
                    dataList.clear();
                }

                HashMap<CommentBean, ArrayList<CommentBean>> map = (HashMap<CommentBean, ArrayList<CommentBean>>) data;
                Iterator iterator = map.keySet().iterator();
                StarBean mode = null;
                ArrayList<CommentBean> list = null;
                if (iterator.hasNext()) {
                    mode = (StarBean) iterator.next();
                    list = map.get(mode);
                }

                if (list.size() == 0 && starModelService.getPageIndex() == 2) {
                    noData = true;
                }

                // 是否到了最后一页
                if (list.size() < starModelService.getPageSize() && starModelService.getPageIndex() > 2) {
                    listView.noMoreData();
                }

                //加载评论和详情
                dataList.addAll(list);
                if (mode != null) {
                    setHeaderData(mode);
                }
                setCommentData();

            }

            @Override
            public <T> void failer(T data) {
                listView.onFinishLoad();
                loadView.setStatus(LoadView.ERROR);
            }
        });


    }

    /**
     * 设置ListView Header数据
     */
    private void setHeaderData(StarBean mode) {
        if (listView.getHeaderViewsCount() == 0) {
            View header = View.inflate(this, R.layout.header_star_content, null);
            imageLoader.displayImage(mode.getSif_Img(),(ImageView) header.findViewById(R.id.img));
            ((TextView) header.findViewById(R.id.name)).setText(mode.getSif_Title());
            ((TextView) header.findViewById(R.id.time)).setText(mode.getSn_Time());
            ((TextView) header.findViewById(R.id.content)).setText(mode.getContent());

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

    @Override
    public void onLoadMore() {
        getComment(false);
    }

    /**
     * 提交评论
     */
    private void commitComment() {
        showWaitDialogs(R.string.doCommentInfo, true);
        starModelService.doStarArticeComment(getIntent().getStringExtra("snId"), CommonUtil.inputFilter(contentInput), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                hideWaitDialog();
                ToastUtil.showMessage(StarContentActivity.this, data.toString());
                contentInput.setText("");
                if (null != noComment) {
                    noComment.setText("评论列表");
                }
                // 刷新评论
                getComment(true);
            }

            @Override
            public <T> void failer(T data) {
                hideWaitDialog();
                ToastUtil.showMessage(StarContentActivity.this, data.toString());
            }
        });
    }

    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            getComment(false);
        }
    }

}
