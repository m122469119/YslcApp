package com.yslc.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.yslc.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义日历控件
 */
public class CalendarView extends LinearLayout implements
        android.view.GestureDetector.OnGestureListener{
    public static final int COLOR_BG_WEEK_TITLE = Color.parseColor("#ffeeeeee");//星期标题背景颜色
    public static final int COLOR_TX_WEEK_TITLE = Color.parseColor("#ffcc3333");//星期标题文字颜色
    public static final int COLOR_TX_THIS_MONTH_DAY = Color.parseColor("#aa564b4b");//当前月日历数字颜色
    public static final int COLOR_TX_OTHER_MONTH_DAY = Color.parseColor("#ffcccccc");//其他月日历数字颜色
    public static final int COLOR_TX_THIS_DAY = Color.parseColor("#ff008000");//今天文字颜色
    public static final int COLOR_BG_THIS_DAY = Color.parseColor("#ffcccccc");//今天背景颜色
    public static final int COLOR_BG_CALENDAR = Color.parseColor("#ffeeeeee");//本控件背景颜色

    private GestureDetector gd;//手势监听器
    private Animation push_left_in;//动画—左进
    private Animation push_left_out;//动画—左出
    private Animation push_right_in;//动画—右进
    private Animation push_right_out;//动画—右出

    private int ROWS_TOTAL = 6;// 日历的行数(不包星期标题）
    private int COLS_TOTAL = 7;// 日历的列数
    private String[][] dates = new String[ROWS_TOTAL][COLS_TOTAL]; //当前日历日期(存储日历数据如：2016-04-08）
    private float tb;//10dp

    private OnCalendarClickListener onCalendarClickListener;//日历点击回调
    private OnCalendarDateChangedListener onCalendarDateChangedListener;//日历翻页回调

    private String[] weekday = new String[] { "日","一","二","三","四","五","六"};//星期标题

    private int calendarYear;//日历年份
    private int calendarMonth;//日历月份
    private Calendar today = Calendar.getInstance();//今天
    /**
     * 当前月份的1号
     */
    private Calendar calendar_day; // 日历这个月第一天（1号）

    private LinearLayout firstCalendar; //第一个日历
    private LinearLayout secondCalendar; //第二个日历
    private LinearLayout currentCalendar; //当前显示的日历
    private RelativeLayout[][] cells;//记录格子
    private TextView title;
    private ViewFlipper calendarContent;

    //存储标记日子的标记图片被标注(key是日期，value是drawable id
    private Map<String, Integer> marksMap = new HashMap<String, Integer>();
    //存储某个日子的背景色(key是日期，value是颜色）
    private Map<String, Integer> dayByColorMap = new HashMap<String, Integer>();

    public CalendarView(Context context) {
        super(context);
        init(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        initDimen();//初始化10dp备用
        setOrientation(LinearLayout.VERTICAL);//设置方向
        setBackgroundColor(COLOR_BG_CALENDAR);//设置背景色
        //实例化手势监听
        gd = new GestureDetector(context, this);
        //初始化日历翻动动画
        initAnimation(context);
        //绘制控件
        drawView();

        //设置日历上的日子（1号）
        calendarYear = today.get(Calendar.YEAR);
        calendarMonth = today.get(Calendar.MONTH);
        calendar_day = new GregorianCalendar(calendarYear, calendarMonth, 1);
        //填充展示日历
        setCalendarDate(firstCalendar);

    }

    /**
     * 绘制控件
     */
    private void drawView() {
        //导航标题
        LinearLayout navTitle = drawNavTitle();
        addView(navTitle);
        //星期标题
        LinearLayout title =drawWeekTitle();
        addView(title);//完成星期标题layout布置
        //日历内容
        calendarContent = drawContent();
        addView(calendarContent);
    }

    /**
     * 绘制日历
     * @return
     */
    private ViewFlipper drawContent() {
        ViewFlipper content =new ViewFlipper(getContext());
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,0,7);
        content.setLayoutParams(params);
        //初始化第一个日历
        firstCalendar = createLinearLayout(LinearLayout.VERTICAL,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //初始化第二个日历
        secondCalendar =  createLinearLayout(LinearLayout.VERTICAL,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //设置默认日历为第一个日历
        currentCalendar = firstCalendar;
        //加入ViewFlipper
        content.addView(firstCalendar);
        content.addView(secondCalendar);
        //绘制线条框架
        drawFrame(firstCalendar);
        drawFrame(secondCalendar);
        return content;
    }

    /**
     * 设置标题
     */
    private void setTitle() {
        title.setText(calendarMonth+1+"月"+calendarYear+"年");
    }

    /**
     * 初始化一个10dp的尺寸
     */
    private void initDimen() {
        Resources res = getResources();
        tb = res.getDimension(R.dimen.history_tb);
    }


    /**
     * 创建一个LinearLayout布局
     * @param orientation
     * @param width
     * @param height
     * @param weight
     * @return
     */
    private LinearLayout createLinearLayout(int orientation, int width, int height, float weight){
        LinearLayout calendar = new LinearLayout(getContext());
        calendar.setOrientation(orientation);
        calendar.setLayoutParams(new LinearLayout.LayoutParams(
                width, height, weight));
        return calendar;
    }

    /**
     * 创建一个LinearLayout布局
     * @param orientation
     * @param width
     * @param height
     * @return
     */
    private LinearLayout createLinearLayout(int orientation, int width, int height){
        LinearLayout calendar = new LinearLayout(getContext());
        calendar.setOrientation(orientation);
        calendar.setLayoutParams(new LinearLayout.LayoutParams(
                width, height));
        return calendar;
    }

    /**
     * 初始化动画
     * @param context
     */
    private void initAnimation(Context context) {
        push_left_in = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
        push_left_out = AnimationUtils.loadAnimation(context, R.anim.push_left_out);
        push_right_in = AnimationUtils.loadAnimation(context, R.anim.push_right_in);
        push_right_out = AnimationUtils.loadAnimation(context, R.anim.push_right_out);
    }


    /**
     * 填充日历（包含日期、标记、背景等）
     */
    private void setCalendarDate(LinearLayout calendarView) {
        //设置标题
        setTitle();
        cells = (RelativeLayout[][]) calendarView.getTag();
        Date save = calendar_day.getTime();//记录日历填充前的时间
        //本月1号是星期几 （1是星期天 ，7是星期六）
        int start = calendar_day.get(Calendar.DAY_OF_WEEK);
        calendar_day.add(Calendar.DAY_OF_MONTH, -(start - 1));//回调到上月
        for(int i=0; i<ROWS_TOTAL; i++){
            for(int j=0; j<COLS_TOTAL; j++){
//                dates[i][j] = format(calendar_day);//记录格子的日期
                TextView textView =((TextView)cells[i][j].getChildAt(0));
                textView.setTag(calendar_day.getTime());//记录格子日期
                textView.setText(String.valueOf(calendar_day.get(Calendar.DAY_OF_MONTH)));
                //设置文字颜色
                if(calendar_day.get(Calendar.MONTH)==calendarMonth){//当前显示月份
                    setThisMonthStyle(textView);
                }else{//其他月份
                    setOtherMonthStyle(textView);
                }
                if(isToday(calendar_day)){//设置今天样式
                    setTodayStyle(textView);
                }
                calendar_day.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        calendar_day.setTime(save);//填完数据回拨到一号
//        dates[0][k] = format(new GregorianCalendar(year, month, temp_day));
//        setMarker();//TODO 事件标记
//        if(dayByColorMap.get(dates[i][j]) != null){
//                            view.setTextColor(Color.WHITE);
//                            view.setBackgroundResource(dayByColorMap.get(dates[i][j]));
//                        }



    }

    /**
     * 比较是否今天
     * <p>比较年月日</p>
     * @param calendar
     * @return
     */
    private boolean isToday(Calendar calendar) {
        if(calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH))
        return true;
        return  false;
    }

    /**
     * 今天样式
     * @param textView
     */
    private void setTodayStyle(TextView textView) {
        textView.setText("今天");
        textView.setTextColor(COLOR_TX_THIS_DAY);
        textView.setBackgroundColor(COLOR_BG_THIS_DAY);
    }

    /**
     * 设置当前显示的月的样式
     * @param textView
     */
    private void setThisMonthStyle(TextView textView) {
        textView.setTextColor(COLOR_TX_THIS_MONTH_DAY);
    }

    /**
     * 设置其他月的样式
     * @param textView
     */
    private void setOtherMonthStyle(TextView textView) {
        textView.setTextColor(COLOR_TX_OTHER_MONTH_DAY);
    }

    /**
     * 日历点击接口
     */
    public interface OnCalendarClickListener {
        void onCalendarClick(View v, String dateFormat);
    }

    public interface OnCalendarDateChangedListener {
        void onCalendarDateChanged(int year, int month);
    }

    /**
     * 下个月
     */
    public synchronized void nextMonth() {
        calendarContent.setInAnimation(push_left_in);
        calendarContent.setOutAnimation(push_left_out);
        calendar_day.add(Calendar.MONTH, 1);//拨到下个月
        updateCurrent();//更新全局变量
        setCalendarDate(nextCalendar());//根据下个月填充数据
        calendarContent.showNext();
//		// 回调
//		if (onCalendarDateChangedListener != null) {
//			onCalendarDateChangedListener.onCalendarDateChanged(calendarYear,
//					calendarMonth + 1);
//		}
    }

    private LinearLayout nextCalendar() {
        if(currentCalendar == firstCalendar){
            currentCalendar = secondCalendar;
        }else{
            currentCalendar = firstCalendar;
        }
        return currentCalendar;
    }

    /**
     * 更新当前年月
     */
    private void updateCurrent() {
        calendarMonth = calendar_day.get(Calendar.MONTH);
        calendarYear = calendar_day.get(Calendar.YEAR);
    }

    /**
     * 上个月
     */
    public synchronized void lastMonth(){
        calendarContent.setInAnimation(push_right_in);
        calendarContent.setOutAnimation(push_right_out);
        calendar_day.add(Calendar.MONTH, -1);//拨到上个月
        updateCurrent();//更新全局变量
        setCalendarDate(nextCalendar());//根据下个月填充数据
        calendarContent.showPrevious();
//		// 回调
//		if (onCalendarDateChangedListener != null) {
//			onCalendarDateChangedListener.onCalendarDateChanged(calendarYear,
//					calendarMonth + 1);
//		}
    }

//    public void showCalendar(int year, int month){
//        calendarYear = year;
//        calendarMonth = month - 1
//        calendar_day.set(calendarYear, calendarMonth, 1);
//    }

//    public  int getCalendarYear() {
//        return calendar_day.get(Calendar.YEAR)+1900;
//    }
//
//    public int getCalendarMonth() {
//        return calendar_day.get(Calendar.MONTH)+1;
//    }
//
//    public void addMark(String date, int id){
//        marksMap.put(date, id);
//        setCalendarDate(currentCalendar);
//    }
//
//    public void addMarks(List<String> date, int id) {
//        for(int i= 0; i< date.size(); i++){
//            marksMap.put(date.get(i), id);
//        }
//        setCalendarDate(currentCalendar);
//    }

//    public void removeMark(String date) {
//        marksMap.remove(date);
//        setCalendarDate(currentCalendar);
//    }

//    void setCalendarDayBgColor(String date, int color){
//        dayByColorMap.put(date, color);
//        setCalendarDate(currentCalendar);
//    }

//    public void removeCalendarDayBgColor(String date){
//        dayByColorMap.remove(date);
//        setCalendarDate(currentCalendar);
//    }

//    public void removeAllBgColor() {
//        dayByColorMap.clear();
//        setCalendarDate(currentCalendar);
//    }




    /**
     * 返回字符串 2016-09-10
     * @param date
     * @return
     */
    private String format(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
    }

    /**
     * 为格子设置标记
     * @param cell 格子
     */
    public void addMarker(RelativeLayout cell) {
        int childCount = cell.getChildCount();
        if(childCount < 2){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    (int) (tb * 0.7), (int) (tb * 0.7));//7dp
            //右下角
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.setMargins(0, 0, 1, 1);
            ImageView markView = new ImageView(getContext());
//            markView.setImageResource(marksMap.get(dates[x][y]));
            markView.setLayoutParams(params);
            markView.setBackgroundResource(R.drawable.calendar_bg_tag);
            cell.addView(markView);
        }
    }

    public void deleteMarker(RelativeLayout cell){
        if(cell.getChildCount() >1 ){
            cell.removeView(cell.getChildAt(1));
        }
    }


    /**
     * 绘制线条框架
     * <p>绘制日历，没有数据</p>
     * @param oneCalendar 日历（一个LinearLayout的container）
     */
    private void drawFrame(LinearLayout oneCalendar) {
        //初始化cells，用于记录格子
        cells = new RelativeLayout[ROWS_TOTAL][COLS_TOTAL];

        //添加日历布局
        LinearLayout content = createLinearLayout(LinearLayout.VERTICAL,
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 7f);//宽度充满，高度0，权重7
        drawRow(content);//画行
        oneCalendar.addView(content);
        oneCalendar.setTag(cells);
    }

    /**
     * 绘制导航布局
     * @return
     */
    private LinearLayout drawNavTitle() {
        LinearLayout navTitle = createLinearLayout(LinearLayout.HORIZONTAL,
                LayoutParams.MATCH_PARENT,0,1.5f);
        navTitle.setGravity(Gravity.CENTER);

        //左右按键和中间标题应该很容易看懂
        ImageButton left = new ImageButton(getContext());
        left.setBackgroundColor(Color.TRANSPARENT);
        ImageButton right = new ImageButton(getContext());
        right.setBackgroundColor(Color.TRANSPARENT);
        title = new TextView(getContext());
        title.setTextSize(getResources().getDimension(R.dimen.calendar_title));
        title.setPadding((int)(0.5*tb),(int)(0.5*tb),(int)(0.5*tb),(int)(0.5*tb));
        navTitle.addView(left);
        navTitle.addView(title);
        navTitle.addView(right);
        left.setImageResource(R.drawable.calendar_prev);
        right.setImageResource(R.drawable.calendar_next);
        //点击事件
        left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lastMonth();
            }
        });
        right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonth();
            }
        });
        return navTitle;
    }

    /**
     * 画行
     * @param content
     */
    private void drawRow(LinearLayout content) {
        //添加日期Tex
        for(int i=0; i< ROWS_TOTAL; i++) {
            //每一行linearLayout
            LinearLayout row = createLinearLayout(LinearLayout.HORIZONTAL,
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
            //绘制日期格子 (每行包含7个格子)
            drawCell(row, i);

            content.addView(row);
        }
    }

    /**
     * 画格子
     * @param row
     * @param i 第几行
     */
    private void drawCell(LinearLayout row, int i) {
        for (int j=0; j< COLS_TOTAL; j++){//循环7次
            //每个格子是一个相对布局
            RelativeLayout cell = new RelativeLayout(getContext());
            cell.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 1));
            cell.setBackgroundResource(R.drawable.calendar_day_bg);

            createCellTextView(cell);//创建textView
            row.addView(cell);
            //添加监听事件
            cell.setOnClickListener(cellClickListener);
            cells[i] [j] = cell;//记录cellView
        }
    }

    private TextView createCellTextView(RelativeLayout cell) {
//        if(cell.getChildAt(0) !=null){
//            return (TextView)cell.getChildAt(0);
//        }else{
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            TextView view = new TextView(getContext());
            view.setLayoutParams(params);
            view.setGravity(Gravity.CENTER);
            view.setTextColor(COLOR_TX_THIS_MONTH_DAY);
            cell.addView(view);
            return view;
//        }
    }


    /**
     * 寻找格子位置并回调该客户端
     */
    private OnClickListener cellClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView tv = (TextView)((ViewGroup) v).getChildAt(0);
            selectCellStyle(tv);//选中后，改变格子样式
            int[] location =findLocation(v);
