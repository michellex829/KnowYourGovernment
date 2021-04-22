package com.michelleweixu.knowyourgovernment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class ProfilePhotoActivity extends AppCompatActivity {
    private static final String TAG = "ProfilePhotoActivityTag";
    private Official off;
    private TextView location;
    String currentLocation;
    private ImageView profileImageView;
    private ImageView partyLogoView;

    private TextView office;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#999999")));
        }

        profileImageView = findViewById(R.id.profile_picture);
        partyLogoView =  findViewById(R.id.party_logo);

        location = findViewById(R.id.currrent_location);
        office = findViewById(R.id.office);
        name = findViewById(R.id.name);

        Intent intent = getIntent();

        if (intent.hasExtra("location")) {
            currentLocation =  (String)intent.getSerializableExtra("location");
            location.setText(currentLocation);
        }

        if (intent.hasExtra("IndividualOfficial")) {
            off = (Official) intent.getSerializableExtra("IndividualOfficial");
            if (off != null) {
                office.setText(off.office);
                name.setText(off.name);

                if (off.photoURL != null) {
                    if (!off.photoURL.trim().isEmpty()) {
                        loadRemoteImage(off.photoURL);
                    }
                }
                else { profileImageView.setImageResource(R.drawable.missing); }
                if (off.party.contains("epublican")) {
                    partyLogoView.setImageResource(R.drawable.rep_logo);
                    getWindow().getDecorView().setBackgroundColor(Color.RED);
                }
                else if (off.party.contains("emocratic")) {
                    partyLogoView.setImageResource(R.drawable.dem_logo);
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                }
                else {
                    getWindow().getDecorView().setBackgroundColor(Color.BLACK);
                    partyLogoView.setVisibility(View.GONE);
                }
            }
        }
    }

    private void loadRemoteImage(final String imageURL) {
        // Needs gradle  implementation 'com.squareup.picasso:picasso:2.71828'
        Picasso.get().load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                //.into(imageView); // Use this if you don't want a callback
                .into(profileImageView,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess: Size: " +
                                        ((BitmapDrawable) profileImageView.getDrawable()).getBitmap().getByteCount());
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.d(TAG, "onError: " + e.getMessage());
                            }
                        });
    }
}