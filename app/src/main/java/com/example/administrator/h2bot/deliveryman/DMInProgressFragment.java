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
import com.example.administrator.h2bot.adapter.DMInProgressOrdersAdapter;
import com.example.administrator.h2bot.adapter.WSInProgressOrdersAdapter;
import com.example.administrator.h2bot.models.TransactionHeaderFileModel;
import com.example.administrator.h2bot.models.UserFile;
import com.example.administrator.h2bot.models.UserWSDMFile;
import com.example.administrator.h2bot.waterstation.WSInProgressFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DMInProgressFragment extends Fragment implements WSInProgressOrdersAdapter.OnItemClickListener{

    private RecyclerView recyclerView;
    private DMInProgressOrdersAdapter POAdapter;
    private List<TransactionHeaderFileModel> uploadPO;
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

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUID = firebaseUser.getUid();

        uploadPO = new ArrayList<>();

        getInProgressData();

        return view;
    }


    public void getInProgressData()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User_File");
        databaseReference.child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String stationId = dataSnapshot.child("station_parent").getValue(String.class);
                        if(stationId != null)
                        {
                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User_WS_DM_File");
                            databaseReference1.child(stationId).child(firebaseUser.getUid())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            UserWSDMFile userWSDMFile = dataSnapshot.getValue(UserWSDMFile.class);
                                            if(userWSDMFile != null)
                                            {
                                                String merchantnum = userWSDMFile.getStation_id();
                                                if(merchantnum != null)
                                                {
                                                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
                                                    databaseReference2.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot post : dataSnapshot.getChildren())
                                                            {
                                                                TransactionHeaderFileModel transactionHeaderFileModel = post.getValue(TransactionHeaderFileModel.class);
                                                                if(transactionHeaderFileModel != null)
                                                                {
                                                                    if(transactionHeaderFileModel.getMerchant_id().equals(merchantnum)
                                                                            && transactionHeaderFileModel.getTrans_status().equals("In-Progress"))
                                                                    {
                                                                        uploadPO.add(transactionHeaderFileModel);
                                                                    }
                                                                }
                                                            }
                                                            POAdapter = new DMInProgressOrdersAdapter(getActivity(), uploadPO);
                                                            recyclerView.setAdapter(POAdapter);
                                                            POAdapter.setOnItemClickListener(DMInProgressFragment.this::onItemClick);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    public void getInProgress()
    {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User_File").child(firebaseUser.getUid());
        databaseReference1
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserFile userFile = dataSnapshot.getValue(UserFile.class);
                        if(userFile.getUser_getUID().equals(firebaseUser.getUid()))
                        {
                            String merchantnum = dataSnapshot.child("station_parent").getValue(String.class);
                            if(merchantnum !=null)
                            {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_WS_DM_File");
                                reference.child(merchantnum).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot postsnap : dataSnapshot.getChildren())
                                        {
                                            if (postsnap.child("delivery_man_id").getValue(String.class).equals(firebaseUID)) {
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Transaction_Header_File");
                                                databaseReference.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot post : dataSnapshot.getChildren()) {
                                                            TransactionHeaderFileModel transactionHeaderFileModel = post.getValue(TransactionHeaderFileModel.class);
                                                            String merchantTransId = transactionHeaderFileModel.getMerchant_id();
                                                            if (transactionHeaderFileModel.getMerchant_id().equals(merchantTransId)
                                                                    && transactionHeaderFileModel.getTrans_status().equals("In-Progress")) {
                                                                uploadPO.add(transactionHeaderFileModel);
                                                            }
                                                        }
                                                        POAdapter = new DMInProgressOrdersAdapter(getActivity(), uploadPO);
                                                        recyclerView.setAdapter(POAdapter);
                                                        POAdapter.setOnItemClickListener(DMInProgressFragment.this::onItemClick);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        //showMessage("Data does not exists!");
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

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
