package com.xzwzz.lady.module.book;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.BaseObjObserver;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.base.BaseActivity;
import com.xzwzz.lady.bean.BookBean;
import com.xzwzz.lady.utils.StatusBarUtil;

public class BookDetailActivity extends BaseActivity {

    private android.webkit.WebView mWebview;
    private String id;

    @Override
    protected boolean hasActionBar() {
        return true;
    }

    @Override
    protected Object getIdOrView() {
        return R.layout.activity_book_detail;
    }

    @Override
    protected void initView() {
        Bundle extras = getIntent().getExtras();
        String name = extras.getString("name");
        id = extras.getString("id");

        setToolbar(name, true);
        mWebview = findViewById(R.id.webview);
        StatusBarUtil.getInstance().setPaddingSmart(this, mWebview);
        initData();

    }

    protected void initData() {
        RetrofitClient.getInstance().createApi().getNoveldetails("Home.Noveldetail", id)
                .compose(RxUtils.io_main())
                .subscribe(new BaseObjObserver<BookBean>(ProgressDialog.show(this, "", "加载中")) {
                    @Override
                    protected void onHandleSuccess(BookBean obj) {
                        String linkCss = "<style type=\"text/css\"> " +
                                "img {" +
                                "width:100%;" +
                                "height:auto;" +
                                "}" +
                                "</style>";
                        String html = "\n<html><header>" + linkCss + "</header>" + obj.post_content + "</body></html>";
                        mWebview.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
                    }
                });
    }

}
