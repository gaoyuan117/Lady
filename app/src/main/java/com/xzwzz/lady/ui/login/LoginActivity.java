package com.xzwzz.lady.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xzwzz.lady.AppConfig;
import com.xzwzz.lady.AppContext;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseListObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.base.BaseActivity;
import com.xzwzz.lady.bean.UserBean;
import com.xzwzz.lady.ui.HomeActivity;
import com.xzwzz.lady.ui.MainActivity;
import com.xzwzz.lady.utils.LoginUtils;
import com.xzwzz.lady.utils.StatusBarUtil;
import com.xzwzz.lady.view.RoundImageView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;

public class LoginActivity extends BaseActivity implements View.OnClickListener, PlatformActionListener {

    private RoundImageView ivLogo;
    private android.widget.Button btnDologin;
    private android.widget.TextView tvFindPass;
    private android.widget.Button btnDoReg;
    private android.widget.EditText etUsername;
    private android.widget.EditText etPassword;
    private android.widget.LinearLayout mLlOtherlogin;
    private android.widget.ImageView mIvOtherLoginQq;
    private android.widget.ImageView mIvOtherLoginWechat;
    private String type;
//    private String[] names = {QQ.NAME, Wechat.NAME};
    private PlatformDb mPlatDB;

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        StatusBarUtil.getInstance().darkMode(this);
        ivLogo = findViewById(R.id.iv_logo);
        btnDologin = findViewById(R.id.btn_dologin);
        tvFindPass = findViewById(R.id.tv_findPass);
        btnDoReg = findViewById(R.id.btn_doReg);
        ivLogo.setImageBitmap(ImageUtils.toRoundCorner(BitmapFactory.decodeResource(getResources(), R.mipmap.logo), 5));
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        mLlOtherlogin = findViewById(R.id.ll_otherlogin);
        mIvOtherLoginQq = findViewById(R.id.iv_other_login_qq);
        mIvOtherLoginWechat = findViewById(R.id.iv_other_login_wechat);
        if (!AppConfig.openOtherLogin) {
            mLlOtherlogin.setVisibility(View.GONE);
        }

        findViewById(R.id.img_close).setOnClickListener(this);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void setListener() {
        tvFindPass.setOnClickListener(this);
        btnDologin.setOnClickListener(this);
        btnDoReg.setOnClickListener(this);
        //微信登录
        mIvOtherLoginQq.setOnClickListener(v -> {
            ToastUtils.showShort("正在登录...");
            type = "wx";
//            otherLogin(names[1]);
        });
        //QQ登录
        mIvOtherLoginWechat.setOnClickListener(v -> {
            ToastUtils.showShort("正在登录...");
            type = "QqBean";
//            otherLogin(names[0]);
        });
    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dologin:
                login();
                break;
            case R.id.btn_doReg:
                ActivityUtils.startActivity(RegisterActivity.class);
                break;
            case R.id.tv_findPass:
                ActivityUtils.startActivity(FindPassActivity.class);
                break;
                case R.id.img_close:
               finish();
                break;
            default:
                break;
        }
    }

    private void login() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        try {
            password = URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (username.length() != 11) {
            ToastUtils.showShort("请输入正确的手机号");
            return;
        }
        if (password.length() < 6 || password.length() > 16) {
            ToastUtils.showShort("请输入6-16位密码");
            return;
        }
        initCallBack();
        RetrofitClient.getInstance().createApi().login("Login.userLogin", username, password).compose(RxUtils.io_main())
                .subscribe(callback);
    }

    private void NextActivity() {
        ActivityUtils.startActivity(MainActivity.class);
        finish();
    }

    private void otherLogin(String name) {
        ToastUtils.showShort("正在授权登录...");
        Platform other = ShareSDK.getPlatform(name);
        other.showUser(null);//执行登录，登录后在回调里面获取用户资料
        other.SSOSetting(false);  //设置false表示使用SSO授权方式
        other.setPlatformActionListener(this);
        other.removeAccount(true);
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        runOnUiThread(() -> {



        });
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        throwable.printStackTrace();
        runOnUiThread(() -> {
            if (i == 8) {
                ToastUtils.showShort("未安装微信客户端");
            } else {
                ToastUtils.showShort("授权登录失败");
            }
        });
    }

    @Override
    public void onCancel(Platform platform, int i) {
        ToastUtils.showShort("授权已取消");
    }

    private BaseListObserver<UserBean> callback;

    private void initCallBack() {
        if (callback == null) {
            callback = new BaseListObserver<UserBean>(ProgressDialog.show(this, "", "登录中...")) {
                @Override
                protected void onHandleSuccess(List<UserBean> list) {
                    if (list != null & list.size() > 0){
//                        AppContext.getInstance().saveUserInfo(list.get(0));
//                        getQq();

                        UserBean bean = new UserBean();
                        bean.token = LoginUtils.getDeviceId(LoginActivity.this);
                        bean.id = list.get(0).id;
                        AppContext.getInstance().saveUserInfo(bean);

                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        finish();
                    }
                }
            };
        }
    }

}
