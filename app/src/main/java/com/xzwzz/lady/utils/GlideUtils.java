package com.xzwzz.lady.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xzwzz.lady.AppConfig;

public class GlideUtils {

    public static void glide(Context context, String url, ImageView imageView) {

        String img = "";

        if (url.startsWith("http")) {
            img = url;
        } else {
            img = AppConfig.MAIN_URL + url;
        }
        Glide.with(context).load(img).into(imageView);
    }
}
