package com.example.administrator.h2bot.waterstation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.h2bot.R;
import com.example.administrator.h2bot.absampletestphase.RatingAdapter;
import com.example.administrator.h2bot.absampletestphase.RatingModel;
import com.example.administrator.h2bot.adapter.WSInProgressOrdersAdapter;
import com.example.administrator.h2bot.models.OrderModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WSFeedbackFragment extends Fragment implements View.OnClickListener {

    RatingBar rateStar;
    TextView rateText;
    RecyclerView recyclerViewRate;
    private RatingAdapter POAdapter;
    private List<RatingModel> uploadPO;
    Button rateButton, laterButton, submitButton;
    EditText additonalComment;
    RatingBar ratingBar, bar;

    FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_rating_main, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uploadPO = new ArrayList<>();
        //Dialog
        findViewId(view);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRate();
            }
        });

        return view;
    }


    public void findViewId(View view)
    {
        rateButton = view.findViewById(R.id.rateButtonMain);
        rateStar = view.findViewById(R.id.rateStarsFeedback);
        rateText = view.findViewById(R.id.textViewStar);
        recyclerViewRate = view.findViewById(R.id.recyclerViewRatingFeedback);
        recyclerViewRate.setHasFixedSize(true);
        recyclerViewRate.setLayoutManager(new LinearLayoutManager(getActivity()));

        displayReviews();
        totalRating();
    }

    //Get Dialog
    public void showDialogRate()
    {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.z_customer_rate, null);

        laterButton = dialogView.findViewById(R.id.laterRatingButton);
        submitButton = dialogView.findViewById(R.id.submitRatingButton);
        additonalComment = dialogView.findViewById(R.id.addtionalRate);
        ratingBar = dialogView.findViewById(R.id.ratingStarsFeedback);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();

        laterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rateString = String.valueOf(ratingBar.getRating());
                String rating_customer_id = "pzSnsbkYLBcQ32f11ckMHAajyT73",
                        rating_merchant_id = firebaseUser.getUid(),
                        rating_number = rateString,
                        rating_comment = additonalComment.getText().toString(),
                        rating_status = "AC";
                ratingOfCustomer(rating_customer_id,rating_merchant_id, rating_number, rating_comment, rating_status);
                showMessage(rateString);
            }
        });
        alertDialog.show();
    }

    private void displayReviews()
    {
        DatabaseReference getAllReview = FirebaseDatabase.getInstance().getReference("Ratings");
        getAllReview.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uploadPO.clear();
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    RatingModel ratingModel = dataSnapshot1.getValue(RatingModel.class);
                    uploadPO.add(ratingModel);
                }
                POAdapter = new RatingAdapter(getActivity(), uploadPO);
                recyclerViewRate.setAdapter(POAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void totalRating()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ratings");
        databaseReference.child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        double count = 0;
                        double ratingCount = 0;
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                        {
                            RatingModel ratingModel = dataSnapshot1.getValue(RatingModel.class);
                            if(ratingModel != null)
                            {
                                double rating = Double.parseDouble(ratingModel.getRating_number());
                                ratingCount = ratingCount + rating;
                                double countUser = Double.parseDouble(ratingModel.getCount_user());
                                count = count + countUser;
                            }

                        }
                        double totalRate = ratingCount / count;
                        String rate = String.valueOf(totalRate);

                        rateStar.setEnabled(false);
                        rateStar.setMax(5);
                        rateStar.setStepSize(0.01f);
                        rateStar.setRating(Float.parseFloat(rate));
                        rateStar.invalidate();

                        rateText.setText(rate);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void ratingOfCustomer(String rating_customer_id, String rating_merchant_id, String rating_number, String rating_comment, String rating_status)
    {

        RatingModel ratingModel = new RatingModel(
                rating_customer_id,
                rating_merchant_id,
                rating_number,
                rating_comment,
                "1",
                rating_status
        );
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Ratings");
        databaseReference.child(firebaseUser.getUid()).child("yzSnsbkYLBcQ32f11ckMHAajyT73").setValue(ratingModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("Successfully rated");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Failed to attempt to save feedback");
                    }
                });
    }


    private void showMessage(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {

        }
    }
}