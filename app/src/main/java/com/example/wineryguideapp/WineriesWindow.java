package com.example.wineryguideapp;

import static android.view.View.GONE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WineriesWindow#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WineriesWindow extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM3 = null;

    private static final String ARG_PARAM2 ="path";

    // TODO: Rename and change types of parameters
    private String wineryName;
    private ArrayList<String> wineryServices;

    private String wineryImagePath;

    private TextView Title;

    private TextView[] servicesView;

    private ImageView imageToShow;

    private StorageReference storageReference;

    private Button registerButton;

    private Button tripButton;

    public WineriesWindow() {
        // Required empty public constructor
    }


    public static WineriesWindow newInstance(String wineryname, String imagePath , ArrayList<String> services) {
        WineriesWindow fragment = new WineriesWindow();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, wineryname);
        args.putString(ARG_PARAM2, imagePath);
        args.putStringArrayList(ARG_PARAM3,services);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            wineryName = getArguments().getString(ARG_PARAM1);
            wineryImagePath = getArguments().getString(ARG_PARAM2);
            wineryServices=getArguments().getStringArrayList(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.winerieswindow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // initialising view components

        storageReference = FirebaseStorage.getInstance().getReference().child(wineryImagePath);

        Title= view.findViewById(R.id.Title);

        imageToShow = view.findViewById(R.id.clickableImage1);

        servicesView= new TextView[3];

        servicesView[0]=view.findViewById(R.id.Service1);
        servicesView[1]=view.findViewById(R.id.Service2);
        servicesView[2]=view.findViewById(R.id.Service3);

        tripButton = view.findViewById(R.id.Trip);

        registerButton = view.findViewById(R.id.Register);

        // assigning values to view components

        Title.setText(wineryName);

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
                Log.e("MyApp", "Error downloading image:", exception);
            }
        });

        // show services names
        for(int i=0;i<wineryServices.size();i++)
        {
            servicesView[i].setText(wineryServices.get(i));
        }
        // set the visibility of emtpy services to gone so it doesn't take space on the screen
        for(int i=0;i<3;i++)
        {
            if (servicesView[i].getText().equals(""))
            {
                servicesView[i].setVisibility(GONE);
            }
        }


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i;
                if(!loginPage.isRegistred)
                {
                    i=new Intent(getContext(),loginPage.class);
                }
                else {
                    i= new Intent(getContext(),mainReservation.class);
                    i.putExtra("services",wineryServices);
                    i.putExtra("winery name",wineryName);
                }

            startActivity(i);
            }
        });


    }
}