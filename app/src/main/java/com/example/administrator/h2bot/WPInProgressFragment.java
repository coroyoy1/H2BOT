package com.example.administrator.h2bot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.h2bot.models.TransactionDetailFileModel;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WPInProgressFragment extends Fragment implements WPInProgressAdapter.OnItemClickListener{
    RecyclerView recyclerView;


    public static final String EXTRA_transactionNo = "transactionNo";
    public static final String EXTRA_customerName = "customerName";
    public static final String EXTRA_contactNo = "contactNo";
    public static final String EXTRA_waterType = "waterType";
    public static final String EXTRA_itemQuantity = "itemQuantity";
    public static final String EXTRA_deliveryFee = "deliveryFee";
    public static final String EXTRA_pricePerGallon = "pricePerGallon";
    public static final String EXTRA_service = "service";
    public static final String EXTRA_address = "address";
    public static final String EXTRA_totalPrice = "totalPrice";

    FirebaseUser currentUser;
    private WPInProgressAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseRef2;
    private ArrayList<TransactionHeaderFileModel> mUploads;
    private ArrayList<TransactionDetailFileModel> mUploads2;
    public WPInProgressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wpin_progress, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        mUploads = new ArrayList<TransactionHeaderFileModel>();
        mUploads2 = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currendId = currentUser.getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("Transaction_Detail_File");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            TransactionHeaderFileModel transactionHeader = postSnapshot.getValue(TransactionHeaderFileModel.class);
                           // TransactionDetailFileModel transactionDetail = postSnapshot.getValue(TransactionDetailFileModel.class);
                                mUploads.add(transactionHeader);

                        }
                        mAdapter = new WPInProgressAdapter(getActivity(), mUploads);
                        recyclerView.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(WPInProgressFragment.this::onItemClick);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
             return view;
    }


    @Override
    public void onItemClick(int position) {
        Intent detailIntent = new Intent(getActivity(), WPPendingTransactionFragment.class);
        TransactionHeaderFileModel clickedItem = mUploads.get(position);

        detailIntent.putExtra(EXTRA_transactionNo, clickedItem.getTrans_no());
        detailIntent.putExtra(EXTRA_customerName, clickedItem.getTrans_status());
//        detailIntent.putExtra(EXTRA_contactNo, clickedItem.getContactNo());
//        detailIntent.putExtra(EXTRA_waterType, clickedItem.getWaterType());
//        detailIntent.putExtra(EXTRA_itemQuantity, clickedItem.getItemQuantity());
//        detailIntent.putExtra(EXTRA_deliveryFee, clickedItem.getDeliveryFee());
//        detailIntent.putExtra(EXTRA_pricePerGallon, clickedItem.getPricePerGallon());
//        detailIntent.putExtra(EXTRA_service, clickedItem.getService());
//        detailIntent.putExtra(EXTRA_totalPrice, clickedItem.getTotalPrice());
//        detailIntent.putExtra(EXTRA_address, clickedItem.getAddress());
//
//        Toast.makeText(getActivity(), "iya sud kay: "+clickedItem.getTransactionNo(), Toast.LENGTH_SHORT).show();
        startActivity(detailIntent);
    }
}
