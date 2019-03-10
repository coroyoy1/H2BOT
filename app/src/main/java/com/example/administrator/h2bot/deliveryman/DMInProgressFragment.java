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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dm_inprogress, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewDMFrag);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
        builder.setMessage("Are you sure to exit application?").setPositiveButton("Yes", dialogClickListener)
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
                                                POAdapter = new DMInProgressOrdersAdapter(getActivity(), uploadPO);
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

    @Override
    public void onItemClick(int position) {

    }
}
