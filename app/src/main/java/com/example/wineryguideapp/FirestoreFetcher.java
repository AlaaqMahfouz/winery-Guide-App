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

        CollectionReference collection ;

        DocumentReference document;

        HashMap<String,Object> data = new HashMap<>();

        Context mainReservationContext;

        CollectionReference reservation = db.collection("Reservation");

        String newReservationDoc;

        private DocumentReference userDocument = db.document("Data/User");

        String regionName;

        String fieldName;

        DocumentSnapshot object;

        static Task<DocumentSnapshot> task;


       static  MutableLiveData<Boolean> isTaskCompleted = new MutableLiveData<>();

        public DocumentSnapshot Fetch(CollectionReference collectionToFetch, DocumentReference documentToFetch ){
            collection=collectionToFetch;
            document=documentToFetch;

            collection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                    for(DocumentSnapshot documentInCollection :documents)
                    {
                        if(documentInCollection.equals(document))
                        {
                            object=documentInCollection;
                        }
                    }

                }
            });

            return object;
        }


            public void FetchDocByDate(int peopleNum , String winery , Timestamp date, ArrayList<String> servicesName, String region, Context context) {

            final long[] maxCapacity = new long[1];

            ArrayList<DocumentSnapshot> documentList = new ArrayList<>();

            mainReservationContext=context;

            regionName=region;

            maxCapacity[0]=0;

            int[] serviceCounter={0};

            for(String service : servicesName) {


                DocumentReference serviceMaxCapacity = db.collection("Wineries").document(region).collection("WineriesList").document(winery).collection("Services").document(service);


                CollectionReference serviceSpots = db.collection("Wineries").document(region).collection("WineriesList").document(winery).collection("Services").document(service).collection("Reserved Spots");


                task = serviceMaxCapacity.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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

                serviceSpots.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {



                        Boolean found = false;
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot document : documents) {

                            if (document.getTimestamp("Date").getSeconds() == (date.getSeconds())) {

                                found = true;


                                if (((long) (Integer.parseInt(document.get("Spots").toString()) + peopleNum)) <= maxCapacity[0]) {
                                    Log.d("MyAPp","service :"+service);
                                    Log.d("MyApp", "max capacity ="+ maxCapacity[0]);
                                    Log.d("MyAPp","sspots to be :"+Integer.parseInt(document.get("Spots").toString()) + peopleNum);

                                    documentList.add(document);

                                } else {
                                    Toast.makeText(context, "the service is full", Toast.LENGTH_SHORT).show();
                                    mainReservation.clearServices();
                                    return;
                                }
                                break;
                            }
                        }

                        updateDocumentsSpots(documentList,peopleNum);

                        if (!found) {
                            Map<String, Object> data = new HashMap<>();
                            data.put("Date", new Timestamp(date.toDate()));
                            data.put("Spots", peopleNum);

                            serviceSpots.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("MyApp", "row added!");
                                }
                            });


                        }

                        serviceCounter[0]++;


                        if(serviceCounter[0] ==servicesName.size()) {


                            data.put("Due-Date",date);
                            data.put("Reservation-Date",Timestamp.now());
                            data.put("Spots",peopleNum);
                            data.put("User",userDocument);
                            data.put("Winery",db.collection("WineriesList").document(winery));
                            data.put("Services",servicesName);
                            long[] totalCost={ 0};
                            getTotalCost(servicesName,winery,totalCost,peopleNum);

                        }
                    }

                });

            }

        }

        public void getTotalCost(ArrayList<String> services , String wineryName,long[] totalCost,int peopleNum) {


            DocumentReference winery = db.document("WineriesList/" + wineryName);


            winery.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {


                        CollectionReference servicesRef = db.collection("Wineries/"+regionName+"/WineriesList/"+ wineryName + "/Services");



                        servicesRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                if(queryDocumentSnapshots.isEmpty())
                                {
                                    Log.d("MyApp","docuemnt is emtpyyyyy");
                                }
                                for (DocumentSnapshot document : queryDocumentSnapshots) {

                                    for (int i = 0; i < services.size(); i++) {
                                        if (document.getId().equals(services.get(i))) {
                                            totalCost[0] += peopleNum*((long) document.get("Cost"));
                                        }
                                    }
                                }

                                data.put("Total Cost",totalCost[0]);

                                reservation.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        newReservationDoc=documentReference.getId();

                                        Intent i = new Intent(mainReservationContext,receiptPage.class);
                                        i.putExtra("new reservation",newReservationDoc);
//                                        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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

