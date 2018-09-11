package com.xzwzz.lady.bean;

/**
 * Created by gaoyuan on 2018/7/20.
 */

public class Pay2Bean {

    /**
     * out_trade_no : 670856_20180720144455924
     * total_fee : 10
     * qrcode : https://qr.alipay.com/bax03207kpbju8xtk4gx80b2
     */

    private String out_trade_no;
    private String total_fee;
    private String qrcode;

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }
}
