package com.github.ali.tdsappfirebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email, password;
    private Button login, signup;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        email = findViewById(R.id.editTextEmail_2);
        password = findViewById(R.id.editTextPassword_2);
        login = findViewById(R.id.buttonLogin_2);
        signup = findViewById(R.id.buttonSignup_2);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);


        signup.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {

                                          final String myEmail = email.getText().toString().trim();
                                          final String myPassword = password.getText().toString().trim();

                                          Log.d("reg", " " + myEmail + " " + myPassword);
                                          if (TextUtils.isEmpty(myEmail)) {
                                              email.setError("Email Field is empty");
                                              return;
                                          }
                                          if (TextUtils.isEmpty(myPassword)) {
                                              password.setError("Password field is empty");
                                              return;
                                          }

                                          mProgressDialog.setMessage("Please Wait");
                                          mProgressDialog.show();

                                          mFirebaseAuth.createUserWithEmailAndPassword(myEmail,myPassword)
                                                 .addOnCompleteListener(RegistrationActivity.this,new OnCompleteListener<AuthResult>() {
                                                     @Override
                                                     public void onComplete(@NonNull Task<AuthResult> task) {

                                                         mProgressDialog.dismiss();
                                                         if (task.isSuccessful()) {
                                                             Toast.makeText(RegistrationActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                                             startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                                         }
                                                         else{
                                                             Toast.makeText(RegistrationActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                                         }

                                                     }
                                                 });

                                      }
                                  });

                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mToMainActivity = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(mToMainActivity);
                        finish();
                    }
                });

    }


}
