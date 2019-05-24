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

public class SelectActivity extends AppCompatActivity {

    private Net net = Net.getInstance();


    private static final int BOTTOM = 0;

    private static final int RIGHT_BOTTOM = 1;
    private static final int RIGHT = 2;
    private static final int RIGHT_TOP = 3;

    private static final int TOP = 4;

    private static final int LEFT_TOP = 5;
    private static final int LEFT = 6;
    private static final int LEFT_BOTTOM = 7;

    private static final int TOUCH_TWO = 0x21;
    private static final int CENTER = 0x19;
    private static final int touchDistanceX=10;
    private static final int touchDistanceY = 15; //
    private static final int touchMIN=5;
    private double MOVESCALE;

    int []District;

    DisplayMetrics dm2;
    ImageView pic;
    int screenWidth;
    int screenHeight;

    private int dragDirection;
    private int lastX; // for direction
    private int lastY;
    private int startX;
    private int startY; // for float y
    private float oriDis = 1f;
    int scaleTimer;

    String fileName;
    private static final int SHORTSCALE=100;
    private static final int LONGSCALE=300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        dm2 = getResources().getDisplayMetrics();
        //dm2.widthPixels dm2.heightPixels

        pic = (ImageView) findViewById(R.id.image);
        District=new int[2];
        Point p = new Point();
        //获取窗口管理器
        WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(p);
        screenWidth = p.x; // 屏幕宽度
        screenHeight=p.y;
        scaleTimer=0;
        dragDirection=0;
        MOVESCALE=Math.sqrt(p.x*p.x+p.y*p.y)/3;
        System.out.println("x "+p.x+" y "+p.y+" MOVESCALE "+MOVESCALE);

        fileName = getSDPath() + "/" + "data-3.txt";
        String filestring=p.x+" "+p.y+"\n";
        System.out.println(fileName);
        appendWriteFileData(fileName,filestring,true);
        Toast.makeText(SelectActivity.this,filestring,Toast.LENGTH_SHORT).show();
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

