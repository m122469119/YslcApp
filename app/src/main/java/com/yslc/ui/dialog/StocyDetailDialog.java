package com.yslc.ui.dialog;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yslc.R;
import com.yslc.bean.MinuteInfo;
import com.yslc.bean.StocksDetail;
import com.yslc.util.KChartUtil;

/**
 * 分时图买卖详情对话框
 *
 * @author HH
 */
public class StocyDetailDialog extends BaseDialog {
    private View view = null;
    private TextView now, as, time, buy1, buy2, buy3, buy4, buy5, sell1, sell2, sell3, sell4, sell5;
    private MinuteInfo info;
    private StocksDetail detail;

    public StocyDetailDialog(Context context, MinuteInfo info, StocksDetail detail) {
        super(context);
        this.info = info;
        this.detail = detail;
        view = View
                .inflate(context, R.layout.stocy_sell_bug_dialog, null);
        // 初始化界面
        findView();
        setContentView(view);
    }

    /**
     * 显示弹窗口
     */
    public void showDialog() {
        show();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 初始化界面
     */
    private void findView() {
        now = (TextView) view.findViewById(R.id.now);
        as = (TextView) view.findViewById(R.id.as);
        time = (TextView) view.findViewById(R.id.time);
        buy1 = (TextView) view.findViewById(R.id.buy1);
        buy2 = (TextView) view.findViewById(R.id.buy2);
        buy3 = (TextView) view.findViewById(R.id.buy3);
        buy4 = (TextView) view.findViewById(R.id.buy4);
        buy5 = (TextView) view.findViewById(R.id.buy5);
        sell1 = (TextView) view.findViewById(R.id.sell1);
        sell2 = (TextView) view.findViewById(R.id.sell2);
        sell3 = (TextView) view.findViewById(R.id.sell3);
        sell4 = (TextView) view.findViewById(R.id.sell4);
        sell5 = (TextView) view.findViewById(R.id.sell5);

        fillData();
    }

    /**
     * 填充数据
     */
    public void fillData() {
        now.setTextColor(ContextCompat.getColor(getContext(), info.getColor()));
        now.setText(String.valueOf(info.getNow()));
        as.setTextColor(ContextCompat.getColor(getContext(), info.getColor()));
        as.setText(info.getStocyGains() + "  " + info.getStocyAs());
        time.setText(KChartUtil.getMinute(info.getMinute()));
        buy1.setText(Html.fromHtml("买①:<font color=\"#ff0000\">" + detail.getBug1() + "</font>" + "  " + "<font color=\"#DAA520\">" + detail.getbAmount1() + "</font>"));
        buy2.setText(Html.fromHtml("买②:<font color=\"#ff0000\">" + detail.getBug2() + "</font>" + "  " + "<font color=\"#DAA520\">" + detail.getbAmount2() + "</font>"));
        buy3.setText(Html.fromHtml("买③:<font color=\"#ff0000\">" + detail.getBug3() + "</font>" + "  " + "<font color=\"#DAA520\">" + detail.getbAmount3() + "</font>"));
        buy4.setText(Html.fromHtml("买④:<font color=\"#ff0000\">" + detail.getBug4() + "</font>" + "  " + "<font color=\"#DAA520\">" + detail.getbAmount4() + "</font>"));
        buy5.setText(Html.fromHtml("买⑤:<font color=\"#ff0000\">" + detail.getBug5() + "</font>" + "  " + "<font color=\"#DAA520\">" + detail.getbAmount5() + "</font>"));
        sell1.setText(Html.fromHtml("卖①:<font color=\"#ff0000\">" + detail.getSell1() + "</font>" + "  " + "<font color=\"#DAA520\">" + detail.getsAmount1() + "</font>"));
        sell2.setText(Html.fromHtml("卖②:<font color=\"#ff0000\">" + detail.getSell2() + "</font>" + "  " + "<font color=\"#DAA520\">" + detail.getsAmount2() + "</font>"));
        sell3.setText(Html.fromHtml("卖③:<font color=\"#ff0000\">" + detail.getSell3() + "</font>" + "  " + "<font color=\"#DAA520\">" + detail.getsAmount3() + "</font>"));
        sell4.setText(Html.fromHtml("卖④:<font color=\"#ff0000\">" + detail.getSell4() + "</font>" + "  " + "<font color=\"#DAA520\">" + detail.getsAmount4() + "</font>"));
        sell5.setText(Html.fromHtml("卖⑤:<font color=\"#ff0000\">" + detail.getSell5() + "</font>" + "  " + "<font color=\"#DAA520\">" + detail.getsAmount5() + "</font>"));
    }

}
