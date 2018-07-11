package com.example.nurulislam.tourmate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nurulislam.tourmate.NEARBY.Result;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

import static com.example.nurulislam.tourmate.MapFragment.TAG;
public class NearbyPlacesesAdapter extends RecyclerView.Adapter<NearbyPlacesesAdapter.PlaceHolder> {
    private List<Result> results;
    public NearbyPlacesesAdapter(@NonNull List<Result> results) {
    this.results = results;
    }

    @NonNull
    @Override
    public NearbyPlacesesAdapter.PlaceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.nearby_row,parent,false);
        return new PlaceHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyPlacesesAdapter.PlaceHolder holder, int position) {

                try {
                    Picasso.get().load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=140&photoreference=" +results.get(position).getPhotos().get(0).getPhotoReference()+"&key=AIzaSyD8cnZMDp7tJYSo1ptKlxexDv8zQAYAiY0").into(holder.placeImageTV);

                }catch (Exception e){
                    Picasso.get().load(results.get(position).getIcon()).into(holder.placeImageTV);
                    Log.d(TAG, "onBindViewHolder: "+e.getMessage());
                }

                try {
                    holder.searchNameTV.setText(results.get(position).getName());
                    holder.placeNameTV.setText(results.get(position).getVicinity());
                }catch (Exception e){
                    Log.d(TAG, "onBindViewHolder: "+e.getMessage());
                }

                try {
                    String open;
                    if (results.get(position).getOpeningHours().getOpenNow()) {
                        open = "Open";
                    } else {
                        open = "Close";
                    }
                    holder.ratingAndOpenTV.setText("Rating : " + results.get(position).getRating() + " |  Now " + open);
                }
                catch (Exception e){
                    Log.d(TAG, "onBindViewHolder: "+e.getMessage());
                    holder.ratingAndOpenTV.setText(" | Now Close");
                }
        }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class PlaceHolder extends RecyclerView.ViewHolder{
        private TextView searchNameTV,ratingAndOpenTV,placeNameTV;
        private ImageView placeImageTV;
        private LinearLayout gotoDetailPage;

        public PlaceHolder(final View itemView) {
            super(itemView);
            searchNameTV = itemView.findViewById(R.id.searchName);
            ratingAndOpenTV = itemView.findViewById(R.id.ratingAndOpen);
            placeNameTV = itemView.findViewById(R.id.placeName);
            placeImageTV = itemView.findViewById(R.id.placeImage);
            gotoDetailPage = itemView.findViewById(R.id.gotoDetailPage);
            gotoDetailPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(itemView.getContext(),PlaceDetailsActivity.class);
                        intent.putExtra("placeId", results.get(getAdapterPosition()).getPlaceId()).putExtra("lat",results.get(getAdapterPosition()).getGeometry().getLocation().getLat())
                                .putExtra("lon",results.get(getAdapterPosition()).getGeometry().getLocation().getLng())
                        .putExtra("placeName", results.get(getAdapterPosition()).getName())
                        .putExtra("placeAdress",results.get(getAdapterPosition()).getVicinity());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        itemView.getContext().startActivity(intent);
                    }catch (Exception e){
                        Toast.makeText(itemView.getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onClick: "+e.getMessage());
                    }
                }
            });
        }
    }

}
