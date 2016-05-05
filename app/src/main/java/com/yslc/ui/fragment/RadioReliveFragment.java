package com.yslc.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.yslc.bean.ColumnBean;
import com.yslc.inf.GetDataCallback;
import com.yslc.data.service.RadioModelService;
import com.yslc.ui.base.BaseFragment;
import com.yslc.R;
import com.yslc.ui.activity.RadioReliveDetialActivity;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.RadioBean;
import com.yslc.view.BaseListView;
import com.yslc.view.LoadView;
import com.yslc.view.LoadView.OnTryListener;

/**
 * 股市广播节目重温Fragment
 *
 * @author HH
 */
public class RadioReliveFragment extends BaseFragment implements OnTryListener,
        OnItemClickListener {
    private BaseListView listView;
    private LoadView loadView;
    private Context context;
    private ColumnBean columnBean;
    private List<RadioBean> infoItemList;
    private RadioModelService radioModelService;

    /**
     * 创建fragment
     * <p>获取上下文，副标题id，实例化数据类</p>
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        columnBean = (ColumnBean) getArguments().getSerializable("bean");
        infoItemList = new ArrayList<>();
    }

    /**
     * 设置布局
     * <p>包含加载更多列表和加载圈圈</p>
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.listview_vedio_relive;
    }

    /**
     * 初始化布局
     * <p>关联listView和加载圈圈并设置监听</p>
     * <p>实例化业务处理类</p>
     * @param views
     */
    @Override
    protected void findView(View views) {
        super.findView(views);

        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);
        listView = (BaseListView) views.findViewById(R.id.listview);
        listView.setOnItemClickListener(this);
        radioModelService = new RadioModelService(context);
    }

    /**
     * 第一次加载
     * <p>下载数据</p>
     */
    @Override
    protected void onFristLoadData() {
        super.onFristLoadData();
        if (loadView.setStatus(LoadView.LOADING)) {
            loadData();
        }
    }

    /**
     * 加载界面列表
     * <p>下载数据，成功后刷新列表</p>
     */
    private void loadData() {
        radioModelService.getRadioReliveListData(columnBean.getId(), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                loadView.setStatus(LoadView.SUCCESS);
                infoItemList = (ArrayList<RadioBean>) data;
                if (infoItemList.size() == 0) {
                    loadView.setStatus(LoadView.EMPTY_DATA);
                } else {
                    listRefersh();//刷新列表
                }
            }

            @Override
            public <T> void failer(T data) {
                loadView.setStatus(LoadView.ERROR);
            }
        });
    }

    /**
     * 进行列表的刷新
     */
    private void listRefersh() {
        QuickAdapter<RadioBean> adapter = new QuickAdapter<RadioBean>(context,
                R.layout.item_radio_relive_listview, infoItemList) {
            @Override
            protected void convert(BaseAdapterHelper helper, RadioBean item) {
                helper.setText(R.id.radioName, item.getRadioName());
                helper.setText(R.id.radioDate, item.getRadioDate());
            }
        };

        listView.setAdapter(adapter);
    }

    /**
     * 点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // 进入广播重温详情
        Intent intent = new Intent(context, RadioReliveDetialActivity.class);
        intent.putExtra("RadP_Id", infoItemList.get(position).getRadioId());
        intent.putExtra("Date", columnBean.getName());
        context.startActivity(intent);
    }

    /**
     * 重试
     */
    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            loadData();
        }
    }
}
