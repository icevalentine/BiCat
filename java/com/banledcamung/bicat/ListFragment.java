package com.banledcamung.bicat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    ViewPager2 viewPager;
    PagerAdapter adapter;

    SharedPreferences tabSP;
    Context ctx;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListFragment(Context ctx) {
        // Required empty public constructor
        this.ctx = ctx;
    }

    public ListFragment(){

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        tabSP = getContext().getSharedPreferences("tabSP",Context.MODE_PRIVATE);
        int tabindex = tabSP.getInt("tabindex",0);
        viewPager = rootView.findViewById(R.id.viewPager);
        adapter = new PagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(tabindex, false);
        TabLayout tabLayout = rootView.findViewById(R.id.tabLayout);
        try {
            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText(R.string.tab0);
                        break;
                    case 1:
                        tab.setText(R.string.tab1);
                        break;
                    case 2:
                        tab.setText("D280");
                        break;
                    case 3:
                        tab.setText("Custom");
                    default:
                }

            }).attach();
        } catch (Exception e){
            e.printStackTrace();
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                SharedPreferences.Editor editor = tabSP.edit();
                editor.putInt("tabindex",tab.getPosition());
                editor.apply();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                SharedPreferences.Editor editor = tabSP.edit();
                editor.putInt("tabindex",tab.getPosition());
                editor.apply();
            }
        });

//        titles = new ArrayList<>();
//        images = new ArrayList<>();
//        for(int i = 1; i <=NUMBER_OF_SAMPLE;i++){
//            String nameIDstr = "s" + i+"_name";
//            int nameID = getResources().getIdentifier(nameIDstr, "string", this.getContext().getPackageName());
//            String name = getString(nameID);
//            titles.add(name);
//            //titles.add(name);
//            String imgidStr = "s"+i+ "_img_"+i;
//            int imgId = getResources().getIdentifier(imgidStr, "drawable", this.getContext().getPackageName());
//            images.add(imgId);
////            String bimgidStr = "b"+i+ "_img_"+i;
////            int bimgId = getResources().getIdentifier(bimgidStr, "drawable", this.getContext().getPackageName());
////            images.add(bimgId);
//        }
//
//        // Inflate the layout for this fragment
//
//        adapter = new Adapter(titles,images);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(),2,GridLayoutManager.VERTICAL,false);
//        dataList.setLayoutManager(gridLayoutManager);
//        dataList.setAdapter(adapter);
        return rootView;
    }

    public void showSelectPopup(){

    }

}