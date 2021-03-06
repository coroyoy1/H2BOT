package com.example.administrator.h2bot.tpaaffiliate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.adapter.TPAAcceptedOrdersAdapter;
import com.example.administrator.h2bot.adapter.WPInProgressOrdersAdapter;
import com.example.administrator.h2bot.dealer.WPInProgressFragment;
import com.example.administrator.h2bot.models.AffiliateStationOrderModel;
import com.example.administrator.h2bot.models.OrderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TPAAcceptedFragment extends Fragment {
    RecyclerView recyclerView;

    private TPAAcceptedOrdersAdapter POAdapter;
    private List<AffiliateStationOrderModel> uploadPO;
    RelativeLayout noOrdersLayout;
    String transactionNo, customerId, stationId;
    Bundle bundle;
    public TPAAcceptedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tpaaccepted, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        noOrdersLayout = view.findViewById(R.id.noOrdersLayout);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        uploadPO = new ArrayList<>();

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Affiliate_WaterStation_Order_File").child(firebaseUser.getUid());
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uploadPO.clear();
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    for (DataSnapshot post1 : post.getChildren()) {
                        for (DataSnapshot post2 : post1.getChildren()) {
                            AffiliateStationOrderModel orderModel = post2.getValue(AffiliateStationOrderModel.class);
                            if (orderModel != null) {
                                Log.d("Hi", "Hi");
                                if (orderModel.getStatus().equals("Accepted by affiliate") || orderModel.getStatus().equals("Dispatched by affiliate")) {
                                    noOrdersLayout.setVisibility(View.INVISIBLE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    uploadPO.add(orderModel);
                                }
                            }
                        }
                    }
                    POAdapter = new TPAAcceptedOrdersAdapter(getActivity(), uploadPO);
                    recyclerView.setAdapter(POAdapter);
                    POAdapter.setOnItemClickListener(TPAAcceptedFragment.this::onItemClick);
                    if (uploadPO.size() == 0) {
                        noOrdersLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                    {
                        attemptToExit();
                        return true;
                    }
                }
                return false;
            }
        });
        return view;
    }

    private void onItemClick(int i) {
    }

    public void attemptToExit()
    {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        getActivity().finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to exit the application?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

}
