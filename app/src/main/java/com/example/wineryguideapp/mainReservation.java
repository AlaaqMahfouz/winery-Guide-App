package com.example.wineryguideapp;

import static androidx.core.content.ContextCompat.startActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class mainReservation extends AppCompatActivity {


    ImageButton Back ;

    ImageButton profilebutton;


    ArrayList<String> servicesAvailable;

    ArrayList<CheckBox> servicesCheckBoxes;

    ArrayList<String> checkedServicesText= new ArrayList<>();

    CheckBox service1;
    CheckBox service2;
    CheckBox service3;


    Timestamp date;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private  CollectionReference servicesRef;
    String currentWineryName;

    Button confirButton;

    static ArrayList<String> checkedServices = new ArrayList<>();



    int numOfServices=0;

    Button selectDateButton;

    CardView materialCardView;

    String region = WineriesPage.regionName;

    EditText peopleNumInput ;

    int peopleNum=0;

    Boolean dateIsSelected=false;

    HashMap<String,Object> data;



    FirestoreFetcher fetcher = new FirestoreFetcher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_reservation);

        Intent i = getIntent();

        peopleNumInput= findViewById(R.id.peopleNum);
        materialCardView = findViewById(R.id.cardView);
        selectDateButton = findViewById(R.id.selectDate);


        confirButton= findViewById(R.id.confirm);

//        dateInput = findViewById(R.id.date);

        // get services of winery from extra
        servicesAvailable = i.getStringArrayListExtra("services");

        currentWineryName = i.getStringExtra("winery name");

        servicesCheckBoxes=new ArrayList<>();

        // initialize checkboxes
        service1=findViewById(R.id.service1box);
        service2 = findViewById(R.id.service2box);
        service3 =findViewById(R.id.service3box);

        // checkboxes of services
        servicesCheckBoxes.add(service1);
        servicesCheckBoxes.add(service2);
        servicesCheckBoxes.add(service3);




    selectDateButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dateIsSelected=true;
            Intent i = new Intent(mainReservation.this,datePicker.class);
            startActivity(i);
        }
    });




        if(servicesAvailable.size()!=0)
        {
            // fill services in ckecboxes
            for(int j=0;j<servicesAvailable.size();j++)
            {
                servicesCheckBoxes.get(j).setText(servicesAvailable.get(j));
            }

            for(int j=0;j<servicesCheckBoxes.size();j++)
            {
                if(servicesCheckBoxes.get(j).getText().equals(""))
                {
                    servicesCheckBoxes.get(j).setVisibility(View.GONE);
                }
            }


        }

        Back = findViewById(R.id.backButton);

        Back.bringToFront();

        profilebutton = (ImageButton) findViewById(R.id.profileButtonImage);


        profilebutton.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                Intent intent = new Intent(mainReservation.this, loginPage.class);
                startActivity(intent);
            }
        });

        Back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }

        });



        confirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!dateIsSelected)
                {
                    Toast.makeText(mainReservation.this, "Please select a date for your reservation.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(peopleNumInput.getText().toString().isEmpty())
                {
                    peopleNumInput.setError("this field is required");
                    return;
                }

                String peopleNumString = peopleNumInput.getText().toString();

                peopleNum= Integer.parseInt(peopleNumString);


                for(int j=0;j<servicesAvailable.size();j++)
                {

                    if(servicesCheckBoxes.get(j).isChecked())
                    {
                        checkedServicesText.add((String) servicesCheckBoxes.get(j).getText());
                        numOfServices++;
                        Log.d("MyAPp","one service is checked..");
                        checkedServices.add((String) servicesCheckBoxes.get(j).getText());
                    }

                }

                if(numOfServices==0)
                {
                    Toast.makeText(mainReservation.this, "Please select at least one service for your reservation.", Toast.LENGTH_SHORT).show();
                    return;
                }


                servicesRef = db.collection("WineriesList").document(currentWineryName).collection("Services");


                date = datePicker.timeStamp;



                   fetcher.updateFirestore(peopleNum,currentWineryName,date,checkedServices,region,materialCardView.getContext());


                    }
                });


    }

           static  public void clearServices(){


               checkedServices.clear();
            }

           }






