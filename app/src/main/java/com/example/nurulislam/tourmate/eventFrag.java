package com.example.nurulislam.tourmate;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nurulislam.tourmate.POJO.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.example.nurulislam.tourmate.MainActivity.TAG;
import static com.example.nurulislam.tourmate.MainActivity.USERID;

public class eventFrag extends Fragment {

    private RecyclerView eventListTV;

    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private Calendar calendar;
    private int year, month, day, hour, minute;
    private String lastDate,createdDate;
    private FirebaseDatabase rootRef;
    private DatabaseReference userRef;
    private FirebaseUser mUser;

    private List<Event> events;
    private FirebaseAuth auth;
    private TextView goneTextView;

    private RecyclerView.Adapter adapter;

    public eventFrag() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance();
        try {
            if (mUser == null){
                startActivity(new Intent(getActivity(),LoginActivity.class));
            }
        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        try{

            userRef = rootRef.getReference().child(USERID).child("All_EVENTS").getRef();
        }catch (Exception e){
            Log.d("eventFrag ",e.getMessage());
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_event, container, false);


        goneTextView = v.findViewById(R.id.goneTextView);

        eventListTV = v.findViewById(R.id.eventList);
        getEentList();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);


        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        eventListTV.setLayoutManager(lm);
        getEentList();



        FloatingActionButton fab =  v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertCustomizedLayout();
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        context = getActivity();
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void alertCustomizedLayout(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.event_dialoge, null);
        final EditText eventNameTV = view.findViewById(R.id.eventName);
        final EditText eventStartLocationTV = view.findViewById(R.id.eventStartLocation);
        final EditText eventDestinationTV = view.findViewById(R.id.eventDestination);
        final EditText eventDeparturedateTV = view.findViewById(R.id.eventDeparturedate);
        final EditText eventBudgetTV = view.findViewById(R.id.eventBudget);

        eventDeparturedateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                calendar.set(i,i1,i2);
                                lastDate = sdf.format(calendar.getTime());
                                Calendar c = Calendar.getInstance();
                                createdDate = sdf.format(c.getTime());
                                eventDeparturedateTV.setText(lastDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
        builder.setView(view)

                .setPositiveButton("Add", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try{
                            String eventName = eventNameTV.getText().toString().trim();
                            String eventStartLocation = eventStartLocationTV.getText().toString().trim();
                            String eventDestination = eventDestinationTV.getText().toString().trim();
                            String eventDeparturedate = eventDeparturedateTV.getText().toString().trim();
                            String eventBudget = eventBudgetTV.getText().toString().trim();
                            double budget = Double.parseDouble(String.valueOf(eventBudget));
                            String eventListId = userRef.push().getKey();
                            Event e = new Event(eventName,eventStartLocation,eventDestination,createdDate,eventDeparturedate,budget,eventListId);
                            userRef.child(eventListId).setValue(e);

                        }catch (Exception e){
                            Log.d(TAG, "onClick: "+e.getMessage());
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .show();
    }

    private void getEentList(){

        try {
            DatabaseReference fetch = rootRef.getReference().child(USERID).child("All_EVENTS");
            fetch.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    events = new ArrayList<>();
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        try {
                            Event n = (Event) d.getValue(Event.class);
                            events.add(n);
                        }catch (Exception e){
                            Log.d(TAG, "onDataChange: "+e.getMessage());
                        }

                    }
                    try {
                        if (events.size() != 0){
                            goneTextView.setVisibility(View.GONE);
                        }else {
                            goneTextView.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){
                        Log.d(TAG, "onCreateView: "+e.getMessage());
                    }
                    Collections.reverse(events);
                    adapter = new EventRowAdapter(events);
                    eventListTV.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            Log.d("My test", "getEentList: "+e.getMessage());
        }

    }

}
