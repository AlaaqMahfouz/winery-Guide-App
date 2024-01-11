package com.example.wineryguideapp;

import static java.lang.reflect.Array.get;

import androidx.annotation.NonNull;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public  class WineriesPage extends AppCompatActivity {


        ImageButton Back;

        ImageButton profilebutton;


        // framelayout that is nesting the frames
        FrameLayout parentFrame;

        // a view to handle gestures
        View frameLayout;


        private final FirebaseFirestore db = FirebaseFirestore.getInstance();

        private CollectionReference wineriesRef = db.collection("Wineries");

        private CollectionReference servicesRef ;



        private CollectionReference wineriesListRef ;


        private DocumentReference regionRef ;

        ArrayList<String> wineryNames;

        Map<String,ArrayList<String>> wineryServices= new HashMap<>();

        ArrayList<String> wineryImagePaths;

        private int currentWinery=0;

        private int wineriesCount;

        static String regionName;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.wineriespage);

                // get extras
                Intent receivedIntent = getIntent();
                regionName = receivedIntent.getStringExtra("region");

                if(regionName!=null)
                {
                        regionRef= wineriesRef.document(regionName);
                }
                else {
                        Log.d("Attention","region name is Null!");
                }


                wineryNames=new ArrayList<>();

                wineryImagePaths=new ArrayList<>();



                // instance of the view who's going to handle the gestures
                frameLayout = new parentFrameView(this);

                // the main framelayout
                parentFrame = findViewById(R.id.FL);

                // adding the view to the layout to handle gestures
                parentFrame.addView(frameLayout);



                Back = findViewById(R.id.backButton);

                // for the back button to be clickable
                Back.bringToFront();

                profilebutton = (ImageButton) findViewById(R.id.profileButtonImage);

                // referencing the wineries list of the inherited region + initialization
                regionRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                // to check if the onSuccess method is completed before showing the fragment , its set to final and as a single array element because inner classes like onSUccess doesn't let you edit variables inside it unless they are final
                                final int[] currentWineryServiceIndex = {0};


                                wineriesListRef = documentSnapshot.getReference().collection("WineriesList");

                                // sort wineries by name so they can be ordered as the services
                                Query wineriesQuery = wineriesListRef.orderBy("__name__", Query.Direction.ASCENDING);


                                wineriesQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                                if(!queryDocumentSnapshots.isEmpty())
                                                {

                                                                wineriesCount = queryDocumentSnapshots.size();

                                                        for(DocumentSnapshot document : queryDocumentSnapshots)
                                                        {

                                                                String currentWinery=document.getId();

                                                                wineryNames.add(document.getId());

                                                                // services collection
                                                                servicesRef = document.getReference().collection("Services");


                                                                wineryImagePaths.add((String) document.get("ImagePath"));

                                                                // loop through services of each winery
                                                                servicesRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                                                                        ArrayList<String> servicesList =new ArrayList<>();

                                                                        @Override
                                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                                                                for( DocumentSnapshot document : queryDocumentSnapshots)
                                                                                {
                                                                                        // fil the array list with services
                                                                                        servicesList.add(document.getId());
                                                                                }
                                                                                // put the list just filled along with the nam eof the its winery
                                                                                wineryServices.put(currentWinery,servicesList);

                                                                                // check if OnSuccess method is completed
                                                                                if(currentWineryServiceIndex[0] ==wineryNames.size()-1)
                                                                                {
                                                                                        showFragment(WineriesWindow.newInstance(wineryNames.get(0),wineryImagePaths.get(0),wineryServices.get(wineryNames.get(0))));
                                                                                }

                                                                                currentWineryServiceIndex[0]++;

                                                                        }

                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                                Log.d("MyApp","failed..");
                                                                        }
                                                                });

                                                        }


                                                }


                                        }
                                });

                        }
                });


                // handle profile button click
                profilebutton.setOnClickListener(new View.OnClickListener(){

                        public void onClick(View v) {
                                Intent intent = new Intent(WineriesPage.this, loginPage.class);
                                startActivity(intent);
                        }
                });


                // handle back button click
                Back.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                               finish();
                        }
                });

        }



        // view to be added to card view to handle gestures
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


                                        if (currentWinery < wineriesCount-1)
                                                currentWinery++;
                                        else
                                                currentWinery = 0;

                                        // left to right swipe
                                } else {
                                        if (currentWinery > 0)
                                                currentWinery--;
                                        else
                                                currentWinery = wineriesCount-1;
                                }


                        }

                        showFragment(WineriesWindow.newInstance(wineryNames.get(currentWinery),wineryImagePaths.get(currentWinery),wineryServices.get(wineryNames.get(currentWinery))));
                        return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                        return true;
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {

                        return true;
                }
        }




}
