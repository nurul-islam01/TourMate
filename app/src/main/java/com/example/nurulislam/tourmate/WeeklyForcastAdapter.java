package com.example.nurulislam.tourmate;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nurulislam.tourmate.Weekly.Forecast;

import java.util.List;

import static com.example.nurulislam.tourmate.MainActivity.TAG;

public class WeeklyForcastAdapter extends ArrayAdapter<Forecast>{
    private Context context;
    private List<Forecast> forecasts;
    private ProgressDialog progressDialog;
    private TextView dayName,maxTemp,conditonText,dateTimeText,minTemp;
    private ImageView conditionIcon;

    public WeeklyForcastAdapter(@NonNull Context context, @NonNull List<Forecast> forecasts) {
        super(context, R.layout.forcast_row, forecasts);
        this.context = context;
        this.forecasts = forecasts;
        progressDialog = new ProgressDialog(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.forcast_row,parent,false);
        dayName = convertView.findViewById(R.id.dayName);
        maxTemp = convertView.findViewById(R.id.maxTemp);
        conditonText = convertView.findViewById(R.id.conditonText);
        dateTimeText = convertView.findViewById(R.id.dateTimeText);
        minTemp = convertView.findViewById(R.id.minTemp);
        conditionIcon = convertView.findViewById(R.id.conditionIcon);
        if (forecasts !=null){
            try {
                dayName.setText(forecasts.get(position).getDay().toString());
                maxTemp.setText(forecasts.get(position).getHigh().toString());
                conditonText.setText(forecasts.get(position).getText().toString());
                dateTimeText.setText(forecasts.get(position).getDate().toString());
                minTemp.setText(forecasts.get(position).getLow().toString());

                switch (forecasts.get(position).getCode()){
                    case "1":
                        conditionIcon.setImageResource(R.drawable.tornado);
                        break;
                    case "2":
                        conditionIcon.setImageResource(R.drawable.storm);
                        break;
                    case "3":
                        conditionIcon.setImageResource(R.drawable.tornado);
                        break;
                    case "4":
                        conditionIcon.setImageResource(R.drawable.thunderstorm);
                        break;
                    case "5":
                        conditionIcon.setImageResource(R.drawable.chance_of_snow);
                        break;
                    case "6":
                        conditionIcon.setImageResource(R.drawable.chance_of_snow_night);
                        break;
                    case "7":
                        conditionIcon.setImageResource(R.drawable.chance_of_snow_night);
                        break;
                    case "08":
                        conditionIcon.setImageResource(R.drawable.drizzle);
                        break;
                    case "9":
                        conditionIcon.setImageResource(R.drawable.drizzle);
                        break;
                    case "10":
                        conditionIcon.setImageResource(R.drawable.cloudy_rain);
                        break;
                    case "11":
                        conditionIcon.setImageResource(R.drawable.snow);
                        break;
                    case "12":
                        conditionIcon.setImageResource(R.drawable.snow);
                        break;
                    case "13":
                        conditionIcon.setImageResource(R.drawable.flurries);
                        break;
                    case "14":
                        conditionIcon.setImageResource(R.drawable.snow_showers);
                        break;
                    case "15":
                        conditionIcon.setImageResource(R.drawable.snow);
                        break;
                    case "16":
                        conditionIcon.setImageResource(R.drawable.snow);
                        break;
                    case "17":
                        conditionIcon.setImageResource(R.drawable.flurries);
                        break;
                    case "18":
                        conditionIcon.setImageResource(R.drawable.sleet);
                        break;
                    case "19":
                        conditionIcon.setImageResource(R.drawable.dust);
                        break;
                    case "20":
                        conditionIcon.setImageResource(R.drawable.fog);
                        break;
                    case "21":
                        conditionIcon.setImageResource(R.drawable.haze);
                        break;
                    case "22":
                        conditionIcon.setImageResource(R.drawable.smoke);
                        break;
                    case "23":
                        conditionIcon.setImageResource(R.drawable.thunderstorm);
                        break;
                    case "24":
                        conditionIcon.setImageResource(R.drawable.windy);
                        break;
                    case "25":
                        conditionIcon.setImageResource(R.drawable.cloudy);
                        break;
                    case "26":
                        conditionIcon.setImageResource(R.drawable.cloudy);
                        break;
                    case "27":
                        conditionIcon.setImageResource(R.drawable.mostly_cloudy_night);
                        break;
                    case "28":
                        conditionIcon.setImageResource(R.drawable.mostly_cloudy);
                        break;
                    case "29":
                        conditionIcon.setImageResource(R.drawable.partly_cloudy_night);
                        break;
                    case "30":
                        conditionIcon.setImageResource(R.drawable.partly_cloudy);
                        break;
                    case "31":
                        conditionIcon.setImageResource(R.drawable.cloudy_night);
                        break;
                    case "32":
                        conditionIcon.setImageResource(R.drawable.sunny);
                        break;
                    case "33":
                        conditionIcon.setImageResource(R.drawable.clear_night);
                        break;
                    case "34":
                        conditionIcon.setImageResource(R.drawable.sunny);
                        break;
                    case "35":
                        conditionIcon.setImageResource(R.drawable.chance_of_snow);
                        break;
                    case "36":
                        conditionIcon.setImageResource(R.drawable.sunny);
                        break;
                    case "37":
                        conditionIcon.setImageResource(R.drawable.scattered_thunderstorms);
                        break;
                    case "38":
                        conditionIcon.setImageResource(R.drawable.scattered_thunderstorms);
                        break;
                    case "39":
                        conditionIcon.setImageResource(R.drawable.scattered_thunderstorms);
                        break;
                    case "40":
                        conditionIcon.setImageResource(R.drawable.scattered_showers);
                        break;
                    case "41":
                        conditionIcon.setImageResource(R.drawable.snow);
                        break;
                    case "42":
                        conditionIcon.setImageResource(R.drawable.snow_showers);
                        break;
                    case "43":
                        conditionIcon.setImageResource(R.drawable.rain_snow);
                        break;
                    case "44":
                        conditionIcon.setImageResource(R.drawable.partly_cloudy);
                        break;
                    case "45":
                        conditionIcon.setImageResource(R.drawable.scattered_thunderstorms);
                        break;
                    case "46":
                        conditionIcon.setImageResource(R.drawable.snow_showers);
                        break;
                    case "47":
                        conditionIcon.setImageResource(R.drawable.thunderstorm);
                        break;
                }
            }catch (Exception e){
                Log.d(TAG, "getView: "+e.getMessage());
            }

        }
        progressDialog.dismiss();
        return convertView;
    }
}
