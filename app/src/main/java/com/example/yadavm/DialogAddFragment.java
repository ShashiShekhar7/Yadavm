package com.example.yadavm;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;
import com.example.yadavm.Models.CartMo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class DialogAddFragment extends DialogFragment {
private TextView textViewItemName;
private ImageView imageViewItemImage;
public ImageButton imageButtonPluskg,imageButtonPlusgm,imageButtonPluspcs,imageButtonMinuskg,imageButtonMinusgm,imageButtonMinuspcs;

public  TextView textViewkg,textViewgm,textViewpcs,textViewKgValue,textViewGmValue,textViewPcsValue,textViewtotal;

public  String name,image,pricekg,pricepcs,itemid;
public LinearLayout linearLayoutkg,linearLayoutgm,linearLayoutpcs;
public int pricekgint,pricegmint,pricepcsint;

public int totalPrice ;

public FirebaseDatabase database;
public DatabaseReference reference;

public Button buttonAddtocart;

public ProgressDialog dialog;

FirebaseAuth firebaseAuth;
FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.dialog_item, container, false);

        dialog = new ProgressDialog(getActivity());


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database =FirebaseDatabase.getInstance();
        reference = database.getReference();

         name = getArguments().getString("itemname");
         image = getArguments().getString("itemimage");
        pricekg = getArguments().getString("itempricekg");
        pricepcs = getArguments().getString("itempricepcs");
        itemid = getArguments().getString("itemid");

pricekgint = Integer.parseInt(pricekg);


pricegmint = pricekgint/10;

pricepcsint = Integer.parseInt(pricepcs);


buttonAddtocart = view.findViewById(R.id.add_to_cart_button);



        linearLayoutkg =view.findViewById(R.id.layout_kg);
        linearLayoutgm =view.findViewById(R.id.layout_gm);
        linearLayoutpcs =view.findViewById(R.id.layout_pcs);

        textViewtotal = view.findViewById(R.id.total);




        textViewkg = view.findViewById(R.id.text_kg);
        textViewgm = view.findViewById(R.id.text_gm);
        textViewpcs = view.findViewById(R.id.text_pcs);



        textViewKgValue = view.findViewById(R.id.text_kg_value);
        textViewGmValue = view.findViewById(R.id.text_gm_value);
        textViewPcsValue = view.findViewById(R.id.text_pcs_value);

        imageButtonPluskg = view.findViewById(R.id.plus_button_kg);
        imageButtonPlusgm = view.findViewById(R.id.plus_button_gm);
        imageButtonPluspcs = view.findViewById(R.id.plus_button_pcs);

        imageButtonMinuskg = view.findViewById(R.id.minus_button_kg);
        imageButtonMinusgm = view.findViewById(R.id.minus_button_gm);
        imageButtonMinuspcs = view.findViewById(R.id.minus_button_pcs);







        if (pricepcs.equals("0")){
           linearLayoutpcs.setVisibility(View.GONE);

        }
        else if (pricekg.equals("0")){
           linearLayoutgm.setVisibility(View.GONE);
           linearLayoutkg.setVisibility(View.GONE);
        }




        imageButtonPluskg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                plusButton(textViewkg,textViewKgValue,pricekgint);

                totalPriceFun();


            }
        });



        imageButtonPlusgm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusButton(textViewgm,textViewGmValue,pricegmint);
                totalPriceFun();


            }
        });


        imageButtonPluspcs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 plusButton(textViewpcs,textViewPcsValue,pricepcsint);
                 totalPriceFun();

            }
        });
        imageButtonMinuskg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        minusButton(textViewkg,textViewKgValue,pricekgint);
                        totalPriceFun();


                    }
                });
        imageButtonMinusgm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusButton(textViewgm,textViewGmValue,pricegmint);
                totalPriceFun();


            }
        });
        imageButtonMinuspcs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               minusButton(textViewpcs,textViewPcsValue,pricepcsint);

               totalPriceFun();
            }

        });
        textViewItemName = view.findViewById(R.id.item_name_d);
        imageViewItemImage = view.findViewById(R.id.item_image_d);
        textViewItemName.setText(name);


        Glide.with(getActivity()).load(image).into(imageViewItemImage);


        buttonAddtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartMo cartMo = new CartMo();
                cartMo.setItemName(name);
                if (Integer.parseInt(textViewtotal.getText().toString().trim()) <= 100) {
                    cartMo.setDeliveryCharge("0");

                }
                else {
                    cartMo.setDeliveryCharge("20");
                }
                cartMo.setItemImage(image);
                cartMo.setItemId(itemid);
                cartMo.setItemPriceprkg(pricekg);
                cartMo.setItemPriceprpcs(pricepcs);
                cartMo.setItemPricekg(textViewKgValue.getText().toString().trim());
                cartMo.setItemPricegm(textViewGmValue.getText().toString().trim());
                cartMo.setItemPricepcs(textViewPcsValue.getText().toString().trim());
                cartMo.setItemPricetotal(textViewtotal.getText().toString().trim());
                cartMo.setItemQuantitykg(textViewkg.getText().toString().trim());
                cartMo.setItemQuantitygm(textViewgm.getText().toString().trim());
                cartMo.setItemQuantitypcs(textViewpcs.getText().toString().trim());

                dialog.show();
                reference.child("User").child(user.getPhoneNumber()).child("Carts").child(itemid).setValue(cartMo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                dialog.dismiss();
                            }
                        });

                getDialog().dismiss();
            }
        });
        return view;
    }

    private void totalPriceFun() {

        if (pricekg.equals("0")){
            String totalPricestr = textViewPcsValue.getText().toString().trim();
            totalPrice = Integer.parseInt(totalPricestr);
            textViewtotal.setText(String.valueOf(totalPrice));
        }
        else if(pricepcs.equals("0")){
            String totalPricestra = textViewKgValue.getText().toString().trim();
            String totalPricestrb = textViewGmValue.getText().toString().trim();

            totalPrice = Integer.parseInt(totalPricestra)+Integer.parseInt(totalPricestrb);
            textViewtotal.setText(String.valueOf(totalPrice));

        }
        else {
            String totalPricestra = textViewKgValue.getText().toString().trim();
            String totalPricestrb = textViewGmValue.getText().toString().trim();
            String totalPricestrc = textViewPcsValue.getText().toString().trim();

            totalPrice = Integer.parseInt(totalPricestra)+Integer.parseInt(totalPricestrb)+Integer.parseInt(totalPricestrc);
            textViewtotal.setText(String.valueOf(totalPrice));
        }
    }

    private void minusButton(TextView textViewa,TextView textViewb,int price) {
        String stringVal,str,stringRes;

        str  = textViewa.getText().toString().trim();
        if (str.equals("0")){
            textViewa.setText("0");
            textViewb.setText("0");
            textViewtotal.setText("0");


        }
        else {
            int prev = Integer.parseInt(str);
            prev--;
            int res = prev*price;
            stringRes = String.valueOf(res);
            stringVal = String.valueOf(prev);
            textViewa.setText(stringVal);
            textViewb.setText(stringRes);




        }
    }

    private void plusButton(TextView textViewa,TextView textViewb,int price) {
        String stringVal,str,stringRes;
         str = textViewa.getText().toString().trim();
        int prev = Integer.parseInt(str);
        prev++;

        int res = prev*price;
        stringRes = String.valueOf(res);
        stringVal = Integer.toString(prev);
        textViewa.setText(stringVal);
        textViewb.setText(stringRes);


    }
}
