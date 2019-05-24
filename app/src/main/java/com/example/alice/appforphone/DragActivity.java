package com.example.alice.appforphone;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class DragActivity extends AppCompatActivity {
    private Net net = Net.getInstance();
    ImageView pic;
    int screenWidth;
    int screenHeight;
    int UnavailabeHeight;
    int secHeight;
    int lastX;
    int lastY;
    int SLIDELeft;
    int SLIDERight;
    long istart;
    long iend;
    double duration;
    double durationTimer=2;
    int leftIndex=0;
    int rightIndex=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
        pic = (ImageView) findViewById(R.id.imageDrag);

        Point p = new Point();
        //获取窗口管理器
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(p);
        screenWidth = p.x; // 屏幕宽度
        screenHeight=p.y;
        UnavailabeHeight=screenHeight/5*2;
        secHeight=screenHeight*3/25;
        SLIDELeft=screenWidth/5;
        SLIDERight=screenWidth-SLIDELeft;
        istart=0;
        iend=0;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        //the first finger
        lastX= (int) event.getRawY(); // height
        lastY = (int) event.getRawX(); // width
        int flag=0;

        if(lastY>SLIDELeft&&lastY<SLIDERight)
            return super.onTouchEvent(event);
        // if is above our limit : do nothing with the flag
        if(lastX>UnavailabeHeight)
            flag=flag+(int)((lastX-UnavailabeHeight)/secHeight);

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(flag==4||flag==0)
                {
                    if(istart==0)
                        istart= System.currentTimeMillis();
                    else
                    {
                        iend= System.currentTimeMillis();
                        duration=Math.round((iend-istart)/1000)%60; // 秒数
                        System.out.println("duration in top/down: "+duration);
                        if(duration>durationTimer)
                        {
                            if(lastY<SLIDELeft)
                            {
                                if(flag==0&&leftIndex>0)
                                    leftIndex--;
                                else if(flag==4&&leftIndex<99)
                                    leftIndex++;
                            }
                            else if(lastY>SLIDERight)
                            {
                                if(flag==0&&rightIndex>0)
                                    rightIndex--;
                                else if(rightIndex==4&&rightIndex<99)
                                    rightIndex++;
                            }
                            durationTimer=durationTimer+2;
                            System.out.println("DurationTimer-duration: "+(durationTimer-duration));
                        }
                    }
                }
                if(lastY<SLIDELeft)
                    flag=flag+leftIndex;
                else if(lastY>SLIDERight)
                    flag=flag+rightIndex;
                System.out.println("[net] flag:"+flag);
                net.sendSelect(flag,0);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                istart=0;
                iend=0;
                duration=0;
                break;
        }
        return super.onTouchEvent(event);
    }


    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);//两点间距离公式
    }
}
