package com.example.wineryguideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class loginPage extends AppCompatActivity {

    // back button
    ImageView Back;

    TextView toLogInButton;

    // check if a user is already registered
    public static Boolean isRegistred=false;

    TextView toRegisterButton;

    // inflate the log in view
   View logInLayoutView;

   View registerLayoutView;

   Button registerButton;

   DocumentReference userData;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    // get user infos
    private CollectionReference dataRef = db.collection("Data");

    CollectionReference usersDataRef =dataRef.document("Users").collection("Users Data");

    EditText nameInput;

    EditText emailInputRegistration;

    EditText phoneNumberInput;

    EditText passwordInputRegistration;

    Button logInButton;

    EditText emailInputLogin;

    EditText passwordInputLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);


        emailInputLogin = findViewById(R.id.emailLogin);

        passwordInputLogin = findViewById(R.id.passwordLogin);

        logInButton=findViewById(R.id.Login);

        registerButton = findViewById(R.id.Register);

        toLogInButton=findViewById(R.id.toLogIn);

        toRegisterButton = findViewById(R.id.toRegister);

        Back = findViewById(R.id.backButton);

        logInLayoutView = findViewById(R.id.logInLayout);

        registerLayoutView = findViewById(R.id.registerLayout);

        userData = dataRef.document("User");

        nameInput = findViewById(R.id.name);


        emailInputRegistration = findViewById(R.id.email);


        passwordInputRegistration = findViewById(R.id.password);


        phoneNumberInput = findViewById(R.id.phoneNumber);

        registerButton.setEnabled(false);

        logInButton.setEnabled(false);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(nameIsValid() && phoneNumberIsValid() && emailIsValid() && passwordIsValid())
                {
                    registerButton.setEnabled(true);
                }
                else{
                    registerButton.setEnabled(false);
                }

            }

        };

        TextWatcher textWatcher1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(emailLogInIsValid() && passwordInputIsValid())
                {
                    logInButton.setEnabled(true);
                }
                else{
                    logInButton.setEnabled(false);
                }
            }
        };

        phoneNumberInput.addTextChangedListener(textWatcher);
        nameInput.addTextChangedListener(textWatcher);
        emailInputRegistration.addTextChangedListener(textWatcher);
        passwordInputRegistration.addTextChangedListener(textWatcher);

        emailInputLogin.addTextChangedListener(textWatcher1);
        passwordInputLogin.addTextChangedListener(textWatcher1);



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

                // get infos from the input fields
                String dataName =nameInput.getText().toString();
                String dataEmail = emailInputRegistration.getText().toString();
                String dataPhone = phoneNumberInput.getText().toString();
                String dataPass= passwordInputRegistration.getText().toString();


                usersDataRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots)
                        {
                            if(document.getId().equals(dataName) || document.get("email").equals(dataEmail) || document.get("phone number").equals(dataPhone))
                            {
                                Toast.makeText(loginPage.this,"Email or User Name already Exist",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else{
                                Map<String,Object> updates = new HashMap<>();

                                updates.put("Name",dataName);
                                updates.put("email",dataEmail);
                                updates.put("password",dataPass);
                                updates.put("phone number",dataPhone);

                                // add infos to User document
                                db.collection("Data")
                                        .document("User")
                                        .update(updates);

                                // create a new document
                                DocumentReference newUserDataRef = usersDataRef.document(dataName);

                                // fill its fields
                                newUserDataRef.set(updates);

                                // check if the user is registred upon reservation request
                                isRegistred=true;

                                finish();

                            }
                        }
                    }
                });


            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                usersDataRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots)
                        {

                            Log.d("MyAPp","email = " + emailInputLogin.getText());
                            Log.d("MyAPp","password = " + passwordInputLogin.getText());
                            Log.d("MyAPp","email in firestore= " + document.get("email"));
                            Log.d("MyAPp","password in firestore = " + document.get("password"));



                            if(document.get("email").equals(emailInputLogin.getText().toString()) && document.get("password").equals(passwordInputLogin.getText().toString()))
                            {
                                Log.d("MyAPp","found");
                                Map<String,Object> updates = new HashMap<>();

                                updates.put("Name",document.get("Name"));
                                updates.put("email",emailInputLogin.getText().toString());
                                updates.put("password",passwordInputLogin.getText().toString());
                                updates.put("phone number",document.get("phone number"));

                                db.collection("Data")
                                        .document("User")
                                        .update(updates);

                                isRegistred=true;
                                finish();
                                return;
                            }
                        }

                        Toast.makeText(loginPage.this, "Email or Password are incorrect. Please try again", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }


        public boolean nameIsValid(){

        String name = nameInput.getText().toString();

            Pattern pattern = Pattern.compile("\\d"); // Matches any single digit
            Matcher matcher = pattern.matcher(name);

        if(name.isEmpty() || name.length()<2 || matcher.find())
        {
            nameInput.setError("name in invalid!");
            return false;
        }
        else {
            return true;
        }

        }

        public boolean phoneNumberIsValid(){

        String phoneNumber = phoneNumberInput.getText().toString();

            Pattern pattern = Pattern.compile("[a-zA-Z]");
            Matcher matcher = pattern.matcher(phoneNumber);


            if(phoneNumber.isEmpty() || phoneNumber.length()!=8 || matcher.find())
        {
            phoneNumberInput.setError("phone number is invalid");
            return false;
        }
        else{
            return true;
        }

        }

        public boolean emailIsValid(){
        String email = emailInputRegistration.getText().toString();

        if(email.isEmpty() || email.length()<10)
        {
            emailInputRegistration.setError("email format is invalid");
            return false;
        }
        else{
            return true;
        }
        }

        public boolean passwordIsValid(){
        String password = passwordInputRegistration.getText().toString();

        if(password.isEmpty() || password.length()<8)
        {
            passwordInputRegistration.setError("password Format is Invalid");
            return false;
        }
        else {
            return true;
        }
        }

        public boolean emailLogInIsValid(){
        String email = emailInputLogin.getText().toString();

        if(email.isEmpty() || email.length()<10)
        {
            emailInputLogin.setError("email format is invalid");
            return false;
        }
        else {
            return true;
        }
        }

        public boolean passwordInputIsValid(){

        String password = passwordInputLogin.getText().toString();

        if(password.isEmpty() || password.length()<8)
        {
            passwordInputLogin.setError("password format is invalid");
            return false;
        }
        else {
            return true;
        }

        }


}