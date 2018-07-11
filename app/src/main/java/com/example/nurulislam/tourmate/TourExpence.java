package com.example.nurulislam.tourmate;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nurulislam.tourmate.POJO.Event;
import com.example.nurulislam.tourmate.POJO.Expense;
import com.example.nurulislam.tourmate.POJO.ExpenseList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.http.Url;

import static android.os.Environment.DIRECTORY_DCIM;
import static com.example.nurulislam.tourmate.MainActivity.USERID;

public class TourExpence extends AppCompatActivity {

    public static final String TAG = "TourExpence";
    private TextView TXP_tourNameTV,TXP_BudgetStatusTV,extraAmountTV, currentHaveAmoundTV;
    private ProgressBar TXP_expenceProgressTV;
    private ImageView extraSpendIconTV,currentSpentIconTV;
    private RecyclerView expenceListRecylerIdTV;
    private Event event;
    private FirebaseDatabase rootref;
    private DatabaseReference useref;
    private FirebaseAuth auth;
    public static String event_id;
    private Expense expens;
    private ExpenseList expenseList;
    private List<ExpenseList> expenseLists;
    private RecyclerView.Adapter setAdapter;
    private double ex_have;
    private double tourBudget;
    private double extraExpense = 0;
    private String eventNam;
    private String mCurrentPhotoPath;
    private File storageDir;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView imgCapturedRV;
    private CaputuredImageAdapter caputuredImageAdapter;
    private FirebaseDatabase rootref2;
    private DatabaseReference useref2;
    private FirebaseUser mUser;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_expence);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        auth = FirebaseAuth.getInstance();
        rootref2 = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        try {
            mUser = auth.getCurrentUser();
            if (mUser == null){
                finish();
                startActivity(new Intent(TourExpence.this,LoginActivity.class));
            }
        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());
        }
        expenseLists = new ArrayList<>();
        try {
            event = (Event) getIntent().getSerializableExtra("event");
            event_id = event.getEventId();
            eventNam = event.getEventName().trim();
            getSupportActionBar().setTitle(eventNam);
            useref2 = rootref2.getReference().child(USERID).child("All_EVENTS").child(event_id).getRef();
            useref2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        Event e = (Event) dataSnapshot.getValue(Event.class);
                        eventNam = e.getEventName().trim();
                        tourBudget = e.getBudget();
                        exrtaExpenseCheck();
                    }catch (Exception e){
                        Toast.makeText(TourExpence.this, "Not Changed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onDataChange: "+e.getMessage());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: "+databaseError.getMessage().toString());
                }
            });
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        try{
            rootref = FirebaseDatabase.getInstance();
            useref = rootref.getReference().child(USERID).child("All_EVENTS").child(event_id).child("Expense").getRef();
        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        TXP_tourNameTV = findViewById(R.id.TXP_tourName);
        TXP_BudgetStatusTV = findViewById(R.id.TXP_BudgetStatus);
        TXP_expenceProgressTV = findViewById(R.id.TXP_expenceProgress);
        extraAmountTV = findViewById(R.id.extraAmount);
        currentHaveAmoundTV = findViewById(R.id.currentHaveAmound);
        extraSpendIconTV = findViewById(R.id.extraSpendIcon);
        currentSpentIconTV = findViewById(R.id.currentSpentIcon);
        expenceListRecylerIdTV = findViewById(R.id.expenceListRecylerId);

        final View bottomSheet = findViewById(R.id.imageBottomSheet);
        imgCapturedRV = findViewById(R.id.imgCapturedRV);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        RecyclerView.LayoutManager bootmsheetRV = new GridLayoutManager(this, 2);
        imgCapturedRV.setLayoutManager(bootmsheetRV);
        imgCapturedRV.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager layoutManager = new LinearLayoutManager(TourExpence.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        expenceListRecylerIdTV.setLayoutManager(layoutManager);

        try{
            TXP_tourNameTV.setText(event.getEventName());
        }catch (Exception e){
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        try {
        useref.child("ExtraExpense").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Expense expense = (Expense) dataSnapshot.getValue(Expense.class);
                    extraAmountTV.setText(String.valueOf(expense.getExtraExpense())+"TK");
                    extraExpense = expense.getExtraExpense();

                }catch (Exception e){
                    Log.d(TAG, "onDataChange: "+e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        extraSpendIconTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TourExpence.this);
                final EditText txt = new EditText(TourExpence.this);
                txt.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setTitle("Add Extra Expence.").setView(txt).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            double exex = Double.parseDouble(String.valueOf(txt.getText()));
                            double ex_ex =exex+extraExpense;
                            tourBudget = tourBudget+exex;
                            Expense ex = new Expense(ex_ex);
                            useref.child("ExtraExpense").setValue(ex) ;
                            useref2.child("budget").getRef().setValue(tourBudget);
                            exrtaExpenseCheck();
                        }catch (Exception e){
                            Log.d(TAG, "onClick: "+e.getMessage());
                        }

                    }
                }).setNegativeButton("Cancel",null).show();
            }
        });

        currentSpentIconTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TourExpence.this);
                LayoutInflater inflater = TourExpence.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.add_expense_dialoge, null);

                final EditText ex_dia_comment;
                final EditText ex_dia_amount;
                ex_dia_comment = view.findViewById(R.id.ex_dia_comment);
                ex_dia_amount = view.findViewById(R.id.ex_dia_amount);

                builder.setView(view).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            String comment = ex_dia_comment.getText().toString();
                            double amount = Double.parseDouble(String.valueOf(ex_dia_amount.getText()));
                            if (amount > ex_have && (ex_have-amount) <= 0){
                                Toast.makeText(TourExpence.this, "Your must Check Your Total Balance", Toast.LENGTH_SHORT).show();
                                Toast.makeText(TourExpence.this, "Add this balance Extra Budget", Toast.LENGTH_SHORT).show();
                            }else if ((ex_have-amount) != -1 && (ex_have-amount) >-1){
                                final String pushKey = useref.child("ExpenseList").push().getKey();
                                expenseList = new ExpenseList(comment,amount,pushKey);
                                useref.child("ExpenseList").child(pushKey).setValue(expenseList);
                            }else {
                                Toast.makeText(TourExpence.this, " Insufficient balance", Toast.LENGTH_SHORT).show();
                            }

                        }catch (Exception e){
                            Log.d(TAG, "onClick: "+e.getMessage());
                        }

                    }
                })
                        .setNegativeButton("Cancel",null)
                        .show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_expense_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.camera:
                openCamera();
                break;
            case R.id.gallery:
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                photoList();
                break;
            case R.id.logoutevent:
                auth.signOut();
                startActivity(new Intent(TourExpence.this,LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (intent.resolveActivity(getPackageManager()) != null && isStoragePermissionGranted() ==true) {
            // Create the File where the photo should go
            File photoFile = null;

                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.d(TAG, "openCamera: 3"+ex.getMessage());
                }

            if (photoFile != null ) {
                Uri photoURI = null;
                try {
                     photoURI = Uri.fromFile(photoFile);
                }catch (Exception e){
                    Log.d(TAG, "openCamera: "+e.getMessage());
                }
                try {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                }catch (Exception e){
                    Log.d(TAG, "openCamera: "+e.getMessage());
                }
                startActivityForResult(intent, 121);
            }
        }
    }

    private File createImageFile() throws IOException {

        File fileBase,storageDir= null;
        try {
            String filepath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).getAbsolutePath();
            fileBase = new File(filepath, "TourMate").getAbsoluteFile();
             storageDir = new File(fileBase,eventNam);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
        }catch (Exception e){
            Log.d(TAG, "createImageFile: "+e.getMessage());
        }


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 121 && resultCode == RESULT_OK) {
            photoUpload();
            openCamera();
            Toast.makeText(this, "Image Save", Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == RESULT_CANCELED){
            try {
                String filepath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).getAbsolutePath();
                File fileBase = new File(filepath, "TourMate").getAbsoluteFile();
                File st = new File(fileBase,eventNam);
                List<File> list = GetFiles(st.toString());
                boolean f = list.get(list.size()-1).delete();
            }catch (Exception e){
                Log.d(TAG, "onActivityResult: "+e.getMessage());
            }
        }
    }

    public ArrayList<File> GetFiles(String DirectoryPath) {
        ArrayList<File> MyFiles = new ArrayList<File>();
        try {
            File f = new File(DirectoryPath);
            File[] files = f.listFiles();
            if (files.length == 0)
                return null;
            else {
                for (int i=0; i<files.length; i++)
                    MyFiles.add(files[i].getAbsoluteFile());
            }
        }catch (Exception e){
            Log.d(TAG, "GetFiles: "+e.getMessage());
        }
        return MyFiles;
    }

    private void exrtaExpenseCheck(){
        try {
            useref.child("ExpenseList").addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {
                        try {
                            double ex =0;
                            expenseLists.clear();
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                ExpenseList expsense = (ExpenseList) d.getValue(ExpenseList.class);
                                ex =ex+expsense.getAmount();
                                expenseLists.add(expsense);
                            }

                            int prog = (int) ((int) ex*100/tourBudget);
                            TXP_expenceProgressTV.setProgress(prog);
                            TXP_BudgetStatusTV.setText("Budget Status ("+ex+"/"+tourBudget+") Tk");
                            ex_have = tourBudget - ex;
                            currentHaveAmoundTV.setText("You have " + ex_have + "Tk");
                            Collections.reverse(expenseLists);
                            setAdapter = new ExpenseListAdapter(expenseLists);
                            expenceListRecylerIdTV.setAdapter(setAdapter);
                        } catch (Exception e) {
                            Log.d(TAG, "onDataChange: "+e.getMessage());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: "+databaseError.getMessage().toString());
                }
            });
        }catch (Exception e){
            Log.d(TAG, "exrtaExpenseCheck: "+e.getMessage());
        }
    }

    private void photoList(){
        try {
            String filepath = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).getAbsolutePath();
            File fileBase = new File(filepath, "TourMate").getAbsoluteFile();
            storageDir = new File(fileBase,eventNam);
            if (storageDir.exists()){
                List<File> allFIles = GetFiles( storageDir.toString());
                Collections.reverse(allFIles);
                caputuredImageAdapter = new CaputuredImageAdapter(allFIles);
                imgCapturedRV.setAdapter(caputuredImageAdapter);

            }
        }catch (Exception e){
            Log.d(TAG, "onOptionsItemSelected: "+e.getMessage());
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public  boolean isInternatePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    private void photoUpload(){
        if (isInternatePermissionGranted()){
                try {
                    String filepath2 = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM).getAbsolutePath();
                    File fileBase = new File(filepath2, "TourMate").getAbsoluteFile();
                    File std = new File(fileBase,eventNam);
                    List<File> allFIle = GetFiles(std.toString());
                    Uri file = Uri.fromFile(allFIle.get(allFIle.size()-1).getAbsoluteFile());
                    StorageReference riversRef = storageReference.child(USERID).child(eventNam+"/"+file.getLastPathSegment());
                    UploadTask uploadTask = riversRef.putFile(file);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(TourExpence.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(TourExpence.this, "Successfully Upload", Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch (Exception e){
                    Log.d(TAG, "photoUpload: "+e.getMessage());
                    Toast.makeText(this, "Upload fail", Toast.LENGTH_SHORT).show();
                }

            }
    }

}
