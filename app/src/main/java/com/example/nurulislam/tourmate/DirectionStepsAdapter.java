package com.example.nurulislam.tourmate;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.example.nurulislam.tourmate.PlaceDetailsActivity.TAG;


public class DirectionStepsAdapter extends RecyclerView.Adapter<DirectionStepsAdapter.Direction> {
    List<String> stringList;

    public DirectionStepsAdapter(List<String> stringList) {
        this.stringList = stringList;
    }

    @NonNull
    @Override
    public Direction onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.direction_row,parent,false);
        return new Direction(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Direction holder, int position) {
        try {
            holder.directionNumberTV.setText(String.valueOf(position+1));
            holder.directionTextTV.setText(stringList.get(position).toString());
        }catch (Exception e){
            Log.d(TAG, "onBindViewHolder: "+e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public class Direction extends RecyclerView.ViewHolder{
        TextView directionNumberTV,directionTextTV;
        public Direction(View itemView) {
            super(itemView);
            directionNumberTV = itemView.findViewById(R.id.directionNumberTV);
            directionTextTV = itemView.findViewById(R.id.directionTextTV);
        }
    }
}
