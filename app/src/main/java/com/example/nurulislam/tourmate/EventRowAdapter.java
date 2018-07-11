package com.example.nurulislam.tourmate;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nurulislam.tourmate.POJO.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.example.nurulislam.tourmate.MainActivity.USERID;
import static com.example.nurulislam.tourmate.TourExpence.TAG;

public class EventRowAdapter extends RecyclerView.Adapter<EventRowAdapter.EventViewHolder> {
    private List<Event> events;

    public EventRowAdapter( @NonNull List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.event_list_row,parent,false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder,final int position) {
        if (events != null){
            try {
                holder.tourNameTV.setText(events.get(position).getEventName().toString());
                holder.tourStartFromTV.setText("Started From : "+events.get(position).getStratingLocation());
                holder.tourCreateDateTV.setText("Created on : "+events.get(position).getStartDate());
                holder.tourBudgedTV.setText(String.valueOf(events.get(position).getBudget())+" TK");
                holder.tourStartDateTV.setText("Starts on : "+events.get(position).getDepratureDate());
                holder.tourDaysGoTV.setText(events.get(position).countDay());

            }catch (Exception e){}
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder{

        TextView tourNameTV,tourStartFromTV,tourCreateDateTV,tourBudgedTV,tourStartDateTV,tourDaysGoTV;
        ImageView editListItemTV;
        ImageView deleteListItemTV;
        CardView cardViewId;

        public EventViewHolder(final  View itemView) {
            super(itemView);

            tourNameTV = itemView.findViewById(R.id.tourName);
            tourStartFromTV = itemView.findViewById(R.id.tourStartFrom);
            tourCreateDateTV = itemView.findViewById(R.id.tourCreateDate);
            tourBudgedTV = itemView.findViewById(R.id.tourBudged);
            tourStartDateTV = itemView.findViewById(R.id.tourStartDate);
            tourDaysGoTV = itemView.findViewById(R.id.tourDaysGo);

            cardViewId = itemView.findViewById(R.id.cardViewId);

            editListItemTV = itemView.findViewById(R.id.editListItem);
            deleteListItemTV = itemView.findViewById(R.id.deleteListItem);

            cardViewId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(itemView.getContext(),TourExpence.class);
                    intent.putExtra("event",  (Serializable)events.get(getAdapterPosition()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    v.getContext().startActivity(intent);
                }
            });

            deleteListItemTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(itemView.getContext());
                    builder1.setMessage("Are Your Sure Do This.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    FirebaseDatabase rootRef;
                                    DatabaseReference userRef;
                                    rootRef = FirebaseDatabase.getInstance();

                                    int po = getAdapterPosition();
                                    String eventId = events.get(po).getEventId();

                                    userRef = rootRef.getReference().child(USERID).child("All_EVENTS").getRef();
                                    userRef.child(eventId).removeValue();
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            });


            editListItemTV.setOnClickListener(new View.OnClickListener() {
                String lastDate;
                @Override
                public void onClick(View v) {

                    final Calendar calendar;
                    final int year, month, day, hour, minute;
                    calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                    hour = calendar.get(Calendar.HOUR_OF_DAY);
                    minute = calendar.get(Calendar.MINUTE);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());

                    final LayoutInflater inflater = (LayoutInflater) itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View view = inflater.inflate(R.layout.event, null);

                    final EditText eventNameTV = view.findViewById(R.id.eventName);
                    final EditText eventStartLocationTV = view.findViewById(R.id.eventStartLocation);
                    final EditText eventDestinationTV = view.findViewById(R.id.eventDestination);
                    final EditText eventDeparturedateTV = view.findViewById(R.id.eventDeparturedate);
                    final EditText eventBudgetTV = view.findViewById(R.id.eventBudget);


                    final int position = getAdapterPosition();
                    eventNameTV.setText(events.get(position).getEventName().toString());
                    eventStartLocationTV.setText(events.get(position).getStratingLocation().toString());
                    eventDestinationTV.setText(events.get(position).getDestination().toString());
                    eventBudgetTV.setText(String.valueOf(events.get(position).getBudget()));
                    eventDeparturedateTV.setText(events.get(position).getDepratureDate().toString());

                    eventDeparturedateTV.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(itemView.getContext(),
                                    new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                            calendar.set(i,i1,i2);
                                            lastDate = sdf.format(calendar.getTime());
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
                                     FirebaseDatabase rootRef;
                                     DatabaseReference userRef;
                                    rootRef = FirebaseDatabase.getInstance();
                                    userRef = rootRef.getReference().child(USERID).child("All_EVENTS").getRef();
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    String createdDate = events.get(getAdapterPosition()).getStartDate();
                                    try{
                                        String eventName = eventNameTV.getText().toString();
                                        String eventStartLocation = eventStartLocationTV.getText().toString();
                                        String eventDestination = eventDestinationTV.getText().toString();
                                        String eventDeparturedate = eventDeparturedateTV.getText().toString();
                                        String eventBudget = eventBudgetTV.getText().toString();
                                        double budget = Double.parseDouble(String.valueOf(eventBudget));
                                        int po = getAdapterPosition();
                                        String eventId = events.get(po).getEventId();
                                        Event e = new Event(eventName,eventStartLocation,eventDestination,createdDate,eventDeparturedate,budget,eventId);
                                        userRef.child(eventId).child("eventName").setValue(eventName);
                                        userRef.child(eventId).child("stratingLocation").setValue(eventStartLocation);
                                        userRef.child(eventId).child("destination").setValue(eventDestination);
                                        userRef.child(eventId).child("depratureDate").setValue(eventDeparturedate);
                                        userRef.child(eventId).child("budget").setValue(budget);
                                        userRef.child(eventId).child("startDate").setValue(createdDate);
                                        userRef.child(eventId).child("eventId").setValue(eventId);

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
            });
        }


    }


}
