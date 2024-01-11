    package com.example.wineryguideapp;

    import static androidx.core.content.ContextCompat.startActivity;

    import android.content.Context;
    import android.content.Intent;
    import android.util.Log;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.lifecycle.MutableLiveData;

    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.Timestamp;
    import com.google.firebase.firestore.CollectionReference;
    import com.google.firebase.firestore.DocumentReference;
    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FieldValue;
    import com.google.firebase.firestore.FirebaseFirestore;
    import com.google.firebase.firestore.QuerySnapshot;

    import java.sql.Time;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    public class FirestoreFetcher {


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // store updates in data to add them in a new document in database
        HashMap<String,Object> data = new HashMap<>();

        // store the context of main reservation that comes as a parameter to the function FetcheDocByDate to use later for starting an activity
        Context mainReservationContext;

        // reference to the Reservation collection in firestore
        CollectionReference reservation = db.collection("Reservation");

        // reference for the new reservation doc created for later use
        String newReservationDoc;

        // reference User Collection which stores user info , to store it in the reservation Doc
        private DocumentReference userDocument = db.document("Data/User");

        String regionName;



            public void updateFirestore(int peopleNum , String winery , Timestamp date, ArrayList<String> servicesName, String region, Context context) {

            // store max capacity from firestore
            final long[] maxCapacity = {0};

            // store services to update their spots later if all services chosed has available spots
            ArrayList<DocumentSnapshot> documentList = new ArrayList<>();

            // assign context for later use is getTotalCost function
            mainReservationContext=context;

            regionName=region;

            // count services to save data in firestore when all services has been handled
            int[] serviceCounter={0};

            // iterate over chosed services to update their data if available
            for(String service : servicesName) {



                DocumentReference serviceMaxCapacity = db.collection("Wineries").document(region).collection("WineriesList").document(winery).collection("Services").document(service);


                CollectionReference serviceSpots = db.collection("Wineries").document(region).collection("WineriesList").document(winery).collection("Services").document(service).collection("Reserved Spots");

                // get max capacity of the service
                 serviceMaxCapacity.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (!documentSnapshot.exists()) {
                            Log.d("MyApp", "docuemnt doesn't exost");

                        }
                        if (documentSnapshot.contains("Max-Capacity")) {
                            maxCapacity[0] = (long) documentSnapshot.get("Max-Capacity");

                        } else {
                            Log.d("MyApp", "max capacity field doesn't exist");
                        }
                    }
                });

                 // update reserved spots doc for each service if it exists or create a new one
                serviceSpots.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        //check if there is a doc in the chosen date
                        Boolean found = false;


                        // iterate over documents inside reserves spots collection
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot document : documents) {


                            if (document.getTimestamp("Date").getSeconds() == (date.getSeconds())) { // there is a recent saved document with the same date as the chosed one

                                found = true;

                                // check if service has enough empty spots
                                if (((long) (Integer.parseInt(document.get("Spots").toString()) + peopleNum)) <= maxCapacity[0]) {

                                    documentList.add(document);

                                } else { // empty spots cannot fit the requirements
                                    Toast.makeText(context, "the service is full", Toast.LENGTH_SHORT).show();
                                    // clear the arralist that store the services chosed so they dont add up to any new services
                                    mainReservation.clearServices();
                                    //exit
                                    return;
                                }
                                break;
                            }
                        }
                        // all services are available , then update spots accordingly
                        updateDocumentsSpots(documentList,peopleNum);

                        if (!found) {  // no document with the chosen date exist

                            if(peopleNum <= maxCapacity[0])
                            {
                                Map<String, Object> data = new HashMap<>();
                                data.put("Date", new Timestamp(date.toDate()));
                                data.put("Spots", peopleNum);
                            }
                            else {
                                Toast.makeText(context, "you might need to kick out some of your group!", Toast.LENGTH_SHORT).show();
                                return;
                            }


                            // create new doc
                            serviceSpots.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("MyApp", "row added!");
                                }
                            });


                        }

                        // increment service counter because service is handled
                        serviceCounter[0]++;

                        // check if all services are handled
                        if(serviceCounter[0] == servicesName.size()) {

                            // fill fields accordingly
                            data.put("Due-Date",date);
                            data.put("Reservation-Date",Timestamp.now());
                            data.put("Spots",peopleNum);
                            data.put("User",userDocument);
                            data.put("Winery",db.collection("WineriesList").document(winery));
                            data.put("Services",servicesName);
                            getTotalCost(servicesName,winery,peopleNum);

                        }
                    }

                });

            }

        }

        public void getTotalCost(ArrayList<String> services , String wineryName,int peopleNum) {


            long[] totalCost={0};


            DocumentReference winery = db.document("WineriesList/" + wineryName);


            winery.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {


                        CollectionReference servicesRef = db.collection("Wineries/"+regionName+"/WineriesList/"+ wineryName + "/Services");


                        // compute total cost of chosen services
                        servicesRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                if(queryDocumentSnapshots.isEmpty())
                                {
                                    Log.d("MyApp","docuemnt is emtpy!");
                                }
                                for (DocumentSnapshot document : queryDocumentSnapshots) {

                                    for (int i = 0; i < services.size(); i++) {
                                        if (document.getId().equals(services.get(i))) { // this document belongs to a chosen service
                                            totalCost[0] += peopleNum*((long) document.get("Cost"));
                                        }
                                    }
                                }

                                // fill last field
                                data.put("Total Cost",totalCost[0]);


                                reservation.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        // store the id of the newly created doc
                                        newReservationDoc=documentReference.getId();

                                        Intent i = new Intent(mainReservationContext,receiptPage.class);
                                        // pass the id of the new doc so the receipt page can fetch it and get the information from it
                                        i.putExtra("new reservation",newReservationDoc);
                                        startActivity(mainReservationContext,i,null);
                                        mainReservation.clearServices();
                                    }
                                });

                            }

                        });

                    } else {
                        // Document not found
                        System.out.println("Document does not exist");
                        Log.d("MyApp","Document doesn't exist");
                    }
                }


            });


        }
        public  void updateDocumentsSpots(ArrayList<DocumentSnapshot> documents,int peopleNum)
        {
            for(DocumentSnapshot document : documents)
            {
                document.getReference().update("Spots", FieldValue.increment(peopleNum));
            }
        }


    }

