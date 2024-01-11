package com.example.wineryguideapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class datePicker extends AppCompatActivity {

    // select button inside date picker
    Button dateSelect;

    DatePicker picker;

    String month;

    String year;

    String day;

    // merge month , year and date into a string
    String dateString;

    Date datePicked;

    // transform date into timeStamp to store in firestore database
    static com.google.firebase.Timestamp timeStamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        dateSelect = findViewById(R.id.button1);

        picker = findViewById(R.id.datePicker);



        dateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get date infos
                month = String.valueOf(picker.getMonth()+1);
                 day = String.valueOf(picker.getDayOfMonth());
                 year = String.valueOf(picker.getYear());

                 // merge date infos
                 dateString= year + "-" + month + "-" + day;


                // date as a string to actual date while handling ParseException
                try {
                    datePicked = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateString);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                // check if the time picked is valid
                LocalDate currentDate = null;
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // check version of API
                    // get current date
                     currentDate = LocalDate.now();

                     // transform date picked to locat Date type
                    java.sql.Date sqlDate = new java.sql.Date(datePicked.getTime());
                    java.util.Date utilDate = new java.util.Date(sqlDate.getTime());
                    LocalDate localDatePicked = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();



                    if(localDatePicked.compareTo(currentDate)<0) // date picked is before or equal to current date
                    {
                        Toast.makeText(datePicker.this," Pls select a valid date", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                assert datePicked != null;
                // date to timestamp
                timeStamp = new Timestamp(datePicked);

                finish();
            }
        });
    }
}