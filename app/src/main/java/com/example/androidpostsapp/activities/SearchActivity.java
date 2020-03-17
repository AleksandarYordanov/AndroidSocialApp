package com.example.androidpostsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.example.androidpostsapp.R;
import com.example.androidpostsapp.databinding.ActivitySearchBinding;
import com.example.androidpostsapp.models.User;
import com.example.androidpostsapp.storage.GlideApp;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {



    private DatabaseReference mReference;
    private StorageReference mStorageRef;

    private ActivitySearchBinding binding;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    FirebaseFirestore rootRef;
    CollectionReference productsRef ;
    private Index index;
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        binding = DataBindingUtil.setContentView(this,R.layout.activity_search);

        rootRef = FirebaseFirestore.getInstance();
      productsRef  = rootRef.collection("products");



        authenticate();

        setBindingElements();

        client = new Client("4WTJKK5KGA", "b93b3643dd212d0ed911be478c39f3e3");
        index = client.getIndex("users");



    }

    private void setBindingElements() {
        setRecycleView();

        searchView();
        mReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        itemSelectListenerMenue();


        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                Query query = new Query(editable.toString())
                       .setHitsPerPage(50);
                index.searchAsync(query, new CompletionHandler() {
                    @Override
                    public void requestCompleted(JSONObject content, AlgoliaException error) {
                        try {
                            JSONArray hits = content.getJSONArray("hits");
                            List<User> list = new ArrayList<>();
                            for (int i = 0; i < hits.length(); i++) {

                                JSONObject jsonObject = hits.getJSONObject(i);
                                User user;
                                Gson gson = new Gson();
                                user= gson.fromJson(jsonObject.toString(),User.class);
                                list.add(user);
                            }
                            SearchUsersAdapter adapter = new SearchUsersAdapter(list,SearchActivity.this);
                            binding.recycleViewSearch.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void searchView() {

    }

    private void setRecycleView() {

        binding.recycleViewSearch.setLayoutManager(new LinearLayoutManager(this));
       // binding.recycleViewSearch.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));



    }



    private void itemSelectListenerMenue() {
        //Set Search Selected
        binding.bottomNavigation.setSelectedItemId(R.id.search);
        //Perform Item Select Listener
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.chat:
                        startActivity(new Intent(getApplicationContext(), ChatHolderActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    private void authenticate() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        String uid = mFirebaseUser.getUid();
        System.out.println();
    }


}
