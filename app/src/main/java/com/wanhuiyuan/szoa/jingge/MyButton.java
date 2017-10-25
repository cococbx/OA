/*
 * MyButton.java
 * classes : com.kinggrid.iapppdf.demo.MyButton
 * @author Ϳ��֮
 * V 1.0.0
 * Create at 2014��5��23�� ����2:51:23
 */
package com.wanhuiyuan.szoa.jingge;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;

/**
 * com.kinggrid.iapppdf.demo.MyButton
 * @author 涂博之<br/>
 * create at 2014年5月23日 下午2:51:23
 */
public class MyButton extends Button {
  private static final String TAG = "MyButton";
  
  public MyButton(final Context context, final int imageResId, final int textResId) {
    super(context);

    this.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(imageResId), null, null);
    this.setBackgroundResource(android.R.color.transparent);
    this.setTextColor(Color.BLACK);
    this.setText(context.getString(textResId));
    this.setTag(textResId);
    this.setPadding(8, 8, 8, 8);
}
}
