package com.example.administrator.h2bot.waterstation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.adapter.WSInProgressOrdersAdapter;
import com.example.administrator.h2bot.models.MerchantCustomerFile;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WSInProgressFragment extends Fragment implements WSInProgressOrdersAdapter.OnItemClickListener{
    private RecyclerView recyclerView;
    private WSInProgressOrdersAdapter POAdapter;
    private List<OrderModel> uploadPO;
    FirebaseUser firebaseUser;
    RelativeLayout noOrdersLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_ws_inprogress, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        noOrdersLayout = view.findViewById(R.id.noOrdersLayout);
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

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uploadPO = new ArrayList<>();

        displayAllData();
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

    private void showMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    private void displayAllData()
    {
        try {
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Customer_File");
            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    uploadPO.clear();
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        for (DataSnapshot post : dataSnapshot1.child(firebaseUser.getUid()).getChildren())
                        {
                            OrderModel orderModel = post.getValue(OrderModel.class);
                            if(orderModel != null)
                            {
                                if(orderModel.getOrder_merchant_id().equals(firebaseUser.getUid()))
                                {
                                    if (orderModel.getOrder_status().equalsIgnoreCase("In-Progress")
                                            || orderModel.getOrder_status().equalsIgnoreCase("Dispatched")
                                            || orderModel.getOrder_status().equalsIgnoreCase("Broadcasting")
                                            || orderModel.getOrder_status().equalsIgnoreCase("Accepted")
                                            || orderModel.getOrder_status().equalsIgnoreCase("Accepted by affiliate")
                                            || orderModel.getOrder_status().equalsIgnoreCase("Dispatched by affiliate"))
                                    {
                                        noOrdersLayout.setVisibility(View.INVISIBLE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        uploadPO.add(orderModel);
                                    }
                                }
                            }
                        }
                        POAdapter = new WSInProgressOrdersAdapter(getActivity(), uploadPO);
                        recyclerView.setAdapter(POAdapter);
                        POAdapter.setOnItemClickListener(WSInProgressFragment.this::onItemClick);
                    }
                    if(uploadPO.size() == 0)
                    {
                        noOrdersLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (DatabaseException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(int position) {

    }
}
