package com.yslc.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yslc.R;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.bean.AlbumModel;
import com.yslc.inf.OnItemClick;
import com.yslc.util.CommonUtil;

import java.util.List;

/**
 * 文件夹选择弹出框
 * <p>
 * Created by HH on 2015/12/23.
 */
public class AlbumPopupWindow extends PopupWindow {
    private Context context;
    private ListView listView;
    private QuickAdapter<AlbumModel> adapter;
    private List<AlbumModel> albunList;
    private ImageLoader imageLoader;
    private int checkPosition = 0;  //上一次选择的文件夹

    public AlbumPopupWindow(Context context, List<AlbumModel> albunList) {
        super(context);
        this.context = context;
        this.albunList = albunList;
        imageLoader = ImageLoader.getInstance();
        View view = View.inflate(context, R.layout.album_popupwindow_listview, null);
        findView(view);
        setContentView(view);
    }

    private void findView(View view) {
        setWidth(CommonUtil.getScreenWidth(context));
        setHeight(CommonUtil.dip2px(context, 320));
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        setAnimationStyle(R.style.AnimBottom);

        listView = (ListView) view.findViewById(R.id.listview);
        listView.setDivider(ContextCompat.getDrawable(context, R.drawable.line_dotted));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!albunList.get(position).isCheck()) {
                    albunList.get(position).setCheck(true);
                    albunList.get(checkPosition).setCheck(false);
                    checkPosition = position;
                    onItemClick.onItemClick(albunList.get(position).getName());
                }

                dismiss();
            }
        });
    }

    public void showDialog(View view) {
        setAdapter();
        showAsDropDown(view, 0, 0);
    }


    private void setAdapter() {
        if (adapter == null) {
            adapter = new QuickAdapter<AlbumModel>(context, R.layout.item_album_dialog, albunList) {
                @Override
                protected void convert(BaseAdapterHelper helper, AlbumModel item) {
                    helper.setText(R.id.albumName, item.getName());
                    helper.setText(R.id.albumLen, item.getCount() + "");
                    helper.setVisible(R.id.isCheck, item.isCheck());
                    imageLoader.displayImage("file://" + item.getRecent(), (ImageView) helper.getView(R.id.albumImg));
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
