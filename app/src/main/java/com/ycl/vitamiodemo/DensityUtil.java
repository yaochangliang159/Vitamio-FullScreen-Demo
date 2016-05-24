package com.ycl.vitamiodemo;


import android.content.Context;



public class DensityUtil {
	  

    public static int dip2px(float dpValue,Context context)
    {

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue,Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    
}  