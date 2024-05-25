package com.banledcamung.bicat;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.graphics.Bitmap;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectImageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Button backBtn, okBtn;
    final String filekey = "filekey";
    ImageView selectedImg, selectedFile;
    View rootView;
    int numberAddedImage;
    boolean isImgSelected;
    boolean isTxtSelected;

    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<String> pickTextFileLauncher;

    SharedPreferences fileSP;
    SharedPreferences imgSP;

    TextView filenameText;

    EditText nameEtxt;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SelectImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectImageFragment newInstance(String param1, String param2) {
        SelectImageFragment fragment = new SelectImageFragment();
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
        fileSP = getContext().getSharedPreferences("fileSP",Context.MODE_PRIVATE);
        imgSP = getContext().getSharedPreferences("imgSP", Context.MODE_PRIVATE);
        numberAddedImage = imgSP.getInt("numberAddedImage",0);
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleImageUri);
        pickTextFileLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::hanleTextFileUri);
    }

    private void hanleTextFileUri(Uri textUri) {
        if(textUri != null){
            String textUriPath = getFileNameFromUri(textUri);
            int cut = textUriPath.lastIndexOf('/');
            if(cut != -1) {textUriPath = textUriPath.substring(cut + 1);}
            filenameText.setText(textUriPath);
            selectedFile.setImageResource(R.drawable.ic_launcher_background);
            isTxtSelected = true;
            try {
                InputStream inputStream = rootView.getContext().getContentResolver().openInputStream(textUri);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                int li = 0;
                SharedPreferences.Editor editor = fileSP.edit();
                while ((line = reader.readLine()) != null) {
                    String key = filekey + numberAddedImage + ";" + li;
                    editor.putString(key, line);
                    li++;
                    //stringBuilder.append(line);
                    //stringBuilder.append("\n");
                }

                String numberofline = filekey + numberAddedImage + "numberOfLine";
                editor.putInt(numberofline, li);
                editor.apply();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if(uri.getScheme() != null && uri.getScheme().equals("content")){
            try(android.database.Cursor cursor = rootView.getContext()
                    .getContentResolver().query(uri, null, null, null, null)){
                if(cursor != null && cursor.moveToFirst()){
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if(index != -1){
                        result=cursor.getString(index);
                    }
                }
            }
        }
        if(result==null){
            result=uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut!=-1){
                result = result.substring(cut+1);
            }
        }
        return result;
    }

    private void handleImageUri(Uri imageUri) {
        if(imageUri != null){
            selectedImg.setImageURI(imageUri);
            isImgSelected = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_select_image, container, false);
        backBtn = rootView.findViewById(R.id.back_btn);
        okBtn = rootView.findViewById(R.id.ok_select_btn);
        selectedImg = rootView.findViewById(R.id.selected_img);
        selectedFile = rootView.findViewById(R.id.selected_file);
        filenameText = rootView.findViewById(R.id.filenametxt);
        nameEtxt = rootView.findViewById(R.id.file_name_text);
        backBtn.setOnClickListener(v->{
            Context context = rootView.getContext();
            if (context instanceof MainActivity) {
                ((MainActivity) context).moveToListFragment();
            }
        });

        selectedImg.setOnClickListener(v->{
            pickImageLauncher.launch("image/*");
        });

        selectedFile.setOnClickListener(v->{
            pickTextFileLauncher.launch("text/plain");
        });
        okBtn.setOnClickListener(v->{
            String titleStr = nameEtxt.getText().toString();
            if(isTxtSelected && isImgSelected){
                int addedSlot = numberAddedImage;
                selectedImg.setDrawingCacheEnabled(true);
                selectedImg.buildDrawingCache();
                Bitmap bitmap = selectedImg.getDrawingCache();
                bitmap = Bitmap.createScaledBitmap(bitmap,400,400,false);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                SharedPreferences.Editor editor = imgSP.edit();
                String keyImg = "imgByteArray" + addedSlot;
                editor.putString(keyImg, Arrays.toString(byteArray));

                String keyName = "keyName" + addedSlot;
                editor.putString(keyName,titleStr);

                numberAddedImage++;
                editor.putInt("numberAddedImage", numberAddedImage);


                editor.apply();
                Context context = rootView.getContext();
                if (context instanceof MainActivity) {
                    ((MainActivity) context).moveToListFragment();
                }
            }
        });
        return rootView;
    }
}