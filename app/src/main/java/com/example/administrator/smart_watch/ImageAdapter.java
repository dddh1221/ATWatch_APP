package com.example.administrator.smart_watch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Administrator on 2018-11-19.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    private Integer[] mLedState = {
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
    };

    public ImageAdapter(Context c){
        mContext = c;
    }

    @Override
    public int getCount() {
        return mLedState.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    void onChange(int position){
        mLedState[position] = R.drawable.led_on;
    }

    void offChange(int position){
        mLedState[position] = R.drawable.led_off;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(80, 80));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView)convertView;
        }

        imageView.setImageResource(mLedState[position]);
        return imageView;
    }
}
