package com.example.wineryguideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class loginPage extends AppCompatActivity {

    ImageView Back;


    TextView toLogInButton;

    public static Boolean isRegistred=false;

    TextView toRegisterButton;

   View logInLayoutView;

   TextView changePassView;

   View changePassLayout;

   View registerLayoutView;

   Button registerButton;

   DocumentReference userData;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference dataRef = db.collection("Data");

    EditText nameInput;

    EditText emailInput;

    EditText phoneNumberInput;

    EditText passwordInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        changePassLayout = findViewById(R.id.changePassLayout);

        registerButton = findViewById(R.id.Register);

        toLogInButton=findViewById(R.id.toLogIn);

        changePassView= findViewById(R.id.changePassButton);

        toRegisterButton = findViewById(R.id.toRegister);

        Back = findViewById(R.id.backButton);

        logInLayoutView = findViewById(R.id.logInLayout);

        registerLayoutView = findViewById(R.id.registerLayout);

        userData = dataRef.document("User");

        nameInput = findViewById(R.id.name);

        emailInput = findViewById(R.id.email);

        passwordInput = findViewById(R.id.password);

        phoneNumberInput = findViewById(R.id.phoneNumber);




        changePassView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerLayoutView.setVisibility(View.GONE);
                logInLayoutView.setVisibility(View.GONE);
                changePassLayout.setVisibility(View.VISIBLE);
            }
        });


        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();
            }
        });

        toLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerLayoutView.setVisibility(View.GONE);
                logInLayoutView.setVisibility(View.VISIBLE);

            }
        });

        toRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInLayoutView.setVisibility(View.GONE);
                registerLayoutView.setVisibility(View.VISIBLE);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String dataName =nameInput.getText().toString();
                String dataEmail = emailInput.getText().toString();
                String dataPhone = phoneNumberInput.getText().toString();
                String dataPass= passwordInput.getText().toString();

                Map<String,Object> updates = new HashMap<>();

                updates.put("Name",dataName);
                updates.put("email",dataEmail);
                updates.put("password",dataPass);
                updates.put("phone number",dataPhone);

                FirebaseFirestore.getInstance()
                    .collection("Data")
                    .document("User")
                    .update(updates);

                isRegistred=true;

                Intent i = new Intent(loginPage.this , HomePage.class);
                startActivity(i);

            }
        });
    }




}