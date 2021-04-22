package com.michelleweixu.knowyourgovernment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {
    private static final String TAG = "OfficialActivityTag";
    private Official off;
    private TextView location;
    String currentLocation;
    private ImageView profileImageView;
    private ImageView partyLogoView;
    private ImageView Facebook;
    private ImageView Twitter;
    private ImageView Youtube;

    private TextView office;
    private TextView name;
    private TextView party;
    private TextView addressTitle;
    private TextView phoneTitle;
    private TextView emailTitle;
    private TextView websiteTitle;
    private TextView address;
    private TextView phoneNo;
    private TextView email;
    private TextView website;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#999999")));
        }
        profileImageView = findViewById(R.id.profile_image);
        partyLogoView =  findViewById(R.id.party_logo);
        Facebook =  findViewById(R.id.facebook);
        Twitter =  findViewById(R.id.twitter);
        Youtube = findViewById(R.id.youtube);
        location = findViewById(R.id.currrent_location);
        office = findViewById(R.id.office);
        name = findViewById(R.id.name);
        party = findViewById(R.id.party);
        addressTitle = findViewById(R.id.title_address);
        phoneTitle = findViewById(R.id.title_phone);
        emailTitle = findViewById(R.id.title_email);
        websiteTitle = findViewById(R.id.title_website);
        address = findViewById(R.id.address);
        phoneNo = findViewById(R.id.phone_no);
        email = findViewById(R.id.email);
        website = findViewById(R.id.website);


        Intent intent = getIntent();

        if (intent.hasExtra("location")) {
            currentLocation =  (String)intent.getSerializableExtra("location");
            location.setText(currentLocation);
        }

        if (intent.hasExtra("IndividualOfficial")) {
        off = (Official) intent.getSerializableExtra("IndividualOfficial");
        if (off != null) {
            name.setText(off.name);
            office.setText(off.office);

            if (off.party != null) {
                if (!off.party.trim().isEmpty()) {
                    party.setText("(" + off.party +")");
                }
            }
            else {
                party.setVisibility(View.GONE);
            }

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

            if (off.officeAddress != null) {
                if (!off.officeAddress.trim().isEmpty()) {
                    addressTitle.setText("Address:");
                    address.setText(off.officeAddress);
                }
            }
            else {
                addressTitle.setVisibility(View.GONE);
                address.setVisibility(View.GONE);
            }

            if (off.phoneNumber != null) {
                if (!off.phoneNumber.trim().isEmpty()) {
                    phoneTitle.setText("Phone: ");
                    phoneNo.setText(off.phoneNumber);
                }
            }
            else {
                phoneTitle.setVisibility(View.GONE);
                phoneNo.setVisibility(View.GONE);
            }

            if (off.website != null) {
                if (!off.website.trim().isEmpty()) {
                    websiteTitle.setText("Website: ");
                    website.setText(off.website);
                }
            }
            else {
                websiteTitle.setVisibility(View.GONE);
                website.setVisibility(View.GONE);
            }

            if (off.emailAddress != null) {
                if (!off.emailAddress.trim().isEmpty()) {
                    emailTitle.setText("Email: ");
                    email.setText(off.emailAddress);
                }
            }
            else {
                emailTitle.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
            }

            if (off.facebook != null) {
                if (!off.facebook.trim().isEmpty()) {
                    Facebook.setImageResource(R.drawable.facebook);
                }
            }
            else {
                Facebook.setVisibility(View.GONE);
            }

            if (off.twitter != null) {
                if (!off.twitter.trim().isEmpty()) {
                    Twitter.setImageResource(R.drawable.twitter);
                }
            }
            else {
                Twitter.setVisibility(View.GONE);
            }

            if (off.youtube != null) {
                if (!off.youtube.trim().isEmpty()) {
                    Youtube.setImageResource(R.drawable.youtube);
                }
            }
            else {
                Youtube.setVisibility(View.GONE);
            }

            Linkify.addLinks(address, Linkify.ALL);
            Linkify.addLinks(phoneNo, Linkify.ALL);
            Linkify.addLinks(email, Linkify.ALL);
            Linkify.addLinks(website, Linkify.ALL);

            // try set color to red
            // look up change button color in alert dialogue
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

    @SuppressLint("SetJavaScriptEnabled")
    public void profilePhotoClick(View v) {
        if (off.photoURL != null) {
            if (!off.photoURL.trim().isEmpty()) {
                Intent intent = new Intent(this, ProfilePhotoActivity.class);
                intent.putExtra("IndividualOfficial", off);
                intent.putExtra("location",currentLocation);
                startActivity(intent);
            }
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    public void partyLogoClick(View v) {
        String url = null;
        if (off.party.contains("epublican")) {
            url = "https://www.gop.com";
        }
        else if (off.party.contains("emocratic")) {
            url = "https://democrats.org";
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    // Twitter (example onClick method to be associated with the Twitter ImageView icon)
    public void twitterClicked(View v) {
        Intent intent = null;
        String name = off.twitter;
        try {
// get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
// no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }


    // Facebook (example onClick method to be associated with the Facebook ImageView icon):
    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + off.facebook;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                // urlToUse = "fb://page/" + channels.get("Facebook");
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        // facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    // YouTube (example onClick method to be associated with the YouTube ImageView icon):
    public void youTubeClicked(View v) {
        String name = off.youtube;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }
}