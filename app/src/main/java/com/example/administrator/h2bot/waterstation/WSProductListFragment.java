package com.example.administrator.h2bot.waterstation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.target.SimpleTarget;
import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.UserWSWDWaterTypeFile;
import com.example.administrator.h2bot.models.MerchantDataAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WSProductListFragment extends Fragment implements View.OnClickListener{

    private RecyclerView recyclerViewPL;
    private FloatingActionButton floatButton;
    private MerchantDataAdapter PLAdapter;
    private DatabaseReference databaseReferencePL;
    private List<UserWSWDWaterTypeFile> uploadPL;
    private FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_productlist, container, false);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewPL = view.findViewById(R.id.WSPLrecyclerView);
        recyclerViewPL.setHasFixedSize(true);
        recyclerViewPL.setLayoutManager(llm);
        recyclerViewPL.setAdapter(PLAdapter);

        floatButton = view.findViewById(R.id.fab);
        floatButton.setOnClickListener(this);


        mAuth = FirebaseAuth.getInstance();
        uploadPL = new ArrayList<>();
        databaseReferencePL = FirebaseDatabase.getInstance().getReference("User_WS_WD_Water_Type_File").child(mAuth.getCurrentUser().getUid());

        databaseReferencePL.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    UserWSWDWaterTypeFile getterSetter = postSnapshot.getValue(UserWSWDWaterTypeFile.class);
                    uploadPL.add(getterSetter);

                }
                PLAdapter = new MerchantDataAdapter(getActivity(), uploadPL);
                recyclerViewPL.setAdapter(PLAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab:
                WSProductAdd additem = new WSProductAdd();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.fade_in, android.R.anim.fade_out)
                        .replace(R.id.fragment_container_ws, additem)
                        .addToBackStack(null)
                        .commit();
                break;
        }

    }
}
