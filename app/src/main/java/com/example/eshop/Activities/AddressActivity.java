package com.example.eshop.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.example.eshop.Adapter.AddressAdapter;
import com.example.eshop.Models.AddressModel;
import com.example.eshop.Models.NewProductModel;
import com.example.eshop.Models.PopularProductModel;
import com.example.eshop.Models.ShowAllModel;
import com.example.eshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.SelectedAddress {

    Button addAddress;
    RecyclerView recyclerView;
    private List<AddressModel> addressModelList;
    private AddressAdapter addressAdapter;

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    Button addAddressBtn,paymentBtn;
    Toolbar toolbar;

    String mAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        toolbar=findViewById(R.id.address_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        //get Data from detailed activity
        Object obj = getIntent().getSerializableExtra("item");

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.address_recycler);
       paymentBtn = findViewById(R.id.payment_btn);
        addAddressBtn = findViewById(R.id.add_address_btn);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addressModelList = new ArrayList<>();
        addressAdapter = new AddressAdapter(getApplicationContext(),addressModelList,this);
        recyclerView.setAdapter(addressAdapter);

        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("Address").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                AddressModel addressModel = doc.toObject(AddressModel.class);
                                addressModelList.add(addressModel);
                            }
                            // Notify the adapter that the dataset has changed
                            addressAdapter.notifyDataSetChanged();
                        } else {
                            // Handle the case where the task was not successful
                        }
                    }
                });
        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddressActivity.this, AddAddressActivity.class));
            }
        });

         paymentBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 double amount = 0.0;

                 if (obj instanceof NewProductModel){
                     NewProductModel newProductModel = (NewProductModel) obj;
                     amount = newProductModel.getPrice();
                 }
                 if (obj instanceof PopularProductModel){
                     PopularProductModel popularProductModel = (PopularProductModel) obj;
                     amount = popularProductModel.getPrice();
                 }
                 if (obj instanceof ShowAllModel){
                    ShowAllModel showAllModel = (ShowAllModel) obj;
                     amount = showAllModel.getPrice();
                 }

                 Intent intent = new Intent(AddressActivity.this,PaymentActivity.class);
                 intent.putExtra("amount",amount);
                 startActivity(intent);
             }
         });




    }

    @Override
    public void setAddress(String address) {

        mAddress = address;

    }
}