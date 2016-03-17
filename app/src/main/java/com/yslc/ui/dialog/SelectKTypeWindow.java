package com.yslc.ui.dialog;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yslc.R;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.KTypeModel;
import com.yslc.inf.OnItemClick;

import java.util.ArrayList;
import java.util.List;

/**
 * K线图类型选择弹出框
 * <p>
 * Created by HH on 2015/12/23.
 */
public class SelectKTypeWindow extends BaseDialog {
    private Context context;
    private ListView listView;
    private QuickAdapter<KTypeModel> adapter;
    private List<KTypeModel> kList;
    private int checkPosition = 4;  //上一次选择的K线类型

    public SelectKTypeWindow(Context context) {
        super(context);
        this.context = context;
        initList();
        View view = View.inflate(context, R.layout.album_popupwindow_listview, null);
        findView(view);
        setContentView(view);
    }

    /**
     * 初始化K线类型
     */
    private void initList() {
        kList = new ArrayList<>();
        kList.add(new KTypeModel("5分钟K线图", 0, false));
        kList.add(new KTypeModel("10分钟K线图", 1, false));
        kList.add(new KTypeModel("30分钟K线图", 2, false));
        kList.add(new KTypeModel("60分钟K线图", 3, false));
        kList.add(new KTypeModel("日K图", 4, true));
        kList.add(new KTypeModel("周K图", 5, false));
        kList.add(new KTypeModel("月K图", 6, false));
    }

    private void findView(View view) {
        listView = (ListView) view.findViewById(R.id.listview);
        listView.setDivider(ContextCompat.getDrawable(context, R.drawable.line_dotted));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!kList.get(position).isCheck()) {
                    kList.get(position).setIsCheck(true);
                    kList.get(checkPosition).setIsCheck(false);
                    checkPosition = position;
                    onItemClick.onItemClick(kList.get(position).getName() + " " + kList.get(position).getType());
                }

                dismiss();
            }
        });
    }

    /**
     * 显示弹出框
     */
    public void showDialog() {
        setAdapter();
        show();
    }


    private void setAdapter() {
        if (adapter == null) {
            adapter = new QuickAdapter<KTypeModel>(context, R.layout.item_album_dialog, kList) {
                @Override
                protected void convert(BaseAdapterHelper helper, KTypeModel item) {
                    helper.setText(R.id.albumName, item.getName());
                    helper.setVisible(R.id.albumLen, false);
                    helper.setVisible(R.id.albumImg, false);
                    helper.setVisible(R.id.isCheck, item.isCheck());
                }
            };
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

}
