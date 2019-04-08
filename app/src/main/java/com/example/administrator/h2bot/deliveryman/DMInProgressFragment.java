package com.example.administrator.h2bot.deliveryman;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.adapter.DMInProgressOrdersAdapter;
import com.example.administrator.h2bot.adapter.WSInProgressOrdersAdapter;
import com.example.administrator.h2bot.models.OrderModel;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserWSDMFile;
import com.example.administrator.h2bot.waterstation.WSInProgressFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DMInProgressFragment extends Fragment implements WSInProgressOrdersAdapter.OnItemClickListener{

    private RecyclerView recyclerView;
    private DMInProgressOrdersAdapter POAdapter;
    private List<OrderModel> uploadPO;
    private List<UserWSDMFile> uploadDM;
    FirebaseUser firebaseUser;
    String firebaseUID;
    RelativeLayout noOrdersLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dm_inprogress, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewDMFrag);
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
        firebaseUID = firebaseUser.getUid();

        uploadPO = new ArrayList<>();

        displayData();

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

    public void displayData()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_DM_File");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                        if(dataSnapshot2.child("delivery_man_id").getValue(String.class).equals(firebaseUID))
                        {
                            String merchantId = dataSnapshot2.child("station_id").getValue(String.class);
                            if (merchantId != null) {
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Customer_File");
                                reference1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot5) {
                                        uploadPO.clear();
                                        for (DataSnapshot dataSnapshot3 : dataSnapshot5.getChildren())
                                        {
                                            for (DataSnapshot dataSnapshot4 : dataSnapshot3.child(merchantId).getChildren())
                                            {
                                                OrderModel orderModel = dataSnapshot4.getValue(OrderModel.class);
                                                if (orderModel != null)
                                                {
                                                    if (orderModel.getOrder_merchant_id().equals(merchantId))
                                                    {
                                                        if (orderModel.getOrder_status().equals("In-Progress")
                                                                || orderModel.getOrder_status().equalsIgnoreCase("Broadcasting")
                                                                || orderModel.getOrder_status().equalsIgnoreCase("Dispatched"))
                                                        {
                                                            noOrdersLayout.setVisibility(View.INVISIBLE);
                                                            recyclerView.setVisibility(View.VISIBLE);
                                                            uploadPO.add(orderModel);
                                                        }
                                                    }
                                                }
                                            }
                                            POAdapter = new DMInProgressOrdersAdapter(getActivity(), uploadPO);
                                            recyclerView.setAdapter(POAdapter);
                                        }
                                        if (uploadPO.size() == 0) {
                                            noOrdersLayout.setVisibility(View.VISIBLE);
                                            recyclerView.setVisibility(View.GONE);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemClick(int position) {

    }
}
