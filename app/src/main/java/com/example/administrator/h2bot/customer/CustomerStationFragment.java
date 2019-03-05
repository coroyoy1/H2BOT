package com.example.administrator.h2bot.customer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.OrderFileModel;
import com.example.administrator.h2bot.models.UserWSBusinessInfoFile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerStationFragment extends Fragment {

    private RecyclerView recyclerView;
    private CustomerStationAdapter customerOrderAdapter;
    private ArrayList<OrderFileModel> orderList;
    private ArrayList<UserWSBusinessInfoFile> businessInfoList;
    private FirebaseUser firebaseUser;
    private DatabaseReference orderFileRef, businessInfoRef;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    DataSnapshot dataSnapshot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_fragment_orders, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        orderList = new ArrayList<OrderFileModel>();
        businessInfoList = new ArrayList<UserWSBusinessInfoFile>();

        orderFileRef = db.getReference("Customer_Order_File").child(firebaseUser.getUid());
        businessInfoRef = db.getReference("User_WS_Business_Info_File");

        businessInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    UserWSBusinessInfoFile infoFile = data.getValue(UserWSBusinessInfoFile.class);
                    orderFileRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot data2: dataSnapshot.getChildren()){
                                OrderFileModel orderFile = data2.getValue(OrderFileModel.class);
                                if(infoFile.getBusiness_id().equals(data2.getKey())){
                                    businessInfoList.add(infoFile);
                                    orderList.add(orderFile);
                                }
                            }
                            customerOrderAdapter = new CustomerStationAdapter(getActivity(), businessInfoList);
                            recyclerView.setAdapter(customerOrderAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getActivity(), "Failed reading data.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed reading data.", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
