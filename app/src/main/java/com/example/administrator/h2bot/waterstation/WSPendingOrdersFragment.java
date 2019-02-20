package com.example.administrator.h2bot.waterstation;

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
import com.example.administrator.h2bot.models.PendingListAdapter;
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

public class WSPendingOrdersFragment extends Fragment {

    private RecyclerView recyclerViewPOConnect;
    private PendingListAdapter POAdapter;
    private List<TransactionHeaderFileModel> uploadPO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_ws_pendingorders, container, false);

        recyclerViewPOConnect = view.findViewById(R.id.recylcerViewPO);
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
                    TransactionHeaderFileModel transactionHeaderFileModel = postSnapshot.getValue(TransactionHeaderFileModel.class);
                    if(transactionHeaderFileModel.getMerchant_id().equals(firebaseUser.getUid())
                            && transactionHeaderFileModel.getTrans_status().equals("Pending"))
                    {
                        uploadPO.add(transactionHeaderFileModel);
                    }
                }
                POAdapter = new PendingListAdapter(getActivity(), uploadPO);
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
