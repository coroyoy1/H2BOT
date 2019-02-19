package com.example.administrator.h2bot.tpaaffiliate;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.h2bot.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TPAAccountSettingFragment extends Fragment {


    Dialog dialog;
    public TPAAccountSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tpa_fragment_account_setting, container, false);
    }

    public void ShowPopUpAccountSettingUpdate(View view) {
        Button cancelBtn;
        Button saveChangesBtn;
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.account_settings_popup);
        cancelBtn = dialog.findViewById(R.id.cancelBtn);
        saveChangesBtn = dialog.findViewById(R.id.saveChangesBtn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Temporary", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }
}
