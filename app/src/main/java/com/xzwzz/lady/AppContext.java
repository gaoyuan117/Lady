package com.xzwzz.lady;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.blankj.utilcode.util.Utils;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.xzwzz.lady.bean.NovelTermBean;
import com.xzwzz.lady.bean.PayDialogBean;
import com.xzwzz.lady.bean.TextAdBean;
import com.xzwzz.lady.bean.UserBean;
import com.xzwzz.lady.utils.SharePrefUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class AppContext extends Application {
    private static AppContext instance;
    private static Handler mHandler;
    private String loginUid;
    private String Token;
    private String username;
    private boolean login = false;
    public static int channel_type;
    public static String channelUrl;
    public static String channelDataUrl;
    public static String text;
    public static TextAdBean textAdBean;
    public static List<NovelTermBean> novelTermList = new ArrayList<>();
    public static List<PayDialogBean> zbChargeList = new ArrayList<>();//直播区充值规则
    public static List<PayDialogBean> zsChargeList = new ArrayList<>();//钻石区充值规则
    public static List<PayDialogBean> avChargeList = new ArrayList<>();//AV区充值规则


    public static Handler getHandler() {
        return mHandler;
    }

    public static AppContext getInstance() {
        if (instance == null) {
            throw new NullPointerException("the application is not defind,please check out ");
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mHandler = new Handler();

        SharePrefUtil.initSharedPref();
        Utils.init(this);
        initLogin();
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//        builder.detectFileUriExposure();
//        initTbs();

//        UMConfigure.init(this, "", null, UMConfigure.DEVICE_TYPE_PHONE, null);
    }

    public boolean isMainProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }

    private void initLogin() {
        UserBean user = getLoginUser();
        if (null != user && user.id != null && Integer.parseInt((user.id)) > 0) {
            login = true;
            loginUid = user.id;
            Token = user.token;
            username = user.user_nicename;
        } else {
            this.cleanLoginInfo();
        }
    }

    public UserBean getLoginUser() {
        UserBean user = new UserBean();
        user.id = getProperty("user.uid");
        user.avatar = getProperty("user.avatar");
        user.user_nicename = getProperty("user.name");
        user.signature = getProperty("user.sign");
        user.token = getProperty("user.token");
        user.votes = getProperty("user.votes");
        user.city = getProperty("user.city");
        user.coin = getProperty("user.coin");
        user.sex = getProperty("user.sex");
        user.signature = getProperty("user.signature");
        user.avatar = getProperty("user.avatar");
        user.level = getProperty("user.level");
        user.avatar_thumb = getProperty("user.avatar_thumb");
        user.birthday = getProperty("user.birthday");
        return user;
    }

    public void cleanLoginInfo() {
        this.loginUid = "0";
        this.login = false;
        removeProperty("user.birthday", "user.avatar_thumb", "user.uid", "user.token", "user.name", "user.pwd", "user.avatar", "user.sign", "user.city", "user.coin", "user.sex", "user.signature", "user.signature", "user.avatar", "user.level");
    }

    public String getProperty(String key) {
        String res = AppConfig.getAppConfig(this).get(key);
        return res;
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    public String getLoginUid() {
        return loginUid;
    }

    public String getToken() {
        return Token;
    }

    public boolean isLogin() {
        return login;
    }

    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    public void saveUserInfo(final UserBean user) {
        this.loginUid = user.id;
        this.Token = user.token;
        this.username = user.user_nicename;
        this.login = true;
        try {
            setProperties(new Properties() {
                {
                    setProperty("user.uid", user.id);
                    setProperty("user.name", user.user_nicename);
                    setProperty("user.token", user.token);
                    setProperty("user.sign", user.signature);
                    setProperty("user.avatar", user.avatar);

                    setProperty("user.coin", user.coin);
                    setProperty("user.sex", user.sex);
                    setProperty("user.signature", user.signature);
                    setProperty("user.avatar_thumb", user.avatar_thumb);
                    setProperty("user.level", user.level);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUserInfo(final UserBean user) {
        setProperties(new Properties() {
            {
                setProperty("user.uid", user.id);
                setProperty("user.name", user.user_nicename);
                setProperty("user.sign", user.signature);
                setProperty("user.avatar", user.avatar);
                setProperty("user.city", user.city == null ? "" : user.city);
                setProperty("user.coin", user.coin);
                setProperty("user.sex", user.sex);
                setProperty("user.signature", user.signature);
                setProperty("user.avatar_thumb", user.avatar_thumb);
                setProperty("user.level", user.level);
            }
        });
    }

    private void initTbs() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
            }

            @Override
            public void onCoreInitFinished() {
            }
        };

        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
            }

            @Override
            public void onInstallFinish(int i) {
            }

            @Override
            public void onDownloadProgress(int i) {
            }
        });

        QbSdk.initX5Environment(getApplicationContext(), cb);
        QbSdk.canLoadX5FirstTimeThirdApp(this);
    }


}
