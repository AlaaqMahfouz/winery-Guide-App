package com.example.wineryguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class datePicker extends AppCompatActivity {

    Button dateChange;

    DatePicker picker;

    LocalDate selectDate;

    String month;

    String year;

    String day;

    String dateString;

    Date datePicked;

    long longDate;

    static com.google.firebase.Timestamp timeStamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        dateChange = findViewById(R.id.button1);

        picker = findViewById(R.id.datePicker);



        dateChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                month = String.valueOf(picker.getMonth()+1);
                 day = String.valueOf(picker.getDayOfMonth());
                 year = String.valueOf(picker.getYear());

                 dateString= year + "-" + month + "-" + day;



                try {
                    datePicked = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateString);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                assert datePicked != null;
                timeStamp = new com.google.firebase.Timestamp(datePicked);
                Log.d("MyApp","date =" + timeStamp);




                finish();
            }
        });
    }


//    public String getDate(){
//
//    }
}