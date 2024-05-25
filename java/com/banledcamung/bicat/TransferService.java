package com.banledcamung.bicat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class TransferService extends Service {
    private ITransfer mTransfer;
    String CHANNEL_ID = "ForegroundServiceChannel";
    String IPAddress;
    int Port;

    public int mMaxLine;
    public int mCurrentLine;
    Socket mSocket;
    private BufferedReader in;
    private ClientThread clientThread;
    private Thread thread;
    private Handler handler;
    private final IBinder binder = new LocalBinder();
    private List<String> stringList = new ArrayList<>();
    public boolean isAutoSend = true;
    @Override
    public void onCreate() {
        super.onCreate();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            creatNotificationChannel();
        }
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IPAddress = intent.getStringExtra("IP");
        Port = intent.getIntExtra("port",0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Transfer Service")
                .setContentText("Service is running...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        handler = new Handler();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateNotification(String content) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Transfer Service")
                        .setContentText(content)
                        .setSmallIcon(R.drawable.ic_launcher_foreground);
                Notification updatedNotification = notificationBuilder.build();
                notificationManager.notify(1, updatedNotification);
            }
        }
    }

    public void creatNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID
                    , "Transfer code", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public void clearData(){
        stringList.clear();
        mMaxLine = 0;
        mCurrentLine = 0;
    }

    public void appendCommand(String line) {
        stringList.add(line);
        mMaxLine++;
    }

    public class LocalBinder extends Binder {
        TransferService getService(){
            return TransferService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void connectToServer(String s, int p){
        mSocket = null;
        IPAddress = s;
        Port = p;
        clientThread = new ClientThread();
        thread = new Thread(clientThread);
        thread.start();
    }

    public void disconnectServer(){
        if(thread!=null) {
            thread.interrupt();
            thread = null;
        }
        updateNotification("Disconnected");
        clientThread = null;
    }

//    public void sendData(String s){
//        if(!"".equals(s)) {
//            String[] lines = s.split("\n");
//            Collections.addAll(stringList, lines);
//            String currentCmd = mTransfer.getTextViewText("messagesent");
//            StringBuilder sb = new StringBuilder();
//            if(!"".equals(currentCmd)){
//                sb.append(currentCmd);
//                sb.append("\n");}
//            sb.append(s);
//            //handler.post(()->mTransfer.setTextViewText("messagesent",sb.toString()));
//        }
//        //handler.post(()->mTransfer.setTextViewText("messagetxt",""));
//    }

    public void setAutoSend(boolean isAuto){
        isAutoSend = isAuto;
    }

    public void sendMessage(String mess){
        if(clientThread!=null){
            clientThread.sendMessage(mess);
        }
    }

    public void sendFirstLine() {
        if(clientThread!=null){
            if(!stringList.isEmpty()){
                clientThread.sendMessage(stringList.get(0));
                stringList.remove(0);
                mCurrentLine++;
                handler.post(()->mTransfer.setPopupProgress(mCurrentLine));
                //        handler.post(()->mTransfer.marksentline());
            } else {
                //         handler.post(()->mTransfer.enableButton("runbtn"));
            }
        }
    }

    public void setInterface(ITransfer transferinterface){
        this.mTransfer = transferinterface;
    }

//    public void markSentCommand(){
//        String textAbove = mTransfer.getTextViewText("sentcmd");
//        String textBelow = mTransfer.getTextViewText("messagesent");
//        String movLine = "";
//
//        String[] linesAbove = textAbove.split("\n");
//        String[] linesBelow = textBelow.split("\n");
//
//        if(linesBelow.length>0){
//            movLine = linesBelow[0];
//            StringBuilder sbb = new StringBuilder();
//            for(int i = 1; i < linesBelow.length; i++){
//                sbb.append(linesBelow[i]);
//                if(i < linesBelow.length-1){
//                    sbb.append("\n");
//                }
//            }
//            handler.post(()->mTransfer.setTextViewText("messagesent",sbb.toString()));
//        }
//
//        StringBuilder sba = new StringBuilder();
//        sba.append(textAbove);
//        if(!movLine.isEmpty()){
//            sba.append("\n").append(movLine);
//        }
//        handler.post(()->mTransfer.setTextViewText("sentcmd",sba.toString()));
//
//    }

    public void setStringList(ArrayList<String> inString){
        stringList = inString;
    }

    class ClientThread implements Runnable{

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()){
                try {
                    if (mSocket == null) {
                        try {
                            mSocket = new Socket(IPAddress, Port);
                            updateNotification("Connected");
                            handler.post(()->{
                                mTransfer.setCheckConnectBtn(true);
                                mTransfer.showToast("Connected to '" + IPAddress + "'");
                            });

                        } catch (Exception e){
                            e.printStackTrace();
                            handler.post(()->{
                                mTransfer.setCheckConnectBtn(false);
                                mTransfer.showToast("" + e);});
                            break;
                        }
                    }
                    InputStreamReader inputStreamReader = new InputStreamReader(mSocket.getInputStream());
                    in = new BufferedReader(inputStreamReader);
                    String mess = in.readLine();
                    if (mess == null) {
                        boolean interrupted = Thread.interrupted();

                        break;
                    } else {
                        if(!isAutoSend){
                            //handler.post(()->mTransfer.enableButton("runbtn"));
                        } else {
                            sendFirstLine();
                            //sendFirst1000Line();
                        }
                        //handler.post(()->mTransfer.setTextViewText("svmess",mess));

                    }
                } catch (UnknownHostException e){
                    e.printStackTrace();
                    handler.post(() -> mTransfer.setCheckConnectBtn(false));
                    //handler.post(()->mTransfer.disableButton("dcn"));
                } catch (NoRouteToHostException e) {
                    e.printStackTrace();
                    handler.post(() -> mTransfer.setCheckConnectBtn(false));
                    //Print warning Host not found
                    handler.post(()->mTransfer.showToast("Server not found"));
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(() -> mTransfer.setCheckConnectBtn(false));
                    //Print warning Internet connection
                    handler.post(()->mTransfer.showToast("No Internet connection"));
                    break;
                } catch (Exception e){
                    e.printStackTrace();
                    handler.post(() -> mTransfer.setCheckConnectBtn(false));
                    //Print warning Internet connection
                    handler.post(()->mTransfer.showToast("" + e));
                    break;
                }
            }
        }

        public void sendMessage(String data) {
            new Thread(()->{
                if(mSocket!=null){
                    try {
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())),true);
                        out.println(data);
                        //Disable button to not allowed send next line until get reponse from server
                        // handler.post(() -> mTransfer.disableButton("runbtn"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void sendFirst1000Line() {
        if(clientThread!=null){
            StringBuilder message = new StringBuilder();
            SharedPreferences IPPort = getApplicationContext().getSharedPreferences("IPPort",MODE_PRIVATE);
            int numberSentLine = IPPort.getInt("numberSentLine",1000);
            int count = 0;
            while(!stringList.isEmpty()){
                message.append(stringList.get(0));
                stringList.remove(0);
                mCurrentLine++;
                count++;
                if(count >= numberSentLine || stringList.isEmpty()){
                    break;
                }
                message.append("\n;");
                //        handler.post(()->mTransfer.marksentline());
            }
            clientThread.sendMessage(message.toString());
            handler.post(()->mTransfer.setPopupProgress(mCurrentLine));
        }
    }
}