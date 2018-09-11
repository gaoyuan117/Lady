package com.xzwzz.lady.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.xzwzz.lady.AppContext;
import com.xzwzz.lady.R;
import com.xzwzz.lady.api.http.RetrofitClient;
import com.xzwzz.lady.api.http.RxUtils;
import com.xzwzz.lady.bean.PayDialogBean;
import com.xzwzz.lady.ui.adapter.DiamondPayAdapter;

import java.util.List;

public class PayUtils {

    static int payType = 2;
    static String id = "";

    public static void payDialog(Activity activity, int imgResouse, String title, String msg, int channl_type, List<PayDialogBean> list) {
        id = list.get(0).getId();
        View view = View.inflate(activity, R.layout.dialog_diamond, null);
        RecyclerView recycler = view.findViewById(R.id.recycler);

        ImageView image = view.findViewById(R.id.img_bg);
        image.setImageResource(imgResouse);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(title);

        TextView des = view.findViewById(R.id.des);
        if (TextUtils.isEmpty(msg)) {
            des.setVisibility(View.GONE);
        } else {
            des.setText(msg);
        }
        ImageView img_weixin = view.findViewById(R.id.img_weixin);
        ImageView img_zfb = view.findViewById(R.id.img_zfb);

        DiamondPayAdapter adapter = new DiamondPayAdapter(R.layout.item_pay_dialog, list);
        recycler.setLayoutManager(new LinearLayoutManager(activity));
        recycler.setAdapter(adapter);
        Dialog dialog = new Dialog(activity, R.style.wx_dialog);
        dialog.setContentView(view);
        dialog.show();

        adapter.setOnItemClickListener((a, view1, position) -> {
            adapter.select(position);
            id = list.get(position).getId();
        });

        view.findViewById(R.id.layout_wx).setOnClickListener(v -> {
            payType = 2;
            img_zfb.setImageResource(R.mipmap.select_false);
            img_weixin.setImageResource(R.mipmap.select_true);
        });

        view.findViewById(R.id.layout_zfb).setOnClickListener(v -> {
            payType = 1;
            img_zfb.setImageResource(R.mipmap.select_true);
            img_weixin.setImageResource(R.mipmap.select_false);
        });

        view.findViewById(R.id.tv_buy).setOnClickListener(v -> buy(dialog, activity, channl_type + "", payType, id));
    }

    @SuppressLint("CheckResult")
    private static void buy(Dialog dialog, Activity activity, String channl_type, int pay_type, String change_id) {
        if (TextUtils.isEmpty(id)) {
            ToastUtils.showShort("请先选择");
            return;
        }

        RetrofitClient.getInstance().createApi().getOrder("Charge.getAliOrder",
                AppContext.getInstance().getLoginUid(), channl_type + "", change_id, pay_type + "", "111")
                .compose(RxUtils.io_main())
                .subscribe(payBean -> {
                    dialog.dismiss();
                    if (payBean.getData().getCode() != 0) {
                        ToastUtils.showShort(payBean.getData().getMsg());
                        return;
                    }
                    try {
                        String pay_url = payBean.getData().getInfo().getPay_url();
                        if (!TextUtils.isEmpty(pay_url)) {
                            Uri uri = Uri.parse(pay_url);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            activity.startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

}
