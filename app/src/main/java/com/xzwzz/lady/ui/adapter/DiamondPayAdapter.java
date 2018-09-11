package com.xzwzz.lady.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xzwzz.lady.R;
import com.xzwzz.lady.bean.PayDialogBean;

import java.util.List;

public class DiamondPayAdapter extends BaseQuickAdapter<PayDialogBean, BaseViewHolder> {

    int position = -1;

    public DiamondPayAdapter(int layoutResId, @Nullable List<PayDialogBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PayDialogBean item) {
        helper.setText(R.id.tv_pay_diamond, item.getName())
                .setText(R.id.tv_pay_money, "￥ " + item.getMoney());

        ImageView imageView = helper.getView(R.id.img_pay);

        if (position==helper.getPosition()){
            imageView.setImageResource(R.mipmap.pay_select_true);
        }else {
            imageView.setImageResource(R.mipmap.pay_select_false);
        }

        if (position==-1&&helper.getPosition()==0){
            imageView.setImageResource(R.mipmap.pay_select_true);
        }
    }

    public void select(int position){
        this.position = position;
        notifyDataSetChanged();
    }
}
