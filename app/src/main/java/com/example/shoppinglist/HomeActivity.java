package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

import Model.Data;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionButton fab_button;
    private FloatingActionButton fab_delete_button;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;

    private Toolbar toolbar;

    private String name;
    private String amount;
    private String note;
    private String post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        toolbar=findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shopping list");

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();

        String uId = mUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Shopping list").child(uId);

        mDatabase.keepSynced(true);


        recyclerView=findViewById(R.id.recycler_home);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        fab_button=findViewById(R.id.fab);
        fab_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog();
            }
        });

        fab_delete_button=findViewById(R.id.fabDelete);
        fab_delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDeleteDialog();
            }
        });
    }

    private void customDialog()
    {
        AlertDialog.Builder myDialog=new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View myView = inflater.inflate(R.layout.input_data, null);
        AlertDialog dialog = myDialog.create();
        dialog.setView(myView);

        EditText name = myView.findViewById(R.id.edt_name);
        EditText amount = myView.findViewById(R.id.edt_amount);
        EditText note = myView.findViewById(R.id.edt_note);
        Button saveButton = myView.findViewById(R.id.save_btn);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mName=name.getText().toString().trim();
                String mAmount = amount.getText().toString().trim();
                String mNote = note.getText().toString().trim();

                if(TextUtils.isEmpty(mName))
                {
                    name.setError("Required field");
                    return;
                }

                String id = mDatabase.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(mName, mAmount, mNote, date, id);

                mDatabase.child(id).setValue(data);

                Toast.makeText(getApplicationContext(), "Data added", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void customDeleteDialog(){

        AlertDialog.Builder myDialog=new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View myView = inflater.inflate(R.layout.delete_all_items, null);
        AlertDialog dialog = myDialog.create();
        dialog.setView(myView);

        Button cancelDeleteButton = myView.findViewById(R.id.cancelDelete_btn);
        Button confirmDeleteButton = myView.findViewById(R.id.confirmDelete_btn);

        confirmDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.removeValue();

                Toast.makeText(getApplicationContext(), "All data deleted", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });
        dialog.show();

        cancelDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder>adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.item_data,
                        MyViewHolder.class,
                        mDatabase
                )
        {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, Data model, int position) {

                viewHolder.setDate(model.getDate());
                viewHolder.setName(model.getName());
                viewHolder.setNote(model.getNote());
                viewHolder.setAmount(model.getAmount());

                viewHolder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        post_key=getRef(position).getKey();
                        name = model.getName();
                        note = model.getNote();
                        amount = model.getAmount();


                        updateData();
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View myview;

        public MyViewHolder(View itemView) {
            super(itemView);
            myview=itemView;
        }

        public void setName(String name){
            TextView mName = myview.findViewById(R.id.name);
            mName.setText(name);
        }

        public void setNote(String note){
            TextView mNote = myview.findViewById(R.id.note);
            mNote.setText(note);
        }

        public void setDate(String date){
            TextView mDate = myview.findViewById(R.id.date);
            mDate.setText(date);
        }
        
        public void setAmount(String amount){
            TextView mAmount = myview.findViewById(R.id.amount);
            mAmount.setText(amount);
        }

    }

    public void updateData()
    {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);

        View mView = inflater.inflate(R.layout.update_item_data, null);

        AlertDialog dialog = myDialog.create();
        dialog.setView(mView);

        final EditText edtName=mView.findViewById(R.id.edt_name_update);
        final EditText edtAmount=mView.findViewById(R.id.edt_amount_update);
        final EditText edtNote=mView.findViewById(R.id.edt_note_update);

        edtName.setText(name);
        edtName.setSelection(name.length());
        edtAmount.setText(amount);
        edtAmount.setSelection(amount.length());
        edtNote.setText(note);
        edtNote.setSelection(note.length());

        Button updateButton=mView.findViewById(R.id.update_btn);
        Button deleteButton=mView.findViewById(R.id.delete_btn);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = edtName.getText().toString().trim();
                amount = edtAmount.getText().toString().trim();
                note = edtNote.getText().toString().trim();

                String date=DateFormat.getDateInstance().format(new Date());
                Data data = new Data(name, amount, note, date, post_key);

                mDatabase.child(post_key).setValue(data);

                dialog.dismiss();

                Toast.makeText(getApplicationContext(), "Data updated", Toast.LENGTH_SHORT).show();

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabase.child(post_key).removeValue();

                dialog.dismiss();

                Toast.makeText(getApplicationContext(), "Data deleted", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.log_out:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}