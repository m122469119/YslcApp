package com.yslc.ui.dialog;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.yslc.R;
import com.yslc.app.Constant;
import com.yslc.util.CommonUtil;
import com.yslc.util.FileUtil;
import com.yslc.util.ToastUtil;

/**
 * 分享Dialog ---> 指定分享到微信，微博，QQ，微信好友，QQ好友，腾讯微博
 *
 * @author HH
 */
public class ShareDialog extends BaseDialog implements OnClickListener {
    public static final int SHARE_APP = 0x01;
    private String logoPath;
    private Context context;
    private ShareParams sp;
    private Share share;

    public ShareDialog(Context context, int type) {
        super(context);
        this.context = context;

        // 保存LOGO到文件夹
        saveLogoToSD();
        share = new Share(type);
        setParams();

        // 初始化界面
        findView();

        // 显示
        show();

        // 全屏
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = CommonUtil.getScreenWidth(context);
        getWindow().setAttributes(lp);
    }

    /**
     * Save logo to sdcard
     */
    private void saveLogoToSD() {
        logoPath = FileUtil.getSdCardPath() + Constant.FILES_LOGO;
        //创建文件夹
        FileUtil.creatMoreFiles(Constant.FILES_LOGO);

        if (!(new File(logoPath).exists())) {
            //保存
            FileUtil.saveBitmapToCard(logoPath,
                    ((BitmapDrawable) ContextCompat.getDrawable(context,
                            R.drawable.ic_launcher)).getBitmap());
        }
    }

    /**
     * 初始化界面
     */
    private void findView() {
        // 设置底部拉出动画
        getWindow().setWindowAnimations(R.style.AnimBottom);

        View view = View.inflate(context, R.layout.dialog_share, null);
        int id = R.id.shareWb;
        for (int i = id; i < id + 6; i++) {
            view.findViewById(i).setOnClickListener(this);
        }
        view.findViewById(R.id.cancel).setOnClickListener(this);

        setContentView(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shareWb:
                share(R.id.shareWb);
                break;

            case R.id.shareQq:
                share(R.id.shareQq);
                break;

            case R.id.shareWx:
                share(R.id.shareWx);
                break;

            case R.id.shareZone:
                share(R.id.shareZone);
                break;

            case R.id.shareFriend:
                share(R.id.shareFriend);
                break;

            case R.id.cancel:
                this.dismiss();
                break;
        }

    }

    /**
     * 开始分享
     */
    private void share(int id) {
        ToastUtil.showMessage(context, "正在准备分享");
        switch (id) {
            case R.id.shareWb:
                Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
                // 设置分享事件回调
                weibo.setPlatformActionListener(new MyPlatformActionListener());
                // 执行图文分享
                weibo.share(sp);
                break;

            case R.id.shareQq:
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                // 设置分享事件回调
                qq.setPlatformActionListener(new MyPlatformActionListener());
                // 执行图文分享
                sp.setTitleUrl(share.titleUrl);
                qq.share(sp);
                break;

            case R.id.shareZone:
                Platform qzone = ShareSDK.getPlatform(QZone.NAME);

                // 设置分享事件回调
                qzone.setPlatformActionListener(new MyPlatformActionListener());
                // 执行图文分享
                sp.setTitle(share.title);
                sp.setTitleUrl(share.titleUrl);
                sp.setSite(share.site);
                sp.setSiteUrl(share.siteUrl);
                qzone.authorize();
                qzone.share(sp);
                break;

            case R.id.shareWx:
                Platform wx = ShareSDK.getPlatform(Wechat.NAME);
                sp.setShareType(Platform.SHARE_WEBPAGE);
                // 设置分享事件回调
                wx.setPlatformActionListener(new MyPlatformActionListener());
                // 执行图文分享
                sp.setTitle(share.title);
                sp.setUrl(share.shareUrl);
                wx.share(sp);
                break;

            case R.id.shareFriend:
                Platform wxfriend = ShareSDK.getPlatform(WechatMoments.NAME);
                sp.setShareType(Platform.SHARE_WEBPAGE);
                // 设置分享事件回调
                wxfriend.setPlatformActionListener(new MyPlatformActionListener());
                // 执行图文分享
                sp.setTitle(share.title);
                sp.setUrl(share.shareUrl);
                wxfriend.share(sp);
                break;

        }

        // 关闭弹出框
        cancel();
    }

    /**
     * 设置分享参数
     */
    private void setParams() {
        sp = new ShareParams();
        sp.setText(share.shareText);
        sp.setImagePath(share.shareImgPath);
    }

    /**
     * 分享回调
     */
    private class MyPlatformActionListener implements PlatformActionListener {

        public void onError(Platform platform, int action, Throwable t) {
            // 操作失败的处理代码
            ToastUtil.showMessage(context, "分享失败,请稍候分享");
        }

        public void onCancel(Platform platform, int action) {
            // 操作取消的处理代码
        }

        @Override
        public void onComplete(Platform arg0, int arg1,
                               HashMap<String, Object> arg2) {
            // 操作成功的处理代码
            ToastUtil.showMessage(context, "分享成功");
        }
    }

    public class Share {
        private String shareUrl;
        private String shareText;
        private String shareImgPath;
        private String title;
        private String titleUrl;
        private String site;
        private String siteUrl;

        public Share() {
        }

        public Share(int type) {
            setType(type);
        }

        /**
         * 设置分享类型
         *
         * @param type
         */
        public void setType(int type) {
            if (type == SHARE_APP) {
                // 分享app
                this.shareUrl = "http://www.yslcw.com";
                this.shareText = "最专业的理财咨询APP";
                this.shareImgPath = logoPath;
                this.title = "越声理财（title）";
                this.titleUrl = "http://www.yslcw.com";
                this.site = "越声理财（site）";
                this.siteUrl = "http://www.yslcw.com";
            }
        }
    }
}
