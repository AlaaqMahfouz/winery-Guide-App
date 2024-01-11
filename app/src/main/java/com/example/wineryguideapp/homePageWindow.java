package com.example.wineryguideapp;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homePageWindow#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homePageWindow extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3="param3";

    private String regionName;

    private ArrayList<String> wineriesList;

    private String imagePath;

    TextView[] wineriesTextViews;

    StorageReference storageReference;

    ImageView imageToShow;


    public homePageWindow() {
        // Required empty public constructor
    }

    /**
     * @param region Parameter 1.
     * @param wineries Parameter 2.
     */
    public static homePageWindow newInstance(String region, ArrayList<String> wineries, String iamgePath) {
        homePageWindow fragment = new homePageWindow();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, region);
        args.putStringArrayList(ARG_PARAM2,wineries);
        args.putString(ARG_PARAM3,iamgePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get arguments
        if (getArguments() != null) {
            regionName = getArguments().getString(ARG_PARAM1);
            wineriesList = getArguments().getStringArrayList(ARG_PARAM2);
            imagePath = getArguments().getString(ARG_PARAM3);
        }

        storageReference = FirebaseStorage.getInstance().getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.homepagewindow, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable  Bundle savedInstanceState) {

        TextView title = view.findViewById(R.id.Title); // Access the region name textview

        title.setText(regionName);

        // reference the text views that will contain the wineries names
        wineriesTextViews= new TextView[5];

        wineriesTextViews[0]=view.findViewById(R.id.Winery1);
        wineriesTextViews[1]=view.findViewById(R.id.Winery2);
        wineriesTextViews[2]=view.findViewById(R.id.Winery3);
        wineriesTextViews[3]=view.findViewById(R.id.Winery4);
        wineriesTextViews[4]=view.findViewById(R.id.Winery5);

        if(wineriesList!=null) {

               for(int i=0;i<wineriesList.size();i++)
               {
                   wineriesTextViews[i].setText(wineriesList.get(i));
               }
        }
        else {
            Log.d("MyApp","wineries List is null");
        }

        // reference the image view
        imageToShow  = view.findViewById(R.id.clickableImage1);

        // get the image path from firestore storage
        storageReference = FirebaseStorage.getInstance().getReference().child(imagePath);

        // load the image into the image view
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(requireContext())
                        .load(uri)
                        .into(imageToShow);
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {
                // Log the error or display a user-friendly message
                Log.d("MyApp", "Error downloading image:", exception);
            }
        });
    }

}