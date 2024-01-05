package com.example.wineryguideapp;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public  class HomePage extends AppCompatActivity {



    // pageNum for indicating which page is currently open

    // storing the pages of Regions
    ArrayList<String> mainActivities;

    // profileButton to handle click
    ImageButton profilebutton;

    // framelayout that is nesting the frames
    FrameLayout parentFrame;

    // a view to handle gestures
    View frameLayout;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
   private CollectionReference regionRef = db.collection("Regions");

    private CollectionReference dataRef = db.collection("Data");

    private DocumentReference userRef = dataRef.document("User");

   //reference which document in the firestore database to be shown
   public DocumentSnapshot DocumentToShow;

   // the num of the collections
   public int collectionsCount;


   public int currentCollection=0;

   // store wineries per region
   HashMap<String,ArrayList<String>> wineriesPerRegions;

   // array list of wineries names for each region
   ArrayList<String> wineriesNames;

   // store image path for each region
   HashMap<String,String> imagePathPerRegion;

   // region Name
   String region;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        // initialising
        regionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty())
                {

                    collectionsCount = queryDocumentSnapshots.size();

                    wineriesPerRegions = new HashMap<>();

                    imagePathPerRegion = new HashMap<>();

                    DocumentToShow= queryDocumentSnapshots.getDocuments().get(currentCollection);

                    region = DocumentToShow.getId();


                    fillWineriesPerRegions();


                }else {
                    Log.d("TAG", "No documents found in regionRef");
            }
        }});


        // instance of the view who's going to handle the gestures
        frameLayout = new parentFrameView(this);

        // the main framelayout
        parentFrame = findViewById(R.id.FL);

        // adding the view to the layout to handle gestures
        parentFrame.addView(frameLayout);


        mainActivities = new ArrayList<>();


        profilebutton = (ImageButton) findViewById(R.id.profileButtonImage);

        // opening profile page
        profilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i;
                if(!loginPage.isRegistred)
                {
                     i = new Intent(HomePage.this, loginPage.class);
                }
                else {
                     i = new Intent(HomePage.this,userPage.class);
                }

                startActivity(i);

            }
        });

        // store references to Wineries

        mainActivities.add("com.example.wineryguideapp.MainActivity1");
        mainActivities.add("com.example.wineryguideapp.MainActivity3");
        mainActivities.add("com.example.wineryguideapp.MainActivity1");
        mainActivities.add("com.example.wineryguideapp.MainActivity3");



        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back press within the fragment
               System.exit(0);
            }
        });

        // check if the user is logged in
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshots) {

                if(documentSnapshots.exists())
                {
                    String dataName=documentSnapshots.getString("Name");
                    String dataEmail = documentSnapshots.getString("email");
                    String dataPhoneNumber = documentSnapshots.getString("phone number");

                    if(dataName!=null && dataEmail!=null && dataPhoneNumber!=null)
                       loginPage.isRegistred=true;
                }

                else{
                    Log.d("Error","failed to load from database");
                }



            }
        });

    }
    
    
    public void fillWineriesPerRegions(){



        regionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                // looping throught documents which are regions
                for (DocumentSnapshot document:queryDocumentSnapshots) {

                    wineriesNames= (ArrayList<String>) document.get("Wineries");

                     String documentImagePath = (String) document.get("imagePath");

                     // ID is the name of the region
                    String regionID=document.getId();

                    wineriesPerRegions.put(regionID, wineriesNames);

                    imagePathPerRegion.put(regionID,documentImagePath);


                }

                showFragment(homePageWindow.newInstance(region,wineriesPerRegions.get(region),imagePathPerRegion.get(region)));


            }
        });





    }

    // right to left swipe handle
    public void switchRegionUp(){


        if(currentCollection<collectionsCount-1) {

            currentCollection++;
            Log.d("MyApp","up");

        }
        else
        {
            currentCollection=0;


        }



        regionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                DocumentToShow= queryDocumentSnapshots.getDocuments().get(currentCollection);

                region = DocumentToShow.getId();

                // show the next fragment
                showFragment(homePageWindow.newInstance(region,wineriesPerRegions.get(region),imagePathPerRegion.get(region)));

            }
        });

    }


    // handel left to right swipe
    public void switchRegionDown(){

        if(currentCollection>0)
        {
            currentCollection--;
            Log.d("MyApp","down");
        }

        else
        {
            currentCollection=collectionsCount-1;

        }

        regionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                DocumentToShow= queryDocumentSnapshots.getDocuments().get(currentCollection);

                region = DocumentToShow.getId();

                // show the next of previous fragment
                showFragment(homePageWindow.newInstance(region,wineriesPerRegions.get(region),imagePathPerRegion.get(region)));

            }
        });

    }


    // switching between fragment (regions)
    public void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in,
                R.anim.slide_in

        );
        fragmentTransaction.replace( R.id.FL, fragment);
        fragmentTransaction.addToBackStack("tag");
        fragmentTransaction.commit();

    }


    // opening the page of wineries of each region
    public void switchActivity (String region) {

        Intent i = new Intent(HomePage.this , WineriesPage.class);
        i.putExtra("region",region);
        startActivity(i);

    }



    // View added to frame layout to handle gestures
    public class parentFrameView extends View implements GestureDetector.OnGestureListener {


        int minDistance = 150;

        private GestureDetector gestureDetector;


        public parentFrameView(Context context) {

            super(context);

            gestureDetector = new GestureDetector(context, this);

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // distance of swipe
            float distanceX = e2.getX() - e1.getX();

            // checking if distance is valid
            if (Math.abs(distanceX) > minDistance) {

                // right to left swipe
                if (distanceX < 0) {

                    switchRegionUp();

                // left to right swipe
                } else {
                    switchRegionDown();
                }

            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // Handle long press event here
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Handle scroll event here
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            // Handle show press event here
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            //open the page on wineries of a region
            switchActivity(region);

            return true;
        }
    }


}


