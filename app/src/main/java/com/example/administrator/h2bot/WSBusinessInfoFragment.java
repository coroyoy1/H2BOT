package com.example.administrator.h2bot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WSBusinessInfoFragment extends Fragment implements View.OnClickListener
        {

    Button updateBI, addproductBI;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_ws_businessinfo, container, false);

       updateBI = view.findViewById(R.id.updateButtonWSBI);
       addproductBI = view.findViewById(R.id.addProductButtonBusinessInfo);

       updateBI.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

           }
       });

       addproductBI.setOnClickListener(this);

       return view;
    }

            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.addProductButtonBusinessInfo:
                        AddItemMerchant additem = new AddItemMerchant();
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container_ws, additem);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        break;
                }
            }
        }
