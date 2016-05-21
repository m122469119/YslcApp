package com.yslc.util;

import com.yslc.bean.AdBean;
import com.yslc.bean.CelebrityComment;
import com.yslc.bean.ColumnBean;
import com.yslc.bean.CommentBean;
import com.yslc.bean.FastInfoBean;
import com.yslc.bean.GoodBean;
import com.yslc.bean.NewBean;
import com.yslc.bean.RadioBean;
import com.yslc.bean.StarBean;
import com.yslc.bean.StockCodeBean;
import com.yslc.bean.StockInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Administrator on 2016/5/3.
 */
public class ParseUtil {

    public static ArrayList<ColumnBean> parseColumnBean(String arg0){
        return parseColumnBean(arg0,"StID", "StName");
    }

    public static ArrayList<ColumnBean> parseColumnBean(String arg0,String id_key,String name_key){
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
                cb.setId(jo.optString(id_key));
                cb.setName(jo.optString(name_key));
                cb.setStOrder(jo.optString("StOrder"));
                listTitle.add(cb);
            }

        } catch (JSONException e) {
            // 暂无数据
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

    /**
     * StarContentActivity评论
     * @param json
     * @return
     */
    public static ArrayList<CommentBean> parseCommentBean(JSONObject json) {
        // 解析评论列表
        ArrayList<CommentBean> list = new ArrayList<>();
        try{
            JSONArray ja = json.getJSONArray("CommentList");
            JSONObject tempJo;
            CommentBean mode;
            for (int i = 0, len = ja.length(); i < len; i++) {
                tempJo = ja.getJSONObject(i);
                mode = new CommentBean();
                mode.setNcikName(tempJo.optString("Ui_Nickname"));
                mode.setUiImg(tempJo.optString("Ui_Img"));
                mode.setTime(tempJo.optString("Snc_Time"));
                mode.setContent(tempJo.optString("Snc_Content"));
                list.add(mode);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        return list;
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

    public static ArrayList<StarBean> parseStarBean(JSONObject jo) {
        ArrayList<StarBean> list = new ArrayList<>();
        try {
            JSONArray infoJa = jo.getJSONArray("StarList");
            JSONObject tempJo;
            StarBean infoItem;
            for (int i = 0, len = infoJa.length(); i < len; i++) {
                tempJo = infoJa.getJSONObject(i);
                infoItem = new StarBean();
                infoItem.setSif_Id(tempJo.optString("Sif_Id"));
                infoItem.setSif_Name(tempJo.optString("Sif_Name"));
                infoItem.setSif_Title(tempJo.optString("Sif_Title"));
                infoItem.setSif_Img(tempJo.optString("Sif_Img"));
                infoItem.setContent(tempJo.optString("content"));
                infoItem.setSn_Time(tempJo.optString("Sn_Time"));
                list.add(infoItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return list;
        }
    }

    /**
     * 解析StarMainActivity数据
     * @param jo
     * @return
     */
    public static ArrayList<StarBean> parseStarBean2(JSONObject jo) {
        ArrayList<StarBean> list = new ArrayList<StarBean>();
        try{
            JSONArray ja = jo.getJSONArray("StarNewsList");
            for (int i = 0, len = ja.length(); i < len; i++) {
                JSONObject tempJo = ja.getJSONObject(i);
                StarBean modes = new StarBean();
                modes.setSif_Id(tempJo.optString("Sn_Id"));
                modes.setSif_Title(tempJo
                        .optString("Sn_Title"));
                modes.setContent(tempJo
                        .optString("Sn_Content"));
                modes.setSn_Time(tempJo.optString("Sn_Time"));
                modes.setSif_ComNumber(tempJo
                        .optString("ComNumber"));
                modes.setSif_Praise(tempJo
                        .optString("Sn_Praise"));

                list.add(modes);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }finally{
            return list;
        }

    }

    /**
     * 明星头部信息
     * @param jo
     * @return
     */
    public static StarBean parseSingleStarBean(JSONObject jo) {
        StarBean mode = new StarBean();
        try {
            // 解析明星个人资料
            JSONObject starJo = jo.getJSONObject("StarInfo");
            if (null != starJo) {
                mode.setSif_Id(starJo.optString("Sif_Id"));
                mode.setSif_Img(starJo.optString("Sif_Img"));
                mode.setSif_Name(starJo
                        .optString("Sif_Name"));
                mode.setSif_Relation(starJo
                        .optString("Sif_Relation"));
                mode.setSif_Degree(starJo
                        .optString("Sif_Degree"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            return mode;
        }
    }

    /**
     * StarContentActivity
     * @param jo
     * @return
     */
    public static StarBean parseSingleStarBean2(JSONObject jo) {
        StarBean modes = new StarBean();
        try{
            JSONObject articalJo = jo.getJSONObject("StarNews");
            if (null != articalJo) {
                // 解析文章详情
                modes.setSif_Title(articalJo
                        .optString("Sn_Title"));
                modes.setSn_Time(articalJo.optString("Sn_Time"));
                modes.setContent(articalJo
                        .optString("Sn_Content"));
                modes.setSif_Img(articalJo.optString("Sn_Img"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return modes;
    }

    public static ArrayList<StockCodeBean> parseStockCodeBean(JSONObject arg0) {

        ArrayList<StockCodeBean> list = new ArrayList<StockCodeBean>();
        try{
            JSONArray ja = arg0.getJSONArray("stock");
            StockCodeBean bean;
            JSONObject jo;
            for (int i = 0, len = ja.length(); i < len; i++) {
                bean = new StockCodeBean();
                jo = ja.getJSONObject(i);
                bean.setStock_Code(jo.optString("Stock_Code"));
                bean.setStock_Name(jo.optString("Stock_Name"));
                bean.setStock_Abbreviation(jo.optString("Stock_Abbreviation"));
                list.add(bean);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return list;
    }

    public static RadioBean parseSingleRadioBean(JSONObject json) {
        RadioBean mode = new RadioBean();
        mode.setRadioUrl(json.optString("Url"));
        mode.setRadioName(json.optString("RadP_Name"));
        mode.setRadioTime(json.optString("TimeSpan"));
        mode.setRadioHostUrl(json.optString("RadP_Img"));
        mode.setRadioHost(json.optString("RadP_Compere"));

        return mode;
    }

    public static RadioBean parseHeadRadioBean(JSONObject json) {
        RadioBean detailBean = new RadioBean();
        detailBean.setRadioName(json.optString("RadP_Name"));
        detailBean.setRadioHost(json.optString("RadP_Compere"));
        detailBean.setRadioHostUrl(json.optString("RadP_Img"));
        detailBean.setRadioTime(json.optString("RadP_Time"));

        return detailBean;
    }

    public static ArrayList<RadioBean> parseRadioBean(JSONObject arg0){
        ArrayList<RadioBean> list = new ArrayList<>();
        try {
            JSONArray ja = arg0.getJSONArray("list");
            RadioBean bean;
            JSONObject jo;
            for (int i = 0, len = ja.length(); i < len; i++) {
                jo = ja.getJSONObject(i);
                bean = new RadioBean();
                bean.setRadioId(jo.optString("DbId"));
                bean.setRadioName(jo.getString("title"));
                bean.setRadioUrl(jo.optString("song"));
                bean.setRadioTime(jo.getString("time"));
                list.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<RadioBean> parseRadioBean(String arg0) {
        ArrayList<RadioBean> list = new ArrayList<>();
        try {
            // 解析广播重温界面列表
            JSONArray infoJa = new JSONArray(arg0);
            RadioBean infoItem;
            JSONObject tempJo;
            for (int i = 0, len = infoJa.length(); i < len; i++) {
                tempJo = infoJa.getJSONObject(i);
                infoItem = new RadioBean();
                infoItem.setRadioId(tempJo.optString("RadP_Id"));
                infoItem.setRadioName(tempJo.optString("RadP_Name"));
                infoItem.setRadioDate(tempJo.optString("RadP_Time"));
                list.add(infoItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static GoodBean parseGoodBean(JSONObject jsonObject) {
        GoodBean bean = new GoodBean();
        try{
            JSONArray good = jsonObject.getJSONArray("msg");
            JSONObject o = good.getJSONObject(0);
            //[{"ProductId": "YSLC0002","ProductName": "投资快报","Price": "360.0000"}]
            bean.setPrice(o.getString("Price"));
//            good.setPrice("360.0000");
            bean.setProductId(o.getString("ProductId"));
            bean.setProductName(o.getString("ProductName"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return bean;
    }

    public static ArrayList<FastInfoBean> parseFastInfoBean(JSONObject jsonObject) {
        JSONArray array = (JSONArray) jsonObject.opt("news");
        ArrayList<FastInfoBean> list = new ArrayList<FastInfoBean>();
        try{
            for(int i=0; i<array.length(); i++){
                FastInfoBean data = new FastInfoBean();
                data.setTitle(array.getJSONObject(i).optString("title"));
                data.setContent(array.getJSONObject(i).optString("content"));
                data.setDate(array.getJSONObject(i).optString("date"));
                list.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<CelebrityComment> parseCelebrityComment(JSONObject jsonObject) {
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
        return data;
    }
}
