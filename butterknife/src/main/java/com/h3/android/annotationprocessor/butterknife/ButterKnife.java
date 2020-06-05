package com.h3.android.annotationprocessor.butterknife;

import android.app.Activity;

public class ButterKnife {

    public static void bind(Activity activity) {
        String className = activity.getClass().getName();
        try {
            //利用反射创建一个实例对象
            Class<?> newClass = Class.forName(className + "_ViewBinding");
            newClass.getConstructor(activity.getClass()).newInstance(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
