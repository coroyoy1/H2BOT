package com.example.administrator.h2bot.dealer;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.models.CaptureActivityPortrait;
import com.google.zxing.integration.android.IntentIntegrator;

import com.google.firebase.database.DatabaseReference;


public class WPPendingTransactionFragment extends Fragment {

    TextView orderNumberTextView,customerNameTextView,contactNumberTextView,waterTypeTextView,pricePerGallonTextView,serviceTextView,
    addressTextView,deliveryFeeTextView,totalPriceTextView,itemQuantityTextView;
    Button viewLocationButton, QRscanButton;

    private DatabaseReference mDatabase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wp_pending_transaction, container, false);

        //TextView
        orderNumberTextView = view.findViewById(R.id.orderNumberTextView);
        customerNameTextView = view.findViewById(R.id.customerNameTextView);
        contactNumberTextView = view.findViewById(R.id.contactNumberTextView);
        waterTypeTextView = view.findViewById(R.id.waterTypeTextView);
        pricePerGallonTextView = view.findViewById(R.id.pricePerGallonTextView);
        serviceTextView = view.findViewById(R.id.serviceTextView);
        addressTextView = view.findViewById(R.id.addressTextView);
        deliveryFeeTextView = view.findViewById(R.id.deliveryFeeTextView);
        totalPriceTextView = view.findViewById(R.id.totalPriceTextView);
        itemQuantityTextView = view.findViewById(R.id.itemQuantityTextView);

        //Button
        viewLocationButton = view.findViewById(R.id.viewLocationButton);
        QRscanButton = view.findViewById(R.id.QRscanButton);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String transaction = bundle.getString("transactionno");
            String customerName = bundle.getString("customername");
            String contactNo = bundle.getString("contactno");
            String waterType = bundle.getString("watertype");
            String address = bundle.getString("address");
            String itemQuantity = bundle.getString("itemquantity");
            String deliveryFee = bundle.getString("deliveryfee");
            String pricePerGallon = bundle.getString("pricepergallon");
            String service = bundle.getString("service");
            String totalPrice = bundle.getString("totalprice");

            orderNumberTextView.setText(transaction);
            customerNameTextView.setText(customerName);
            addressTextView.setText(contactNo);
            contactNumberTextView.setText(contactNo);
            waterTypeTextView.setText(waterType);
            pricePerGallonTextView.setText(pricePerGallon);
            serviceTextView.setText(service);
            deliveryFeeTextView.setText(deliveryFee);
            totalPriceTextView.setText(totalPrice);
            itemQuantityTextView.setText(itemQuantity);
            addressTextView.setText(address);
        }
        viewLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WaterDealerGoogleMapFragment detail = new WaterDealerGoogleMapFragment();
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_wp, detail).addToBackStack(null).commit();
            }
        });
        final Activity activity = getActivity();

        QRscanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(WPPendingTransactionFragment.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setCaptureActivity(CaptureActivityPortrait.class);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
            }
        });
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
    }
}
