package com.banledcamung.bicat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    EditText wifiNameEdtx, wifiPasswordEdtx, macTxt, ipEdtx, portEdtx, numberSentLineTxt;

    Button confirmBtn;
    Handler handler;

    private ClientThread clientThread;

    TextView statusTxt;
    private Thread thread;
    Socket mSocket;
    private BufferedReader in;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        wifiNameEdtx = rootView.findViewById(R.id.wifi_name);
        wifiPasswordEdtx = rootView.findViewById(R.id.wifi_password);
        ipEdtx = rootView.findViewById(R.id.ip);
        portEdtx = rootView.findViewById(R.id.port);
        macTxt = rootView.findViewById(R.id.mac);
        numberSentLineTxt = rootView.findViewById(R.id.numberSentLine);
        confirmBtn = rootView.findViewById(R.id.confirm_button);
        statusTxt = rootView.findViewById(R.id.setting_status_text);
        handler = new Handler();
        SharedPreferences IPPort = getContext().getSharedPreferences("IPPort", Context.MODE_PRIVATE);
        String curIP = IPPort.getString("IP","192.168.1.255");
        ipEdtx.setText(curIP);
        int curPort = IPPort.getInt("Port",3333);
        portEdtx.setText(""+curPort);
        String curSSID = IPPort.getString("SSID","");
        wifiNameEdtx.setText(curSSID);
        String curPass = IPPort.getString("Pass","");
        wifiPasswordEdtx.setText(curPass);
        String curMac = IPPort.getString("mac","");
        macTxt.setText(curMac);
        int numberSentLine = IPPort.getInt("numberSentLine",1000);
        numberSentLineTxt.setText(""+numberSentLine);
        confirmBtn.setOnClickListener(v->{

            SharedPreferences.Editor editor = IPPort.edit();
            String ip = ipEdtx.getText().toString();

            String portStr = portEdtx.getText().toString();
            String numberSentLineStr = numberSentLineTxt.getText().toString();
            if(!"".equals(ip)){
                editor.putString("IP",ip);
            }
            if(!"".equals(portStr)){
                int port = Integer.parseInt(portStr);
                editor.putInt("Port",port);
            }
            if(!"".equals(numberSentLineStr)){
                int newNumberSentLine = Integer.parseInt(numberSentLineStr);
                if(newNumberSentLine < 1) {
                    newNumberSentLine = 1;
                    numberSentLineTxt.setText("1");
                } else if (newNumberSentLine > 100000){
                    newNumberSentLine = 100000;
                    numberSentLineTxt.setText("100000");
                }
                editor.putInt("numberSentLine",newNumberSentLine);
            }

            String SSID = wifiNameEdtx.getText().toString().trim();
            String Pass = wifiPasswordEdtx.getText().toString().trim();
            String Mac = macTxt.getText().toString().trim();

            if("".equals(SSID)) {
                Toast.makeText(getActivity(), "Vui lòng nhập tên Wifi", Toast.LENGTH_SHORT).show();
            } else if ("".equals(Mac)){
                Toast.makeText(getActivity(), "Vui lòng nhập MAC", Toast.LENGTH_SHORT).show();
            } else {
                editor.putString("SSID", SSID);
                editor.putString("Pass", Pass);
                editor.putString("mac",Mac);
                editor.apply();

                StringBuilder sb = new StringBuilder();
                sb.append("Manual Config").append(";;").append(SSID).append(";;").append(Pass);
                clientThread = new ClientThread(sb.toString());
                thread = new Thread(clientThread);
                thread.start();

                statusTxt.setText("Saved");
            }

        });

        return rootView;
    }

    class ClientThread implements Runnable{
        String mess;

        public ClientThread(String toString) {
            mess = toString;
        }

        @Override
        public void run() {
            //String esp32IPAddress = ipEdtx.getText().toString();
            String esp32IPAddress = "192.168.4.1";
           // int port = Integer.parseInt(portEdtx.getText().toString());
            int port = 3333;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (mSocket == null) {
                        mSocket = new Socket(esp32IPAddress, port);
                        sendMessage(mess);
                    }
                    InputStreamReader inputStreamReader = new InputStreamReader(mSocket.getInputStream());
                    in = new BufferedReader(inputStreamReader);
                    String mess = in.readLine();
                    if (mess == null) {
                        boolean interrupted = Thread.interrupted();
                        break;
                    } else {

                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    handler.post(() -> statusTxt.setText("" + e));

                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(() -> statusTxt.setText("" + e));
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(() -> statusTxt.setText("" + e));
                }
            }
        }

        public void sendMessage(String data) {
            new Thread(()->{
                if(mSocket!=null){
                    try {
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())),true);
                        out.println(data);
                      //  handler.post(()->status.setText("Success"));
                    } catch (IOException e) {
                        e.printStackTrace();
                     //   handler.post(()->status.setText(e.toString()));
                    }
                }
            }).start();
        }
    }
}