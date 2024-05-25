package com.banledcamung.bicat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    boolean isImgSelected, isTxtSelected;

    List<String> titles;
    List<Bitmap> images;

    List<Integer> slots;
    CustomAdapter adapter;
    Button addBtn;
    SharedPreferences imgSP;
    RelativeLayout layout, selectImgLayout;

    RecyclerView customList;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomFragment newInstance(String param1, String param2) {
        CustomFragment fragment = new CustomFragment();
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
        imgSP = getContext().getSharedPreferences("imgSP",Context.MODE_PRIVATE);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_custom, container, false);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button addBtn = rootView.findViewById(R.id.custom_add_btn);
        layout = rootView.findViewById(R.id.custom_layout);
        //selectImgLayout = rootView.findViewById(R.id.select_img_layout);
        customList = rootView.findViewById(R.id.customlist);
        titles = new ArrayList<>();
        images = new ArrayList<>();
        slots = new ArrayList<>();

        int numberAddedImage = imgSP.getInt("numberAddedImage",0);
        if(numberAddedImage > 0) {
            for (int i = 0; i < numberAddedImage; i++) {
                String isDeleteKey = "isDelete"+i;
                boolean isDeleted = imgSP.getBoolean(isDeleteKey,false);
                if(!isDeleted){ //If this slot isn't deleted
                    String keyImg = "imgByteArray" + i;
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
                        images.add(bitmap);

                        String keyName = "keyName" + i;
                        String name = imgSP.getString(keyName, "");
                        titles.add(name);
                        slots.add(i);
                    }
                }
            }

            adapter = new CustomAdapter(titles, images, slots);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(),2,GridLayoutManager.VERTICAL,false);
            customList.setLayoutManager(gridLayoutManager);
            customList.setAdapter(adapter);
            addBtn.bringToFront();
        }
        addBtn.setOnClickListener(v->{
            Context context = rootView.getContext();
            if (context instanceof MainActivity) {
                ((MainActivity) context).moveToSelectImage();
            }
        });
        return rootView;
    }

    private void showSelectImgLayout() {
    }

    private void createPopupWindow() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_select,null);

        PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT , true);
        layout.post(new Runnable() {
            @Override
            public void run() {
                popupWindow.showAtLocation(layout, Gravity.CENTER,0,0);
            }
        });
    }
}
