package com.example.administrator.h2bot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

public class WSAccountSettingsFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_accountsettings,
                container, false);
        Button updateDoc = (Button)view.findViewById(R.id.updateDocument);
        Button updateAcc = (Button)view.findViewById(R.id.updateAccount);
        updateAcc.setOnClickListener(this);
        updateDoc.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.updateDocument:
                Intent intent = new Intent(getActivity(), WSAccountSettingsUpdateDoc.class);
                intent.putExtra("Data", "Some Data");
                startActivity(intent);
            break;
            case R.id.updateAccount:
                Intent intent2 = new Intent(getActivity(), WSAccountSettingsUpdateAcc.class);
                intent2.putExtra("Data", "Some Data");
                startActivity(intent2);
                break;
        }
    }
}
