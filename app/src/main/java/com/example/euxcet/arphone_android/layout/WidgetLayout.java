package com.example.euxcet.arphone_android.layout;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class WidgetLayout {
    RelativeLayout layout;
    ArrayList<Widget> widgets;
    public WidgetLayout(Activity activity) {
        layout = new RelativeLayout(activity);
        widgets = new ArrayList<Widget>();
    }

    public void addWidget(Widget widget) {
        widgets.add(widget);
        layout.addView(widget.getView());
    }

    public Widget getWidget(MotionEvent e) {
        int x = (int)e.getRawX();
        int y = (int)e.getRawY();
        for (int i = 0; i < widgets.size(); i++) {
            Widget widget = widgets.get(i);
            Pair<Integer, Integer> pos = widget.getPosition();
            Pair<Integer, Integer> size = widget.getSize();
            if (x >= pos.first && x <= pos.first + size.first && y >= pos.second && y <= pos.second + size.second) {
                return widget;
            }
        }
        return null;
    }

    public Widget getWidget(View v) {
        for(int i = 0; i < widgets.size(); i++) {
            if (v == widgets.get(i).getView()) {
                return widgets.get(i);
            }
        }
        return null;
    }

    public Widget getInvisibleWidget(MotionEvent e) {
        int x = (int)e.getRawX();
        int y = (int)e.getRawY();
        float minDistance = Float.POSITIVE_INFINITY; // TODO: set proper threshold
        Widget result = null;
        for(int i = 0; i < widgets.size(); i++)
            if (widgets.get(i).getVisibility() == View.INVISIBLE) {
                Widget widget = widgets.get(i);
                Pair<Integer, Integer> pos = widgets.get(i).getPosition();
                float distance = (x - pos.first) * (x - pos.first) + (y - pos.second) * (y - pos.second);
                if (distance < minDistance) {
                    minDistance = distance;
                    result = widget;
                }
            }
        return result;
    }

    public Context getContext() {
        return layout.getContext();
    }

    public RelativeLayout getLayout() {
        return layout;
    }

    public void pushWidget(Widget widget) {
        widget.setVisibility(View.INVISIBLE);
        /*
        System.out.println(widget.getName());
        for(int i = 0; i < widgets.size(); i++)
            if (widgets.get(i).getName().equals(widget.getName())) {
                System.out.println("Find one");
                widgets.get(i).setVisibility(View.INVISIBLE);
            }
            */
    }

    public void pullWidget(Widget widget, MotionEvent e) {
        widget.setPosition((int)e.getRawX() - 140, (int)e.getRawY() - 140);
        widget.setVisibility(View.VISIBLE);
        /*
        for(int i = 0; i < widgets.size(); i++)
            if (widgets.get(i).getName().equals(name)){
                widgets.get(i).setVisibility(View.VISIBLE);
            }
            */
    }

    public void moveWidget(Widget widget, int x, int y) {
        for(int i = 0; i < widgets.size(); i++)
            if (widgets.get(i).getName().equals(widget.getName())) {
                widgets.get(i).setPosition(x, y);
            }
    }
}
