package com.example.wineryguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class userPage extends AppCompatActivity {

    private TextView nameText;

    private TextView emailText;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dataRef = db.collection("Data");

    private DocumentReference document = dataRef.document("User");
    private TextView phoneNumberText;

   static public String dataName;

    private String dataEmail;

    private String dataPhoneNumber;

    private Button signOutButton;

     private ImageButton Back;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);


        nameText = findViewById(R.id.nameText);

        emailText = findViewById(R.id.emailText);

        phoneNumberText = findViewById(R.id.phoneNumber);

        signOutButton = findViewById(R.id.signOut);

        Back = findViewById(R.id.backButton);

        // for the back button to be clickable
        Back.bringToFront();



        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshots) {

            if(documentSnapshots.exists())
            {
                dataName=documentSnapshots.getString("Name");
                dataEmail = documentSnapshots.getString("email");
                dataPhoneNumber = documentSnapshots.getString("phone number");

                nameText.setText(dataName);

                emailText.setText(dataEmail);

                phoneNumberText.setText(dataPhoneNumber);
            }

            else{
                Log.d("Error","failed to load from database");
            }



            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginPage.isRegistred=false;

                Map<String,Object> updates = new HashMap<>();

                updates.put("Name",null);
                updates.put("email",null);
                updates.put("password",null);
                updates.put("phone number",null);

                FirebaseFirestore.getInstance()
                        .collection("Data")
                        .document("User")
                        .update(updates);

                Intent intent = new Intent(userPage.this, HomePage.class);
                startActivity(intent);
            }
        });

        Back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


}