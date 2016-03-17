package com.yslc.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.yslc.bean.ColnumBean;
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
    private ColnumBean colnumBean;
    private List<RadioBean> infoItemList;
    private RadioModelService radioModelService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        colnumBean = (ColnumBean) getArguments().getSerializable("bean");
        infoItemList = new ArrayList<>();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.listview_vedio_relive;
    }

    @Override
    protected void findView(View views) {
        super.findView(views);

        loadView = (LoadView) views.findViewById(R.id.view);
        loadView.setOnTryListener(this);
        listView = (BaseListView) views.findViewById(R.id.listview);
        listView.setOnItemClickListener(this);
        radioModelService = new RadioModelService(context);
    }

    @Override
    protected void onFristLoadData() {
        super.onFristLoadData();
        if (loadView.setStatus(LoadView.LOADING)) {
            loadData();
        }
    }

    /**
     * 加载界面列表
     */
    private void loadData() {
        radioModelService.getRadioReliveListData(colnumBean.getId(), new GetDataCallback() {
            @Override
            public <T> void success(T data) {
                loadView.setStatus(LoadView.SUCCESS);
                infoItemList = (ArrayList<RadioBean>) data;
                if (infoItemList.size() == 0) {
                    loadView.setStatus(LoadView.EMPTY_DATA);
                } else {
                    listRefersh();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // 进入广播重温详情
        Intent intent = new Intent(context, RadioReliveDetialActivity.class);
        intent.putExtra("RadP_Id", infoItemList.get(position).getRadioId());
        intent.putExtra("Date", colnumBean.getName());
        context.startActivity(intent);
    }

    @Override
    public void onTry() {
        if (loadView.setStatus(LoadView.LOADING)) {
            loadData();
        }
    }
}
