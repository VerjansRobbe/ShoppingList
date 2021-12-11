package com.example.shoppinglist;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toolbar;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar=findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shopping list");
    }
}