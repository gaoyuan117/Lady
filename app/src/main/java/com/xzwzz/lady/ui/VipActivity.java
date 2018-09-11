


package com.xzwzz.lady.ui;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.xzwzz.lady.AppConfig;
import com.xzwzz.lady.AppContext;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseListObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.bean.PayBean;
import com.xzwzz.lady.bean.UserInfoBean;
import com.xzwzz.lady.bean.VipBean;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class VipActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;
    private TextView textView, tvWx;

    private String s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        initView();
        findViewById(R.id.img_back).setOnClickListener(view -> finish());
        TextView tvTitle = findViewById(R.id.tv_title);

        tvTitle.setText("会员续费");
    }


    protected void initView() {

        editText = findViewById(R.id.et_kami);
        textView = findViewById(R.id.text);
        textView.setText(AppContext.text + "");
        findViewById(R.id.img_nianka).setOnClickListener(this);
        findViewById(R.id.img_yueka).setOnClickListener(this);
        findViewById(R.id.img_jika).setOnClickListener(this);
        findViewById(R.id.img_zhongshen).setOnClickListener(this);

        findViewById(R.id.btn_jihuo).setOnClickListener(this);

        findViewById(R.id.tv_kefu).setOnClickListener(this);

        tvWx = findViewById(R.id.tv_wx);
        tvWx.setOnClickListener(this);
        if (TextUtils.isEmpty(AppConfig.QQ)){
            tvWx.setText("暂未设置联系方式");
        }else {
            tvWx.setText(AppConfig.QQ);

        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_yueka:
                if (AppConfig.pay_type.equals("1")) {//发卡
                    ka(AppConfig.YUE);
                } else {
                    selectPay("1", AppConfig.yueka);
                }
                break;
            case R.id.img_jika:
                if (AppConfig.pay_type.equals("1")) {//发卡
                    ka(AppConfig.JI);
                } else {
                    selectPay("2", AppConfig.jika);
                }

                break;
            case R.id.img_nianka:
                if (AppConfig.pay_type.equals("1")) {//发卡
                    ka(AppConfig.YEAR);
                } else {
                    selectPay("3", AppConfig.nianka);
                }
                break;

            case R.id.img_zhongshen:
                if (AppConfig.pay_type.equals("1")) {//发卡
                    ka(AppConfig.FOREVER);
                } else {
                    selectPay("4", AppConfig.zhongshenka);
                }
                break;

            case R.id.tv_kefu:

                break;

            case R.id.btn_jihuo:
                kami();
                break;
            case R.id.tv_wx:
                copy();
                break;
        }
    }

    private void copy() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(AppConfig.QQ);
        ToastUtils.showShort("复制成功");
    }


    private void ka(String url) {
        if (TextUtils.isEmpty(url)) {
            wxDialog();

        } else {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private void wxDialog() {
        Dialog dialog = new Dialog(this, R.style.wx_dialog);
        View view = View.inflate(this, R.layout.dialog_commom, null);
        TextView tvMessage = view.findViewById(R.id.tv_message);
        tvMessage.setText("请联系客服购买卡密\n" + AppConfig.QQ);
        view.findViewById(R.id.tv_close).setOnClickListener(v -> dialog.dismiss());
        dialog.setContentView(view);
        dialog.show();
    }


    private void kami() {
        s = editText.getText().toString();
        if (TextUtils.isEmpty(s)) {
            ToastUtils.showShort("请输入卡密");
            return;
        }
        RetrofitClient.getInstance().createApi().kami("Charge.exchange", AppContext.getInstance().getLoginUid(), s)
                .compose(RxUtils.io_main())
                .subscribe(new Observer<VipBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(VipBean vipBean) {
                        if (vipBean.getData().getCode() == 0) {
                            ToastUtils.showShort("续费成功");
                            isMember();
                            finish();
                        } else {
                            ToastUtils.showShort(vipBean.getData().getMsg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void isMember() {
        RetrofitClient.getInstance().createApi().getBaseUserInfo("User.getBaseInfo", AppContext.getInstance().getToken(), AppContext.getInstance().getLoginUid())
                .compose(RxUtils.io_main())
                .subscribe(new BaseListObserver<UserInfoBean>() {
                    @Override
                    protected void onHandleSuccess(List<UserInfoBean> list) {
                        if (list.size() > 0) {
                            UserInfoBean bean = list.get(0);
                            AppConfig.IS_MEMBER = (bean.is_member == 1);
                        }
                    }
                });
    }


    private void getOrder(String change_type, String money, String type) {
        RetrofitClient.getInstance().createApi().getOrder("Charge.getAliOrder",
                AppContext.getInstance().getLoginUid(), change_type, money, type, "111")
                .compose(RxUtils.io_main())
                .subscribe(new Consumer<PayBean>() {
                    @Override
                    public void accept(PayBean payBean) throws Exception {
                        try {
                            String pay_url = payBean.getData().getInfo().getPay_url();
                            if (!TextUtils.isEmpty(pay_url)) {
                                Uri uri = Uri.parse(pay_url);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void selectPay(String type, String money) {

        final CharSequence[] charSequences = {"支付宝", "微信"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("选择支付方式")
                .setItems(charSequences, (dialog, which) -> {
                    Log.i("abc", "" + which);
                    getOrder(type, money, (which + 1) + "");

                }).show();
    }

}
