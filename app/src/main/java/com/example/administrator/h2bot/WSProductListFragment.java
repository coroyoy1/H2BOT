package com.example.administrator.h2bot;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class WSProductListFragment extends Fragment {

    private RecyclerView recyclerViewPL;
    private MerchantDataAdapter PLAdapter;
    private DatabaseReference databaseReferencePL;
    private List<MerchantGetterSetter> uploadPL;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_productlist, container, false);

        recyclerViewPL = view.findViewById(R.id.WSPLrecyclerView);
        recyclerViewPL.setHasFixedSize(true);
        recyclerViewPL.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAuth = FirebaseAuth.getInstance();
        uploadPL = new ArrayList<>();
        databaseReferencePL = FirebaseDatabase.getInstance().getReference("User Items").child(mAuth.getCurrentUser().getUid());
        databaseReferencePL.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    MerchantGetterSetter getterSetter = postSnapshot.getValue(MerchantGetterSetter.class);
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
}
