package net.sourceforge.simcpux.wxapi;

import android.app.Activity;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.yslc.util.ToastUtil;

/**
 * Created by Administrator on 2016/5/18.
 * TODO 微信支付回调
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if(baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX){
            switch (baseResp.errCode){
                case 0:
                    ToastUtil.showMessage(this,"成功");
                    break;
                case -1:
                    ToastUtil.showMessage(this,"错误");
                    break;
                case -2:
                    ToastUtil.showMessage(this,"取消");
                    break;
            }
        }
//        if(baseResp.errCode == 0){
//            ToastUtil.showMessage(this,"成功");
//        }
    }
}
