package com.example.administrator.h2bot.deliveryman;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.adapter.DMCompletedOrderAdapter;
import com.example.administrator.h2bot.adapter.DMInProgressOrdersAdapter;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.example.administrator.h2bot.models.UserWSDMFile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DMCompleteFragment extends Fragment{
    private RecyclerView recyclerView;
    private DMCompletedOrderAdapter POAdapter;
    private List<OrderModel> uploadPO;
    private List<UserWSDMFile> uploadDM;
    FirebaseUser firebaseUser;
    String firebaseUID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dm_completeorder, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewDMCom);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUID = firebaseUser.getUid();

        uploadPO = new ArrayList<>();

        displayData();

        return view;
    }

    public void displayData()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_DM_File");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren())
                    {
                        UserWSDMFile userWSDMFile = dataSnapshot2.getValue(UserWSDMFile.class);
                        if(userWSDMFile != null)
                        {
                            String merchantId = userWSDMFile.getStation_id();
                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Customer_File");
                            reference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    uploadPO.clear();
                                    for (DataSnapshot dataSnapshot3 : dataSnapshot.getChildren())
                                    {
                                        for (DataSnapshot dataSnapshot4 : dataSnapshot3.child(merchantId).getChildren())
                                        {
                                            OrderModel orderModel = dataSnapshot4.getValue(OrderModel.class);
                                            if(orderModel != null)
                                            {
                                                if (orderModel.getOrder_merchant_id().equals(merchantId)
                                                        && orderModel.getOrder_status().equals("In-Progress"))
                                                {
                                                    uploadPO.add(orderModel);
                                                }
                                            }
                                        }
                                        POAdapter = new DMCompletedOrderAdapter(getActivity(), uploadPO);
                                        recyclerView.setAdapter(POAdapter);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }
}