//            dates[location[0]][location[1]]
            Date d = (Date)tv.getTag();
//            Toast.makeText(getContext(),format(d),Toast.LENGTH_SHORT).show();
            if (onCalendarClickListener != null) {
                onCalendarClickListener.onCalendarClick
                        (v, format(d));
            }
        }


    };

    private TextView prvSelect;
    /**
     * 选中后得TextView样式
     * @param tv
     */
    private void selectCellStyle(TextView tv) {
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundColor(Color.parseColor("#0088ec"));
        if(null != prvSelect){
            resetCellStyle(prvSelect);
        }
        prvSelect = tv;
    }

    private void resetCellStyle(TextView prvSelect) {
        Calendar c = Calendar.getInstance();
        c.setTime((Date) prvSelect.getTag());
        if(c.get(Calendar.MONTH) == calendarMonth){
            prvSelect.setTextColor(COLOR_TX_THIS_MONTH_DAY);
        }else{
            prvSelect.setTextColor(COLOR_TX_OTHER_MONTH_DAY);
        }
        prvSelect.setBackgroundColor(Color.parseColor("#eeeeee"));
        if(isToday(c)){//设置今天样式
            setTodayStyle(prvSelect);
        }
    }

    private int[] findLocation(View v) {
        int row = 0, col = 0;
        ViewGroup parent = (ViewGroup) v.getParent();//row contain
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (v == parent.getChildAt(i)) {
                col = i;
                break;
            }
        }

        ViewGroup p_parent = (ViewGroup) parent.getParent();//content contain
        for (int i = 0; i < p_parent.getChildCount(); i++) {
            if (parent == p_parent.getChildAt(i)) {
                row = i;
                break;
            }
        }
        int position[]= {row, col};
        return position;
    }

    /**
     * 绘制星期标题
     * @return
     */
    private LinearLayout drawWeekTitle() {
        LinearLayout title = createLinearLayout(LinearLayout.HORIZONTAL,
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.5f);//宽填满，高0，权重0.5
//        LinearLayout title = new LinearLayout(getContext());
        title.setBackgroundColor(COLOR_BG_WEEK_TITLE);//设置背景颜色

        ((LinearLayout.LayoutParams)title.getLayoutParams())
                .setMargins(0, 0, 0, (int) (tb * 1.2));//bottom margin 10dp

//        title.setLayoutParams(layout);
        addSevenTextView(title);//添加7个TextView
        return title;
    }

    /**
     * 为一个LinearLayout 添加7个相同大小的TextView
     * @param title
     */
    private void addSevenTextView(LinearLayout title) {
        //添加周末TextView
        for(int i = 0; i< COLS_TOTAL; i++) {
            TextView view = new TextView(getContext());
            view.setGravity(Gravity.CENTER);
            view.setText(weekday[i]);
            view.setTextColor(COLOR_TX_WEEK_TITLE);
            view.setLayoutParams(new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));//宽度0 高度充满，权重1
            title.addView(view);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(gd != null){
            if(gd.onTouchEvent(ev))
                return  true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.gd.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //向左
        if(e1.getX() - e2.getX() > 20){
            nextMonth();
        }else if(e2.getX() - e1.getX() > 20){//向右滑
            lastMonth();
        }
        return false;
    }

    //--------get set ----------
    public OnCalendarClickListener getOnCalendarClickListener() {
        return  onCalendarClickListener;
    }

    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener){
        this.onCalendarClickListener = onCalendarClickListener;
    }

    public OnCalendarDateChangedListener getOnCalendarDateChangedListener(){
        return  onCalendarDateChangedListener;
    }

    public void setOnCalendarDateChangedListener(
            OnCalendarDateChangedListener onCalendarDateChangedListener){
        this.onCalendarDateChangedListener = onCalendarDateChangedListener;
    }
}
