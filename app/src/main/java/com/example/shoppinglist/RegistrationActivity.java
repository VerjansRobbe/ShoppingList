package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
                //Alle velden worden uitgelezen, naar een string omgezet, getrimd en in variabelen gestoken
                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();
                String mRepeatPassword = repeatPassword.getText().toString().trim();

                //Roept de methode isConnected aan om te zien of er wel een internetverbinding is.
                if(!isConnected(this))
                {
                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                    return;
                }

                //het veld "email" moet ingevuld zijn
                if(TextUtils.isEmpty(mEmail))
                {
                    email.setError("Required field");
                    return;
                }
                //Het veld "wachtwoord" moet ingevuld zijn
                if(TextUtils.isEmpty(mPassword))
                {
                    password.setError("Required field");
                    return;
                }
                //Het veld "herhaal wachtwoord" moet ingevuld zijn
                if(TextUtils.isEmpty(mRepeatPassword))
                {
                    repeatPassword.setError("Required field");
                    return;
                }
                //De lengte van het wachtwoord moet minstens 8 karakters bevatten
                if(mPassword.length()<8)
                {
                    password.setError("Password must contain at least 8 characters");
                    return;
                }
                //Het wachtwoord moet gelijk zijn aan het herhaalde wachtwoord
                if(!mPassword.equals(mRepeatPassword))
                {
                    Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                    return;
                }

                mDialog.setMessage("Processing");
                mDialog.show();

                //Hier word de user aangemaakt in de database en word HomeActivity geladen
                mAuth.createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                            Toast.makeText(getApplicationContext(), "Account successfully created", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Account creation failed", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }

                    }
                });
            }
        });

        //Hier word er doorverwezen naar de MainActivity om in te loggen als je al een account zou hebben
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });


    }

    //Hier word er getest of er wel een internetverbinding aanwezig is.
    private boolean isConnected(View.OnClickListener onClickListener) {

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }
        else {
            connected = false;
        }
        return connected;

    }
}