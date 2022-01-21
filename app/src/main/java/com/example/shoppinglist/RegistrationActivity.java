package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private EditText repeatPassword;
    private TextView signin;
    private Button registrationButton;

    private FirebaseAuth mAuth;

    private ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        
        mAuth=FirebaseAuth.getInstance();

        mDialog=new ProgressDialog(this);

        email=findViewById(R.id.email_registration);
        password=findViewById(R.id.password_registration);
        repeatPassword = findViewById(R.id.repeat_password_registration);
        registrationButton=findViewById(R.id.btn_registration);
        signin=findViewById(R.id.signin_text);

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();
                String mRepeatPassword = repeatPassword.getText().toString().trim();

                if(TextUtils.isEmpty(mEmail))
                {
                    email.setError("Required field");
                    return;
                }
                if(TextUtils.isEmpty(mPassword))
                {
                    password.setError("Required field");
                    return;
                }
                if(TextUtils.isEmpty(mRepeatPassword))
                {
                    repeatPassword.setError("Required field");
                    return;
                }
                if(mPassword.length()<8)
                {
                    password.setError("Password must contain at least 8 characters");
                    return;
                }
                if(!mPassword.equals(mRepeatPassword))
                {
                    Toast.makeText(getApplicationContext(), "Passwords match", Toast.LENGTH_SHORT).show();
                    return;
                }

                mDialog.setMessage("Processing");
                mDialog.show();

                mAuth.createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                            Toast.makeText(getApplicationContext(), "Succesfull", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }

                    }
                });
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}