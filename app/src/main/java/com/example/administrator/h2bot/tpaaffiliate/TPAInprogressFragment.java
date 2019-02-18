package com.example.administrator.h2bot.tpaaffiliate;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.administrator.h2bot.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TPAInprogressFragment extends Fragment {

    Dialog dialog;
    public TPAInprogressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tpainprogress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new Dialog(getActivity());
    }

    public void QrCode(View view) {
        Button cancelBtn;
        dialog.setContentView(R.layout.qr_code_popup);
        cancelBtn = dialog.findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

//    public void snackBar(){
//        View parentLayout = findViewById(android.R.id.content);
//        Snackbar.make(parentLayout, "Please go to the respective station for further negotiation.", Snackbar.LENGTH_INDEFINITE)
//                .setAction("Okay", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                    }
//                })
//                .setActionTextColor(getResources().getColor(android.R.color.white ))
//                .show();
//    }
}
