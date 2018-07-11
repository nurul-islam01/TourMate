package com.example.nurulislam.tourmate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationPresenter;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nurulislam.tourmate.NEARBY.NearBy;
import com.example.nurulislam.tourmate.NEARBY.Result;
import com.example.nurulislam.tourmate.PLACE_DETAILS.PlaceDetails;
import com.example.nurulislam.tourmate.SELECTED_DISTANCE.SelectPlace;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.clustering.ClusterManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.nurulislam.tourmate.MainActivity.LAT;
import static com.example.nurulislam.tourmate.MainActivity.LON;
import static com.example.nurulislam.tourmate.MainActivity.USERNAME;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "MainActivity";
    private GoogleMap googleMap;
    private GoogleMapOptions mapOptions;
    private double lat, lon;
    private Context context;
    private String typeName;
    private double distanceArea;
    private Spinner searchTypeTV, distanceTV;
    private ImageView findTV;
    private NearByUrlinterfaceApi nearByUrlinterfaceApi;
    private String NEARBY_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
    private ClusterManager<AllItemCluster> clusterManager;
    private List<AllItemCluster> clusterList;
    private LatLng userLatlng;
    //bottombar everythis
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView nearbyPlascesesTV;
    private NearbyPlacesesAdapter nearbyAdapter;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    public MapFragment() {

    }

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(context);
        try {
            if (auth.getCurrentUser() == null){
                getActivity().finish();
                startActivity(new Intent(getActivity(),LoginActivity.class));
            }
        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());
        }
        userLatlng = new LatLng(LAT, LON);
        mapOptions = new GoogleMapOptions();
        mapOptions.zoomControlsEnabled(true);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance(mapOptions);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.mapContainer, mapFragment);
        ft.commit();
        mapFragment.getMapAsync(this);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        final View bottomSheet = v.findViewById(R.id.bottomsheeet);

        searchTypeTV = v.findViewById(R.id.searchType);
        distanceTV = v.findViewById(R.id.distance);
        findTV = v.findViewById(R.id.findTV);
        //bottomsheet id's finding
        nearbyPlascesesTV = v.findViewById(R.id.nearbyPlasceses);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        nearbyPlascesesTV.setLayoutManager(layoutManager);

            ArrayAdapter<CharSequence> search = ArrayAdapter.createFromResource(
                    getActivity().getBaseContext(),
                    R.array.search_type,
                    android.R.layout.simple_spinner_dropdown_item);
            searchTypeTV.setAdapter(search);

            ArrayAdapter<CharSequence> distance = ArrayAdapter.createFromResource(
                    getActivity().getBaseContext(),
                    R.array.distance,
                    android.R.layout.simple_spinner_dropdown_item);
            distanceTV.setAdapter(distance);

            findTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                       String serType = searchTypeTV.getSelectedItem().toString();
                       String distan = distanceTV.getSelectedItem().toString();
                       switch (serType){
                           case "Restaurante":
                               typeName = "restaurant";
                               break;
                           case "Cafe":
                               typeName = "cafe";
                               break;
                               case "Gas Station":
                               typeName = "gas_station";
                               break;
                               case "Train Station":
                               typeName = "train_station";
                               break;
                               case "Travel Agency":
                               typeName = "travel_agency";
                               break;
                           case "Atm":
                               typeName = "atm";
                               break;
                           case "Bank":
                               typeName = "bank";
                               break;
                           case "Hospital":
                               typeName = "hospital";
                               break;
                           case "Shopping Mall":
                               typeName = "supermarket";
                               break;
                           case "Department Store":
                               typeName = "department_store";
                               break;
                           case "Mosque":
                               typeName = "mosque";
                               break;
                           case "Bus Station":
                               typeName = "bus_station";
                               break;
                           case "Taxi Stand":
                               typeName = "taxi_stand";
                               break;
                           case "Pharmacy":
                               typeName = "pharmacy";
                               break;
                           case "Post Office":
                               typeName = "post_office";
                               break;
                           case "Hair Care":
                               typeName = "hair_care";
                               break;
                           case "Beauty Salon":
                               typeName = "beauty_salon";
                               break;
                           case "Hindu Temple":
                               typeName = "hindu_temple";
                               break;
                           case "Church":
                               typeName = "church";
                               break;
                           case "Police":
                               typeName = "police";
                               break;
                       }
                       switch (distan){
                           case "0.5 KM":
                               distanceArea = 0.5;
                               break;
                           case "1 KM":
                               distanceArea = 1;
                               break;
                           case "1.5 KM":
                               distanceArea = 1.5;
                           case "2 KM":
                               distanceArea = 2;
                               break;
                           case "2.5 KM":
                               distanceArea = 2.5;
                               break;
                           case "3 KM":
                               distanceArea = 3;
                               break;
                           case "4 KM":
                               distanceArea = 4;
                               break;
                           case "5 KM":
                               distanceArea = 5;
                               break;
                           case "7 KM":
                               distanceArea = 7;
                               break;
                               case "10 KM":
                               distanceArea = 10;
                               break;
                       }
                        nearbyPlaces( typeName, distanceArea*1000);

                }
            });

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        context = getActivity();
        this.context = context;
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        try{
            googleMap.addMarker(new MarkerOptions().position(userLatlng).title(USERNAME).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatlng,15));
        }catch (Exception e){
            Log.d(TAG, "onMapReady: "+e.getMessage());
        }
    }


    public boolean checkLocationPermission(){
        if(ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},11);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }



    public void nearbyPlaces(String typeName, double distanceArea){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(NEARBY_BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        nearByUrlinterfaceApi = retrofit.create(NearByUrlinterfaceApi.class);
        String nearbyApiKey = getString(R.string.nearby_api_key);
        String customUrl = "json?location="+LAT+","+LON+"&radius="+distanceArea+"&type="+typeName+"&key="+nearbyApiKey;
        Call<NearBy> call = nearByUrlinterfaceApi.nearByUrlApi(customUrl);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        call.enqueue(new Callback<NearBy>() {
            @Override
            public void onResponse(Call<NearBy> call, Response<NearBy> response) {
                if (response.isSuccessful()){

                    try {
                        NearBy nearBy = response.body();
                        putAllMarker(nearBy.getResults());
                        nearbyAdapter = new NearbyPlacesesAdapter(nearBy.getResults());
                        nearbyPlascesesTV.setAdapter(nearbyAdapter);
                        progressDialog.dismiss();
                    }catch (Exception e){
                        Log.d(TAG, "onResponse: "+e.getMessage());
                    }
                }
            }
            @Override
            public void onFailure(Call<NearBy> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t.toString());
            }
        });
    }
    private void putAllMarker(List<Result> results){
        if (clusterList !=null){
            clusterList.clear();
            googleMap.clear();
        }
        clusterList = new ArrayList<>();
        for (int i=0; i < results.size(); i++){
            try {
                final Result result = results.get(i);
                LatLng latLng = new LatLng(result.getGeometry().getLocation().getLat(),result.getGeometry().getLocation().getLng());
                AllItemCluster allItemCluster = new AllItemCluster(latLng,result.getName(),result.getPlaceId(),result.getVicinity());
                clusterList.add(allItemCluster);
                String snipt = "";
                if (typeName.equals("restaurant") || typeName.equals("cafe")|| typeName.equals("hospital")|| typeName.equals("supermarket")){
                    snipt = "Rating : "+String.valueOf(result.getRating())+"%";
                }else {
                    snipt = result.getVicinity();
                }
                googleMap.addMarker(new MarkerOptions().position(latLng).title(result.getName().toString()).snippet(snipt));

                clusterManager = new ClusterManager<AllItemCluster>(context,googleMap);
                googleMap.setOnCameraIdleListener(clusterManager);
            }catch (Exception e){
                Log.d(TAG, "putAllMarker: "+e.getMessage());
            }
        }

        try{
            googleMap.addMarker(new MarkerOptions().position(userLatlng).title("Nurul Islam").snippet("My Current Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatlng,15));
        }catch (Exception e){
            Log.d(TAG, "onMapReady: "+e.getMessage());
        }
    }



}
