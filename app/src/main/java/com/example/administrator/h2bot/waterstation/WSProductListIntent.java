package com.example.administrator.h2bot.waterstation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.squareup.picasso.Picasso;

public class WSProductListIntent extends Fragment implements View.OnClickListener {
    TextView itemN, itemP, itemQ, itemU;
    ImageView imageView;
    Button backBu, updateBu;
    String itemUi;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ws_productlistintent, container, false);
        itemN = view.findViewById(R.id.PLIitemname);
        itemP = view.findViewById(R.id.PLIprice);
        itemQ = view.findViewById(R.id.PLIquantity);
        itemU = view.findViewById(R.id.PLItype);
        imageView = view.findViewById(R.id.PLIimage);

        backBu = view.findViewById(R.id.PLIbackbutton);
        updateBu = view.findViewById(R.id.PLIupdatebutton);

        backBu.setOnClickListener(this);
        updateBu.setOnClickListener(this);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String itemNa = bundle.getString("ItemNameMDA");
            String itemPr = bundle.getString("ItemPriceMDA");
            String itemQu = bundle.getString("ItemQuantityMDA");
            String itemTy = bundle.getString("ItemTypeMDA");
            String itemIm = bundle.getString("ItemImageMDA");
            itemUi = bundle.getString("ItemUidMDA");
            itemN.setText("Item Name: "+itemNa);
            itemP.setText("    Price: "+itemPr);
            itemQ.setText(" Quantity: "+itemQu);
            itemU.setText("     Type: "+itemTy);
            Picasso.get().load(itemIm)
                    .fit()
                    .centerCrop()
                    .into(imageView);
            DataGet(itemUi);

        }
        return view;
    }

    public String DataGet(String itemUD)
    {
        return itemUD;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.PLIbackbutton:
                WSProductListFragment additem = new WSProductListFragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container_ws, additem)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.PLIupdatebutton:
                String uidString = DataGet(itemUi);
                WSProductListUpdate updateitem = new WSProductListUpdate();
                AppCompatActivity activityapp = (AppCompatActivity) v.getContext();
                activityapp.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container_ws, updateitem)
                        .addToBackStack(null)
                        .commit();
                Bundle args = new Bundle();
                args.putString("ItemUidPLI", uidString);
                updateitem.setArguments(args);
                Toast.makeText(getActivity(), DataGet(itemUi), Toast.LENGTH_LONG).show();
                break;
        }
    }
}
