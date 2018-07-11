package com.example.nurulislam.tourmate;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nurulislam.tourmate.NEARBY.Result;
import com.example.nurulislam.tourmate.PLACE_DETAILS.PlaceDetails;
import com.example.nurulislam.tourmate.SELECTED_DISTANCE.SelectPlace;
import com.example.nurulislam.tourmate.SELECTED_DISTANCE.Step;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.nurulislam.tourmate.MainActivity.LAT;
import static com.example.nurulislam.tourmate.MainActivity.LON;
import static com.example.nurulislam.tourmate.MainActivity.USERNAME;

public class PlaceDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "PlaceDetailsActivity";
    private FrameLayout directionMapContainer;
    private GoogleMap googleMap;
    private GoogleMapOptions mapOptions;
    private Result result;
    private String DESTINATION_BASE_URL = "https://maps.googleapis.com/maps/api/directions/";
    private String PLACE_DETAILS_BASE_URL = "https://maps.googleapis.com/maps/api/place/details/";
    private SelectPlaceApi selectPlaceApi;
    private PlaceDetailsApi placeDetailsApi;
    private TextView formattedNameTV, formattedAddressTV,formattedPhoneTV,ratingAndOpenTV,distanceAndTimeTV;
    private Button weeklyOpenTV;
    private ImageView destinationImage;
    private String placeName,placeAddress;
    private double lat, lon;
    private RecyclerView directionDetailsContainer;
    private DirectionStepsAdapter directionStepsAdapter;
    private BottomSheetBehavior behavior;
    List<String> diresctionString;
    private String placeId;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        try {
            if (auth.getCurrentUser() == null){
                finish();
                startActivity(new Intent(PlaceDetailsActivity.this,LoginActivity.class));
                Toast.makeText(this, "Please Login First", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        formattedNameTV = findViewById(R.id.formattedName);
        formattedAddressTV = findViewById(R.id.formattedAddress);
        distanceAndTimeTV = findViewById(R.id.distanceAndTime);
        formattedPhoneTV = findViewById(R.id.formattedPhone);
        ratingAndOpenTV = findViewById(R.id.ratingAndOpen);
        weeklyOpenTV = findViewById(R.id.weeklyOpen);
        destinationImage = findViewById(R.id.destinationImage);
        directionDetailsContainer = findViewById(R.id.directionDetailsContainer);
        View bottomSheet = findViewById(R.id.directionBottomsheet);
        directionMapContainer = findViewById(R.id.detailsMapContainer);

        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(100);
        try {
            Intent intent = getIntent();
            placeId = intent.getStringExtra("placeId");
             placeName = intent.getStringExtra("placeName");
            placeAddress = intent.getStringExtra("placeAddress");
             lat = intent.getDoubleExtra("lat",0);
             lon = intent.getDoubleExtra("lon",0);
            placeDetails(placeId);
            selectPlace(lat,lon);

        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());
        }



        SupportMapFragment mapFragment = SupportMapFragment.newInstance(mapOptions);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.detailsMapContainer,mapFragment);
        ft.commit();
        mapFragment.getMapAsync(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        directionDetailsContainer.setLayoutManager(layoutManager);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

    }
    public boolean checkLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},11);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public  void  selectPlace(double lat, double lon){
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(DESTINATION_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        selectPlaceApi = retrofit.create(SelectPlaceApi.class);
        String customUrl = "json?origin="+LAT+","+LON+"&destination="+lat+","+lon+"&key=AIzaSyBf_XEMw3xjzWns-yhbNQbC2DA7woKIyTY";

        Call<SelectPlace> call = selectPlaceApi.SetSelectPlaceUrl(customUrl);
        call.enqueue(new Callback<SelectPlace>() {
            @Override
            public void onResponse(Call<SelectPlace> call, Response<SelectPlace> response) {
                if (response.isSuccessful()){
                    try {
                        SelectPlace selectPlace = response.body();
                        distanceAndTimeTV.setText("Distance : "+selectPlace.getRoutes().get(0).getLegs().get(0).getDistance().getText()+" | Duration : "+selectPlace.getRoutes().get(0).getLegs().get(0).getDuration().getText());
                        List<Step> steps =selectPlace.getRoutes().get(0).getLegs().get(0).getSteps();
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(steps.get(0).getStartLocation().getLat(),steps.get(0).getStartLocation().getLng())).title(USERNAME).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(steps.get(steps.size()-1).getEndLocation().getLat(),steps.get(steps.size()-1).getEndLocation().getLng())).title(placeName).snippet(placeAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.destination)));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(steps.get(0).getStartLocation().getLat(),steps.get(0).getStartLocation().getLng()),15));
                        diresctionString = new ArrayList<>();
                        for (int i = 0;i<steps.size(); i++){
                            double startLat = steps.get(i).getStartLocation().getLat();
                            double startLng = steps.get(i).getStartLocation().getLng();
                            LatLng startLatlng = new LatLng(startLat,startLng);
                            double endLat = steps.get(i).getEndLocation().getLat();
                            double endLng = steps.get(i).getEndLocation().getLng();
                            LatLng endLatlng = new LatLng(endLat,endLng);
                            if (i<steps.size()-1) {
                                LatLng end = new LatLng(steps.get(i).getEndLocation().getLat(), steps.get(i).getEndLocation().getLng());
                                googleMap.addMarker(new MarkerOptions().position(end).title(String.valueOf(Html.fromHtml(steps.get(i+1).getHtmlInstructions()))).snippet("" +
                                        " Distance : "+steps.get(i).getDistance().getText() + " | Duration : "+steps.get(i).getDuration().getText()).icon(BitmapDescriptorFactory.fromResource(R.drawable.stepmarker)));
                            }
                            Polyline polyline = googleMap.addPolyline(new PolylineOptions().add(startLatlng).add(endLatlng).color(Color.RED));
                            String direct = String.valueOf(Html.fromHtml(steps.get(i).getHtmlInstructions()))+"\n Distance : "+steps.get(i).getDistance().getText() + " | Duration : "+steps.get(i).getDuration().getText();
                            diresctionString.add(direct);
                        }
                        directionStepsAdapter = new DirectionStepsAdapter(diresctionString);
                        directionDetailsContainer.setAdapter(directionStepsAdapter);
                        progressDialog.dismiss();
                    }catch (Exception e){
                        Log.d(TAG, "onResponse: "+e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<SelectPlace> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });

    }
    private void placeDetails(String placeId){
        progressDialog.setMessage("Loading");
        progressDialog.show();
        Retrofit retrofit1 = new Retrofit.Builder().baseUrl(PLACE_DETAILS_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        placeDetailsApi = retrofit1.create(PlaceDetailsApi.class);
        String customUrl2 = "json?placeid="+placeId+"&key="+"AIzaSyDpOLO2sIFZQFRGYyBBcgaxSH6OANcBPxA";
        Call<PlaceDetails> call2 = placeDetailsApi.setPlaceDetailApi(customUrl2);
        call2.enqueue(new Callback<PlaceDetails>() {
            @Override
            public void onResponse(Call<PlaceDetails> call, Response<PlaceDetails> response) {
                if (response.isSuccessful()){
                    try {
                        final PlaceDetails placeDetails = response.body();
                        placeName = placeDetails.getResult().getName();
                        formattedNameTV.setText(placeName);
                        placeAddress = placeDetails.getResult().getFormattedAddress();
                        formattedAddressTV.setText(placeAddress);
                        formattedPhoneTV.setText(placeDetails.getResult().getInternationalPhoneNumber());
                        Picasso.get().load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=140&photoreference=" + placeDetails.getResult().getPhotos().get(0).getPhotoReference()+ "&key=AIzaSyDpOLO2sIFZQFRGYyBBcgaxSH6OANcBPxA").into(destinationImage);
                        String openOrNOt;
                        if (placeDetails.getResult().getOpeningHours().getOpenNow()){
                            openOrNOt = "Open";
                        }else {
                            openOrNOt = "Close";
                        }
                        ratingAndOpenTV.setText("Rating : "+placeDetails.getResult().getRating().toString()+"% | Now "+openOrNOt);
                        progressDialog.dismiss();
                        weeklyOpenTV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(PlaceDetailsActivity.this);
                                    builder1.setMessage(placeDetails.getResult().getOpeningHours().getWeekdayText().get(0)+"\n"
                                            +placeDetails.getResult().getOpeningHours().getWeekdayText().get(1)+"\n"+placeDetails.getResult().getOpeningHours().getWeekdayText().get(2)+"\n"+placeDetails.getResult().getOpeningHours().getWeekdayText().get(3)+"\n"
                                            +placeDetails.getResult().getOpeningHours().getWeekdayText().get(4)+"\n"+placeDetails.getResult().getOpeningHours().getWeekdayText().get(5)+"\n"
                                            +"\n"+placeDetails.getResult().getOpeningHours().getWeekdayText().get(6));
                                    builder1.setCancelable(true);
                                    builder1.setNegativeButton("Ok",null);
                                    AlertDialog alrt = builder1.create();
                                    alrt.show();

                                }catch (Exception e){
                                    Log.d(TAG, "onClick: "+e.getMessage());
                                }


                            }
                        });
                    }catch (Exception e){
                        Log.d(TAG, "onResponse: "+e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaceDetails> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_event,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutmenu){
            auth.signOut();
            startActivity(new Intent(PlaceDetailsActivity.this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}
