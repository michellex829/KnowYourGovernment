
package com.michelleweixu.knowyourgovernment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener {
    private static final String TAG = "MainActivity";
    private final List<Official> officialList = new ArrayList<>();
    Geocoder geocoder;
    private static int MY_LOCATION_REQUEST_CODE_ID = 111;
    String locationFromAPI;
    Location currentLocation = null;
    private LocationManager locationManager;
    private Criteria criteria;
    private RecyclerView recyclerView;
    private String currZIPCode;
    OfficialAdapter mAdapter;
    private MainActivity mainAct;
    private String input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainAct = this;

        // set action bar color to gray
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#999999")));
        }

        geocoder = new Geocoder(this, Locale.getDefault());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();
        // use gps for location
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if (!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot Be accessed/loaded without an internet connection\n");
            AlertDialog dialog = builder.create();
            dialog.show();
            // mAdapter.notifyDataSetChanged();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID);
        } else {
            setLocation();
            CivicInfoDownloader rd = new CivicInfoDownloader(this, currZIPCode);
            new Thread(rd).start();
        }

        recyclerView = findViewById(R.id.recyclerView);
        mAdapter = new OfficialAdapter(officialList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // this is standard for options menu
        getMenuInflater().inflate(R.menu.options_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
            case R.id.menu_location:
                locationEntryDialog();
                return true;
            case R.id.menu_about:
                openAboutActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void setCurrLocation(String city, String state, String zip) {
        if (city.trim().isEmpty() && zip.trim().isEmpty() && state != null) {
            locationFromAPI = state;
        }
        else {
            locationFromAPI = city + ", " + state + " " + zip;
        }
        ((TextView) findViewById(R.id.currrent_location)).setText(locationFromAPI);
    }

    public void openOfficialActivity(View v, Official off) {
        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("IndividualOfficial", off);
        intent.putExtra("location",locationFromAPI);
        startActivity(intent);
    }

    public void openAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void locationEntryDialog() {
        if (!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot Be accessed/loaded without an internet connection\n");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create an edittext and set it to be the builder's view
        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                input = et.getText().toString().trim();
                officialList.clear();
                CivicInfoDownloader rd = new CivicInfoDownloader(mainAct, input);
                new Thread(rd).start();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();

            }
        });

        // builder.setMessage("Please enter a value:");
        builder.setTitle("Enter a City, State or a Zip Code:");

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void onClick(View v) {
        int position = recyclerView.getChildLayoutPosition(v);
        Official off = officialList.get(position);
        openOfficialActivity(v, off);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull
            String[] permissions, @NonNull
                    int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                setLocation();
                CivicInfoDownloader rd = new CivicInfoDownloader(this, currZIPCode);
                new Thread(rd).start();
                return;
            }
        }
        ((TextView) findViewById(R.id.currrent_location)).setText("No Permission");
    }

    @SuppressLint("MissingPermission")
    private void setLocation() {
        String bestProvider = locationManager.getBestProvider(criteria, true);
        ((TextView) findViewById(R.id.currrent_location)).setText(bestProvider);

        if (bestProvider != null) { currentLocation = locationManager.getLastKnownLocation(bestProvider); }

        if (currentLocation != null) {
            try {
                List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                setZIPCode(addresses);
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
         } else {
            ((TextView) findViewById(R.id.currrent_location)).setText("Location unavailable");
        }
    }

    private void setZIPCode(List<Address> addresses) {
        if (addresses.size() == 0) {
            ((TextView) findViewById(R.id.currrent_location)).setText("Location unavailable.");
        }
        else {
                Address add = addresses.get(0);
                currZIPCode = String.format("%s", (add.getPostalCode() == null ? "" : add.getPostalCode()));
        }
    }

    public void addOfficialToList(Official off) {
        this.officialList.add(off);
        Collections.sort(officialList);
        mAdapter.notifyDataSetChanged();
    }
}

