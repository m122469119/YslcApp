package com.yslc.ui.activity;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yslc.R;
import com.yslc.bean.CelebrityComment;
import com.yslc.ui.adapter.BaseAdapterHelper;
import com.yslc.ui.adapter.MyFragmentAdapter;
import com.yslc.ui.adapter.QuickAdapter;
import com.yslc.ui.base.BaseActivity;
import com.yslc.ui.fragment.CelebrityFragment;
import com.yslc.util.HttpUtil;
import com.yslc.view.CalendarView;
import com.yslc.view.LoadView;

import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2016/4/18.
 */
public class InvestPaperActivity extends BaseActivity
        implements LoadView.OnTryListener,AdapterView.OnItemClickListener{
    private MyFragmentAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ArrayList<Fragment> fragmentList;//fragment
    private TextView no, title, date;
    private DrawerLayout drawer;
    private ListView navigationView;
    private QuickAdapter<CelebrityComment> adapter;
    private LoadView loadView;
    private PopupWindow dateView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_celebrity_com;
    }

    @Override
    protected String getToolbarTitle() {
        return "投资快报";
    }

    private ArrayList<CelebrityComment> data;

    /**
     * 开启线程下载数据
     */
    private void getData(String time){
        loadView.setStatus(LoadView.LOADING);
        RequestParams params = new RequestParams();
        params.put("date", time);
        String[] strs = time.split("-");
        date.setText(strs[2]);
        HttpUtil.originGet(HttpUtil.GET_CELEBRITY_COMMENT, this, params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        super.onSuccess(jsonObject);
                        loadView.setStatus(LoadView.SUCCESS);
                        parseData(jsonObject);
                        if (data.size() == 0) {
                            loadView.setStatus(LoadView.EMPTY_DATA);
                        } else {
                            showData(data);
                        }
                    }


                    @Override
                    public void onFailure(Throwable throwable, JSONObject jsonObject) {
                        super.onFailure(throwable, jsonObject);
                        loadView.setStatus(LoadView.ERROR);
                    }
                });
    }

    /**
     * 显示数据
     * @param data
     */
    private void showData(ArrayList<CelebrityComment> data) {
        updateFragment(data);
        updateNavList(data);
        showFirstTitle(data);//显示第一个fragment的标题
    }

    /**
     * 根据数据更新导航栏
     * @param data
     */
    private void updateNavList(ArrayList<CelebrityComment> data){
        if(null == adapter){
            adapter = new QuickAdapter<CelebrityComment>(this,
                    android.R.layout.simple_list_item_1,data){
                @Override
                protected void convert(BaseAdapterHelper helper, CelebrityComment item) {
                    //拼接成（AA01）头版 样式
                    helper.setText(android.R.id.text1, "("+item.getNo()+")"+item.getTitle());
                }
            };
            navigationView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 根据数据更新或创建fragment
     * @param data
     */
    private void updateFragment(ArrayList<CelebrityComment> data){
        //根据数据创建fragment
        if(fragmentList == null){
            fragmentList = handleData(data);
        }else{
            fragmentList.clear();
            fragmentList.addAll(handleData(data));
        }
        //配置适配器
        if(mSectionsPagerAdapter == null){
            mSectionsPagerAdapter = new MyFragmentAdapter(getSupportFragmentManager(), fragmentList);
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }else {
            mSectionsPagerAdapter.notifyDataSetChanged();
        }
        mViewPager.setCurrentItem(0);
    }

    /**
     * 显示第一个fragment标题
     * @param data
     */
    private void showFirstTitle(ArrayList<CelebrityComment> data){
        if(data.size()!= 0){
            title.setText(data.get(0).getTitle());
            no.setText(data.get(0).getNo());
        }

    }
    /**
     * 根据数据生成fragment
     * @param data
     * @return
     */
    private ArrayList<Fragment> handleData(ArrayList<CelebrityComment> data) {
            ArrayList<Fragment> list = new ArrayList<Fragment>();
        for (CelebrityComment comment: data){
            list.add( CelebrityFragment.newInstance(comment.getUrl(), comment.getTitle(),
                    comment.getNo()));
        }
        return list;
    }

    /**
     * 解析数据
     * @param jsonObject 数据源
     * @return 解析结果
     */
    private ArrayList<CelebrityComment> parseData(JSONObject jsonObject){
        ArrayList<CelebrityComment> data = new ArrayList<CelebrityComment>();
        try{
            JSONArray array = jsonObject.getJSONArray("section");
            for(int i=0; i<array.length(); i++){
                CelebrityComment comment = new CelebrityComment();
                comment.setNo(array.getJSONObject(i).optString("Number"));
                comment.setTitle(array.getJSONObject(i).optString("Title"));
                comment.setUrl(array.getJSONObject(i).optString("url"));
                data.add(comment);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        if(this.data==null){
            this.data = data;
        }else{
            this.data.clear();
            this.data.addAll(data);
        }

        return data;
    }

    @Override
    protected void initView() {
        initTitle();//初始化标题
        //loadview
        loadView = (LoadView)findViewById(R.id.view);
        loadView.setOnTryListener(this);
        //抽屉
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //导航栏
        navigationView = (ListView) findViewById(R.id.nav_view);
        navigationView.setOnItemClickListener(this);

        //viewPager
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.addOnPageChangeListener(viewPagerListener);

//        getData("2016-04-01");
        getData(new SimpleDateFormat("yy-MM-dd").format(new Date()));
    }


    /**
     * viewPager监听
     * <p>更新标题</p>
     */
    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //更改标题
            Fragment f =fragmentList.get(position);
            setTitle(data.get(position).getNo(),data.get(position).getTitle());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    /**
     * 初始化标题
     */
    private void initTitle() {
        no = (TextView) findViewById(R.id.celebrity_no);
        title = (TextView) findViewById(R.id.celebrity_title);
        date = (TextView) findViewById(R.id.celebrity_date);
        no.setOnClickListener(noEvent);
        date.setOnClickListener(dateEvent);
    }

    /**
     * 点击板块事件
     * <p>打开抽屉</p>
     */
    View.OnClickListener noEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            toggle();
        }
    };

    /**
     * 打开抽屉
     */
    private void toggle() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            drawer.openDrawer(GravityCompat.START);
        }
    }

    /**
     * 点击日期事件
     */
    View.OnClickListener dateEvent = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            RelativeLayout layout = new RelativeLayout(InvestPaperActivity.this);
            if(dateView == null){//初始化日历控件
                layout.setBackgroundResource(R.color.greyTrans2);//设置阴影布局

                CalendarView calendarView = new CalendarView(InvestPaperActivity.this);
                RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (int)navigationView.getHeight()*2/3);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                calendarView.setLayoutParams(params);
                calendarView.setOnCalendarClickListener(new CalendarView.OnCalendarClickListener() {
                    @Override
                    public void onCalendarClick(View v, String dateFormat) {
                        dateView.dismiss();
                        getData(dateFormat);
//                        calendarView.addMarker((RelativeLayout)v);
                    }
                });

                layout.addView(calendarView);
                dateView = new PopupWindow(layout,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        drawer.getHeight());
                dateView.setAnimationStyle(R.style.AnimBottom);
                dateView.setOutsideTouchable(true);
                dateView.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dateView.dismiss();
                    }
                });
                dateView.showAtLocation(layout,Gravity.BOTTOM,0,0);
            }else if(!dateView.isShowing()){
                dateView.showAtLocation(layout,Gravity.BOTTOM,0,0);
            }else {
//                dateView.dismiss();
            }
        }
    };

    /**
     * 重写返回键
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else if(dateView != null && dateView.isShowing()){//判断日历控件是否弹出
            dateView.dismiss();
        }else if(!webViewGoBack()){//判断webView是否能返回
            super.onBackPressed();
        }
    }

    /**
     * webView返回
     * @return 如果能返回成功，则true
     */
    private boolean webViewGoBack() {
        if(mSectionsPagerAdapter != null){
            Fragment fragment = mSectionsPagerAdapter.getCurrentFragment();

            if(fragment!= null && fragment instanceof CelebrityFragment){
                return ((CelebrityFragment) fragment).goBack();
            }
        }

        return false;
    }

    /**
     * 导航栏点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mViewPager.setCurrentItem(position);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    /**
     * fragment回调
     * <p>设置副标题</p>
     * @param no 当前板块号
     * @param title 当前标题
     */
    public void setTitle(String no, String title) {
        this.title.setText(title);
        this.no.setText(no);
    }

    @Override
    public void onTry() {
        getData("2016-04-01");
//        if(loadView.setStatus(LoadView.LOADING)){
//        }
    }
}
