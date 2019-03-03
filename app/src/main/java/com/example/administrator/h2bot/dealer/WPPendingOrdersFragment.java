package com.example.administrator.h2bot.dealer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.adapter.PendingListAdapter;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WPPendingOrdersFragment extends Fragment {
    private RecyclerView recyclerViewPOConnect;
    private WPPendingListAdapter POAdapter;
    private List<TransactionHeaderFileModel> uploadPO;
    public WPPendingOrdersFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wppending_orders, container, false);
        recyclerViewPOConnect = view.findViewById(R.id.recyclerViewPOConnect);
        recyclerViewPOConnect.setHasFixedSize(true);
        recyclerViewPOConnect.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uploadPO = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
//                    if(postSnapshot.child())
                    //                  String detUserMerchant = postSnapshot.child("merchant_id").getValue(String.class);
                    TransactionHeaderFileModel transactionHeaderFileModel = postSnapshot.getValue(TransactionHeaderFileModel.class);
                    if(transactionHeaderFileModel.getMerchant_id().equals(firebaseUser.getUid())
                            && transactionHeaderFileModel.getTrans_status().equals("Pending"))
                    {
                        uploadPO.add(transactionHeaderFileModel);
                    }
                }
                POAdapter = new WPPendingListAdapter(getActivity(), uploadPO);
                recyclerViewPOConnect.setAdapter(POAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Data does not exists!");
            }
        });
        return view;
    }
    private void showMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

}
