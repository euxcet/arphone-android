package com.example.euxcet.arphone_android;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.euxcet.arphone_android.layout.Widget;
import com.example.euxcet.arphone_android.layout.WidgetLayout;

public class HomeActivity extends Activity implements View.OnTouchListener {

    private WidgetLayout widgetLayout;
    private Net net;

    private final int BORDER_WIDTH = 100;
    private int windowWidth;
    private int windowHeight;

    /*
        touchStatus = 0(Nothing), 1(Pull)
     */
    private int touchStatus;
    private Widget focusWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        net = Net.getInstance();


        WindowManager wm = this.getWindowManager();
        windowWidth = wm.getDefaultDisplay().getWidth();
        windowHeight = wm.getDefaultDisplay().getHeight();


        widgetLayout = new WidgetLayout(this);
        Widget widget0 = new Widget("widget0", widgetLayout.getContext(), 200, 200, windowWidth, windowHeight);
        Widget widget1 = new Widget("widget1", widgetLayout.getContext(), 200, 200, windowWidth, windowHeight);
        Widget widget2 = new Widget("widget2", widgetLayout.getContext(), 200, 200, windowWidth, windowHeight);
        widget0.setPosition(0, 0);
        widget1.setPosition(100, 400);
        widget2.setPosition(100, 700);


        widgetLayout.addWidget(widget0);
        //widgetLayout.addWidget(widget1);
        //widgetLayout.addWidget(widget2);


        focusWidget = null;
        this.setContentView(widgetLayout.getLayout());
        widget0.getView().setOnTouchListener(this);
        widgetLayout.getLayout().setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        int x = (int)e.getRawX();
        int y = (int)e.getRawY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                focusWidget = widgetLayout.getWidget(v);
                if (focusWidget == null & onBorder(e)) {
                    focusWidget = widgetLayout.getInvisibleWidget(e);
                    System.out.println(focusWidget);
                    touchStatus = 1;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                System.out.println(touchStatus);
                if (touchStatus != 0) {
                    showToast("Pull!!");
                    touchStatus = 0;
                    if (focusWidget != null) {
                        pullWidget(focusWidget, e);
                    }
                }
                else if (focusWidget != null) {
                    if (onBorder(e)) {
                        pushWidget(focusWidget, e);
                        showToast("Push!!");
                    }
                    else {
                        focusWidget.setPosition(x - 140, y - 140);
                    }
                }
                focusWidget = null;
                break;

            default:
                break;
        }
        return true;
    }

    public void showToast(String message) {
        Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean onLeftOrRightBorder(MotionEvent e) {
        return (e.getRawX() < BORDER_WIDTH || e.getRawX() > windowWidth - BORDER_WIDTH);
    }

    private boolean onTopOrBottomBorder(MotionEvent e) {
        return (e.getRawY() < BORDER_WIDTH || e.getRawY() > windowHeight - BORDER_WIDTH);
    }

    private boolean onBorder(MotionEvent e) {
        return onLeftOrRightBorder(e) || onTopOrBottomBorder(e);
    }

    private void pullWidget(Widget widget, MotionEvent e) {
        widgetLayout.pullWidget(widget, e);
        net.sendByte((byte)5);
        net.sendInt((int)e.getRawX());
        net.sendInt((int)e.getRawY());
        net.sendInt(windowWidth);
        net.sendInt(windowHeight);
        net.sendByte((byte)100);
    }

    private void pushWidget(Widget widget, MotionEvent e) {
        widgetLayout.pushWidget(widget);
        net.sendByte((byte)5);
        net.sendInt((int)e.getRawX());
        net.sendInt((int)e.getRawY());
        net.sendInt(windowWidth);
        net.sendInt(windowHeight);
        net.sendByte((byte)100);
    }

    private void moveWidget(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    }
}

