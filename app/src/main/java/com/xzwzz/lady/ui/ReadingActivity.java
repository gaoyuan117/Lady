package com.xzwzz.lady.ui;

import android.webkit.WebView;

import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseObjObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.base.BaseActivity;
import com.xzwzz.lady.bean.NovieceBean;

public class ReadingActivity extends BaseActivity {

    private WebView webView;

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_reading;
    }

    @Override
    protected void initView() {
        webView = findViewById(R.id.webview);
        setToolbar("政策条款",true);
        noviece();
    }

    // 绘制HTML
    private String getHtmlData(String bodyHTML) {
        String head = "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> " +
                "<style>img{max-width: 100%; width:auto; height:auto;}</style>" +
                "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }


    private void noviece(){
        RetrofitClient.getInstance().createApi().noviece("Home.clause")
                .compose(RxUtils.io_main())
                .subscribe(new BaseObjObserver<NovieceBean>() {
                    @Override
                    protected void onHandleSuccess(NovieceBean bean) {
                        webView.loadData(getHtmlData(bean.getPost_content()), "text/html; charset=utf-8", "utf-8");
                    }
                });
    }

}
