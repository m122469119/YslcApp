package com.yslc.util;

import com.yslc.bean.AdBean;
import com.yslc.bean.ColumnBean;
import com.yslc.bean.CommentBean;
import com.yslc.bean.NewBean;
import com.yslc.bean.StockInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/3.
 */
public class ParseUtil {

    public static ArrayList<ColumnBean> parseColumnBean(String arg0){
//         获取栏目成功
        ArrayList listTitle = new ArrayList<>();
        try {
            JSONArray ja = new JSONArray(arg0);
            // 解析数据
            ColumnBean cb;
            JSONObject jo;
            for (int i = 0, len = ja.length(); i < len; i++) {
                jo = ja.getJSONObject(i);
                cb = new ColumnBean();
                cb.setId(jo.optString("StID"));
                cb.setName(jo.optString("StName"));
                cb.setStOrder(jo.optString("StOrder"));
                listTitle.add(cb);
            }

//                                callback.success(listTitle);
        } catch (JSONException e) {
            // 暂无数据
//                                callback.failer(null);
            e.printStackTrace();
        }
        return listTitle;
    }

    public static ArrayList<NewBean> parseNewBean (String json) {
        ArrayList<NewBean> list = new ArrayList<>();
        try {
            //**** 同一接口，（快讯和咨讯）返回的json格式不统一，仅供测试（后期需修改） *******
            JSONArray infoJa;
            if (json.indexOf("[") < json.indexOf("{")) {
                infoJa = new JSONArray(json);
            } else {
                infoJa = new JSONObject(json).getJSONArray("NewsInfo");
            }

            NewBean infoItem;
            JSONObject tempJo;
            for (int i = 0, len = infoJa.length(); i < len; i++) {
                tempJo = infoJa.getJSONObject(i);
                infoItem = new NewBean();
                infoItem.setNild(tempJo.optString("NiId"));
                infoItem.setNiTitle(tempJo.optString("NiTitle"));
                infoItem.setNiContent(tempJo.optString("NiContent"));
                infoItem.setNiTime(tempJo.optString("NiTime"));
                infoItem.setWhatColor(tempJo.optString("NiTop"));
                infoItem.setReadNum(tempJo.optString("NiNumber"));
                infoItem.setNiImg(tempJo.optString("NiImg"));
                list.add(infoItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return list;
        }
    }

    public static ArrayList<StockInfo> parseStockInfo(String json) {
        ArrayList<StockInfo> list = new ArrayList<>();
        try {
            JSONArray ja = new JSONArray(json);
            JSONObject jo;
            for (int i = 0, len = ja.length(); i < len; i++) {
                jo = ja.getJSONObject(i);
                StockInfo mode = new StockInfo();
                mode.setName(jo.optString("name"));
                mode.setDiffer(jo.optString("differ"));
                mode.setNow(jo.optString("now"));
                mode.setProportion(jo.optString("proportion"));
                list.add(mode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<AdBean> parseAdBean(String json) {
        ArrayList<AdBean> list = new ArrayList<>();
        try {
            JSONArray adJa = new JSONObject(json).getJSONArray("PagePicture");
            AdBean ad;
            JSONObject jo;
            for (int i = 0, len = adJa.length(); i < len; i++) {
                ad = new AdBean();
                jo = adJa.getJSONObject(i);
                ad.setTitle(jo.optString("Title"));
                ad.setImgUrl(jo.optString("Img"));
                ad.setLinkUrl(jo.optString("Url"));
                list.add(ad);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return list;
        }
    }

    public static ArrayList<CommentBean> parseCommentBean(String json) {
        ArrayList<CommentBean> listData = new ArrayList<>();
        try {
            JSONObject tempJo;
            CommentBean comment;
            JSONArray ja = new JSONArray(json);
            for (int i = 0, len = ja.length(); i < len; i++) {
                tempJo = ja.getJSONObject(i);
                comment = new CommentBean();
                comment.setNcid(tempJo.optString("Ncid"));
                comment.setNcikName(tempJo.optString("Nickname"));
                comment.setUiImg(tempJo.optString("UiImg"));
                comment.setContent(tempJo.optString("Content"));
                comment.setTime(tempJo.optString("Time"));
                listData.add(comment);
            }
//                            callback.success(listData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listData;
    }
}
