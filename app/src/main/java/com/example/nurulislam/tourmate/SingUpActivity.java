package com.example.nurulislam.tourmate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nurulislam.tourmate.POJO.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SingUpActivity extends AppCompatActivity {

    public  static final String TAG = "SingUpActivity";
    private EditText userNameTV,userEmailTV,userPhoneTV, userPasswordTV;
    private TextView errorShowTV;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    public  DatabaseReference rootRef;
    private DatabaseReference userRef;
    public  String userid;
    private String name,email,phone,password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        progressDialog = new ProgressDialog(this);
        userNameTV = findViewById(R.id.userName);
        userEmailTV = findViewById(R.id.userEmail);
        userPhoneTV = findViewById(R.id.userPhone);
        userPasswordTV = findViewById(R.id.userPassword);
        errorShowTV = findViewById(R.id.errorShow);
        auth = FirebaseAuth.getInstance();

    }

    public void signUp(View view) {


        name = userNameTV.getText().toString().trim();
        email = userEmailTV.getText().toString().trim();
        phone = userPhoneTV.getText().toString().trim();
        password = userPasswordTV.getText().toString().trim();
        if (name.length()<4){
            errorShowTV.setText("\nName atlest 4 character");
            userNameTV.setError("Please enter your name");
        }
        else if (email.length()<10){
            errorShowTV.setText("\nWrong Email");
            userEmailTV.setError("Plase enter your eamil");
        }
        else if (phone.length()<11 || phone.length() >15){
            errorShowTV.setText("\nWrong Phone number");
            userPhoneTV.setError("Please enter your phone number");
        }
        else if (password.length() < 5){
            errorShowTV.setText("\nPlease enter your password more then 6 character");
            userPasswordTV.setError("Please enter your password more then 6 character");
        }else if (name.length() >= 3 && email.length() >=10 && phone.length() >=11 &&  password.length() >=6 ){
            progressDialog.setMessage("Loading....");
            progressDialog.show();
            Task<AuthResult> task = auth.createUserWithEmailAndPassword(email,password);
            task.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        try {
                            rootRef = FirebaseDatabase.getInstance().getReference();
                            currentUser = auth.getCurrentUser();
                            userid = currentUser.getUid();
                            userRef = rootRef.child(userid);
                            SignUp user = new SignUp(name,email,phone,userid);
                            userRef.child("USER_PROFILE").setValue(user);
                            Toast.makeText(SingUpActivity.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SingUpActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            progressDialog.dismiss();
                        }catch (Exception e){
                            Log.d(TAG, "onComplete: "+e.getMessage());
                        }
                    }else if (task.isCanceled()){
                        progressDialog.dismiss();
                        errorShowTV.setText("Regestration Failed");
                    }

                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(SingUpActivity.this, "Regestration is not complete try again", Toast.LENGTH_SHORT).show();
                    errorShowTV.setText("Regestration Failed");
                }
            });
        }  else {
            errorShowTV.setText("Regestration Failed");
        }

    }


}
