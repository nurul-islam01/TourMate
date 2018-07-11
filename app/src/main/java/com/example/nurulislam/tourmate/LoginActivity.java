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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private EditText signEmailTV, signPasswordTV;
    private TextView singInErrorTV;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signEmailTV = findViewById(R.id.signEmail);
        signPasswordTV = findViewById(R.id.signPassword);
        singInErrorTV = findViewById(R.id.singInError);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        if (auth.getCurrentUser() != null){
            try {
                finish();
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }catch (Exception e){
                Log.d(TAG, "onCreate: ");
            }
        }
    }

    public void goSignUp(View view) {
        startActivity(new Intent(LoginActivity.this,SingUpActivity.class));
    }

    public void signIn(View view) {
        String email = signEmailTV.getText().toString().trim();
        String password = signPasswordTV.getText().toString().trim();
        if (email.isEmpty()){
            signEmailTV.setError("Email is empty");
        }else if (password.isEmpty()){
            signPasswordTV.setError("Password is empty");
        }else {
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            Task<AuthResult> task = auth.signInWithEmailAndPassword(email,password);
            task.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    try {
                        user = auth.getCurrentUser();
                        if (user.getUid() !=null){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            progressDialog.dismiss();
                        }

                    }catch (Exception e){
                        Log.d(TAG, "onComplete: "+e.getMessage());
                    }
                }
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    singInErrorTV.setText(e.getMessage());
                }
            });
        }


    }
}
