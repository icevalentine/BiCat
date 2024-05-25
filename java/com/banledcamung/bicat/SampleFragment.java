package com.banledcamung.bicat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SampleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SampleFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    int tabIndex;
    static int NUMBER_OF_SAMPLE_550 = 40;
    static int NUMBER_OF_SAMPLE_400 = 109;
    static int NUMBER_OF_SAMPLE_280 = 45;
    RecyclerView dataList;
    List<String> titles;
    List<Integer> images;
    List<Integer> tabIndexs;
    Adapter adapter;
    public SampleFragment(int i) {
        // Required empty public constructor
        this.tabIndex = i +1;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SampleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SampleFragment newInstance(int param1, String param2) {
        SampleFragment fragment = new SampleFragment(param1);
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sample, container, false);
        dataList = rootView.findViewById(R.id.datalist);
        titles = new ArrayList<>();
        images = new ArrayList<>();
        int numberOfSample = 0;
        String keyTab;
        if(tabIndex==1){
            keyTab="s";
            numberOfSample = NUMBER_OF_SAMPLE_550;
        } else if (tabIndex==2) {
            keyTab="b";
            numberOfSample = NUMBER_OF_SAMPLE_400;
        } else {
            keyTab="c";
            numberOfSample = NUMBER_OF_SAMPLE_280;
        }
        for(int i = 1; i <=numberOfSample;i++) {
            //String nameIDstr = "s" + i + "_name";
            //int nameID = getResources().getIdentifier(nameIDstr, "string", this.getContext().getPackageName());
            String name = getString(R.string.s_name) + " " + i;
            titles.add(name);
            String imgidStr = keyTab + i + "_img_" + i;
            int imgId = getResources().getIdentifier(imgidStr, "drawable", this.getContext().getPackageName());
            images.add(imgId);

        }
        adapter = new Adapter(titles,images, tabIndex);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(),2,GridLayoutManager.VERTICAL,false);
        dataList.setLayoutManager(gridLayoutManager);
        dataList.setAdapter(adapter);
        return rootView;
    }
}