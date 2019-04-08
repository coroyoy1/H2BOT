package com.example.administrator.h2bot.tpaaffiliate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.example.administrator.h2bot.adapter.TPADeliveredOrdersAdapter;
import com.example.administrator.h2bot.adapter.WPCompletedOrdersAdapter;
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

public class TPADeliveredOrdersFragment extends Fragment {
    RecyclerView recyclerView;
    FirebaseUser currentUser;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseRef2;
    private RecyclerView.LayoutManager mLayoutManager;
    private TPADeliveredOrdersAdapter mAdapter;
    private List<AffiliateStationOrderModel> mUploads;
    RelativeLayout noOrdersLayout;
    int count;

    public TPADeliveredOrdersFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tpa_delivered_order_fragment, container, false);
        mUploads = new ArrayList<AffiliateStationOrderModel>();
        recyclerView = view.findViewById(R.id.recyclerView);

        mUploads = new ArrayList<AffiliateStationOrderModel>();
        //mUploads2 = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        noOrdersLayout = view.findViewById(R.id.noOrdersLayout);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currendId = currentUser.getUid();
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Affiliate_WaterStation_Order_File").child(currendId);
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        Log.d("Hi", "Hello");
                        for (DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()) {

                            AffiliateStationOrderModel orderModel = dataSnapshot3.getValue(AffiliateStationOrderModel.class);
                            if (orderModel != null) {
                                Log.d("Hihi", "Hello");

                                if (orderModel.getStatus().equals("Completed with affiliate")) {
                                    Log.d("Hi", "gago");
                                    noOrdersLayout.setVisibility(View.INVISIBLE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    mUploads.add(orderModel);
                                }
                            }
                        }
                    }
                    mAdapter = new TPADeliveredOrdersAdapter(getActivity(), mUploads);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(TPADeliveredOrdersFragment.this::onItemClick);
                }
                if(mUploads.size() == 0)
                {
                    noOrdersLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
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
    private void onItemClick(int i) {
    }
}
