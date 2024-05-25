package com.banledcamung.bicat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    ImageView avatar;
    TextView title;

    ToggleButton runBtn;
    int imgID;
    String titleText;
    Button goToListBtn;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public HomeFragment(int id, String text){
        this.imgID = id;
        this.titleText = text;
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        avatar = rootView.findViewById(R.id.avatar2);
        title = rootView.findViewById(R.id.home_title);
        SharedPreferences ItemData = getContext().getSharedPreferences("ItemData", Context.MODE_PRIVATE);
        imgID = ItemData.getInt("imgID",0);
        titleText = ItemData.getString("titleText","");

        goToListBtn = rootView.findViewById(R.id.moveToList);
        goToListBtn.setOnClickListener(v->{
            Context context = rootView.getContext();
            if (context instanceof MainActivity) {
                ((MainActivity) context).moveToListFragment();
            }
        });
        if(imgID < 0) {
            int position = -1 - imgID;
            SharedPreferences imgSP = getContext().getSharedPreferences("imgSP", Context.MODE_PRIVATE);
            String keyImg = "imgByteArray" + position;
            String imageByteArrayString = imgSP.getString(keyImg, "");
            if (!imageByteArrayString.isEmpty()) {
                String[] byteArrayStrings = imageByteArrayString.substring(1, imageByteArrayString.length() - 1).split(",");
                byte[] byteArray = new byte[byteArrayStrings.length];

                for (int j = 0; j < byteArrayStrings.length; j++) {
                    try {
                        byteArray[j] = Byte.parseByte(byteArrayStrings[j].trim());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                avatar.setImageBitmap(bitmap);
            }
            String keyName = "keyName" + position;
            titleText = imgSP.getString(keyName, "");
            } else {
                avatar.setImageResource(imgID);
            }
        if ("".equals(titleText)) {
            title.setText("No Item Selected");
            goToListBtn.setEnabled(true);
        } else {
            title.setText("" + titleText);
        }


        return rootView;
    }



    public void setAvatar(int id){
    }

    public void moveTo(View view) {
        Context context = this.getContext();
        try {
            if (context instanceof MainActivity) {
                ((MainActivity) context).replaceFragment(new ListFragment());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}