package com.xzwzz.lady.bean;

/**
 * Created by gaoyuan on 2018/7/20.
 */

public class PayBean {


    /**
     * ret : 200
     * data : {"code":1003,"msg":"订单信息有误，请重新提交","info":{"pay_url":"https://qr.alipay.com/bax00353cxctqoueuskp200c"}}
     * msg :
     */

    private int ret;
    private DataBean data;
    private String msg;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * code : 1003
         * msg : 订单信息有误，请重新提交
         * info : {"pay_url":"https://qr.alipay.com/bax00353cxctqoueuskp200c"}
         */

        private int code;
        private String msg;
        private InfoBean info;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public static class InfoBean {
            /**
             * pay_url : https://qr.alipay.com/bax00353cxctqoueuskp200c
             */

            private String pay_url;

            public String getPay_url() {
                return pay_url;
            }

            public void setPay_url(String pay_url) {
                this.pay_url = pay_url;
            }
        }
    }
}
