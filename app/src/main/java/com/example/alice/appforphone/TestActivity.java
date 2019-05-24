package com.example.alice.appforphone;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.cert.CertificateEncodingException;

public class TestActivity extends AppCompatActivity {

    private Net net = Net.getInstance();

    int []District;

    ImageView pic;  // 窗口大小

    private int lastX; // for direction
    private int lastY;
    String fileName;

    private int timer; // 记录滑动次数 70*3
    private static final int LIMIT=30; // 小球极坐标
    int genX,genY,cancelX,cancelY;
    // gen 仅仅在sendPos处修改； cancel 仅仅在用户按下时修改
    // 使用 gen 发给电脑坐标，用户按下传递给cancel ；cancel delete时，用sendPos再次发给电脑

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // 窗口大小
        pic = (ImageView) findViewById(R.id.image);
        District=new int[2];
        Point p = new Point();
        //获取窗口管理器
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(p);

        timer=0;
        // 得到文件名
        Intent intent = getIntent();
        fileName = getSDPath() + "/" + intent.getStringExtra("name")+".txt";
        String filestring=p.x+" "+p.y+"\n";
        System.out.println("filename"+fileName);
        appendWriteFileData(fileName,filestring,false);
        Toast.makeText(TestActivity.this,filestring,Toast.LENGTH_SHORT).show();
        generate();
    }

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        return sdDir.toString();
    }

    //向指定的文件中追加的数据
    public static void appendWriteFileData(String file, String content,boolean cover) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(file, cover);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // *** readme
    // generate - sendPos -- wait for touch - touch finish - (delete - sendPos) generate (delete - sendPos)   sendPos  (delete -sendPos)   -     wait for touch;
    //            net send(1,x,y)              send(0,0,0)  send(0,0,0) send           send(0,0,0) send    send(1,x,y) send(0,0,0) send
    // to be done
    private void generate()
    {
        int x,y;
        int flag=timer/70;
        double angle=Math.random();
        angle=angle*Math.PI;
        // to be modified
        if(Math.abs(angle-Math.PI/2)<Math.atan(0.5)) // 指向手机上面
        {
            angle=Math.random()*Math.atan(2);
            if(flag==1||(flag==2&&Math.random()<=0.5))
                angle=Math.PI-angle;
        }
        System.out.println("[generate] "+angle+" "+Math.sin(angle));
        double length=Math.random();
        x=(int)(length*LIMIT*Math.cos(angle));
        y=(int)(length*LIMIT*Math.sin(angle));
        sendPos(x,y);
    }

    public void sendPos(int x,int y)
    {
        if(timer%70==0)
        {
            String showstring="请";
            if(timer==0)
                showstring=showstring+"右手持握";
            if(timer==70)
                showstring=showstring+"左手持握";
            if(timer==140)
                showstring=showstring+"双手持握";
            if(timer>=210)
                showstring="实验结束，感谢参与 :D";
            AlertDialog.Builder ab=new AlertDialog.Builder(this);  //(普通消息框)

            ab.setTitle("提醒");  //设置标题
            ab.setMessage(showstring);//设置消息内容
            ab.setPositiveButton("好的",null);//设置确定按钮
            //ab.setNegativeButton("取消",null);//设置取消按钮
            //ab.setNeutralButton("其他",null);
            ab.show();//显示弹出框
        }

        net.sendBallPos(1,x,y);
        String filestring="[generate] "+x+" "+y+'\n';
        Toast.makeText(TestActivity.this,filestring,Toast.LENGTH_SHORT).show();
        appendWriteFileData(fileName,filestring,true);
        genX=x;
        genY=y;
    }


    public void forCancelClick(View v)
    {
        timer--;
        Toast.makeText(TestActivity.this,"successfully canceled!",Toast.LENGTH_SHORT).show();
        String filestring="Delete\n";
        appendWriteFileData(fileName,filestring,true);
        net.sendBallPos(0,0,0); // 取消点亮的新球
        sendPos(cancelX,cancelY);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                timer++;
                cancelX=genX;
                cancelY=genY; // for delete
                lastY = (int) event.getRawY();
                lastX = (int) event.getRawX();
                String tosent=event.getX()+" "+event.getY()+"pointPos "+lastX+" "+lastY+" "+pic.getRight()+" "+pic.getBottom();
                System.out.println(tosent);

                String filestring="[startPos] "+lastX+" "+lastY+"\n";
                appendWriteFileData(fileName,filestring,true);
                Toast.makeText(TestActivity.this,filestring,Toast.LENGTH_SHORT).show();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                lastY = (int) event.getRawY();
                lastX = (int) event.getRawX();
                break;


            case MotionEvent.ACTION_MOVE:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                filestring=lastX+" "+lastY+"\n";
                appendWriteFileData(fileName,filestring,true);
                break;

            case MotionEvent.ACTION_UP:
                // to be done
                lastY = (int) event.getRawY();
                lastX = (int) event.getRawX();
                filestring=lastX+" "+lastY+"\n\n";
                appendWriteFileData(fileName,filestring,true);
                net.sendBallPos(0,0,0);
                generate(); // 产生下一个位置
                break;

            // 第二个手指抬起
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }
        return super.onTouchEvent(event);
    }


}
