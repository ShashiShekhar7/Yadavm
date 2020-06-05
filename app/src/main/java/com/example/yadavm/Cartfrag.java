package com.example.yadavm;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yadavm.Adapters.CartAd;
import com.example.yadavm.Models.CartMo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Cartfrag extends Fragment {
    FirebaseDatabase database;
    DatabaseReference reference;
    private RecyclerView recyclerView;
    private CartAd cartAd;
    private List<CartMo> mCartMoList;
    private Button buttonPlace;
    private TextView textViewNothing;


    FirebaseAuth firebaseAuth;
    FirebaseUser user;


    public Cartfrag() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cartfrag,container,false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("");

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        ((MainActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("User").child(Objects.requireNonNull(user.getPhoneNumber())).child("Carts");
        recyclerView  = (RecyclerView)view.findViewById(R.id.recycler_cat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCartMoList = new ArrayList<>();
        buttonPlace = (Button)view.findViewById(R.id.button_place);
        textViewNothing = view.findViewById(R.id.text_nothing_in_cart);
        buttonPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DialogPlaceButton dialogPlaceButton = new DialogPlaceButton();
                assert fm != null;
                dialogPlaceButton.show(fm,"Hello");
            }
        });
        readPost();
        return view;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_toolbar,menu);
    }
    private void readPost(){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isAdded()) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.hasChildren()) {
                            buttonPlace.setVisibility(View.VISIBLE);
                        } else {
                            textViewNothing.setVisibility(View.VISIBLE);
                        }

                        mCartMoList = new ArrayList<>();

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            mCartMoList.add(dataSnapshot1.getValue(CartMo.class));
                        }
                        cartAd = new CartAd(getContext(), mCartMoList);
                        recyclerView.setAdapter(cartAd);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.notification){
            Intent intent = new Intent(getActivity(),Notification.class);
            startActivity(intent );
        }
        return super.onOptionsItemSelected(item);

    }


}
