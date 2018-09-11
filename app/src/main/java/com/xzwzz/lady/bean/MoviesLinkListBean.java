package com.xzwzz.lady.bean;

public class MoviesLinkListBean {
    /**
     * id : 12
     * title : 小草资源网
     * img_url : http://ozk3zmyhw.bkt.clouddn.com/20180711_5b45b6df06596.jpg
     * url : https://www.baidu.com/
     * uptime : 2018-07-27 21:38:54
     * listorder : 5
     * coin : 14
     */

    private String id;
    private String title;
    private String img_url;
    private String url;
    private String uptime;
    private String listorder;
    private String coin;
    private String watch_num;
    private int is_buy;

    public int getIs_buy() {
        return is_buy;
    }

    public void setIs_buy(int is_buy) {
        this.is_buy = is_buy;
    }

    public String getWatch_num() {
        return watch_num;
    }

    public void setWatch_num(String watch_num) {
        this.watch_num = watch_num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getListorder() {
        return listorder;
    }

    public void setListorder(String listorder) {
        this.listorder = listorder;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }
}
