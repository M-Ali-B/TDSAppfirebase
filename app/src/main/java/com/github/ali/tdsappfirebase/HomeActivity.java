package com.github.ali.tdsappfirebase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.ali.tdsappfirebase.Model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    Toolbar mToolbar;
    FloatingActionButton mFloatingActionButton;
    RecyclerView mRecyclerView;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("TDS App");

        mFloatingActionButton = findViewById(R.id.fabID);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddData();
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
       String uid = mFirebaseUser.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("All Data").child(uid);


        mRecyclerView = findViewById(R.id.recycleviewID);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);


    }


    private void AddData() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.inputlayout, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();


        final EditText name = view.findViewById(R.id.name);
        final EditText description = view.findViewById(R.id.description);

        Button save = view.findViewById(R.id.buttonSave);
        Button cancel = view.findViewById(R.id.buttonCancel);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String myName = name.getText().toString();
                String myDescription = description.getText().toString();

                if (TextUtils.isEmpty(myName)) {
                    name.setError("Required Field");
                    return;
                }
                if (TextUtils.isEmpty(myDescription)) {
                    description.setError("Required Field");
                    return;
                }

                String id = mDatabaseReference.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(id, myName, myDescription, mDate);

                mDatabaseReference.child(id).setValue(data);

                Toast.makeText(HomeActivity.this, "Entered Sucessfully ", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
//        Query query = FirebaseDatabase.getInstance()
//                .getReference()
//                .child("All Data")
//                .child(uid)
//                .limitToLast(50);
        Query query = mDatabaseReference
                .limitToLast(50);

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(query, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setName(model.getName());
                holder.setDescription(model.getDescription());
                holder.setDate(model.getDate());

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                View view = LayoutInflater.from(viewGroup.getContext())
//                        .inflate(R.layout.itemlayoutdesign, viewGroup, false);

                View view = getLayoutInflater().inflate(R.layout.itemlayoutdesign,viewGroup,false);
                return new MyViewHolder(view);
            }
        };

        mRecyclerView.setAdapter(adapter);

        adapter.startListening();

    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView nameTextView = mView.findViewById(R.id.name_field);
            nameTextView.setText(name);

        }

        public void setDescription(String description) {

            TextView descriptionTextView = mView.findViewById(R.id.description_field);
            descriptionTextView.setText(description);
        }

        public void setDate(String Date) {
            TextView dateTextView = mView.findViewById(R.id.date);
            dateTextView.setText(Date);
        }
    }


}
