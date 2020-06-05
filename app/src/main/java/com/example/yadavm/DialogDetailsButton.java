package com.example.yadavm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yadavm.Adapters.CartPlaceButtonAd;
import com.example.yadavm.Models.CartMo;
import com.example.yadavm.Models.OrderItemsMo;
import com.example.yadavm.Models.OrderMo;
import com.example.yadavm.Models.PriceMo;
import com.example.yadavm.Models.UserMo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class DialogDetailsButton extends DialogFragment {
    private TextView orderId,orderAddress,textTotal,deliveryChargeText,textViewGlobaltotal;
    DatabaseReference reference;

    Button buttonConfirm;

    RecyclerView recyclerView;

    private CartPlaceButtonAd cartPlaceButtonAd;
    private ArrayList<CartMo> cartMoList;

    int orderidint;
    int globaltotal;

    String allItems;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_cart_place_button,container,false);

        reference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        orderId = view.findViewById(R.id.order_id);
        Random rnd = new Random();
        orderidint = 100000 + rnd.nextInt(900000);
        orderId.setText(String.valueOf("ORDER ID#"+orderidint));
        orderAddress = view.findViewById(R.id.text_address);
        textTotal = view.findViewById(R.id.text_tota);
        deliveryChargeText = view.findViewById(R.id.text_delivery);
        textViewGlobaltotal = view.findViewById(R.id.global_total_text);
        recyclerView = view.findViewById(R.id.recycler_dialog_order);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartMoList = new ArrayList<>();
        cartPlaceButtonAd = new CartPlaceButtonAd(cartMoList);
        recyclerView.setAdapter(cartPlaceButtonAd);
        readPost();
        buttonConfirm = view.findViewById(R.id.button_confirm_plce);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                OrderMo orderMo = new OrderMo();
                orderMo.setOrderId(String.valueOf(orderidint));
                orderMo.setOrderTotal(String.valueOf(globaltotal));

                DateFormat dateFormattime = new SimpleDateFormat("HH:mm");
                Date time = new Date();

                DateFormat dateFormatdate = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();

                orderMo.setOrderItems(allItems);
                orderMo.setOrderStatus("1");
                orderMo.setOrderTime(dateFormattime.format(new Date()));
                orderMo.setOrderDate(dateFormatdate.format(new Date()));

                reference.child("User").child(user.getPhoneNumber()).child("My Orders").child(String.valueOf(orderidint)).setValue(orderMo);

                reference.child("User").child(user.getPhoneNumber()).child("Carts").removeValue();


                getDialog().dismiss();
            }
        });


        reference.child("User").child(user.getPhoneNumber()).child("Carts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String items="";
                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    OrderItemsMo orderItemsMo = ds.getValue(OrderItemsMo.class);
                    items = items+"\n"+orderItemsMo.getItemName();

                    allItems = items;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child(user.getPhoneNumber()).child("User").child(user.getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserMo userMo = dataSnapshot.getValue(UserMo.class);


                String orderaddress = userMo.getAddress();
                orderAddress.setText(orderaddress);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child(user.getPhoneNumber()).child("Carts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int sum = 0;
                for (DataSnapshot ds :dataSnapshot.getChildren()){

                    PriceMo priceMo = ds.getValue(PriceMo.class);

                    String ff = priceMo.getItemPricetotal();
                    String deliverycharge = priceMo.getDeliveryCharge();


                    int deliveryChar = Integer.parseInt(deliverycharge);

                    int pValue = Integer.parseInt(String.valueOf(ff));
                    sum +=pValue;
                    globaltotal = deliveryChar+sum;
                    String globalStr = String.valueOf(globaltotal);

                    String d = String.valueOf(sum);
                    textTotal.setText("Rs."+d);
                    if (sum>=100){
                        deliveryChargeText.setText("Rs.20");
                    }
                    else {
                        deliveryChargeText.setText("Rs.0");
                    }

                    textViewGlobaltotal.setText("Total Rs."+globalStr);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }



    private void readPost(){
        reference.keepSynced(true);
        reference.child("User").child(user.getPhoneNumber()).child("Carts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //pdialog.show();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    CartMo shopmodal = dataSnapshot1.getValue(CartMo.class);
                    cartMoList.add(shopmodal);
                    cartPlaceButtonAd.notifyDataSetChanged();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });




    }

}
