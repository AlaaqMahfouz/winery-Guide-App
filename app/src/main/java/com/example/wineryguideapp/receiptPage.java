package com.example.wineryguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class receiptPage extends AppCompatActivity {

    TextView userNameText;

    TextView wineryNameText;

    TextView dateText;

    TextView spots;

    TextView totalCost;

    Button doneButton;

    FirebaseFirestore db=FirebaseFirestore.getInstance();

    CollectionReference reservationRef =db.collection("Reservation");

    ArrayList<String> services=new ArrayList<>();

    ArrayAdapter<String> adapter ;

    DocumentReference newReservationDoc ;

    ListView servicesList;

    String newReservationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_page);

        Intent i = getIntent();

        // get te id of the new made document in reservation
       newReservationId= i.getStringExtra("new reservation");


        userNameText =findViewById(R.id.userName);

        wineryNameText = findViewById(R.id.wineryName);

        servicesList=findViewById(R.id.ServicesListView);


        dateText = findViewById(R.id.date);

        spots=findViewById(R.id.Spots);

        totalCost=findViewById(R.id.TotalCost);

        doneButton=findViewById(R.id.done);


       // check if its null
        assert newReservationId != null;

        newReservationDoc=reservationRef.document(newReservationId);


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        newReservationDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                DocumentReference userDoc= (DocumentReference) documentSnapshot.get("User");

                userDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String userName = (String) documentSnapshot.get("Name");
                        userNameText.setText(userName);
                    }
                });

                DocumentReference wineryRef = (DocumentReference) documentSnapshot.get("Winery");

                wineryRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String wineryName = documentSnapshot.getId();
                            wineryNameText.setText(wineryName);
                    }
                });

                // get date and trasnform it to well formated string for display purposes
                Date date = documentSnapshot.getTimestamp("Due-Date").toDate();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                String dateString = dateFormat.format(date);


                dateText.setText(dateString);

                spots.setText( documentSnapshot.get("Spots").toString());

                totalCost.setText( (documentSnapshot.get("Total Cost").toString()) + "$");

                services = (ArrayList<String>) documentSnapshot.get("Services");

                // fill services in array list inside the list view
                adapter = new ArrayAdapter<>(receiptPage.this,R.layout.list_item, services);

                servicesList.setAdapter(adapter);


            }
        });




    }
}