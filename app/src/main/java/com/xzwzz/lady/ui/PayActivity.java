package com.xzwzz.lady.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xzwzz.lady.AppContext;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;

public class PayActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout layoutYue;
    ImageView imgYue;
    LinearLayout layoutJi;
    ImageView imgJi;
    LinearLayout layoutNian;
    LinearLayout layoutWx;
    LinearLayout layoutZfb;
    ImageView imgNian;
    ImageView imgWx;
    ImageView imgZfb;


    int type = 1;
    int pay_type = 2;
    String money = "29";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);


        layoutYue = findViewById(R.id.layout_yue);
        layoutJi = findViewById(R.id.layout_ji);
        layoutNian = findViewById(R.id.layout_nian);
        layoutWx = findViewById(R.id.layout_wx);
        layoutZfb = findViewById(R.id.layout_zfb);

        imgYue = findViewById(R.id.img_yue);
        imgJi = findViewById(R.id.img_ji);
        imgNian = findViewById(R.id.img_nian);

        imgWx = findViewById(R.id.img_weixin);
        imgZfb = findViewById(R.id.img_zfb);

        layoutYue.setOnClickListener(this);
        layoutJi.setOnClickListener(this);
        layoutNian.setOnClickListener(this);
        layoutWx.setOnClickListener(this);
        layoutZfb.setOnClickListener(this);
        findViewById(R.id.tv_buy).setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("会员续费");


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_yue:
                type = 1;
                money = "29";
                imgYue.setImageResource(R.mipmap.select_true);
                imgJi.setImageResource(R.mipmap.select_false);
                imgNian.setImageResource(R.mipmap.select_false);

                break;
            case R.id.layout_ji:
                type = 2;
                money = "59";
                imgYue.setImageResource(R.mipmap.select_false);
                imgJi.setImageResource(R.mipmap.select_true);
                imgNian.setImageResource(R.mipmap.select_false);

                break;
            case R.id.layout_nian:
                type = 3;
                money = "139";
                imgYue.setImageResource(R.mipmap.select_false);
                imgJi.setImageResource(R.mipmap.select_false);
                imgNian.setImageResource(R.mipmap.select_true);
                break;
            case R.id.layout_wx:
                pay_type = 2;
                imgWx.setImageResource(R.mipmap.select_true);
                imgZfb.setImageResource(R.mipmap.select_false);

                break;
            case R.id.layout_zfb:
                pay_type = 1;
                imgWx.setImageResource(R.mipmap.select_false);
                imgZfb.setImageResource(R.mipmap.select_true);

                break;
            case R.id.tv_buy:
                getOrder();

                break;
            case R.id.img_back:
                finish();

                break;
        }
    }

    private void getOrder() {
        RetrofitClient.getInstance().createApi().getOrder("Charge.getAliOrder",
                AppContext.getInstance().getLoginUid(), type + "", money, pay_type + "", "111")
                .compose(RxUtils.io_main())
                .subscribe(payBean -> {
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
                });
    }


}
