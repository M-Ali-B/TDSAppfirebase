package com.github.ali.tdsappfirebase;

import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
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

    private String POST_KEY;
    private String POST_NAME;
    private String POST_DESCRIPTION;


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
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull final Data model) {
                holder.setName(model.getName());
                holder.setDescription(model.getDescription());
                holder.setDate(model.getDate());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        POST_KEY = getRef(position).getKey();
                        POST_NAME = model.getName();
                        POST_DESCRIPTION = model.getDescription();
                        updateData();
                    }
                });

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                View view = LayoutInflater.from(viewGroup.getContext())
//                        .inflate(R.layout.itemlayoutdesign, viewGroup, false);

                View view = getLayoutInflater().inflate(R.layout.itemlayoutdesign, viewGroup, false);
                return new MyViewHolder(view);
            }
        };

        mRecyclerView.setAdapter(adapter);

        adapter.startListening();

    }

    private void updateData() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View mView = layoutInflater.inflate(R.layout.updatelayout, null);
        builder.setView(mView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();


        final EditText name = mView.findViewById(R.id.name);
        final EditText description = mView.findViewById(R.id.description);

        name.setText(POST_NAME);
        name.setSelection(POST_NAME.length());
        description.setText(POST_DESCRIPTION);
        description.setSelection(POST_DESCRIPTION.length());

//        String myName = name.getText().toString().trim();
//        String myDescription = description.getText().toString().trim();

        Button update = mView.findViewById(R.id.buttonUpdate);
        Button delete = mView.findViewById(R.id.buttonDelete);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myName = name.getText().toString().trim();
                String myDescription = description.getText().toString().trim();

                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(POST_KEY, myName, myDescription, mDate);
                mDatabaseReference.child(POST_KEY).setValue(data);
                dialog.dismiss();

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseReference.child(POST_KEY).removeValue();
                dialog.dismiss();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.sidemenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.signout:
                mFirebaseAuth.signOut();
                startActivity(new Intent(this,MainActivity.class));
                break;

        }

        return super.onOptionsItemSelected(item);
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
