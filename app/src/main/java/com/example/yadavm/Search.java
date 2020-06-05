package com.example.yadavm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.yadavm.Adapters.HomeAd;
import com.example.yadavm.Adapters.SearchAd;
import com.example.yadavm.Models.SearchMo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Search extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;

    private RecyclerView recyclerView;
    private HomeAd homeadapter;
    private ArrayList<SearchMo> list;


    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);



        reference = FirebaseDatabase.getInstance().getReference().child("Sweets");


        recyclerView = findViewById(R.id.recycler_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        searchView = findViewById(R.id.search);








    }




    @Override
    protected void onStart() {
        super.onStart();
        if (reference != null)
        {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){
                        list = new ArrayList<>();
                        for (DataSnapshot ds:dataSnapshot.getChildren()){

                            list.add(ds.getValue(SearchMo.class));
                        }
                        SearchAd searchAd = new SearchAd(list);
                   recyclerView.setAdapter(searchAd);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(Search.this, "hello", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    searchFun(newText);
                    return true;
                }
            });
        }

    }

    private void searchFun(String newText) {
        ArrayList<SearchMo> myList = new ArrayList<>();
        for (SearchMo object : list){
            if (object.getItemName().toLowerCase().contains(newText.toLowerCase())){
                myList.add(object);
            }
        }
        SearchAd searchAd = new SearchAd(myList);
        recyclerView.setAdapter(searchAd);
    }
}
