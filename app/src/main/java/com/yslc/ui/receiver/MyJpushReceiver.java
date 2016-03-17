package com.yslc.ui.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import com.yslc.ui.activity.ShowNoticationActivity;
import com.yslc.ui.activity.StarContentActivity;
import com.yslc.ui.activity.WebActivity;

import cn.jpush.android.api.JPushInterface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * 自定义极光推送广播接收器
 *
 * @author HH
 */
public class MyJpushReceiver extends BroadcastReceiver {
    private String Pi_Type = null;
    private String nid = null;
    private String snId = null;
    private String Pi_Value = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        // 消息
        try {
            if (null == bundle.getString(JPushInterface.EXTRA_EXTRA)) {
                return;
            }

            JSONObject jo = new JSONObject(
                    bundle.getString(JPushInterface.EXTRA_EXTRA));

            Pi_Type = jo.optString("Pi_Type");

            if (Pi_Type.equals("1")) {
                // 资讯
                nid = jo.optString("Pi_Value");
            } else if (Pi_Type.equals("2")) {
                // 明星博文
                snId = jo.optString("Pi_Value");
            } else if (Pi_Type.equals("3")) {
                // 单条消息
                Pi_Value = jo.optString("Pi_Value");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            // 接收用户注册ID

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
                .getAction())) {
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
                .getAction())) {
            // 在这里可以做些统计，或者做些其他工作

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
                .getAction())) {
            // 在这里可以自己写代码去定义用户点击后的行为
            if (Pi_Type.equals("1")) {
                // 进入资讯
                Intent i = new Intent(context, WebActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("nid", nid);
                context.startActivity(i);
            } else if (Pi_Type.equals("2")) {
                // 进入明星博文
                Intent i = new Intent(context, StarContentActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("snId", snId);
                context.startActivity(i);
            } else if (Pi_Type.equals("3")) {
                // 单条消息（通知）
                Intent i = new Intent(context, ShowNoticationActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("info", Pi_Value);
                context.startActivity(i);
            }

            // 清除通知栏
            JPushInterface.clearNotificationById(context, intent.getExtras()
                    .getInt(JPushInterface.EXTRA_NOTIFICATION_ID));
        } else {
            Log.d("", "Unhandled intent - " + intent.getAction());
        }
    }

}
