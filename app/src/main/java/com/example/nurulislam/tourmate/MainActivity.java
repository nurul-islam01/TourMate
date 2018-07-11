package com.example.nurulislam.tourmate;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nurulislam.tourmate.POJO.SignUp;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "MainActivity";
    private FirebaseAuth auth;
    private FirebaseUser user;
    public static String USERID,USERNAME,USEREMAIL,USER_PHONE;
    public static double LAT,LON;
    //Location
    private FusedLocationProviderClient client;
    private LocationRequest request;
    private Toolbar toolbar;
    private FirebaseDatabase rootref;
    private DatabaseReference userRef;
    private FirebaseUser mUser;
    TextView userNameMTV,userEmailMTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_event);

        rootref= FirebaseDatabase.getInstance();
        client = LocationServices.getFusedLocationProviderClient(this);
        request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(10000);
        request.setFastestInterval(5000);
        getDeviceCurrentLocation();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header=navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        userNameMTV = header.findViewById(R.id.userNameMTV);
        userEmailMTV = header.findViewById(R.id.userEmailMTV);

        auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        try {
            if (mUser == null){
                finish();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
            else if(auth.getCurrentUser() != null){
                USERID = mUser.getUid();
                userRef = rootref.getReference().child(USERID).child("USER_PROFILE");
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            SignUp signUp = (SignUp) dataSnapshot.getValue(SignUp.class);
                            USERNAME = signUp.getUserName();
                            USEREMAIL = signUp.getUserEmail();
                            USER_PHONE = signUp.getUserPhone();
                            USERID = signUp.getUserId();
                            userEmailMTV.setText(signUp.getUserEmail());
                            userNameMTV.setText(signUp.getUserName());
                        }catch (Exception e){
                            Log.d(TAG, "onDataChange: "+e.getMessage());
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: "+databaseError.getMessage());
                    }
                });
            }
        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null){
            getSupportActionBar().setTitle("All Event");
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction().replace(R.id.allFragmentContainer, new eventFrag()).commit();
        }

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logoutmenu) {
            auth.signOut();
            finish();
            startActivity(new Intent(this,LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_map:
                getSupportActionBar().setTitle("Map");
                getSupportFragmentManager().beginTransaction().replace(R.id.allFragmentContainer,new MapFragment()).commit();
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;
            case R.id.nav_weather:
                getSupportActionBar().setTitle("Weather");
                Intent intent = new Intent(this,Weather.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.nav_all_event:
                getSupportActionBar().setTitle("All Event");
                getSupportFragmentManager().beginTransaction().replace(R.id.allFragmentContainer,new MapFragment()).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.allFragmentContainer, new eventFrag()).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 11 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getDeviceCurrentLocation();
        }
    }

    public void getDeviceCurrentLocation(){
        if(checkLocationPermission()){
            try{
            client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    try{
                         LAT = location.getLatitude();
                         LON = location.getLongitude();
                         }catch (Exception e){
                        Log.d(TAG, "onSuccess: "+e.getMessage());
                    }
                }

            });}catch (Exception e){
                Log.d(TAG, "getDeviceCurrentLocation: "+e.getMessage());
            }
        }else{
            checkLocationPermission();
        }
    }

    public boolean checkLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},11);
            return false;
        }
        return true;
    }

}
