package com.example.androidpostsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.example.androidpostsapp.R;
import com.example.androidpostsapp.databinding.ActivitySearchBinding;
import com.example.androidpostsapp.models.AppUser;
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

public class SearchActivity extends BaseActivity {



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



        client = new Client("4WTJKK5KGA", "b93b3643dd212d0ed911be478c39f3e3");
        index = client.getIndex("users");

        setBindingElements();





    }

    private void setBindingElements() {
        setRecycleView();

        searchView();
        mReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        setActionBar(binding.bottomNavigation,R.id.search);


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
                            List<AppUser> list = new ArrayList<>();
                            for (int i = 0; i < hits.length(); i++) {

                                JSONObject jsonObject = hits.getJSONObject(i);
                                AppUser appUser;
                                Gson gson = new Gson();
                                appUser = gson.fromJson(jsonObject.toString(), AppUser.class);
                                list.add(appUser);
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







}
