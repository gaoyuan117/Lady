package com.xzwzz.lady.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.base.BaseActivity;
import com.xzwzz.lady.bean.BindBean;
import com.xzwzz.lady.utils.LoginUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BindPhoneActivity extends BaseActivity {

    private EditText etPhone;
    private EditText etPwd;


    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_bind_phone;
    }

    @Override
    protected void initView() {
        super.initView();
        setToolbar("绑定账号",true);
        etPhone = findViewById(R.id.et_username);
        etPwd = findViewById(R.id.et_password);

        findViewById(R.id.btn_dologin).setOnClickListener(v -> bind());

    }

    private void bind() {
        String phone = etPhone.getText().toString();
        String pwd = etPwd.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShort("请输入手机号");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showShort("请输入密码");
            return;
        }

        RetrofitClient.getInstance().createApi().bind("User.setMobile", phone, LoginUtils.getDeviceId(this), pwd)
                .compose(RxUtils.io_main())
                .subscribe(new Observer<BindBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BindBean bindBean) {
                        if (bindBean.getRet() == 200) {
                            if (bindBean.getData().getCode() == 0) {
                                ToastUtils.showShort("绑定成功");
                                finish();
                            }
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
}