    //打开指定文件，读取其数据，返回字符串对象
    public String readFileData(String fileName){

        String result="";

        try{

            FileInputStream fis = this.openFileInput(fileName);

            //获取文件长度
            int lenght = fis.available();

            byte[] buffer = new byte[lenght];

            fis.read(buffer);

            //将byte数组转换成指定格式的字符串
            result = new String(buffer, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return  result;
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                lastY = (int) event.getRawY();
                lastX = (int) event.getRawX();
                startX=lastX;
                startY=lastY;
                String tosent=event.getX()+" "+event.getY()+"pointPos "+lastX+" "+lastY+" "+pic.getRight()+" "+pic.getBottom();
                System.out.println(tosent);

                // 2/7 3/7 2/7
                District[0]=1;
                if(lastX<screenWidth/7*2)
                    District[0]=0;
                else if(lastX>screenWidth/7*5)
                    District[0]=2;
                int threeSecY=pic.getBottom()/3;
                District[1]=(int)lastY/threeSecY;
                if(District[1]==3)
                    District[1]=2;
                String toPrint="district "+District[0]+" "+District[1]+"\n";
                System.out.println(toPrint);
                dragDirection=CENTER;
                String filestring="[startPos] "+lastX+" "+lastY+"\n";
                appendWriteFileData(fileName,filestring,true);
                Toast.makeText(SelectActivity.this,filestring,Toast.LENGTH_SHORT).show();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                lastY = (int) event.getRawY();
                lastX = (int) event.getRawX();
                dragDirection = TOUCH_TWO;
                oriDis = distance(event);
                break;


            case MotionEvent.ACTION_MOVE:
                switch (dragDirection)
                {
                    case TOUCH_TWO: //双指操控 old version
                        // to be done
                        break;

                    default: // get direction and moving scale
                        dragDirection = getDirectionWIRHOUT(lastX,lastY, (int) event.getX(),
                                (int) event.getY(),District[0],District[1]);
                        double sendScale=Math.sqrt(Math.pow(startX-event.getX(),2)+Math.pow(startY-event.getY(),2))/MOVESCALE;
                        if (sendScale>1)
                            sendScale=1;
                        if(dragDirection!= CENTER)
                        {
                            //net.sendSelect(dragDirection,sendScale);
                            //Toast.makeText(SelectActivity.this,"send "+dragDirection+" Scale "+sendScale,Toast.LENGTH_SHORT).show();
                            System.out.println("send "+dragDirection);
                        }
                        break;

                }
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                // to be done
                lastY = (int) event.getRawY();
                lastX = (int) event.getRawX();
                //if(dragDirection!=CENTER)
                if(false)
                {
                    int goingWidth=Math.abs(startX-lastX);
                    if(goingWidth>LONGSCALE)
                        net.sendSelect(dragDirection,1);
                    else if(goingWidth<SHORTSCALE)
                        net.sendSelect(dragDirection,0);
                }
                dragDirection = 0;
                break;

                // 第二个手指抬起
            case MotionEvent.ACTION_POINTER_UP:
                dragDirection = 0;
                break;
        }
        return super.onTouchEvent(event);
    }
    // all deal within this funtion: lastXX, x :lasttime and now pos; DistrictX: startpos
    protected int getDirection(int lastXX,int lastYY, int x, int y,int DistrictX,int DistrictY) {
        //System.out.println("getDirection "+" leftX "+lastXX+" lastYY "+lastYY+" x "+x+" y "+y);
        // x width y height

        double allX=x-startX;
        double allY=y-startY;
        double tempX=x-lastXX;
        double tempY=y-lastYY;
        if(tempX==0&&tempY==0)
        {
            System.out.println("[getDir] ZERO!");
            return CENTER;
        }
        double cos=(allX*tempX+allY*tempY)/Math.sqrt((allX*allX+allY*allY)*(tempX*tempX+tempY*tempY));
        //System.out.println("[getDir] allX allY tempX tempY: "+allX+" "+allY+" "+tempX+" "+tempY);

        if(cos<0)
        {
            System.out.println("[getDir] different Direction!!!");
            // to be done
            return CENTER;
        }
        // standard vector (1,0) right
        double standardCos=allX/Math.sqrt(allX*allX+allY*allY);
        double standardAng=Math.acos(standardCos);
        //System.out.println("[getDir] allX allY tempX tempY: "+allX+" "+allY+" "+tempX+" "+tempY);
        System.out.println("[getDir] standartcos: "+standardAng+" District "+DistrictX+" "+DistrictY);
        // -a2=-Ally; neg 顺时针 - Ally pos 顺时
        if(DistrictX==1)
        {
            if(DistrictY==0&&allY>0&&Math.abs(standardCos)<0.5)
                return BOTTOM;
            if(DistrictY==2&&allY<0&&Math.abs(standardCos)<0.5)
                return TOP;
            return CENTER;
        }
        else if(DistrictX==0)
        {
            if(DistrictY==0&&allY>0&&standardCos<0.866&&standardCos>0.15) // 30-60 degree
                return RIGHT_BOTTOM;
            if(DistrictY==1&&Math.abs(standardCos)>0.8)
                return RIGHT;
            if(DistrictY==2&&allY<0&&standardCos<0.866&&standardCos>0.15)
                return RIGHT_TOP;
            return CENTER;
        }
        else
        {
            if(DistrictY==0&&allY>0&&standardCos>-0.866&&standardCos<-0.15) // 30-60 degree
                return LEFT_BOTTOM;
            if(DistrictY==1&&Math.abs(standardCos)>0.8)
                return LEFT;
            if(DistrictY==2&&allY<0&&standardCos>-0.866&&standardCos<-0.15)
                return LEFT_TOP;
        }
        return CENTER;

    }

    // all deal within this funtion: lastXX, x :lasttime and now pos; DistrictX: startpos
    protected int getDirectionWIRHOUT(int lastXX,int lastYY, int x, int y,int DistrictX,int DistrictY) {
        //System.out.println("getDirection "+" leftX "+lastXX+" lastYY "+lastYY+" x "+x+" y "+y);
        // x width y height

        double allX=x-startX;
        double allY=y-startY;
        double tempX=x-lastXX;
        double tempY=y-lastYY;
        if(tempX==0&&tempY==0)
        {
            System.out.println("[getDir] ZERO!");
            return CENTER;
        }
        double cos=(allX*tempX+allY*tempY)/Math.sqrt((allX*allX+allY*allY)*(tempX*tempX+tempY*tempY));
        //System.out.println("[getDir] allX allY tempX tempY: "+allX+" "+allY+" "+tempX+" "+tempY);

        if(cos<0)
        {
            System.out.println("[getDir] different Direction!!!");
            // to be done
            return CENTER;
        }
        // standard vector (1,0) right
        double standardCos=allX/Math.sqrt(allX*allX+allY*allY);
        double standardAng=Math.acos(standardCos)/Math.PI*180;
        //System.out.println("[getDir] allX allY tempX tempY: "+allX+" "+allY+" "+tempX+" "+tempY);
        System.out.println("[getDir] standartcos: "+standardAng+" District "+DistrictX+" "+DistrictY);
        // -a2=-Ally; neg 顺时针 - Ally pos 顺时
        if(standardAng<10)
            return RIGHT;
        if(standardAng>168)
            return LEFT;
        if(Math.abs(standardAng-90)<12) // 78-102
        {
            if(allY>0)
                return BOTTOM;
            if(allY<0)
                return TOP;
        }
        if(Math.abs(standardAng-65)<12) // 53-77
        {
            if(allY>0)
                return RIGHT_BOTTOM;
            if(allY<0)
                return RIGHT_TOP;
        }
        if(Math.abs(standardAng-115)<12) // 103-127
        {
            if(allY>0)
                return LEFT_BOTTOM;
            if(allY<0)
                return LEFT_TOP;
        }
        return CENTER;

    }

    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);//两点间距离公式
    }

}
