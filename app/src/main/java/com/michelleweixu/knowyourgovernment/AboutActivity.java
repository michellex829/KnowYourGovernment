package com.michelleweixu.knowyourgovernment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private static final String civicInfoAPIURL = "https://developers.google.com/civic-information/";
    private TextView api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#999999")));
        }

        api = findViewById(R.id.civic_api);
        SpannableString content = new SpannableString("Google Civic Information API");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        api.setText(content);

    }

    public void clickCivicAPI(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(civicInfoAPIURL));
        startActivity(i);
        // api.getPaint().setUnderlineText(true);
    }
}