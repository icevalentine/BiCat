package com.banledcamung.bicat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.banledcamung.bicat.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity implements ITransfer {

    ActivityMainBinding binding;
    PopupWindow popupWindow;

    TextView progressText;
    ProgressBar runningProgressBar;
    HomeFragment homeFragment;
    RelativeLayout mainlayout;
    RelativeLayout runBtnPanel;
    RelativeLayout progressPanel;
    ToggleButton connectBtn, runBtn;
    Button debugBtn;
    DatagramClientThread datagramClientThread;

    Thread datagramThread;
    boolean mBound = false;
    TextView debugText;
    int mMaxLine;
    TransferService mService;
    Handler handler;
    boolean isConnected, isReceived;
    int tryCount = 0;

    int debugCount = 0;

    public boolean debugMode = false;
    String receivedData;

    String mac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        debugText = findViewById(R.id.debug_text);
        setCustomDensity();
        mainlayout =findViewById(R.id.frame_layout);
        runBtnPanel= findViewById(R.id.run_button_pannel);
        connectBtn = findViewById(R.id.connectBtn);
        runBtn = findViewById(R.id.run_btn);
        progressPanel = findViewById(R.id.progress_panel);
        runningProgressBar = findViewById(R.id.running_progress_bar);
        progressText = findViewById(R.id.progress_text);
        debugBtn = findViewById(R.id.debug_btn);
        runningProgressBar.setMax(100);
        homeFragment = new HomeFragment();
        handler = new Handler();
        isConnected = false;
        replaceFragment(homeFragment);
        setPanelVisible(View.VISIBLE);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId()==R.id.home_){
                replaceFragment(new HomeFragment());
                setPanelVisible(View.VISIBLE);
            } else if (item.getItemId()==R.id.list_){
                replaceFragment(new ListFragment());
            } else if (item.getItemId()==R.id.setting_){
                replaceFragment(new SettingFragment());
            } else {
                replaceFragment(new HelpFragment());
            }
            return true;
        });

        debugBtn.setOnClickListener(v->{
            if(!debugMode) {
                debugCount++;
                if (debugCount >= 3) {
                    debugMode = true;
                    debugText.setText("Debug ON");
                }
            } else {
                debugCount--;
                if (debugCount <= 0) {
                    debugMode = false;
                    debugText.setText("");
                    debugCount = 0;
                }
            }
        });

        runBtn.setOnClickListener(v->{
            if(runBtn.isChecked()){
                if(mBound) {
                    mService.sendMessage("Continue");
                    mService.setAutoSend(true);

                }
            } else {
                if(mBound) {
                    mService.sendMessage("Paused");
                    mService.setAutoSend(false);
                }
            }
        });

        connectBtn.setOnClickListener(v->{
            if(connectBtn.isChecked()){
                SharedPreferences IPPort = getSharedPreferences("IPPort",MODE_PRIVATE);
                String udpIP = IPPort.getString("IP","192.168.1.255");
                mac = IPPort.getString("mac","");
//                if(!debugMode){
//                    udpIP = changeSubString(udpIP);
//                }

                String message = "getip;;" + mac;
                int port = IPPort.getInt("Port",3333);
                isReceived = false;
                receivedData = "";
                if(datagramClientThread==null){
                    datagramClientThread = new DatagramClientThread(port, udpIP);
                    datagramThread = new Thread(datagramClientThread);
                    datagramThread.start();
                }
                tryCount = 0;
                handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isReceived) {
                            datagramClientThread = null;
                            datagramThread.interrupt();
                            if (!isConnected && debugMode) {
                                    debugText.setText("Receive '" + receivedData + "'");
                            }
                            return;
                        } else {
                            datagramClientThread.sendMessage(message);
                            tryCount++;
                            if (debugMode) {
                                debugText.setText("Request get IP (" + tryCount + ")\n" + "Receive '" + receivedData +"'");
                            }
                            if (tryCount >= 6) {
                                showToast("Cannot get IP");
                                if (debugMode) {
                                    debugText.setText("Cannot get IP");
                                }
                                setCheckConnectBtn(false);
                                if (datagramClientThread.datagramSocket != null) {
                                    datagramClientThread.datagramSocket.close();
                                }
                                datagramClientThread = null;
                                datagramThread.interrupt();
                                return;
                            }
                            handler.postDelayed(this, 1000);

                        }
                            
                    }
                },1000);
            } else {
                if (datagramClientThread != null && datagramClientThread.datagramSocket != null) {
                    datagramClientThread.datagramSocket.close();
                }
                mService.sendMessage("Disconnect");
                mService.disconnectServer();
                runBtn.setChecked(false);
                runBtn.setEnabled(false);
                isConnected = false;
            }
        });

        Intent serviceIntent = new Intent(MainActivity.this, TransferService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try{
                if(mService==null) {
                    startForegroundService(serviceIntent);
                }
                bindService(serviceIntent, conection, Context.BIND_AUTO_CREATE);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String changeSubString(String input) {
        int count = 0;
        int index = 0;

        for(int i = 0; i < input.length();i++){
            if(input.charAt(i)=='.'){
                count++;
                if(count==3){
                    index = i;
                    break;
                }
            }
        }
        if(count < 3){ return input;}
        return input.substring(0,index+1) + "255";
    }

    public void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
        setPanelVisible(View.GONE);
    }

    private void setPanelVisible(int visible) {
        runBtnPanel.setVisibility(visible);
        progressPanel.setVisibility(visible);
    }

    public void showSelectPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_select,null);
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT , true);
        popupWindow.showAtLocation(mainlayout, Gravity.CENTER,0,0);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void selectItem(int position, int tabIndex) throws IOException {
        binding.bottomNavigationView.setSelectedItemId(R.id.home_);
        int curPos = position+1;
        String keyTab;
        if(tabIndex==1){
            keyTab="s";
        } else if(tabIndex==2) {
            keyTab="b";
        } else {
            keyTab="c";
        }
        String imgIDstr = keyTab+curPos+"_img_"+curPos;
        int imgID = getResources().getIdentifier(imgIDstr,"drawable",getPackageName());

//        String nameIDstr = "s" + curPos+"_name";
//        int nameID = getResources().getIdentifier(nameIDstr, "string",getPackageName());
        String name = getString(R.string.s_name) + " " + curPos;
        SharedPreferences ItemData = getSharedPreferences("ItemData",MODE_PRIVATE);
        SharedPreferences.Editor editor = ItemData.edit();
        editor.putInt("imgID",imgID);
        editor.putString("titleText",name);
        editor.apply();
        homeFragment = new HomeFragment();
        replaceFragment(homeFragment);
        setPanelVisible(View.VISIBLE);


        try {
            String fileName = keyTab + curPos + "_file_"+curPos+".txt";
            InputStream inputStream = getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            mService.clearData();
            mMaxLine = 0;
            while((line = reader.readLine())!= null) {
                mService.appendCommand(line);
                mMaxLine++;
            }

            runningProgressBar.setMax(mMaxLine);
            runningProgressBar.setProgress(0);
            progressText.setText("0%");
        } catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    private ServiceConnection conection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TransferService.LocalBinder binder = (TransferService.LocalBinder) service;
            mService = binder.getService();
            try {
                mService.setInterface((ITransfer) MainActivity.this);
            } catch (Exception e){
                e.printStackTrace();
            }
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences ItemData = getSharedPreferences("ItemData",MODE_PRIVATE);
        SharedPreferences.Editor editor = ItemData.edit();
        editor.remove("titleText");
        editor.remove("imgID");
        editor.apply();
        if(mService!=null) {
            mService.clearData();
            mService.sendMessage("Disconnect");
            unbindService(conection);
            stopService(new Intent(MainActivity.this, TransferService.class));
        }
    }

    @Override
    public void setPopupProgress(int currentProgress) {
        runningProgressBar.setProgress(currentProgress);
        int percent = currentProgress*100/mMaxLine;
        progressText.setText(percent+"%");
    }

    @Override
    public void setCheckConnectBtn(boolean b) {
        connectBtn.setChecked(b);
        runBtn.setEnabled(b);
        isConnected = b;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void showToast(String text) {
        if(debugMode) {
            debugText.setText(text);
        }
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setConneted(boolean b) {
        isConnected = b;
    }

    public void onReponseReceived(String data) {
        isReceived = true;
        receivedData = data;
    }

    public void setCustomDensity(){
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float currentDensity = displayMetrics.density;
        float scalingFactor = displayMetrics.widthPixels*1.0f/1080;
        displayMetrics.density = 2.625f*scalingFactor;
        displayMetrics.densityDpi=(int)(480*scalingFactor+0.5);
        //displayMetrics.scaledDensity = 2.8875f*scalingFactor;
//        displayMetrics.xdpi = 537.88135f*scalingFactor;
//        displayMetrics.ydpi = 539.1013f*scalingFactor;

        //  debugText.setText(displayMetrics.xdpi + " " + displayMetrics.ydpi+" "+ displayMetrics.scaledDensity+" "+displayMetrics.widthPixels);
    }

    public void moveToListFragment() {
        replaceFragment(new ListFragment());
    }

    public void moveToSelectImage() { replaceFragment(new SelectImageFragment());
    }

    public void selectCustomItem(int position) {
        binding.bottomNavigationView.setSelectedItemId(R.id.home_);
        SharedPreferences fileSP = getSharedPreferences("fileSP",MODE_PRIVATE);
        SharedPreferences ItemData = getSharedPreferences("ItemData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ItemData.edit();
        int negativePosition = -1 - position;
        editor.putInt("imgID",negativePosition);
        editor.apply();

        homeFragment = new HomeFragment();
        replaceFragment(homeFragment);
        setPanelVisible(View.VISIBLE);

        //set file
        mService.clearData();
        String keylinenumber = "filekey" + position + "numberOfLine";
        mMaxLine = fileSP.getInt(keylinenumber,0);
        if(mMaxLine>0){
            for(int lineindex = 0; lineindex < mMaxLine; lineindex++){
                String key = "filekey" + position + ";" + lineindex;
                String line = fileSP.getString(key, "");
                mService.appendCommand(line);
            }

            runningProgressBar.setMax(mMaxLine);
            runningProgressBar.setProgress(0);
            progressText.setText("0%");
        }
    }

    class DatagramClientThread implements Runnable{
        int port;
        String ip;

        DatagramSocket datagramSocket;
        InetAddress inetAddress;

        public DatagramClientThread(int port, String ip){
            this.port = port;
            this.ip = ip;
        }

        @Override
        public void run() {

            try{
                if(datagramSocket==null){
                    datagramSocket=new DatagramSocket(port);
                    inetAddress=InetAddress.getByName(ip);
                }
                DatagramPacket dp = new DatagramPacket(new byte[1024],1024);
                while (!Thread.currentThread().isInterrupted()) {
                    datagramSocket.receive(dp);
                    String data = new String(dp.getData(), 0, dp.getLength(), "UTF-8");
                    receivedData = data;
                    if(!"".equals(data) && !data.contains("getip") && data.contains(mac)){
                        onReponseReceived(data);
                        String receiveIP = getIP(data.trim());
                        connectToServer(receiveIP, port);
                        datagramSocket.close();
                    }
                    //handler.post(() -> statusTxt.setText("receive : '" + data + "'"));
                }
            } catch (Exception e){
                e.printStackTrace();
                Thread.interrupted();
                if(datagramSocket!=null) {
                    datagramSocket.close();
                }
                // handler.post(()->statusTxt.setText(""+e));
            }
        }
        public void sendMessage(String mess){
            new Thread(()->{
                if(datagramSocket!=null){
                    try{
                        byte[] buffer = mess.getBytes();
                        DatagramPacket p = new DatagramPacket(buffer, buffer.length, inetAddress,
                                port);
                        datagramSocket.send(p);
                        //handler.post(()->statusTxt.setText("send : '"+ mess +"'"));
                    } catch (Exception e){
                        e.printStackTrace();
                        //handler.post(()->statusTxt.setText(""+e));
                    }
                }
            }).start();
        }

    }


    private String getIP(String input) {
        int count = 0;
        int index = 0;

        for (int i = input.length() - 1; i >= 0; i--) {
            if (input.charAt(i) == ';') {
                count++;
                index = i;
                break;
            }
        }
        if (count < 1) {
            return input.trim();
        }
        return input.substring(index + 1);
    }
    private void connectToServer(String ip, int port) {
        mService.connectToServer(ip, port);
    }
}
