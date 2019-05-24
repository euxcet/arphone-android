package com.example.alice.appforphone;

import android.graphics.Bitmap;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

class WriteBytesRunnable implements Runnable {
    DataOutputStream os;
    private byte[] data;
    private int size;
    WriteBytesRunnable(DataOutputStream os, byte[] data, int size) {
        this.os = os;
        this.data = data;
        this.size = size;
    }
    public void run() {
        try {
            System.out.println(data);
            if(os==null)
                System.out.println("ERROR:os is null");
            os.write(data, 0, size);
        }
        catch(Exception e) {
            System.out.println("Error: send message failed.");
            e.printStackTrace();
        }
    }
}

public class Net {
    private static Net net;

    private String ip;
    private int port;
    private Socket socket;
    private DataOutputStream os;
    private BufferedReader is;

    private Runnable connectRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (socket != null) {
                    socket.close();
                }
                socket = new Socket(ip, port);
                os = new DataOutputStream(socket.getOutputStream());
                if(os==null)

                is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }
            catch(Exception e) {
                System.out.println("Error: create socket failed.");
                e.printStackTrace();
            }
        }
    };

    private Runnable closeRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                socket.close();
            }
            catch(Exception e) {
                System.out.println("Error: close socket failed.");
                e.printStackTrace();
            }
        }
    };

    private Net(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public static Net getInstance(String ip, int port) {
        if (net == null) {
            net = new Net(ip, port);
        }
        return net;
    }

    public static Net getInstance() {
        return net;
    }

    public void close() {
        new Thread(closeRunnable).start();
    }

    public void connect() {
        new Thread(connectRunnable).start();
    }

    public boolean connected() {
        return socket != null && socket.isConnected();
    }

    public void sendBytes(byte[] data, int size) {
        Thread thread = new Thread(new WriteBytesRunnable(os, data, size));
        thread.start();
        try {
            thread.join();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void sendByte(byte num) {
        byte[] data = new byte[1];
        data[0] = num;
        sendBytes(data, 1);
    }

    public void sendShort(short num) {
        byte[] data = new byte[2];
        data[0] = (byte)(num & 0xff);
        data[1] = (byte)((num >> 8) & 0xff);
        sendBytes(data, 2);
    }

    public void sendInt(int num) {
        byte[] data = new byte[4];
        data[0] = (byte)(num & 0xff);
        data[1] = (byte)((num >> 8) & 0xff);
        data[2] = (byte)((num >> 16) & 0xff);
        data[3] = (byte)((num >> 24) & 0xff);
        sendBytes(data, 4);
    }

    public void sendDouble(double num) {
        byte[] data = new byte[8];
        long value = Double.doubleToRawLongBits(num);
        for(int i = 0; i < 8; i++) {
            data[i] = (byte)((value >> (i << 3)) & 0xff);
        }
        sendBytes(data, 8);
    }

    public void sendString(String str, int size) {
        sendBytes(str.getBytes(), str.length());
        for(int i = 0; i < size - str.length(); i++) {
            sendByte((byte)0);
        }
    }

    /*
        Identifier
        Name
        Type
        Width
        Height
        Size
        Content
        Correction
     */
    public void sendImage(Bitmap bitmap) {
        sendByte((byte)0);
        // TODO
        sendString("name", 20);
        sendString("png", 5);
        sendShort((short)bitmap.getWidth());
        sendShort((short)bitmap.getHeight());

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        byte[] data = os.toByteArray();

        sendInt(data.length);
        sendBytes(data, data.length);
        sendByte((byte)100);
    }

    /*
        Identifier
        Type
        Correction
     */
    public void sendMode(int mode) {
        sendByte((byte)1);
        sendByte((byte)mode);
        sendByte((byte)100);
    }

    /*
        Identifier
        X
        Y
        Correction
     */
    public void sendEnlarge(int x, int y) {
        sendByte((byte)2);
        sendShort((short)x);
        sendShort((short)y);
        sendByte((byte)100);
    }

    /*
        Identifier
        Scale
        Correction
     */
    public void sendScale(double x) {
        sendByte((byte)3);
        sendDouble(x);
        sendByte((byte)100);
    }

    /*
        Identifier
        X
        Y
        Correction
     */
    public void sendCursor(int x, int y) {
        sendByte((byte)4);
        sendShort((short)x);
        sendShort((short)y);
        sendByte((byte)100);
    }

    public void sendSelect(int index,double y)
    {
        sendByte((byte)8);
        sendByte((byte)index);
        int sendY=(int)y*255;
        System.out.println("[send Select] "+sendY);
        sendByte((byte)sendY);
        sendByte((byte)100);
    }

    public void sendBallPos(int flag,int x,int y)
    {
        sendByte((byte)9);
        sendByte((byte)flag);
        sendByte((byte)x);
        sendByte((byte)y);
        sendByte((byte)100);
    }
}
