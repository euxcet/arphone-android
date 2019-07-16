package com.example.euxcet.arphone_android.layout;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.euxcet.arphone_android.R;

public class Widget {
    private ImageView v;
    private String name;
    private int px;
    private int py;
    private int width;
    private int height;
    private int windowWidth;
    private int windowHeight;
    private int visibility;

    public String getName() {
        return name;
    }

    public void setVisibility(int s) {
        visibility = s;
        v.setVisibility(visibility);
    }

    public int getVisibility() {
        return visibility;
    }

    public void setPosition(int x, int y) {
        px = Math.min(Math.max(x, 0), windowWidth - width);
        py = Math.min(Math.max(y, 0), windowHeight - height);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.setMargins(px, py,0,0);
        v.setLayoutParams(params);
    }

    public void setPosition(Pair<Integer, Integer> p) {
        setPosition(p.first, p.second);
    }

    public Pair<Integer ,Integer> getPosition() {
        return new Pair<>(px, py);
    }

    public Pair<Integer, Integer> getSize() {
        return new Pair<>(width, height);
    }

    public ImageView getView() {
        return v;
    }

    public Widget(String name, Context context, int width, int height, int windowWidth, int windowHeight) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;

        v = new ImageView(context);
        v.setImageResource(R.drawable.ic_launcher_background);

        setVisibility(View.VISIBLE);
    }
}
