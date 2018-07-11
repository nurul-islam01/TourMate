package com.example.nurulislam.tourmate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nurulislam.tourmate.Weekly.WeeklyForecast;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.nurulislam.tourmate.Weather.TAG;

public class ForcastFragment extends Fragment implements WeeklyObjectPass{

    private ListView weeklyForecastList;
    private WeeklyForcastAdapter adapter;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        try {
            if (auth.getCurrentUser() == null){
                getActivity().finish();
                startActivity(new Intent(getActivity(),LoginActivity.class));
            }
        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forcast, container, false);
        weeklyForecastList = v.findViewById(R.id.weeklyForecastList);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        context = getActivity();
        ((Weather) context).setWeeklyObject(this);
    }

    @Override
    public void setWeeklyObjectpass(WeeklyForecast weeklyForecast) {
        try {
            if (weeklyForecast.equals(null)){
                Toast.makeText(getActivity(), "This city is not found in Yahoo Weather Api", Toast.LENGTH_SHORT).show();
            }else if (weeklyForecast.getQuery().getResults() !=null){

                adapter = new WeeklyForcastAdapter(getActivity(), weeklyForecast.getQuery().getResults().getChannel().getItem().getForecast());
                weeklyForecastList.setAdapter(adapter);

            }
        }catch (Exception e){
            Log.d(TAG, "setWeeklyObjectpass: "+e.getMessage());
        }

    }
}